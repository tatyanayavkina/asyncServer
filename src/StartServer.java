
import server.ServerProcessor;
import utils.Config;
import utils.XMLReader;

import java.io.IOException;
import java.util.HashMap;

public class StartServer {

    public static void main(String[] args) {
        try{
            Config config = XMLReader.readParams();
            HashMap<String, String> users = XMLReader.readUsers();
            ServerProcessor ServerProcessor = new ServerProcessor(config, users);
            ServerProcessor.start();
        } catch(IOException e){
            System.out.println("ServerProcessor creation error");
            System.exit(-1);
        } catch (Exception e){
            System.out.println("Params reading error");
            System.exit(-1);
        }

    }
}
