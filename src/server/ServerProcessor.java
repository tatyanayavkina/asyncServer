package server;

import utils.Config;

import java.io.IOException;
import java.nio.ByteBuffer;
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

    private void storeMessage( String message ){
        synchronized ( messageList ){
            if ( messageList.size() == messageStoreLimit ){
                messageList.remove(0);
            }

            messageList.add( message );
        }
    }

    private ByteBuffer prepareMessage(String message){
        byte[] messageBytes = message.getBytes( StandardCharsets.UTF_8 );
        ByteBuffer writeBuffer = ByteBuffer.allocate(messageBytes.length + 4);
        writeBuffer.putInt(messageBytes.length);
        writeBuffer.put(messageBytes);

        return writeBuffer;
    }

    private void sendMessage(String message, int clientId){
        //send message to all connected clients except client with id
        Iterable<AsyncServerClientState> clientStates = this.tcpServer.getAllConnectionsExceptOne( clientId );
        for ( AsyncServerClientState clientState : clientStates ) {
            ByteBuffer writeBuffer = prepareMessage( message );

        }
    }

    public void handleClient(AsyncServerClientState clientState){
        tcpServer.addConnection( clientState );
        AsyncServerReadHandler readHandler = new AsyncServerReadHandler( this );
        clientState.getChannel().read( clientState.getReadSizeBuffer(), clientState, readHandler );
    }

    public void handleInputMessage(String message, AsyncServerClientState clientState){
        storeMessage( message );
        sendMessage( message, clientState.getInstance() );
    }

    public void start(){
        tcpServer.start();
    }
}
