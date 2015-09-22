
import server.ServerProcessor;
import utils.Config;
import utils.XMLReader;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created on 15.09.2015.
 */
public class StartServer {

    public static void main(String[] args) {
        Config config = XMLReader.readParams();
        HashMap<String, String> users = XMLReader.readUsers();

        try{
            ServerProcessor ServerProcessor = new ServerProcessor(config, users);
            ServerProcessor.start();
        } catch (IOException e){

        }

    }
}
