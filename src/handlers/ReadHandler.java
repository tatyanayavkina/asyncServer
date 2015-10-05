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
public class ReadHandler implements CompletionHandler<Integer, ClientState> {
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
            method = processor.getClass().getMethod(callback, String.class, ClientState.class);
        } catch (SecurityException e) {
            // ...
        } catch (NoSuchMethodException e) {
            // ...
        }
        return  method;
    }

    public void completed(Integer result, ClientState clientState){
        if (result == -1)
        {
            try {
                clientState.getChannel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        ByteBuffer readSizeBuffer = clientState.getReadSizeBuffer();

        if (clientState.getReadBuffer() == null) {
            if (readSizeBuffer.hasRemaining())
                clientState.getChannel().read( readSizeBuffer, clientState, this );

            readSizeBuffer.flip();
            int size = readSizeBuffer.getInt();
            ByteBuffer rBuffer = ByteBuffer.allocate(size);
            clientState.setReadBuffer(rBuffer);
            clientState.getChannel().read( rBuffer, clientState, this );
        } else {
            ByteBuffer readBuffer = clientState.getReadBuffer();
            if (readBuffer.hasRemaining())
                clientState.getChannel().read( readBuffer, clientState, this );

            readBuffer.flip();
            byte[] readBytes = readBuffer.array();
            String message = new String( readBytes, StandardCharsets.UTF_8 );

            readSizeBuffer.clear();
            clientState.setReadBuffer(null);

            Method callbackMethod;
            if ( callback != null && ( callbackMethod = prepareCallback() ) != null){
                try {
                    callbackMethod.invoke(processor, message, clientState);
                } catch (IllegalArgumentException e) {

                } catch (IllegalAccessException e) {

                } catch (InvocationTargetException e) {

                }
            }

            if ( isMessageExchange ){
                clientState.getChannel().read(clientState.getReadSizeBuffer(), clientState, this);
            }
        }
    }

    public void failed(Throwable ex, ClientState clientState){
        System.out.printf("Error while reading from client #%02d!%n", clientState.getInstance());
    }
}
