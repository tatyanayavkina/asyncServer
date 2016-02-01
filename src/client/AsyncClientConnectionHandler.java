package client;

import handlers.ChannelAndBuffersContainer;

import java.nio.channels.CompletionHandler;

public class AsyncClientConnectionHandler implements CompletionHandler<Void, ChannelAndBuffersContainer>{
    private final ClientProcessor clientProcessor;

    public AsyncClientConnectionHandler(ClientProcessor clientProcessor){
        this.clientProcessor = clientProcessor;
    }


    @Override
    public void completed(Void result, ChannelAndBuffersContainer channelAndBuffersContainer) {
        //handle connection
        clientProcessor.handleConnection( channelAndBuffersContainer );
    }

    @Override
    public void failed(Throwable exc, ChannelAndBuffersContainer channelAndBuffersContainer) {
        System.out.println("Connect fault!");
        exc.printStackTrace();
    }
}
