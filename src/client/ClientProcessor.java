package client;

import handlers.WriteHandler;
import server.AsyncServerClientState;
import utils.ChatProcessor;
import utils.JsonConverter;
import utils.MessageWriter;
import utils.UserCredentials;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created on 23.09.2015.
 */
public class ClientProcessor implements ChatProcessor{
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

    public void handleConnection(AsynchronousSocketChannel channel){
        if ( authorize( channel ) ){
//            handleUserInput();
//            handleServerInput();
        }
    }

    private boolean authorize(AsynchronousSocketChannel channel){
        sendAuthorizationMessage( channel );
        return readAuthorizationMessage( );
    }

    private void sendAuthorizationMessage(AsynchronousSocketChannel channel){
        UserCredentials credentials = new UserCredentials( username, password );
        String jsonCredentials = JsonConverter.toJson( credentials );
        AsyncServerClientState clientState = MessageWriter.createClientState(channel, jsonCredentials);
        clientState.getChannel().write( clientState.getWriteBuffer(), clientState, new WriteHandler() );
    }

    private boolean readAuthorizationMessage(){
        return true;
    }

}
