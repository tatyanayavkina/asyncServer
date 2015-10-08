package handlers;

import utils.Message;
import utils.MessageWriter;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 06.10.2015.
 */
public class ServerMessageWriteHandler implements CompletionHandler<Integer, ChannelAndBuffersContainer> {
    private final ArrayList<Message> messageArrayList;

    public ServerMessageWriteHandler(ArrayList<Message> messageArrayList){
        this.messageArrayList = messageArrayList;
    }

    private List<Message> getNewMessagesList( int lastSendMessageIndex ){
        List<Message> messageList = null;
        synchronized (messageArrayList){

            if ( !messageArrayList.isEmpty() ){
                int messageListSize = messageArrayList.size();
                for(int i = 0; i < messageListSize; i++){
                    Message message = messageArrayList.get(i);
                    if( message.getId() ==  lastSendMessageIndex && ( i + 1 != messageListSize ) ){
                        messageList =  messageArrayList.subList( messageArrayList.indexOf( message ), messageListSize);
                    }
                }
            }
        }
        System.out.println("List="+messageList);
        return messageList;
    }

    @Override
    public void completed(Integer result, ChannelAndBuffersContainer channelAndBuffersContainer)
    {
        if ( channelAndBuffersContainer.getWriteBuffer().hasRemaining() ) {
            channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, this );
        }
        channelAndBuffersContainer.setWriteBuffer(null);

        synchronized ( channelAndBuffersContainer ){
            int lastSendMessageIndex = channelAndBuffersContainer.getLastSendMessageIndex();
            List<Message> messageList = getNewMessagesList( lastSendMessageIndex );
            if( messageList == null ){
                channelAndBuffersContainer.setReadyToWrite( true );
                return;
            }
            Message lastSendMessage = messageList.get( messageList.size() - 1);

            channelAndBuffersContainer.setLastSendMessageIndex( lastSendMessage.getId() );
            ByteBuffer writeBuffer = MessageWriter.createWriteBuffer( messageList );
            channelAndBuffersContainer.setWriteBuffer( writeBuffer );
            channelAndBuffersContainer.getChannel().write( channelAndBuffersContainer.getWriteBuffer(), channelAndBuffersContainer, this );
        }
    }

    @Override
    public void failed(Throwable exc, ChannelAndBuffersContainer chanelState)
    {
        System.out.printf("Error while writing to client #%02d!%n", chanelState.getInstance());
    }
}
