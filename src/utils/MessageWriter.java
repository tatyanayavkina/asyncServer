package utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;

public class MessageWriter {

    public static ByteBuffer createWriteBuffer (SortedMap<Integer,Message> messagesMap){
        String messagesListString = JsonConverter.toJson( messagesMap );
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
