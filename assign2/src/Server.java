import java.io.*;
import java.net.*;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
public class Server {


    //Server attributes
    private final int port;
    private final int mode;
    private ServerSocketChannel serverSocket;
    private final ExecutorService threadsGame;
    private final ExecutorService threadsPlayers;

    //Constants

    private final int TIMEOUT = 10000;
    private final int PING = 5000;
    private final int MAX_PARALLEL_GAMES = 5;
    private final int MAX_PLAYERS = 4; 
    private final int TIME_INTERVAL = 2;
    
    //Constructor
    public Server(int port, int mode) throws IOException{

        this.port = port;
        this.mode = mode;
        this.threadsGame = Executors.newFixedThreadPool(this.MAX_PARALLEL_GAMES);
        this.threadsPlayers = Executors.newFixedThreadPool(this.MAX_PLAYERS);
        
        Database db = new Database();
        
        int i = 1;
        Player p = db.getPlayer(i);
        System.out.println("Players in database:");
        System.out.println("ID, Name, Money, Current Game, Current Bet");

        while(p != null) {
            System.out.println(p.getId() + ", " + p.getName() + ", " + p.getMoney() + ", " + p.getCurrentGame() + ", " + p.getCurrBet());
            i++;
            p = db.getPlayer(i);
        }
    }
    
    //Start Server
    public void start() throws IOException {

        this.serverSocket = ServerSocketChannel.open();
        System.out.println("Server started on port " + this.port + " with mode " + this.mode);

    }

    //Run Server
    public void run() throws IOException {

         

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
            server.start();
            server.run();
        
        } catch (IOException e) {

            System.out.println("Server Error: " + e.getMessage());
            System.exit(1);

        } 
    }
    
}
