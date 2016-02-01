package handlers;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class ChannelAndBuffersContainer {
    private  int id;
    private final ByteBuffer readSizeBuffer;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private boolean readyToWrite;
    private int lastSendMessageIndex;

    private AsynchronousSocketChannel channel;


    public ChannelAndBuffersContainer(){
        this.readSizeBuffer = ByteBuffer.allocate(4);
    }

    public void setId(int id){
        this.id = id;
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

    public int getId()
    {
        return id;
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
