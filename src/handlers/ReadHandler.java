package handlers;

import utils.ChatProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * Created on 15.09.2015.
 */
public class ReadHandler implements CompletionHandler<Integer, ChannelAndBuffersContainer> {
    private final ChatProcessor processor;
    private boolean isAuthorized;

    public ReadHandler(boolean isAuthorized, ChatProcessor processor){
        this.isAuthorized = isAuthorized;
        this.processor = processor;
    }

    public void completed(Integer result, ChannelAndBuffersContainer channelAndBuffersContainer){
        if (result == -1)
        {
            try {
                channelAndBuffersContainer.getChannel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        ByteBuffer readSizeBuffer = channelAndBuffersContainer.getReadSizeBuffer();

        if (channelAndBuffersContainer.getReadBuffer() == null) {
            if (readSizeBuffer.hasRemaining())
                channelAndBuffersContainer.getChannel().read( readSizeBuffer, channelAndBuffersContainer, this );

            readSizeBuffer.flip();
            int size = readSizeBuffer.getInt();
            ByteBuffer rBuffer = ByteBuffer.allocate(size);
            channelAndBuffersContainer.setReadBuffer(rBuffer);
            channelAndBuffersContainer.getChannel().read( rBuffer, channelAndBuffersContainer, this );
        } else {
            ByteBuffer readBuffer = channelAndBuffersContainer.getReadBuffer();
            if (readBuffer.hasRemaining())
                channelAndBuffersContainer.getChannel().read( readBuffer, channelAndBuffersContainer, this );

            readBuffer.flip();
            byte[] readBytes = readBuffer.array();
            String message = new String( readBytes, StandardCharsets.UTF_8 );

            readSizeBuffer.clear();
            channelAndBuffersContainer.setReadBuffer(null);

            if (isAuthorized){
                processor.handleInputMessage( message, channelAndBuffersContainer);
                channelAndBuffersContainer.getChannel().read(channelAndBuffersContainer.getReadSizeBuffer(), channelAndBuffersContainer, this);
            } else {
                processor.handleAuthorization( message, channelAndBuffersContainer);
            }
        }
    }

    public void failed(Throwable ex, ChannelAndBuffersContainer channelAndBuffersContainer){
        System.out.printf("Error while reading from client #%02d!%n", channelAndBuffersContainer.getId());
        processor.close(channelAndBuffersContainer);
    }
}
