import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
public class Connections {
    public static void sendRequest(SocketChannel socketChannel,String request){
        try{
            //System.out.println("Sending request: " + request);
            OutputStream output = socketChannel.socket().getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(request);
            //System.out.println("Request sent");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public static String hasResponse(SocketChannel socketChannel){
        try{
            InputStream in = socketChannel.socket().getInputStream();
            //System.out.println("Checking for response");
            if(in.available() > 0){
                //System.out.println("Response found");
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                socketChannel.read(buffer);
                //System.out.println("Response received");
                return new String(buffer.array()).trim();
            }else{
                //System.out.println("No response found");
                return null;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }

    }
    public static String receiveResponse(SocketChannel socketChannel){
        try{
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            socketChannel.read(buffer);
            return new String(buffer.array()).trim();
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}