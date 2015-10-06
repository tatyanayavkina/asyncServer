package client;

import handlers.WriteHandler;
import handlers.ChannelAndBuffersContainer;
import utils.JsonConverter;
import utils.Message;
import utils.MessageWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created on 18.09.2015.
 */
public class UserInputHandler implements Runnable {
    private ClientProcessor clientProcessor;
    private ChannelAndBuffersContainer channelAndBuffersContainer;
    private boolean connected;
    private BufferedReader reader;

    private String author;
    private String IP;

    private final String CLOSE = "@close";

    public UserInputHandler(ClientProcessor clientProcessor, ChannelAndBuffersContainer channelAndBuffersContainer){
        this.clientProcessor = clientProcessor;
        this.channelAndBuffersContainer = channelAndBuffersContainer;
        this.reader = new BufferedReader( new InputStreamReader( System.in ) );
        this.connected = true;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setIP(String IP){
        this.IP = IP;
    }

    public void run(){
        String ln;
        try{
            while( connected ){
                ln = reader.readLine();

                if ( ln.equals( CLOSE ) ){
                    connected = false;
                    continue;
                }
                Message message = new Message(author, IP, ln);
                ArrayList<Message> messageList = new ArrayList<Message>();
                messageList.add( message );
                ByteBuffer writeBuffer = MessageWriter.createWriteBuffer( messageList );
                channelAndBuffersContainer.setWriteBuffer( writeBuffer );
                channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, new WriteHandler() );
            }
        } catch (IOException e) {

        } finally {
            clientProcessor.stop();
        }

    }
}
