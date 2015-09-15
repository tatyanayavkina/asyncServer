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
    private final AsyncClientConnectionHandler connectionHandler;

    public AsyncTcpClient(String host, int port) throws IOException{
        this.host = host;
        this.port = port;
        this.group = AsynchronousChannelGroup.withFixedThreadPool(3, Executors.defaultThreadFactory());
        this.channel = AsynchronousSocketChannel.open(this.group);
        this.connectionHandler = new AsyncClientConnectionHandler();
    }

    private void connect(){
        AsyncServerClientState clientState = AsyncServerClientState.newInstance();
        clientState.initChannel(channel);
        channel.connect(new InetSocketAddress(host, port), clientState, new AsyncClientConnectionHandler());
    }
}
