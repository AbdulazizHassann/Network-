package concurrentHTTPAskServer;
import java.net.*;
import java.io.*;

public class concHTTPAsk {
    public static void main( String[] args)throws IOException {

        if(args.length == 0){
        System.err.println("enter a port number.");
        return;
       }
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            String errorMSG = "port number error : " + e.getMessage();
            System.err.println(errorMSG);
            return;
        }
    
        System.err.println("\nHello world this concurrent HTTP server!\n ");
        System.err.println("the server is runnig at the port: " + port);
       
        try (ServerSocket concHTTPServer = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket clientSocket = concHTTPServer.accept();
                    myRunnable runnable = new myRunnable(clientSocket);
                    Thread thread = new Thread(runnable);
                    thread.start();
                } catch (IOException e) {
                    String errorMSG = "Connection error: " + e.getMessage();
                    System.err.println(errorMSG);  
                }
            }
        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage());  
        }
        
       
        
    }
}

