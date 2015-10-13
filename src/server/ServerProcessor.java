package server;

import handlers.ChannelAndBuffersContainer;
import handlers.ReadHandler;
import handlers.ServerMessageWriteHandler;
import handlers.WriteHandler;
import utils.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 21.09.2015.
 */
public class ServerProcessor implements ChatProcessor{
    private HashMap<String,String> users;
    private final int messageStoreLimit;
    private final TreeMap<Integer,Message> messageStore;
    private AsyncTcpServer tcpServer;

    private AtomicInteger messageCounter;

    public ServerProcessor(Config config, HashMap<String,String> users) throws IOException{
        this.messageStoreLimit = config.getMessageLimit();
        this.users = users;
        this.messageStore = new TreeMap<Integer, Message>();
        this.tcpServer = new AsyncTcpServer(this, config.getHost(), config.getPort(), config.getThreadCount());
        this.messageCounter = new AtomicInteger();
    }

    private boolean authenticate(String credentialsStr, ChannelAndBuffersContainer channelAndBuffersContainer) {
        UserCredentials credentials = (UserCredentials) JsonConverter.fromJson(credentialsStr, UserCredentials.class);
        boolean isAuthenticated = ( credentials != null && isUserRegistered( credentials ) );
        sendAuthenticationMessage(channelAndBuffersContainer, isAuthenticated);

        return isAuthenticated;
    }

    private boolean isUserRegistered( UserCredentials credentials ){
        boolean isRegistered = false;
        String name = credentials.getName();

        if ( users.containsKey( name ) ){
            String userPass = users.get( name );
            String password = credentials.getPassword();

            if ( userPass.equals( password ) ){
                isRegistered = true;
            }
        }

        return isRegistered;
    }

    private void sendAuthenticationMessage(ChannelAndBuffersContainer channelAndBuffersContainer, boolean isAuthenticated){
        UtilityMessage utilityMessage;

        if ( isAuthenticated ){
            utilityMessage = new UtilityMessage( UtilityMessage.StatusCodes.AUTHORIZED );
        } else {
            utilityMessage = new UtilityMessage( UtilityMessage.StatusCodes.NONAUTHORIZED );
        }

        String utilityMessageJson = JsonConverter.toJson(utilityMessage);
        ByteBuffer writeBuffer = MessageWriter.createWriteBuffer(utilityMessageJson);
        channelAndBuffersContainer.setWriteBuffer( writeBuffer );
        channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, new WriteHandler());
    }


    public void sendMessageList (ChannelAndBuffersContainer channelAndBuffersContainer, ServerMessageWriteHandler writeHandler){
        SortedMap<Integer,Message> subMessageMap;
        int lastSendMessageIndex = channelAndBuffersContainer.getLastSendMessageIndex();
        synchronized ( messageStore ){
            subMessageMap = messageStore.tailMap(lastSendMessageIndex + 1);
        }
        if( subMessageMap.size() == 0 ){
            channelAndBuffersContainer.setReadyToWrite( true );
            return;
        }

        channelAndBuffersContainer.setLastSendMessageIndex( subMessageMap.lastKey() );
        ByteBuffer writeBuffer = MessageWriter.createWriteBuffer( subMessageMap );
        channelAndBuffersContainer.setWriteBuffer( writeBuffer );
        channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, writeHandler);
    }


    private void storeMessage( Message message ){
        synchronized (messageStore){
            if ( messageStore.size() == messageStoreLimit ){
                Integer key = messageStore.firstKey();
                messageStore.remove(key);
            }

            messageStore.put(message.getId(), message);
        }
    }

    private void sendMessage(Message message){
        //send message to all connected clients
        Iterable<ChannelAndBuffersContainer> channelAndBuffersContainers = this.tcpServer.getAllConnections();
        for ( ChannelAndBuffersContainer channelAndBuffersContainer : channelAndBuffersContainers ) {
            synchronized(channelAndBuffersContainer){
                if ( channelAndBuffersContainer.getReadyToWrite() ){
                    channelAndBuffersContainer.setReadyToWrite(false);
                    if ( channelAndBuffersContainer.getLastSendMessageIndex() == 0){
                        channelAndBuffersContainer.setLastSendMessageIndex( message.getId() - 1 );
                    }
                    sendMessageList( channelAndBuffersContainer, new ServerMessageWriteHandler( this ));
                }
            }
        }
    }

    public void handleNewClient(ChannelAndBuffersContainer channelAndBuffersContainer){
        ReadHandler readHandler = new ReadHandler( false, this );
        channelAndBuffersContainer.getChannel().read( channelAndBuffersContainer.getReadSizeBuffer(), channelAndBuffersContainer, readHandler );
    }

    public void handleAuthorization(String credentials, ChannelAndBuffersContainer channelAndBuffersContainer){
        if ( authenticate( credentials, channelAndBuffersContainer) ){
            tcpServer.addConnection(channelAndBuffersContainer);
            ReadHandler readHandler = new ReadHandler( true, this );
            channelAndBuffersContainer.getChannel().read( channelAndBuffersContainer.getReadSizeBuffer(), channelAndBuffersContainer, readHandler );
        }
    }

    public void handleInputMessage(String messageListString, ChannelAndBuffersContainer channelAndBuffersContainer){
        SortedMap<Integer, Message> messageMap = JsonConverter.fromJsonToMap(messageListString);
        for( Map.Entry<Integer,Message> entry : messageMap.entrySet() ){
            Message message = entry.getValue();
            int id = messageCounter.incrementAndGet();
            message.setId(id);
            storeMessage( message );
            sendMessage( message );
            System.out.println("message" + message.toOutStr());
        }
    }

    public void start(){
        tcpServer.start();
    }

    public void close(ChannelAndBuffersContainer channelAndBuffersContainer){
        tcpServer.removeConnection( channelAndBuffersContainer );
    }
}
