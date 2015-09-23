package client;

import server.AsyncServerClientState;

import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class AsyncClientConnectionHandler implements CompletionHandler<Void, AsyncServerClientState>{
    private UserInputHandler inputHandler;
    private final ClientProcessor clientProcessor;

    public AsyncClientConnectionHandler(ClientProcessor clientProcessor){
        this.clientProcessor = clientProcessor;
    }


    @Override
    public void completed(Void result, AsyncServerClientState clientState) {
        // handle user input
        this.inputHandler = new UserInputHandler(this.clientProcessor, clientState.getChannel());
        new Thread(this.inputHandler).start();
        // handle server messages
        clientState.getChannel().read(clientState.getReadSizeBuffer(), clientState, new AsyncClientReadHandler());
    }

    @Override
    public void failed(Throwable exc, AsyncServerClientState clientState) {
        System.out.println("Connect fault!");
        exc.printStackTrace();
    }
}
