package handlers;

import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class WriteHandler implements CompletionHandler<Integer, ChannelAndBuffersContainer> {

    @Override
    public void completed(Integer result, ChannelAndBuffersContainer channelAndBuffersContainer)
    {
        if ( channelAndBuffersContainer.getWriteBuffer().hasRemaining() ) {
            channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, this );
        }
    }

    @Override
    public void failed(Throwable exc, ChannelAndBuffersContainer chanelState)
    {
        System.out.printf("Error while writing to client #%02d!%n", chanelState.getInstance());
    }
}
