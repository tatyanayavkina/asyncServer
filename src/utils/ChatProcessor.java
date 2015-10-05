package utils;

import handlers.ChannelAndBuffersContainer;

/**
 * Created on 25.09.2015.
 */
public interface ChatProcessor {
    void handleAuthorization(String credentials, ChannelAndBuffersContainer channelAndBuffersContainer);
    void handleInputMessage(String messageString, ChannelAndBuffersContainer channelAndBuffersContainer);
}
