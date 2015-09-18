package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.Executors;

/**
 * Created on 15.09.2015.
 */
public class AsyncTcpServer {
    private static String HOST = "localhost";
    private final int port;
    private final AsynchronousChannelGroup group;
    private final AsynchronousServerSocketChannel serverChannel;

    public AsyncTcpServer( int port, int threadCount ) throws IOException {
        this.port = port;
        this.group = AsynchronousChannelGroup.withFixedThreadPool( threadCount, Executors.defaultThreadFactory() );
        this.serverChannel = AsynchronousServerSocketChannel.open( this.group );
    }

    private void bindAddress() throws IOException{
        InetSocketAddress hostAddress = new InetSocketAddress( HOST, port );
        serverChannel.bind(hostAddress);
    }

    private void accept(){
        serverChannel.accept(AsyncServerClientState.newInstance(), new AsyncServerAcceptHandler(serverChannel));
    }

    public void start() {
        try{
            bindAddress();
            accept();
        } catch ( IOException ex ){
            System.out.println("IOException");
            stop();
        }
    }
    public void stop(){
        group.shutdown();
//        group.awaitTermination(5, TimeUnit.SECONDS);
    }
}
