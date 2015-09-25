package client;

import handlers.WriteHandler;
import server.AsyncServerClientState;
import utils.MessageWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.AsynchronousSocketChannel;


/**
 * Created on 18.09.2015.
 */
public class UserInputHandler implements Runnable {
    private ClientProcessor clientProcessor;
    private AsynchronousSocketChannel channel;
    private boolean connected;
    private BufferedReader reader;

    private final String CLOSE = "@close";

    public UserInputHandler(ClientProcessor clientProcessor, AsynchronousSocketChannel channel){
        this.clientProcessor = clientProcessor;
        this.channel = channel;
        this.reader = new BufferedReader( new InputStreamReader( System.in ) );
        this.connected = true;
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

                AsyncServerClientState clientState = MessageWriter.createClientState( channel, ln);
                clientState.getChannel().write( clientState.getWriteBuffer(), clientState, new WriteHandler() );
            }
        } catch (IOException e) {

        } finally {
            clientProcessor.stop();
        }

    }
}
