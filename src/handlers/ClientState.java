package handlers;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 15.09.2015.
 */
public class ClientState {
    private static final AtomicInteger counter = new AtomicInteger();

    private final int instance;
    private final ByteBuffer readSizeBuffer;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;

    private AsynchronousSocketChannel channel;

    private ClientState(final int instance)
    {
        this.instance = instance;
        this.readSizeBuffer = ByteBuffer.allocate(4);
    }

    public ClientState(AsynchronousSocketChannel asc){
        this.instance = -1;
        this.channel = asc;
        this.readSizeBuffer = ByteBuffer.allocate(4);
    }

    public static ClientState newInstance()
    {
        return new ClientState(counter.getAndIncrement());
    }

    public ClientState initChannel(AsynchronousSocketChannel asc)
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
