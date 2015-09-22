package utils;

import server.AsyncServerClientState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 22.09.2015.
 */
public class ConnectionAutoIncrementMap extends HashMap<Integer, AsyncServerClientState> {
    public int connectionCounter;

    public ConnectionAutoIncrementMap() {
        this.connectionCounter = 0;
    }

    public int getNextId() {
        return connectionCounter++;
    }

    public void pushConnection(int connectionId, AsyncServerClientState clientState) {
        this.put(connectionId, clientState);
    }

    public Iterable<AsyncServerClientState> getAllExceptOne(int exceptConnectionId) {
        ArrayList<AsyncServerClientState> result;
        Set<Entry<Integer, AsyncServerClientState>> set;
        synchronized (this){
            int resultSize = size() - 1;
            if (resultSize < 1)
                return new ArrayList<AsyncServerClientState>(0);

            result = new ArrayList<AsyncServerClientState>(resultSize);
            set = entrySet();
        }

        for ( Map.Entry<Integer, AsyncServerClientState> s : set ) {
            Integer key = s.getKey();

            if(key != exceptConnectionId){
                result.add(s.getValue());
            }
        }

        return result;
    }
}
