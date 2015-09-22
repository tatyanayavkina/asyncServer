package server;

import utils.Config;

import java.io.IOException;
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

    public void storeMessage( String message ){
        synchronized ( messageList ){
            if ( messageList.size() == messageStoreLimit ){
                messageList.remove(0);
            }

            messageList.add( message );
        }
    }

    public void handleInputMessage(AsyncServerClientState clientState){
        AsyncServerReadHandler readHandler = new AsyncServerReadHandler();
        clientState.getChannel().read(clientState.getReadSizeBuffer(), clientState, readHandler);
    }

    public void start(){
        tcpServer.start();
    }
}
