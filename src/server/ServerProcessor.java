package server;

import utils.Config;
import utils.JsonConverter;
import utils.MessageWriter;
import utils.UserCredentials;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 21.09.2015.
 */
public class ServerProcessor {
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

    private boolean authenticate(AsynchronousSocketChannel channel) {
        UserCredentials credentials = (UserCredentials) JsonConverter.fromJson(, UserCredentials.class);
        boolean isAuthenticated = ( credentials != null && isUserRegistered( credentials ) );
        sendAuthenticationMessage(tcpServerSocketProcessor, isAuthenticated);

        return isAuthenticated;
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
        ByteBuffer writeBuffer;
        AsyncServerClientState bufClientState;
        //send message to all connected clients except client with id
        Iterable<AsyncServerClientState> clientStates = this.tcpServer.getAllConnectionsExceptOne( clientId );
        for ( AsyncServerClientState clientState : clientStates ) {
            bufClientState = MessageWriter.createClientState( clientState.getChannel(), message );
            bufClientState.getChannel().write( bufClientState.getWriteBuffer(), bufClientState, new AsyncServerWriteHandler() );
        }
    }

    public void handleClient(AsyncServerClientState clientState){


        if ( authenticate( clientState.getChannel() ) ){
            tcpServer.addConnection( clientState );
            AsyncServerReadHandler readHandler = new AsyncServerReadHandler( true, this, "handleInputMessage" );
            clientState.getChannel().read( clientState.getReadSizeBuffer(), clientState, readHandler );
        }
    }

    public void handleInputMessage(String message, AsyncServerClientState clientState){
        storeMessage( message );
        sendMessage( message, clientState.getInstance() );
    }

    public void start(){
        tcpServer.start();
    }
}
