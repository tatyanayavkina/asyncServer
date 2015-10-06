package handlers;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 15.09.2015.
 */
public class ChannelAndBuffersContainer {
    private static final AtomicInteger counter = new AtomicInteger();

    private final int instance;
    private final ByteBuffer readSizeBuffer;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private boolean readyToWrite;

    private AsynchronousSocketChannel channel;

    private ChannelAndBuffersContainer(final int instance)
    {
        this.instance = instance;
        this.readSizeBuffer = ByteBuffer.allocate(4);
    }

    public ChannelAndBuffersContainer(AsynchronousSocketChannel asc){
        this.instance = -1;
        this.channel = asc;
        this.readSizeBuffer = ByteBuffer.allocate(4);
    }

    public static ChannelAndBuffersContainer newInstance()
    {
        return new ChannelAndBuffersContainer(counter.getAndIncrement());
    }

    public ChannelAndBuffersContainer initChannel(AsynchronousSocketChannel asc)
    {
        this.channel = asc;
        return this;
    }

    public void setReadBuffer(ByteBuffer readBuffer){
        this.readBuffer = readBuffer;
    }


    public void setWriteBuffer(ByteBuffer writeBuffer){
        this.writeBuffer = writeBuffer;
    }

    public void setReadyToWrite(boolean readyToWrite){
        this.readyToWrite = readyToWrite;
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

    public boolean getReadyToWrite(){
        return readyToWrite;
    }

    public ByteBuffer getReadSizeBuffer(){
        return readSizeBuffer;
    }
}
