package handlers;

import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class WriteHandler implements CompletionHandler<Integer, ClientState> {

    @Override
    public void completed(Integer result, ClientState clientState)
    {
        if ( clientState.getWriteBuffer().hasRemaining() ) {
            clientState.getChannel().write( clientState.getWriteBuffer(), clientState, this );
        }
    }

    @Override
    public void failed(Throwable exc, ClientState chanelState)
    {
        System.out.printf("Error while writing to client #%02d!%n", chanelState.getInstance());
    }
}
