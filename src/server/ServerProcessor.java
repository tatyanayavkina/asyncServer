package server;

import handlers.ChannelAndBuffersContainer;
import handlers.ReadHandler;
import handlers.WriteHandler;
import utils.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 21.09.2015.
 */
public class ServerProcessor implements ChatProcessor{
    private HashMap<String,String> users;
    private final int messageStoreLimit;
    private final ArrayList<Message> messageList;
    private AsyncTcpServer tcpServer;

    public ServerProcessor(Config config, HashMap<String,String> users) throws IOException{
        this.messageStoreLimit = config.getMessageLimit();
        this.users = users;
        this.messageList = new ArrayList<Message>(messageStoreLimit);
        this.tcpServer = new AsyncTcpServer(this, config.getHost(), config.getPort(), config.getThreadCount());
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


    private void storeMessage( Message message ){
        synchronized ( messageList ){
            if ( messageList.size() == messageStoreLimit ){
                messageList.remove(0);
            }

            messageList.add( message );
        }
    }

    private void sendMessage(String message ){
        //send message to all connected clients
        Iterable<ChannelAndBuffersContainer> clientStates = this.tcpServer.getAllConnections();
        for ( ChannelAndBuffersContainer channelAndBuffersContainer : clientStates ) {
            ByteBuffer writeBuffer = MessageWriter.createWriteBuffer( message );
            channelAndBuffersContainer.setWriteBuffer( writeBuffer );
            channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, new WriteHandler() );
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

    public void handleInputMessage(String messageString, ChannelAndBuffersContainer channelAndBuffersContainer){
        Message message = (Message) JsonConverter.fromJson(messageString, Message.class);
        storeMessage( message );
        System.out.println("message" + message.toOutStr());
        sendMessage( messageString );
    }

    public void start(){
        tcpServer.start();
    }
}
