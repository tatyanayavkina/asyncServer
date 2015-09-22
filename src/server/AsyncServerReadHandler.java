package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerReadHandler implements CompletionHandler<Integer, AsyncServerClientState> {
    private final AsyncServerWriteHandler writeHandler = new AsyncServerWriteHandler();
    private final ServerProcessor serverProcessor;

    public AsyncServerReadHandler(ServerProcessor serverProcessor){
        this.serverProcessor = serverProcessor;
    }

    public void completed(Integer result, AsyncServerClientState clientState){
        if (result == -1)
        {
            try {
                clientState.getChannel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

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

            String message = new String( readBytes, StandardCharsets.UTF_8 );
            serverProcessor.handleInputMessage(message, clientState);
            System.out.println(message);

            readSizeBuffer.clear();
            clientState.deleteReadBuffer();
            clientState.getChannel().read( clientState.getReadSizeBuffer(), clientState, this );
        }
    }

    public void failed(Throwable ex, AsyncServerClientState clientState){
        System.out.printf("Error while reading from client #%02d!%n", clientState.getInstance());
    }
}
