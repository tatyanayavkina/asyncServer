package client;

import server.AsyncServerClientState;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * Created on 17.09.2015.
 */
public class AsyncClientReadHandler implements CompletionHandler<Integer, AsyncServerClientState>{

    @Override
    public void completed(Integer result, AsyncServerClientState clientState) {
        if(result != -1){
            ByteBuffer readSizeBuffer = clientState.getReadSizeBuffer();

            if (clientState.getReadBuffer() == null) {
                if (readSizeBuffer.hasRemaining())
                    clientState.getChannel().read( clientState.getReadSizeBuffer(), clientState, this );

                readSizeBuffer.flip();
                int size = readSizeBuffer.getInt();
                System.out.println("Received " + size + " bytes");

                ByteBuffer rBuffer = ByteBuffer.allocate(size);
                clientState.setReadBuffer(rBuffer);

                clientState.getChannel().read( clientState.getReadBuffer(), clientState, this );
            } else {
                ByteBuffer readBuffer = clientState.getReadBuffer();
                if (readBuffer.hasRemaining())
                    clientState.getChannel().read( clientState.getReadBuffer(), clientState, this );

                readBuffer.flip();
                byte[] readBytes = readBuffer.array();

                String str = new String( readBytes, StandardCharsets.UTF_8 );
                System.out.println(str);

                readSizeBuffer.clear();
                clientState.deleteReadBuffer();
                clientState.getChannel().read( clientState.getReadSizeBuffer(), clientState, this );
            }

        }
    }

    @Override
    public void failed(Throwable exc, AsyncServerClientState clientState) {

    }
}
