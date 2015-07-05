package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URLEncoder;

/**
 * Created by tareq.aziz on 6/30/15.
 */
public class Client {
    public static void main(String[] args){

        for (int i = 0; i <100000 ; i++){
            try{
                Socket socket = new Socket("localhost",8080);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeUTF("Hi ! I am Client !!");
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
