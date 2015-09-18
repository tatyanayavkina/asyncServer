package client;

import server.AsyncServerClientState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by Татьяна on 18.09.2015.
 */
public class UserInputHandler implements Runnable {
    private AsyncTcpClient tcpClient;
    private AsynchronousSocketChannel channel;
    private boolean connected;
    private BufferedReader reader;

    private final String CLOSE = "@close";

    public UserInputHandler(AsyncTcpClient tcpClient, AsynchronousSocketChannel channel){
        this.tcpClient = tcpClient;
        this.channel = channel;
        this.reader = new BufferedReader( new InputStreamReader( System.in ) );
        this.connected = true;
    }

    public void run(){
        String ln;
        ByteBuffer readBuffer = ByteBuffer.allocate(1);
        try{
            while( connected ){
                ln = reader.readLine();

                if ( ln.equals( CLOSE ) ){
                    connected = false;
                    continue;
                }
                ByteBuffer writeBuffer = ByteBuffer.wrap( ln.getBytes( "UTF-8" ) );
                AsyncServerClientState clientState = new AsyncServerClientState( channel, readBuffer, writeBuffer );
                clientState.getWriteBuffer().flip();
                clientState.getChannel().write( clientState.getWriteBuffer(), clientState, new AsyncClientWriteHandler() );

            }
        } catch (IOException e) {

        } finally {
            tcpClient.close();
        }

    }
}
