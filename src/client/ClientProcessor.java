package client;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created on 23.09.2015.
 */
public class ClientProcessor {
    private String username;
    private String password;
    private String IP;

    private AsyncTcpClient tcpClient;

    public ClientProcessor(String host, int port, String username, String password) throws IOException {
        this.username = username;
        this.password = password;
        this.IP = InetAddress.getLocalHost().toString();
        this.tcpClient = new AsyncTcpClient(host, port, this);
    }

    public void start(){
        tcpClient.connect();
    }

}
