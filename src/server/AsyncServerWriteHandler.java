package server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerWriteHandler implements CompletionHandler<Integer, AsyncServerClientState> {

    @Override
    public void completed(Integer result, AsyncServerClientState clientState)
    {
        if ( clientState.getWriteBuffer().hasRemaining() ) {
            clientState.getChannel().write( clientState.getWriteBuffer(), clientState, this );
        }
    }

    @Override
    public void failed(Throwable exc, AsyncServerClientState chanelState)
    {
        System.out.printf("Error while writing to client #%02d!%n", chanelState.getInstance());
    }
}
