package Server;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by tareq.aziz on 6/30/15.
 */
public class ClientService extends Thread {

    Socket clientSocket = null;
    int clientID;
    BufferedReader reader;

    StringBuilder headerFromClient;
    StringBuilder paramFromClient;
    String contentHeader = "Content-Length: ";
    String line = "";
    String methodToken = "";
    String response = "";
    String clientQuery = "";
    int contentLength = 0;
    StringTokenizer tokenizer;
    boolean validRequest;
    boolean requestedFileExists = false;


    ClientService(Socket socket, int id){
        clientSocket = socket;
        clientID = id;
    }


    private void readHeader() throws IOException {

        headerFromClient = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        try{
            while (!(line = reader.readLine()).equals("")){
                headerFromClient.append(line + "\n");
                if (line.startsWith(contentHeader)){
                    contentLength = Integer.parseInt(line.substring(contentHeader.length()));
                }
            }
        } catch (NullPointerException e){
            validRequest = false;
        }
    }


    private void tokenize(){
        tokenizer = new StringTokenizer(headerFromClient.toString());
    }


    private boolean isValidRequest(){

        tokenize();
        if (tokenizer.hasMoreElements()){
            methodToken = tokenizer.nextToken();
            clientQuery = tokenizer.nextToken();
        }
        if (isGET()||isPOST()){
            validRequest = true;
        }
        return validRequest;
    }


    private boolean isGET(){
        return methodToken.equals("GET");
    }


    private boolean isPOST(){
        return methodToken.equals("POST");
    }


    private void retrieveReqParam() throws IOException {
        paramFromClient = new StringBuilder();
        int c = 0;
        for (int i = 0; i < contentLength; i++) {
            c = reader.read();
            paramFromClient.append((char) c);
        }
    }



    private void responseToPost() throws IOException {
        retrieveReqParam();
        response += "\n\n\nThe param with Post request is : \n\n" + paramFromClient;
        System.out.println("Param with Post Content :\n" +paramFromClient);
    }



    private void responseToGet() throws IOException {
        retrieveReqParam();
        System.out.println("Param with GET request : \n" + clientQuery);

        clientQuery = clientQuery.replaceAll("/\\?param=|%2F", "/");

        File file = new File(clientQuery);
        requestedFileExists = file.exists();
        if (requestedFileExists){
            response = FileServiceUtility.readFile(clientQuery);
            System.out.println("File exists and served to client");


        } else {
            response += paramFromClient.toString();
            System.out.println("File doesn't exists !");

        }
    }


    private void responseToValidRequest() throws IOException {


        System.out.println("Request Header :\n" +headerFromClient);

        if (isPOST()){
            responseToPost();
        }else if (isGET()){
            responseToGet();
        }
    }


    private void responseToInValidRequest() {
        response = "We do not server requests other than GET or POST !!!";
        System.out.println("client "+ clientID + " invalid request");
    }


    public void writeToClient() throws IOException {
        PrintWriter out;
        out = new PrintWriter(clientSocket.getOutputStream());
        if (requestedFileExists){
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Content-Length: " + response.length());
            out.println();
            out.println(response);
        } else if (isGET()){
            out.println(response);
            out.println("\nRequested file : " + clientQuery + "  doesn't exists");
        } else {
            out.println(response);
        }
        out.flush();
        out.close();
    }


    @Override
    public void run(){
        System.out.println("client " + clientID + " created");
        try {
            readHeader();
            if (isValidRequest())
                responseToValidRequest();
            else
                responseToInValidRequest();
            writeToClient();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("\t\t\t\t\t\t\t\t\t\tclient " + clientID + " died");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
