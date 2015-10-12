package server;

import handlers.ChannelAndBuffersContainer;
import utils.ConnectionAutoIncrementMap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.Executors;

/**
 * Created on 15.09.2015.
 */
public class AsyncTcpServer {
    private ServerProcessor serverProcessor;
    private final String host;
    private final int port;
    private final AsynchronousChannelGroup group;
    private final AsynchronousServerSocketChannel serverChannel;
    private final ConnectionAutoIncrementMap connectionsMap;

    public AsyncTcpServer( ServerProcessor serverProcessor, String host, int port, int threadCount ) throws IOException {
        this.serverProcessor = serverProcessor;
        this.host = host;
        this.port = port;
        this.group = AsynchronousChannelGroup.withFixedThreadPool( threadCount, Executors.defaultThreadFactory() );
        this.serverChannel = AsynchronousServerSocketChannel.open(this.group);
        this.connectionsMap = new ConnectionAutoIncrementMap();
    }

    private void bindAddress() throws IOException{
        InetSocketAddress hostAddress = new InetSocketAddress( host, port );
        serverChannel.bind(hostAddress);
    }

    private void accept(){
        serverChannel.accept(new ChannelAndBuffersContainer(), new AsyncServerAcceptHandler(serverChannel, serverProcessor));
    }

    public Iterable<ChannelAndBuffersContainer> getAllConnections(){
        return connectionsMap.getAll();
    }

    public void addConnection (ChannelAndBuffersContainer channelAndBuffersContainer){
        synchronized (connectionsMap){
            int instance = connectionsMap.getNextId();
            channelAndBuffersContainer.setId(instance);
            channelAndBuffersContainer.setReadyToWrite( true );
            channelAndBuffersContainer.setLastSendMessageIndex( 0 );
            connectionsMap.pushConnection( instance, channelAndBuffersContainer );
        }
    }

    public void removeConnection (ChannelAndBuffersContainer channelAndBuffersContainer){
        synchronized (connectionsMap){
            connectionsMap.remove(channelAndBuffersContainer.getId());
        }
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
