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

    private final int TIMEOUT = 100000;
    private final int PING = 5000;
    private final int MAX_PARALLEL_GAMES = 5;
    private final int MAX_PLAYERS = 4; 
    private final int TIME_INTERVAL = 2;
    Database database = new Database();
    //Constructor
    public Server(int port, int mode) throws IOException{

        this.port = port;
        this.mode = mode;
        this.threadsGame = Executors.newFixedThreadPool(this.MAX_PARALLEL_GAMES);
        this.threadsPlayers = Executors.newFixedThreadPool(this.MAX_PLAYERS);
        int i = 1;
        Player p = database.getPlayer(i);
        System.out.println("Players in database:");
        System.out.println("ID, Name, Money, Current Game, Current Bet");

        while(p != null) {
            System.out.println(p.getId() + ", " + p.getName() + ", " + p.getMoney() + ", " + p.getCurrentGame() + ", " + p.getCurrBet());
            i++;
            p = this.database.getPlayer(i);
        }
    }
    
    //Start Server
    public void start() throws IOException {

        this.serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(this.port));
        System.out.println("Server started on port " + this.port + " with mode " + this.mode);

    }

    //Run Server
    public void run() throws IOException {
        System.out.println("Waiting for connections...");
        Runnable task = () -> {
            System.out.println("Waiting for connections...");
            try{
                while(true) {
                    System.out.println("Waiting for connections...");
                    acceptConnections();
                }
            }catch(IOException e) {
                System.out.println("Server Error: " + e.getMessage());
            }

        };

        Thread.Builder builder = Thread.ofVirtual();
        Thread t = builder.start(task);
    }
    public void acceptConnections() throws IOException {
        try{

            SocketChannel socket = this.serverSocket.accept();
            System.out.println("New connection from ");
            Runnable client = () -> {
                try {
                    handleClient(socket);
                } catch (IOException e) {
                    System.out.println("Server Error: " + e.getMessage());
                }
            };
            this.threadsPlayers.execute(client);
            System.out.println("Connection accepted");
        }catch(IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
    public void handleClient(SocketChannel socket) throws IOException {
        try {
            System.out.println("Handling client...");
            socket.socket().setSoTimeout(this.TIMEOUT);
            InputStream input = socket.socket().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.socket().getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            String choice = reader.readLine();
            switch (choice) {
                case "login":
                    attemptLogin(reader, writer);
                    break;
                case "register":
                    attemptRegister(reader, writer);
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    writer.println("Invalid choice");
                    break;
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
    void attemptLogin(BufferedReader reader, PrintWriter writer) throws IOException {
        writer.println("login");
        String username = "";
        while (true){
            username = reader.readLine();
            Player player = this.database.getPlayerByName(username);
            if (player != null){
                writer.println("username found");
                break;
            }
            writer.println("username not found");
        }
        while (true){
            String password = reader.readLine();
            String passwordHash = this.database.getPlayerPassword(username);
            if (password.equals(passwordHash)){
                writer.println("password correct");
                break;
            }
            writer.println("password incorrect");
        }
    }
    void attemptRegister(BufferedReader reader, PrintWriter writer) throws IOException {
        String username = "";
        while (true){
            username = reader.readLine();
            Player player = this.database.getPlayerByName(username);
            if (player == null){
                writer.println("username not found");
                break;
            }
            writer.println("username found");
        }
        Player player = new Player(1, username, 1000, 0, 0);
        String password = reader.readLine();
        this.database.createPlayer(player, password);
        writer.println("register successful");
        
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
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Server Error: " + e.getMessage());
                    System.exit(1);
                }
         }
        } catch (IOException e) {

            System.out.println("Server Error: " + e.getMessage());
            System.exit(1);

        } 
    }
    
}
