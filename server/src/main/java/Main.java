import collections.Users;
import threads.ClientThread;
import sqltasks.SQLBridge;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by snels on 21.11.2016.
 */
public class Main {

    public static void main(String[] args) {
        try {
            ServerSocket socketListener = new ServerSocket(1234);
            Users users = new Users();
            SQLBridge SQLBridge = new SQLBridge();
            SQLBridge.startSQL();

            while (true) {
                Socket client = null;
                while (client == null) {
                    client = socketListener.accept();
                }
                new ClientThread(client, SQLBridge, users);
            }
        } catch (SocketException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        }
    }
}
