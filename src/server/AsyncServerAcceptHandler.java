package server;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncServerClientState> {
    private final ServerProcessor serverProcessor;
    private final AsynchronousServerSocketChannel serverChannel;
    private final AsyncServerReadHandler readHandler ;

    public AsyncServerAcceptHandler( AsynchronousServerSocketChannel serverChannel, ServerProcessor serverProcessor ) {
        this.serverProcessor = serverProcessor;
        this.serverChannel = serverChannel;
        this.readHandler = new AsyncServerReadHandler();
    }

    @Override
    public void completed( AsynchronousSocketChannel channel,  AsyncServerClientState clientState ) {
        // accept next connection
        serverChannel.accept(AsyncServerClientState.newInstance(), this);
        // init channel
        clientState.initChannel(channel);
        // handle this connection
        serverProcessor.handleInputMessage( clientState );
    }

    @Override
    public void failed( Throwable exc, AsyncServerClientState clientState ) {
        System.out.println("Error while accepting client: " + exc.toString());
    }
}
