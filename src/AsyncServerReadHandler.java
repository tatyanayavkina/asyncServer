import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerReadHandler implements CompletionHandler<Integer, AsyncServerClientState> {
    private final AsyncServerWriteHandler writeHandler = new AsyncServerWriteHandler();

    public void completed(Integer result, AsyncServerClientState clientState){
        if (result == -1)
        {
            try
            {
                clientState.getChannel().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return;
        }

        ByteBuffer rb = clientState.getReadBuffer();
        if (rb.hasRemaining())
            clientState.getChannel().read(clientState.getReadBuffer(), clientState, this);

        rb.flip();

        int receivedNo = rb.getInt();
        System.out.println("received = " + receivedNo);
        rb.flip();

        ByteBuffer wb = clientState.getWriteBuffer();
        wb.clear();
        wb.putInt(receivedNo * 2);
        wb.flip();

        // write answer
        clientState.getChannel().write(wb, clientState, writeHandler);

        // read next
        rb.clear();
        clientState.getChannel().read(clientState.getReadBuffer(), clientState, this);
    }

    public void failed(Throwable ex, AsyncServerClientState clientState){
        System.out.printf("Error while reading from client #%02d!%n", clientState.getInstance());
    }
}
