import java.io.IOException;

/**
 * Created on 17.09.2015.
 */
public class StartClient {
    public static void main(String[] args) {
        try {
            AsyncTcpClient asyncClient = new AsyncTcpClient("localhost", 9999);
            asyncClient.connect();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
