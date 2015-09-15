import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class AsyncServerReadHandler implements CompletionHandler<Integer, AsyncServerClientState> {

    public void completed(Integer result, AsyncServerClientState clientState){

    }

    public void failed(Throwable ex, AsyncServerClientState clientState){

    }
}
