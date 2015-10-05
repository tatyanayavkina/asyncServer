package handlers;

import utils.ChatProcessor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Method;

/**
 * Created on 15.09.2015.
 */
public class ReadHandler implements CompletionHandler<Integer, ChannelAndBuffersContainer> {
    private final ChatProcessor processor;
    private String callback;
    private boolean isMessageExchange;

    public ReadHandler(boolean isMessageExchange, ChatProcessor processor, String callback){
        this.isMessageExchange = isMessageExchange;
        this.processor = processor;
        this.callback = callback;
    }

    private Method prepareCallback(){
        Method method = null;
        try {
            method = processor.getClass().getMethod(callback, String.class, ChannelAndBuffersContainer.class);
        } catch (SecurityException e) {
            // ...
        } catch (NoSuchMethodException e) {
            // ...
        }
        return  method;
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

            Method callbackMethod;
            if ( callback != null && ( callbackMethod = prepareCallback() ) != null){
                try {
                    callbackMethod.invoke(processor, message, channelAndBuffersContainer);
                } catch (IllegalArgumentException e) {

                } catch (IllegalAccessException e) {

                } catch (InvocationTargetException e) {

                }
            }

            if ( isMessageExchange ){
                channelAndBuffersContainer.getChannel().read(channelAndBuffersContainer.getReadSizeBuffer(), channelAndBuffersContainer, this);
            }
        }
    }

    public void failed(Throwable ex, ChannelAndBuffersContainer channelAndBuffersContainer){
        System.out.printf("Error while reading from client #%02d!%n", channelAndBuffersContainer.getInstance());
    }
}
