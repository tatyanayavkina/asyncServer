package server;

import handlers.ClientState;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, ClientState> {
    private final ServerProcessor serverProcessor;
    private final AsynchronousServerSocketChannel serverChannel;

    public AsyncServerAcceptHandler( AsynchronousServerSocketChannel serverChannel, ServerProcessor serverProcessor ) {
        this.serverProcessor = serverProcessor;
        this.serverChannel = serverChannel;
    }

    @Override
    public void completed( AsynchronousSocketChannel channel,  ClientState clientState ) {
        // accept next connection
        serverChannel.accept(ClientState.newInstance(), this);
        // init channel
        clientState.initChannel(channel);
        // handle this connection
        serverProcessor.handleNewClient(clientState);
    }

    @Override
    public void failed( Throwable exc, ClientState clientState ) {
        System.out.println("Error while accepting client: " + exc.toString());
    }
}
