import java.net.*;
import java.io.*;
import java.lang.Thread;  
/**
 * This program demonstrates a simple TCP/IP socket client.
 *
 * @author www.codejava.net
 */
public class TimeClient {
 
    public static void main(String[] args) {
        if (args.length < 3) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String[] list = args[2].split(",");
        int[] listInteger = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            listInteger[i] = Integer.parseInt(list[i]);
        }
        try (Socket socket = new Socket(hostname, port)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            for (int i = 0; i < listInteger.length; i++) {
                writer.println(""+listInteger[i]);

                String time = reader.readLine();

                System.out.println("Curr Result :" + time);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            writer.println("done");
            String time = reader.readLine();
            System.out.println("Global Result :" + time);


        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
        
        
    }
}