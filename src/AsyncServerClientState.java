import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerClientState {
    private static final AtomicInteger counter = new AtomicInteger();

    private final int instance;
    private final ByteBuffer readBuffer;
    private final ByteBuffer writeBuffer;

    private AsynchronousSocketChannel channel;

    private AsyncServerClientState(final int instance)
    {
        this.instance = instance;
        this.readBuffer = ByteBuffer.allocate(4);
        this.writeBuffer = ByteBuffer.allocate(4);
    }

    public static AsyncServerClientState newInstance()
    {
        return new AsyncServerClientState(counter.getAndIncrement());
    }

    public AsyncServerClientState initChannel(AsynchronousSocketChannel asc)
    {
        this.channel = asc;
        return this;
    }

    public AsynchronousSocketChannel getChannel()
    {
        return channel;
    }

    public ByteBuffer getReadBuffer()
    {
        return readBuffer;
    }

    public ByteBuffer getWriteBuffer()
    {
        return writeBuffer;
    }

    public int getInstance()
    {
        return instance;
    }

    public void writeInt (int number){
        writeBuffer.clear();
        writeBuffer.putInt(number);
        writeBuffer.flip();
    }
}
