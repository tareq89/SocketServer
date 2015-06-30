package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by tareq.aziz on 6/30/15.
 */
public class ClientService extends Thread {

    Socket clientSocket;
    int clientID;
    boolean running = true;


    ClientService(Socket socket, int id){
        clientSocket = socket;
        clientID = id;
    }

    @Override
    public void run(){
        System.out.println("client " + clientID + " created");
        try {

            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            String fromClient = dataInputStream.readUTF();
            System.out.println(fromClient);

            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeUTF("<h1> Hello Client !!! </h1>");
            dataOutputStream.flush();
            dataOutputStream.close();

            Thread.sleep(1000);
            clientSocket.close();
            System.out.println("\t\t\t\t\t\t\t\t\t\tclient " + clientID + " died");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        try{
//
//            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
//            System.out.println(dataInputStream.readUTF());
//            System.out.println("\n\n");
//            clientSocket.close();
//
//        } catch (Exception e){
//
//        }

    }
}
