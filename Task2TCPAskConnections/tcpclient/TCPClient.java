package Task2TCPAskConnections.tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    Integer buffer_size = 512;
    boolean Shutdown ; 
    Integer Timeout; 
    Integer Limit ; 
  // Constructor for creating a TCP client with optional settings
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.Shutdown = shutdown;
        this.Limit = limit;
        this.Timeout = timeout;
    }
    // Sends a request to the server and receives a response
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        Socket socket =  new Socket(hostname, port); 
        try{
            // Set timeout if specified
            if(Timeout !=null && Timeout > 0){
                socket.setSoTimeout(Timeout);
              }

          OutputStream output = socket.getOutputStream();
           
          // Send data to the server if there is data to send
          if(toServerBytes !=null){
            output.write(toServerBytes);
            output.flush();
        }
        // close output if shutdown is true.
           if(Shutdown){
            socket.shutdownOutput();
           }
         // Create a small buffer to read incoming data (bytes).
          InputStream input = socket.getInputStream();
          byte [] buffer = new byte[buffer_size];         
          int bytesFromServer = input.read(buffer); 
          // Counter for the total number of bytes received
          int byteCounter = 0; 
          do {
            // stop reading if the server closes the connection. 
            if (bytesFromServer ==-1) {
                break;
            }
            // do not read more than the specified amount if limet is set. 
            if (Limit != null && (byteCounter + bytesFromServer) > Limit) {
                response.write(buffer, 0, Limit - byteCounter);
                break; 
            }
            //save the recieving bytes and update the total bytes counter. 
            response.write(buffer, 0, bytesFromServer);
            byteCounter += bytesFromServer;
        
            // keep reading until the server closes the connection
        } while ((bytesFromServer = input.read(buffer)) != -1);
        
        }catch(IOException e){
            e.printStackTrace();
         }
     //finally close the socket if it is not already closed
         finally{
            if(socket != null && !socket.isClosed()){
                try{
                    socket.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
         }
         
        return response.toByteArray();
    }
}