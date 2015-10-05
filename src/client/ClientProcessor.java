package client;

import handlers.ReadHandler;
import handlers.WriteHandler;
import handlers.ChannelAndBuffersContainer;
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
        ChannelAndBuffersContainer channelAndBuffersContainer = new ChannelAndBuffersContainer( channel );
        ReadHandler readHandler = new ReadHandler( false, this );
        channelAndBuffersContainer.getChannel().read(channelAndBuffersContainer.getReadSizeBuffer(), channelAndBuffersContainer, readHandler);
    }


    private void sendAuthorizationMessage(AsynchronousSocketChannel channel){
        UserCredentials credentials = new UserCredentials( username, password );
        String jsonCredentials = JsonConverter.toJson( credentials );
        ChannelAndBuffersContainer channelAndBuffersContainer = MessageWriter.createClientState( channel, jsonCredentials );
        channelAndBuffersContainer.getChannel().write(channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, new WriteHandler());
    }

    public void handleAuthorization(String message, ChannelAndBuffersContainer channelAndBuffersContainer){

        UtilityMessage utilityMessage = (UtilityMessage) JsonConverter.fromJson( message, UtilityMessage.class );
        UtilityMessage.StatusCodes statusCode = utilityMessage.getCode();
        System.out.println(statusCode.getDescription());

        if ( statusCode == UtilityMessage.StatusCodes.AUTHORIZED){
            UserInputHandler inputHandler = new UserInputHandler(this, channelAndBuffersContainer.getChannel());
            inputHandler.setAuthor(username);
            inputHandler.setIP(IP);
            new Thread(inputHandler).start();
            ReadHandler readHandler = new ReadHandler( true, this );
            channelAndBuffersContainer.getChannel().read( channelAndBuffersContainer.getReadSizeBuffer(), channelAndBuffersContainer, readHandler );
        } else {
            stop();
        }

    }

    public void handleInputMessage(String messageString, ChannelAndBuffersContainer channelAndBuffersContainer){
        Message message = (Message) JsonConverter.fromJson(messageString, Message.class);
        System.out.println("message" + message.toOutStr());
    }

}
