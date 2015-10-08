package handlers;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created on 15.09.2015.
 */
public class ChannelAndBuffersContainer {
    private  int instance;
    private final ByteBuffer readSizeBuffer;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private boolean readyToWrite;
    private int lastSendMessageIndex;

    private AsynchronousSocketChannel channel;


    public ChannelAndBuffersContainer(){
        this.readSizeBuffer = ByteBuffer.allocate(4);
    }

    public void setInstance(int instance){
        this.instance = instance;
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

    public void setLastSendMessageIndex(int lastSendMessageIndex){
        this.lastSendMessageIndex = lastSendMessageIndex;
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

    public int getLastSendMessageIndex(){
        return lastSendMessageIndex;
    }

    public ByteBuffer getReadSizeBuffer(){
        return readSizeBuffer;
    }
}
