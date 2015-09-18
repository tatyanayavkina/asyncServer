package client;

import server.AsyncServerClientState;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * Created on 17.09.2015.
 */
public class AsyncClientReadHandler implements CompletionHandler<Integer, AsyncServerClientState>{

    @Override
    public void completed(Integer result, AsyncServerClientState clientState) {
        if(result != -1){
            ByteBuffer bb = clientState.getReadBuffer();

            if (bb.hasRemaining())
                clientState.getChannel().read(clientState.getReadBuffer(), clientState, this);

            bb.flip();
            int received = bb.getInt();
            System.out.println("Received " + received);
            bb.flip();

            bb.clear();
            clientState.getChannel().read(clientState.getReadBuffer(), clientState, this);
        }
    }

    @Override
    public void failed(Throwable exc, AsyncServerClientState clientState) {

    }
}
