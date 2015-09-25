package client;

import handlers.ClientState;

import java.nio.channels.CompletionHandler;

/**
 * Created on 15.09.2015.
 */
public class AsyncClientConnectionHandler implements CompletionHandler<Void, ClientState>{
//    private UserInputHandler inputHandler;
    private final ClientProcessor clientProcessor;

    public AsyncClientConnectionHandler(ClientProcessor clientProcessor){
        this.clientProcessor = clientProcessor;
    }


    @Override
    public void completed(Void result, ClientState clientState) {
        //handle connection
        clientProcessor.handleConnection( clientState.getChannel() );

//        this.inputHandler = new UserInputHandler(this.clientProcessor, clientState.getChannel());
//        new Thread(this.inputHandler).start();
//        clientState.getChannel().read(clientState.getReadSizeBuffer(), clientState, new AsyncClientReadHandler());
    }

    @Override
    public void failed(Throwable exc, ClientState clientState) {
        System.out.println("Connect fault!");
        exc.printStackTrace();
    }
}
