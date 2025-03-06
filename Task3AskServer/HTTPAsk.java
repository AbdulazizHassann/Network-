package Task3AskServer;

import java.net.*;
import java.util.HashMap;
import java.io.*;
import Task3AskServer.tcpclient.TCPClient;
public class HTTPAsk {

    public static void main( String[] args) throws IOException{
        
        int port = Integer.parseInt(args[0]);
        ServerSocket HTTPServer = new ServerSocket(port);
        System.err.println("\nHello world this is HTTP server\n ");
        System.err.println("the server is runnig at the port: " + port);

     
        while (true) { 
            
            Socket clientSocket = HTTPServer.accept(); 
            OutputStream output = clientSocket.getOutputStream();
           
           InputStream input = clientSocket.getInputStream();
           byte[] buffer = new byte[2048];
           int bytesFromServer = input.read(buffer);
         
           if(bytesFromServer ==-1){
           clientSocket.close();
           continue;
           } 
           String request = new String(buffer , 0, bytesFromServer);
          
           // extract the URL part from the first line of the request
            String[] lines = request.split("\r\n");
            if (lines.length < 1) {
                String errorMSG ="Invalid HTTP request";
                sendResponse(output, "400 Bad Request", errorMSG.getBytes("UTF-8"));
                clientSocket.close();
                continue;
            }
            // extract the URL from the request line
            String firstLine = lines[0];
            String[] requestFromClient = firstLine.split(" ");
           
            // Check if the request has at least 2 parts  
            if (requestFromClient.length < 3 || !requestFromClient[0].equals("GET") || !requestFromClient[2].startsWith("HTTP/")) {
                String errorMSG ="Invalid HTTP request";
                sendResponse(output, "400 Bad Request", errorMSG.getBytes("UTF-8"));
                clientSocket.close();
                continue;
            }
            // extract the URL from the request and Parse query parameters from the URL
            String url = requestFromClient[1];
           if (!url.startsWith("/ask?")) {
            String errorMSG ="Invalid URL";
             sendResponse(output, "404 Not Found", errorMSG.getBytes("UTF-8"));
             clientSocket.close();
             continue;
            }


           HashMap<String, String> query = queryParams(url);
           String hostname = query.get("hostname");
           String portNR = query.get("port");
           String timeout = query.get("timeout");
           String limit = query.get("limit");
           String sendData = query.get("string");
           String shutdownStr = query.get("shutdown");

       // if either hostname or port is missing, send 400 response and close connection  
           if(hostname ==null || portNR ==null){
            sendResponse(output,"400 Bad Request",new byte[0]);
            clientSocket.close();
            continue;
           }
            // parse and convert parameters  
            int portValue ;
            try{
                portValue = Integer.parseInt(portNR);
            }catch(NumberFormatException e ){
                String errorMSG = "Error: " + e.getMessage();
                sendResponse(output, "400 Bad Request", errorMSG.getBytes("UTF-8"));
                clientSocket.close();
                continue;
            }

            Integer timeoutValue = (timeout != null) ? Integer.parseInt(timeout) : null;
            Integer  limitValue = (limit != null) ? Integer.parseInt(limit) : null;
            boolean shutdown = (shutdownStr != null) && shutdownStr.equalsIgnoreCase("true");
            byte[] sendDatabytes = (sendData != null) ? sendData.getBytes("UTF-8") : new byte[0];
           try{
          
            // Send request via TCP client and return response  
            TCPClient TCP = new TCPClient(shutdown,timeoutValue, limitValue);
            byte [] responseFromServer = TCP.askServer(hostname, portValue, sendDatabytes);
            System.out.println("Received response from TCPClient. Sending HTTP response...");
           sendResponse(output, "200 OK", responseFromServer);
           output.flush(); 

           }catch(IOException e){
            String errorMSG = "TCPClient error: " + e.getMessage();
            sendResponse(output, "500 Internal Server Error", errorMSG.getBytes("UTF-8"));
           }
           // finnaly close the client socket safely  
           finally {
            try {
                clientSocket.close();
                System.out.println("Closed client connection.");
            } catch (IOException e) {
                String errorMSG = "TCPClient error: " + e.getMessage();
                System.err.println(errorMSG);
            }
        }
        
          
          
        }
    } 

    // Method to extract and parse query parameters from the URL  
    public static HashMap<String, String> queryParams(String url) {
        HashMap<String, String> queryP = new HashMap<>();

        if (url.startsWith("/ask?")) {
            String[] splitHalf = url.split("\\?", 2);
            if (splitHalf.length == 2) {
                String[] pairs = splitHalf[1].split("&");

                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        queryP.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        }
        return queryP;
    }
    // Method to send the HTTP response
    private static void sendResponse(OutputStream output, String status, byte[] body) throws IOException {
        // create HTTP-header
        String headers = "HTTP/1.1 " + status + "\r\n" +
                         "Content-Type: text/plain; charset=UTF-8\r\n" +
                         "Content-Length: " + body.length + "\r\n" +
                         "Connection: close\r\n" +
                         "\r\n";
    
       
        output.write(headers.getBytes("UTF-8"));
        output.write(body);
        output.flush();
    }
    
}

