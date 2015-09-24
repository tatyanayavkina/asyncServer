package server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Method;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerReadHandler implements CompletionHandler<Integer, AsyncServerClientState> {
    private final AsyncServerWriteHandler writeHandler = new AsyncServerWriteHandler();
    private final ServerProcessor serverProcessor;
    private String callback;
    private boolean isMessageExchange;

    public AsyncServerReadHandler(boolean isMessageExchange, ServerProcessor serverProcessor, String callback){
        this.isMessageExchange = isMessageExchange;
        this.serverProcessor = serverProcessor;
        this.callback = callback;
    }

    public AsyncServerReadHandler(boolean isMessageExchange, ServerProcessor serverProcessor){
        this.isMessageExchange = isMessageExchange;
        this.serverProcessor = serverProcessor;
        this.callback = null;
    }

    private Method prepareCallback(){
        Method method = null;
        try {
            method = serverProcessor.getClass().getMethod(callback, String.class, AsyncServerClientState.class);
        } catch (SecurityException e) {
            // ...
        } catch (NoSuchMethodException e) {
            // ...
        }
        return  method;
    }

    public void completed(Integer result, AsyncServerClientState clientState){
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
                clientState.getChannel().read( clientState.getReadSizeBuffer(), clientState, this );

            readSizeBuffer.flip();
            int size = readSizeBuffer.getInt();
            System.out.println("Received " + size + " bytes");

            ByteBuffer rBuffer = ByteBuffer.allocate(size);
            clientState.setReadBuffer(rBuffer);

            clientState.getChannel().read( clientState.getReadBuffer(), clientState, this );
        } else {
            ByteBuffer readBuffer = clientState.getReadBuffer();
            if (readBuffer.hasRemaining())
                clientState.getChannel().read( clientState.getReadBuffer(), clientState, this );

            readBuffer.flip();
            byte[] readBytes = readBuffer.array();

            String message = new String( readBytes, StandardCharsets.UTF_8 );
            System.out.println(message);

//            serverProcessor.handleInputMessage(message, clientState);
            Method callbackMethod;
            if ( callback != null && ( callbackMethod = prepareCallback() ) != null){
                try {
                    callbackMethod.invoke(serverProcessor, message, clientState);
                } catch (IllegalArgumentException e) {

                } catch (IllegalAccessException e) {

                } catch (InvocationTargetException e) {

                }
            }

            readSizeBuffer.clear();
            clientState.deleteReadBuffer();

            if ( isMessageExchange ){
                clientState.getChannel().read(clientState.getReadSizeBuffer(), clientState, this);
            }
        }
    }

    public void failed(Throwable ex, AsyncServerClientState clientState){
        System.out.printf("Error while reading from client #%02d!%n", clientState.getInstance());
    }
}
