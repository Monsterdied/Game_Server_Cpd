import java.io.*;
import java.net.*;


public class Server {


    //Server attributes
    private int port;
    private int mode;

    //Constants

    private final int TIMEOUT = 10000;
    private final int PING = 5000;
    private final int MAX_PARALLEL_GAMES = 5;
    private final int MAX_PLAYERS = 4;  

    

    public Server(int port, int mode) {

        this.port = port;
        this.mode = mode;

    }
    

    public static void main(String[] args) {
        
        //Check for correct number of command line arguments
        if(args.length != 2) {

            System.out.println("Usage: java Server <port> <mode>");
            System.exit(1);

        }

        //Receive and handle command line arguments
        int port = Integer.parseInt(args[0]);
        int mode = Integer.parseInt(args[1]);

        //Check for valid modes
        if(mode != 1 && mode != 2) {

            System.out.println("Invalid mode. Choose 1 or 2");
            System.exit(1);

        }

        try {

            Server server = new Server(port, mode);
        
        }

    } catch (IOException e) {

        System.out.println("Server Error: " + e.getMessage());
        System.exit(1);

    }
    
}
