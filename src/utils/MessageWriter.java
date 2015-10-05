package utils;

import handlers.ChannelAndBuffersContainer;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * Created on 23.09.2015.
 */
public class MessageWriter {

    private static ByteBuffer createWriteBuffer( String message ){
        byte[] messageBytes = message.getBytes( StandardCharsets.UTF_8 );
        ByteBuffer writeBuffer = ByteBuffer.allocate(messageBytes.length + 4);
        writeBuffer.putInt(messageBytes.length);
        writeBuffer.put(messageBytes);
        writeBuffer.flip();

        return writeBuffer;
    }

    public static ChannelAndBuffersContainer createClientState ( AsynchronousSocketChannel channel, String message ){
        ByteBuffer writeBuffer = createWriteBuffer( message );
        ChannelAndBuffersContainer channelAndBuffersContainer = new ChannelAndBuffersContainer( channel );
        channelAndBuffersContainer.setWriteBuffer(writeBuffer);

        return channelAndBuffersContainer;
    }
}
