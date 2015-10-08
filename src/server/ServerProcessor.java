package server;

import handlers.ChannelAndBuffersContainer;
import handlers.ReadHandler;
import handlers.ServerMessageWriteHandler;
import handlers.WriteHandler;
import utils.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 21.09.2015.
 */
public class ServerProcessor implements ChatProcessor{
    private HashMap<String,String> users;
    private final int messageStoreLimit;
    private final ArrayList<Message> messageList;
    private AsyncTcpServer tcpServer;

    private AtomicInteger messageCounter;

    public ServerProcessor(Config config, HashMap<String,String> users) throws IOException{
        this.messageStoreLimit = config.getMessageLimit();
        this.users = users;
        this.messageList = new ArrayList<Message>(messageStoreLimit);
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

    private List<Message> getSubMessagesList( int lastSendMessageIndex ){
        List<Message> subMessageList = null;
        synchronized (messageList){
            if ( !messageList.isEmpty() ){
                int messageListSize = messageList.size();

                for( Message message: messageList ){
                    int index = messageList.indexOf( message );
                    if( message.getId() ==  lastSendMessageIndex && ( index + 1 != messageListSize ) ){
                        subMessageList =  messageList.subList( index + 1, messageListSize );
                    }
                }
            }
        }

        return subMessageList;
    }

    public void sendMessageList (ChannelAndBuffersContainer channelAndBuffersContainer, ServerMessageWriteHandler writeHandler){
            int lastSendMessageIndex = channelAndBuffersContainer.getLastSendMessageIndex();
            List<Message> subMessageList = getSubMessagesList( lastSendMessageIndex );
            if( subMessageList == null ){
                channelAndBuffersContainer.setReadyToWrite( true );
                return;
            }
            Message lastSendMessage = subMessageList.get( subMessageList.size() - 1);

            channelAndBuffersContainer.setLastSendMessageIndex( lastSendMessage.getId() );
            ByteBuffer writeBuffer = MessageWriter.createWriteBuffer( subMessageList );
            channelAndBuffersContainer.setWriteBuffer( writeBuffer );
            channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, writeHandler);
    }


    private void storeMessage( Message message ){
        synchronized ( messageList ){
            if ( messageList.size() == messageStoreLimit ){
                messageList.remove(0);
            }

            messageList.add( message );
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
        List<Message> messageArrayList = JsonConverter.fromJsonToList(messageListString);
        for( Message message: messageArrayList ){
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
