package client;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created on 23.09.2015.
 */
public class ClientProcessor {
    private String username;
    private String password;
    private String IP;

    private AsyncTcpClient tcpClient;

    public ClientProcessor(String host, int port, String username, String password) throws IOException {
        this.username = username;
        this.password = password;
        this.IP = InetAddress.getLocalHost().toString();
        this.tcpClient = new AsyncTcpClient(host, port, this);
    }

    public void start(){
        tcpClient.connect();
    }

    public void stop(){
        tcpClient.close();
    }

    public void handleConnection( AsynchronousSocketChannel channel){
        if ( authorize() ){
//            handleUserInput();
//            handleServerInput();
        }
    }

    private boolean authorize(){
        sendAuthorizationMessage( );
        return readAuthorizationMessage( );
    }

    private void sendAuthorizationMessage(){

    }

    private boolean readAuthorizationMessage(){
        return true;
    }

}
