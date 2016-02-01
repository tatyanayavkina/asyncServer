package server;

import handlers.ChannelAndBuffersContainer;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


public class AsyncServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, ChannelAndBuffersContainer> {
    private final ServerProcessor serverProcessor;
    private final AsynchronousServerSocketChannel serverChannel;

    public AsyncServerAcceptHandler( AsynchronousServerSocketChannel serverChannel, ServerProcessor serverProcessor ) {
        this.serverProcessor = serverProcessor;
        this.serverChannel = serverChannel;
    }

    @Override
    public void completed( AsynchronousSocketChannel channel,  ChannelAndBuffersContainer channelAndBuffersContainer) {
        // accept next connection
        serverChannel.accept(new ChannelAndBuffersContainer(), this);
        // init channel
        channelAndBuffersContainer.initChannel(channel);
        // handle this connection
        serverProcessor.handleNewClient(channelAndBuffersContainer);
    }

    @Override
    public void failed( Throwable exc, ChannelAndBuffersContainer channelAndBuffersContainer) {
        System.out.println("Error while accepting client: " + exc.toString());
    }
}
