package client;

import handlers.ClientState;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Executors;

/**
 * Created on 15.09.2015.
 */
public class AsyncTcpClient {
    private final int port;
    private final String host;
    private final AsynchronousChannelGroup group;
    private final AsynchronousSocketChannel channel;
    private final ClientProcessor clientProcessor;

    public AsyncTcpClient(String host, int port, ClientProcessor clientProcessor) throws IOException{
        this.host = host;
        this.port = port;
        this.group = AsynchronousChannelGroup.withFixedThreadPool(2, Executors.defaultThreadFactory());
        this.channel = AsynchronousSocketChannel.open(this.group);
        this.clientProcessor = clientProcessor;
    }

    public void connect(){
        ClientState clientState = new ClientState( channel );
        channel.connect(new InetSocketAddress(host, port), clientState, new AsyncClientConnectionHandler(this.clientProcessor) );
    }

    public void close(){
        try{
            channel.close();
        } catch (IOException e){
            System.out.println("Connection closing error!");
        }

    }
}
