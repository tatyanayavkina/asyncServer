package utils;

import handlers.ChannelAndBuffersContainer;

public interface ChatProcessor {
    void handleAuthorization(String credentials, ChannelAndBuffersContainer channelAndBuffersContainer);
    void handleInputMessage(String messageString, ChannelAndBuffersContainer channelAndBuffersContainer);
    void close(ChannelAndBuffersContainer channelAndBuffersContainer);
}
