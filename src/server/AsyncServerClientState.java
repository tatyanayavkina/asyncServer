package server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerClientState {
    private static final AtomicInteger counter = new AtomicInteger();

    private final int instance;
    private final ByteBuffer readSizeBuffer;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;

    private AsynchronousSocketChannel channel;

    private AsyncServerClientState(final int instance)
    {
        this.instance = instance;
        this.readSizeBuffer = ByteBuffer.allocate(4);
    }

    public AsyncServerClientState(AsynchronousSocketChannel asc){
        this.instance = -1;
        this.channel = asc;
        this.readSizeBuffer = ByteBuffer.allocate(4);
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

    public void setReadBuffer(ByteBuffer readBuffer){
        this.readBuffer = readBuffer;
    }

    public void deleteReadBuffer(){
        this.readBuffer = null;
    }

    public void setWriteBuffer(ByteBuffer writeBuffer){
        this.writeBuffer = writeBuffer;
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

    public ByteBuffer getReadSizeBuffer(){
        return readSizeBuffer;
    }
}
