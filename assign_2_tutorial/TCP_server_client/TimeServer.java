import java.io.*;
import java.net.*;
import java.util.Date;
 
/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class TimeServer extends Thread{
    private Socket socket;
    private int sum = 0;
    static int totalSum = 0;
    TimeServer(Socket socket){
        this.socket = socket;
    }
    public static void main(String[] args) {
        if (args.length < 1) return;

    
        int port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                Thread t = new TimeServer(socket);
                t.start();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    @Override 
    public void run() 
    { 
        try {
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            while (true) {
                String time = reader.readLine();
                if (time.equals("done")) {
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
                    totalSum += sum;
                    System.out.println("Client finished :"+ totalSum);
                    writer.println(totalSum);
                    break;
                }
                int add = Integer.parseInt(time);
                sum += add;
                System.out.println("New client connected: "+ time);
                System.out.println("New Sum :"+ sum);

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                System.out.println("Sending time to client: "+ new Date().toString());
                writer.println(sum);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    } 
}