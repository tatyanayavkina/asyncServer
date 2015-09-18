import server.AsyncTcpServer;

import java.io.IOException;

/**
 * Created on 15.09.2015.
 */
public class StartServer {

    public static void main(String[] args) {
        try {
            AsyncTcpServer asyncServer = new AsyncTcpServer(9999, 10);
            asyncServer.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
