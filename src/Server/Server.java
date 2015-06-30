package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tareq.aziz on 6/30/15.
 */
public class Server {
    public static void main(String[] args){

        try{
            ExecutorService executor = Executors.newFixedThreadPool(20);
            ServerSocket serverSocket = new ServerSocket(8080);
            int clientID = 1;

            while (true){
                Socket clientSocket = serverSocket.accept();
                ClientService clientService = new ClientService(clientSocket, clientID++);
                executor.execute(clientService);

            }

        } catch (Exception e){

            e.printStackTrace();
        }
    }
}
