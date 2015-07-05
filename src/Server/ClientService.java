package Server;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tareq.aziz on 6/30/15.
 */
public class ClientService extends Thread {

    Socket clientSocket = null;
    int clientID;
    BufferedReader reader;
    StringBuilder requestHeader;
    String response = "";


    ClientService(Socket socket, int id) {

        clientSocket = socket;
        clientID = id;
        requestHeader = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Exception in ClientService : " + e.getMessage());
        }
    }



    private void readHeader() throws IOException {


        String line = "";
        try {
            while (!(line = reader.readLine()).equals("")) {
                requestHeader.append(line + "\n");
            }
        } catch (NullPointerException e) {
//            System.out.println("Exception in readHeader : " + e.getMessage());
        }
    }


    private String[] getTokenizedHeader() {
        String[] tokens = null;
        try {
            tokens = requestHeader.toString().split(" ");
        } catch (Exception e){
            System.out.println("Exception in : " + e.getMessage());
        }
        return tokens;
    }


    private String getRequestMethod(){
        String[] tokenizer = getTokenizedHeader();
        String requestMethod = "";
        if (!tokenizer.equals(null)) {
            requestMethod = tokenizer[0];
        }
        return requestMethod;
    }


    private boolean isSupportedRequest() {
        String requestMethod = getRequestMethod();
        if (isGET(requestMethod) || isPOST(requestMethod)) {
            return true;
        }
        return false;
    }


    private boolean isGET(String requestMethod) {
        return requestMethod.equals("GET");
    }


    private boolean isPOST(String requestMethod) {
        return requestMethod.equals("POST");
    }


    private int getPostContentLength(){

        int contentLength = 0;
        Pattern p = Pattern.compile("Content-Length: (\\d+)");
        Matcher m = p.matcher(requestHeader.toString());
        if (m.find()){
            contentLength = Integer.parseInt(m.group(1));
        }
        return contentLength;
    }

    private String retrieveReqParam() throws IOException {
        StringBuilder paramFromClient = new StringBuilder();
        int contentLength = getPostContentLength();
        int c;
        for (int i = 0; i < contentLength; i++) {
            c = reader.read();
            paramFromClient.append((char) c);
        }
        return paramFromClient.toString();
    }


    private void responseToValidRequest() throws IOException {

        System.out.println("Request Header :\n" + requestHeader);
        String requestMethod = getRequestMethod();
        if (isPOST(requestMethod)) {
            responsePost();
        } else if (isGET(requestMethod)) {
            responseGet();
        }
    }


    private void responseToInValidRequest() {
        response = "403 Forbidden";
        System.out.println("client " + clientID + " unsupported request");
    }

    private void responsePost() throws IOException {
        String paramFromClient = retrieveReqParam();
        response += "\n\n\nThe param with Post request is : \n\n" + paramFromClient;
        System.out.println("Param with Post Content :\n" + paramFromClient);
    }




    private String getClientQuery(){
        String clientQuery = "";
        String[] tokenizer = getTokenizedHeader();
        if (!tokenizer.equals(null)) {
            if (tokenizer.length >=2){
                clientQuery = tokenizer[1];
            }
        }
        clientQuery = clientQuery.replaceAll("/\\?param=|%2F", "/");
        String htmlDirectory = "/home/tareq.aziz/IdeaProjects/SocketProgramming/src/Server/html";
        clientQuery = htmlDirectory + clientQuery;
        return clientQuery;
    }



    private boolean isRequestedFileExists(){
        String clientQuery = getClientQuery();
        File file = new File(clientQuery);
        boolean fileExists = file.exists();
        return fileExists;
    }


    private void responseGet() throws IOException {
        String clientQuery = getClientQuery();
        System.out.println("Param with GET request : \n" + clientQuery);

        if (isRequestedFileExists()) {
            response = FileServiceUtility.readFile(clientQuery);
            System.out.println("File exists and served to client");
        } else {
            response = "\nHTTP Error 404 - File or Directory not found";
            System.out.println("File doesn't exists !");
        }
    }





    public void writeToClient() throws IOException {
        PrintWriter out;
        out = new PrintWriter(clientSocket.getOutputStream());
        if (isRequestedFileExists()) {
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Content-Length: " + response.length());
            out.println();
            out.println(response);
        } else {
            out.println(response);
        }
        out.flush();
        out.close();
    }


    @Override
    public void run() {
        System.out.println("client " + clientID + " created");

        try {


                readHeader();
                if (isSupportedRequest()) {
                    responseToValidRequest();
                }
                else {
                    responseToInValidRequest();
                }
                writeToClient();



        } catch (IOException e) {
            System.out.println("Exception in Run : " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.requestCounter -= 1;
            System.out.println("\t\t\t\t\t\t\t\t\t\tclient " + clientID + " died");
        }


    }
}