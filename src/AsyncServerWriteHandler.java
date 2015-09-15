import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerWriteHandler implements CompletionHandler<Integer, AsyncServerClientState> {

    @Override
    public void completed(Integer result, AsyncServerClientState chanelState)
    {
        ByteBuffer wb = chanelState.getWriteBuffer();

        AsynchronousSocketChannel channel = chanelState.getChannel();
        if (wb.remaining() > 0)
        {
            channel.write(wb, chanelState, this);
        }
        else
        {
            wb.flip();
        }
    }

    @Override
    public void failed(Throwable exc, AsyncServerClientState chanelState)
    {
        System.out.printf("Error while writing to client #%02d!%n", chanelState.getInstance());
    }
}
