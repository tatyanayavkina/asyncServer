package handlers;

import server.ServerProcessor;

import java.nio.channels.CompletionHandler;


/**
 * Created on 06.10.2015.
 */
public class ServerMessageWriteHandler implements CompletionHandler<Integer, ChannelAndBuffersContainer> {
    private ServerProcessor processor;

    public ServerMessageWriteHandler(ServerProcessor processor){
        this.processor = processor;
    }

    @Override
    public void completed(Integer result, ChannelAndBuffersContainer channelAndBuffersContainer)
    {
        if ( channelAndBuffersContainer.getWriteBuffer().hasRemaining() ) {
            channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, this );
        }
        channelAndBuffersContainer.setWriteBuffer(null);

        synchronized ( channelAndBuffersContainer ){
            processor.sendMessageList( channelAndBuffersContainer, this );
        }
    }

    @Override
    public void failed(Throwable exc, ChannelAndBuffersContainer chanelState)
    {
        System.out.printf("Error while writing to client #%02d!%n", chanelState.getId());
    }
}
