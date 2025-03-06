package tcpclient;

import java.net.Socket;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TCPClient {
    
    
    public TCPClient() {
    }
/* this method takes server hostname , portNR and data that client want to send.
    When the server receives the data, it sends a response as a byte array .
  */
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
    
        /* Stores data received from the server.  */
        ByteArrayOutputStream responseFromServer =new ByteArrayOutputStream();
        Socket socket = null;
        try{
        /* Connects to the server. */
        socket = new Socket(hostname , port);
      
     /* client send data(bytes) to the server. */
      OutputStream output = socket.getOutputStream();
      if(toServerBytes!=null){
        output.write(toServerBytes);
        socket.shutdownOutput(); // closed as soon client is finshing send all his data. 
      }
      /* Small buffer to store incoming bytes from the server.*/
      byte [] buffer = new byte[512];

      InputStream input = socket.getInputStream();
      
    /*Number of bytes received from the serve.  */
      int bytesFromServer ;
 /* Read and store bytes from the server until no more data is available (-1). */
      do{
        bytesFromServer = input.read(buffer);
        if(bytesFromServer > 0){
            responseFromServer.write(buffer,0,bytesFromServer);
        }
      }while(bytesFromServer!=-1);
      
  
    }catch(IOException e){
       e.printStackTrace();
    }
    /* Ensures the socket is closed, even if an error occurs in try-catch. */
       closeSocket(socket);
      
    return responseFromServer.toByteArray();
        
    }
/* This method connects the client to the server and listens for a response.  */
    public byte[] askServer(String hostname, int port) throws IOException {
       /* The difference in this method is that it does not send data to the server.  
          It only receives a response. Otherwise, it works the same as the above method. */

        Socket socket = null;
        ByteArrayOutputStream responseFromServer = new ByteArrayOutputStream();
        try{
            byte[] buffer = new byte[512];
            socket = new Socket(hostname , port);
            InputStream input = socket.getInputStream();
            int bytesFromServer;
            do{
                bytesFromServer  = input.read(buffer);
                if(bytesFromServer >= 0){
                    responseFromServer.write(buffer,0,bytesFromServer);
                }
            }while(bytesFromServer!=-1);
        }catch(IOException e){
            e.printStackTrace();
        }
        if(socket != null){
        closeSocket(socket);
        }
        return responseFromServer.toByteArray();
    }
 /* This method is used to finish communication with the server. */  
    public void closeSocket(Socket socket){
           if(socket != null && !socket.isClosed()){
            try{
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
