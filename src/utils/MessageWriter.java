package utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created on 23.09.2015.
 */
public class MessageWriter {

    public static ByteBuffer createWriteBuffer(ArrayList<Message> messagesList){
        String messagesListString = JsonConverter.toJson( messagesList );
        return createWriteBuffer( messagesListString );
    }

    public static ByteBuffer createWriteBuffer(String message){
        byte[] messageBytes = message.getBytes( StandardCharsets.UTF_8 );
        ByteBuffer writeBuffer = ByteBuffer.allocate(messageBytes.length + 4);
        writeBuffer.putInt(messageBytes.length);
        writeBuffer.put(messageBytes);
        writeBuffer.flip();

        return writeBuffer;
    }
}
