package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by tareq.aziz on 6/30/15.
 */
public class Server {
    public static int requestCounter;
    public static void main(String[] args){


        try{
            ExecutorService executor = Executors.newCachedThreadPool();
            ServerSocket serverSocket = new ServerSocket(8080);
            int clientID = 1;

            while (true){
                    if (requestCounter <=1){
                        requestCounter += 1;
                        Socket clientSocket = serverSocket.accept();
                        ClientService clientService = new ClientService(clientSocket, clientID++);
                        executor.execute(clientService);
                    } else {
                        Socket clientSocket = serverSocket.accept();
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                        String response = "Server is BUSY";
                        out.println(response);
                        System.out.println(response);
                        out.flush();
                        out.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
