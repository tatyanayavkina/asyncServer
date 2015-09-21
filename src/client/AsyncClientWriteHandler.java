package client;

import server.AsyncServerClientState;

import java.nio.channels.CompletionHandler;

/**
 * Created on 17.09.2015.
 */
public class AsyncClientWriteHandler implements CompletionHandler<Integer, AsyncServerClientState> {

    @Override
    public void completed(Integer result, AsyncServerClientState clientState) {
        if ( clientState.getWriteBuffer().hasRemaining() ) {
            clientState.getChannel().write( clientState.getWriteBuffer(), clientState, this );
        }
    }

    @Override
    public void failed(Throwable exc, AsyncServerClientState attachment) {

    }
}
