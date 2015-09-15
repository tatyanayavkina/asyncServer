import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class AsyncClientConnectionHandler implements CompletionHandler<Void,AsyncServerClientState>{

    @Override
    public void completed(Void result, AsyncServerClientState clientState) {
        clientState.getChannel().write(clientState.getWriteBuffer(), clientState, AsyncClientWriteHandler());
    }

    @Override
    public void failed(Throwable exc, AsyncServerClientState clientState) {
        System.out.println("Connect fault!");
        exc.printStackTrace();
    }
}
