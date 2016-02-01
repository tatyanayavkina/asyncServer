package handlers;

import java.nio.channels.CompletionHandler;

public class WriteHandler implements CompletionHandler<Integer, ChannelAndBuffersContainer> {

    @Override
    public void completed(Integer result, ChannelAndBuffersContainer channelAndBuffersContainer)
    {
        if ( channelAndBuffersContainer.getWriteBuffer().hasRemaining() ) {
            channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, this );
        }
        channelAndBuffersContainer.setWriteBuffer(null);
    }

    @Override
    public void failed(Throwable exc, ChannelAndBuffersContainer chanelState)
    {
        System.out.printf("Error while writing to client #%02d!%n", chanelState.getId());
    }
}
