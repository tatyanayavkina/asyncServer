import java.nio.channels.CompletionHandler;

/**
 * Created on 17.09.2015.
 */
public class AsyncClientReadHandler implements CompletionHandler<Integer, AsyncServerClientState>{

    @Override
    public void completed(Integer result, AsyncServerClientState clientState) {

    }

    @Override
    public void failed(Throwable exc, AsyncServerClientState clientState) {

    }
}
