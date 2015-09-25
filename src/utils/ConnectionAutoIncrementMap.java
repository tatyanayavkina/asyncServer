package utils;

import handlers.ClientState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 22.09.2015.
 */
public class ConnectionAutoIncrementMap extends HashMap<Integer, ClientState> {
    public int connectionCounter;

    public ConnectionAutoIncrementMap() {
        this.connectionCounter = 0;
    }

    public int getNextId() {
        return connectionCounter++;
    }

    public void pushConnection(int connectionId, ClientState clientState) {
        this.put(connectionId, clientState);
    }

    public Iterable<ClientState> getAllExceptOne(int exceptConnectionId) {
        ArrayList<ClientState> result;
        Set<Entry<Integer, ClientState>> set;
        synchronized (this){
            int resultSize = size() - 1;
            if (resultSize < 1)
                return new ArrayList<ClientState>(0);

            result = new ArrayList<ClientState>(resultSize);
            set = entrySet();
        }

        for ( Map.Entry<Integer, ClientState> s : set ) {
            Integer key = s.getKey();

            if(key != exceptConnectionId){
                result.add(s.getValue());
            }
        }

        return result;
    }
}
