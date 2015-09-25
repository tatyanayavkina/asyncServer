package utils;

import handlers.ClientState;

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

        return writeBuffer;
    }

    public static ClientState createClientState ( AsynchronousSocketChannel channel, String message ){
        ByteBuffer writeBuffer = createWriteBuffer( message );
        ClientState clientState = new ClientState( channel );
        clientState.setWriteBuffer( writeBuffer );
        clientState.getWriteBuffer().flip();

        return clientState;
    }
}
