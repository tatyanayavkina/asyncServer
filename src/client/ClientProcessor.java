package client;

import handlers.ReadHandler;
import handlers.WriteHandler;
import handlers.ChannelAndBuffersContainer;
import utils.*;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

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

    public void handleConnection(ChannelAndBuffersContainer channelAndBuffersContainer){
        sendAuthorizationMessage(channelAndBuffersContainer);
        ReadHandler readHandler = new ReadHandler( false, this );
        channelAndBuffersContainer.getChannel().read(channelAndBuffersContainer.getReadSizeBuffer(), channelAndBuffersContainer, readHandler);
    }


    private void sendAuthorizationMessage(ChannelAndBuffersContainer channelAndBuffersContainer){
        UserCredentials credentials = new UserCredentials( username, password );
        String jsonCredentials = JsonConverter.toJson(credentials);
        ByteBuffer writeBuffer = MessageWriter.createWriteBuffer(jsonCredentials);
        channelAndBuffersContainer.setWriteBuffer( writeBuffer );
        channelAndBuffersContainer.getChannel().write(channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, new WriteHandler());
    }

    public void handleAuthorization(String message, ChannelAndBuffersContainer channelAndBuffersContainer){

        UtilityMessage utilityMessage = (UtilityMessage) JsonConverter.fromJson( message, UtilityMessage.class );
        UtilityMessage.StatusCodes statusCode = utilityMessage.getCode();
        System.out.println(statusCode.getDescription());

        if ( statusCode == UtilityMessage.StatusCodes.AUTHORIZED){
            UserInputHandler inputHandler = new UserInputHandler(this, channelAndBuffersContainer);
            inputHandler.setAuthor(username);
            inputHandler.setIP(IP);
            new Thread(inputHandler).start();
            ReadHandler readHandler = new ReadHandler( true, this );
            channelAndBuffersContainer.getChannel().read( channelAndBuffersContainer.getReadSizeBuffer(), channelAndBuffersContainer, readHandler );
        } else {
            stop();
        }

    }

    public void handleInputMessage(String messageListString, ChannelAndBuffersContainer channelAndBuffersContainer){
        SortedMap<Integer,Message> messageMap = JsonConverter.fromJsonToMap(messageListString);
        for( Map.Entry<Integer,Message> entry : messageMap.entrySet() ){
            // if we are not message author then print it to console
            Message message = entry.getValue();
            if( !username.equals( message.getAuthor())){
                System.out.println("message" + message.toOutStr());
            }
        }
    }

    public void close(ChannelAndBuffersContainer channelAndBuffersContainer){
        tcpClient.close();
    }

}
