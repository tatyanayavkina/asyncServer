package utils;

import handlers.ChannelAndBuffersContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 22.09.2015.
 */
public class ConnectionAutoIncrementMap extends HashMap<Integer, ChannelAndBuffersContainer> {
    public int connectionCounter;

    public ConnectionAutoIncrementMap() {
        this.connectionCounter = 0;
    }

    public int getNextId() {
        return connectionCounter++;
    }

    public void pushConnection(int connectionId, ChannelAndBuffersContainer channelAndBuffersContainer) {
        this.put(connectionId, channelAndBuffersContainer);
    }

    public Iterable<ChannelAndBuffersContainer> getAllExceptOne(int exceptConnectionId) {
        ArrayList<ChannelAndBuffersContainer> result;
        Set<Entry<Integer, ChannelAndBuffersContainer>> set;
        synchronized (this){
            int resultSize = size() - 1;
            if (resultSize < 1)
                return new ArrayList<ChannelAndBuffersContainer>(0);

            result = new ArrayList<ChannelAndBuffersContainer>(resultSize);
            set = entrySet();
        }

        for ( Map.Entry<Integer, ChannelAndBuffersContainer> s : set ) {
            Integer key = s.getKey();

            if(key != exceptConnectionId){
                result.add(s.getValue());
            }
        }

        return result;
    }
}
