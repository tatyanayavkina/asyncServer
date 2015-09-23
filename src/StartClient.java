import client.AsyncTcpClient;
import client.ClientProcessor;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created on 17.09.2015.
 */
public class StartClient {
    public static void main(String[] args) {
        System.out.println("Enter your name, please");

        Scanner scanner = new Scanner(System.in);
        String username = scanner.next();

        System.out.println("Enter your password, please");
        String password = scanner.next();

        try {
            ClientProcessor clientProcessor = new ClientProcessor("localhost", 9999, username, password);
            clientProcessor.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
