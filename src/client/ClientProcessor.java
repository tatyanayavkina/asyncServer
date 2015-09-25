package client;

import handlers.ReadHandler;
import handlers.WriteHandler;
import handlers.ClientState;
import utils.*;

import java.io.IOException;
import java.net.InetAddress;
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
        sendAuthorizationMessage(channel);
        ClientState clientState = new ClientState( channel );
        ReadHandler readHandler = new ReadHandler( false, this, "handleAuthorizationMessage");
        clientState.getChannel().read( clientState.getReadSizeBuffer(), clientState, readHandler );
    }


    private void sendAuthorizationMessage(AsynchronousSocketChannel channel){
        UserCredentials credentials = new UserCredentials( username, password );
        String jsonCredentials = JsonConverter.toJson( credentials );
        ClientState clientState = MessageWriter.createClientState( channel, jsonCredentials );
        clientState.getChannel().write( clientState.getWriteBuffer(), clientState, new WriteHandler() );
    }

    public void handleAuthorizationMessage(String message, ClientState clientState){
        boolean isAuthorized = false;
        UtilityMessage utilityMessage = (UtilityMessage) JsonConverter.fromJson( message, UtilityMessage.class );
        UtilityMessage.StatusCodes statusCode = utilityMessage.getCode();
        System.out.println ( statusCode.getDescription() );

        if ( statusCode == UtilityMessage.StatusCodes.AUTHORIZED){
            isAuthorized = true;
        }

    }

}
