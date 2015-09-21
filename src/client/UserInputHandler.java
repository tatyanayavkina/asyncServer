package client;

import server.AsyncServerClientState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * Created on 18.09.2015.
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
        byte[] lnBytes;
        ByteBuffer writeBuffer;
        try{
            while( connected ){
                ln = reader.readLine();

                if ( ln.equals( CLOSE ) ){
                    connected = false;
                    continue;
                }
                lnBytes = ln.getBytes( StandardCharsets.UTF_8 );
                writeBuffer = ByteBuffer.allocate(lnBytes.length + 4);
                writeBuffer.putInt(lnBytes.length);
                writeBuffer.put(lnBytes);

                AsyncServerClientState clientState = new AsyncServerClientState( channel );
                clientState.setWriteBuffer(writeBuffer);
                clientState.getWriteBuffer().flip();

                clientState.getChannel().write( clientState.getWriteBuffer(), clientState, new AsyncClientWriteHandler() );
            }
        } catch (IOException e) {

        } finally {
            tcpClient.close();
        }

    }
}
