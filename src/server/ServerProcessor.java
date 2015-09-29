package server;

import handlers.ClientState;
import handlers.ReadHandler;
import handlers.WriteHandler;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 21.09.2015.
 */
public class ServerProcessor implements ChatProcessor{
    private HashMap<String,String> users;
    private final int messageStoreLimit;
    private final ArrayList<String> messageList;
    private AsyncTcpServer tcpServer;

    public ServerProcessor(Config config, HashMap<String,String> users) throws IOException{
        this.messageStoreLimit = config.getMessageLimit();
        this.users = users;
        this.messageList = new ArrayList<String>(messageStoreLimit);
        this.tcpServer = new AsyncTcpServer(this, config.getHost(), config.getPort(), config.getThreadCount());
    }

    private boolean authenticate(String credentialsStr, ClientState clientState) {
        UserCredentials credentials = (UserCredentials) JsonConverter.fromJson(credentialsStr, UserCredentials.class);
        boolean isAuthenticated = ( credentials != null && isUserRegistered( credentials ) );
        sendAuthenticationMessage(clientState, isAuthenticated);

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

    private void sendAuthenticationMessage(ClientState clientState, boolean isAuthenticated){
        UtilityMessage utilityMessage;

        if ( isAuthenticated ){
            utilityMessage = new UtilityMessage( UtilityMessage.StatusCodes.AUTHORIZED );
        } else {
            utilityMessage = new UtilityMessage( UtilityMessage.StatusCodes.NONAUTHORIZED );
        }

        String utilityMessageJson = JsonConverter.toJson( utilityMessage );
        ClientState bufClientState = MessageWriter.createClientState( clientState.getChannel(), utilityMessageJson);
        bufClientState.getChannel().write( bufClientState.getWriteBuffer(), bufClientState, new WriteHandler());
    }


    private void storeMessage( String message ){
        synchronized ( messageList ){
            if ( messageList.size() == messageStoreLimit ){
                messageList.remove(0);
            }

            messageList.add( message );
        }
    }

    private void sendMessage(String message, int clientId){
        ClientState bufClientState;
        //send message to all connected clients except client with id
        Iterable<ClientState> clientStates = this.tcpServer.getAllConnectionsExceptOne( clientId );
        for ( ClientState clientState : clientStates ) {
            bufClientState = MessageWriter.createClientState( clientState.getChannel(), message );
            bufClientState.getChannel().write( bufClientState.getWriteBuffer(), bufClientState, new WriteHandler() );
        }
    }

    public void handleNewClient(ClientState clientState){
        ReadHandler readHandler = new ReadHandler( false, this, "handleAuthorization" );
        clientState.getChannel().read( clientState.getReadSizeBuffer(), clientState, readHandler );
    }

    public void handleAuthorization(String credentials, ClientState clientState){
        if ( authenticate( credentials, clientState ) ){
            tcpServer.addConnection( clientState );
            ReadHandler readHandler = new ReadHandler( true, this, "handleInputMessage" );
            clientState.getChannel().read( clientState.getReadSizeBuffer(), clientState, readHandler );
        }
    }

    public void handleInputMessage(String message, ClientState clientState){
        storeMessage( message );
        System.out.println("message" + message);
        sendMessage( message, clientState.getInstance() );
    }

    public void start(){
        tcpServer.start();
    }
}
