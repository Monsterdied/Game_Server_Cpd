import java.io.*;
import java.net.*;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Objects;  
import java.util.ArrayList;
public class Server {


    //Server attributes
    private final int port;
    private final int mode;
    private ServerSocketChannel serverSocket;
    private final ExecutorService threadsGame;
    private final ExecutorService threadsPlayers;
    private SocketChannel socket;
    //Constants

    private final int TIMEOUT = 100000;
    private final int PING = 5000;
    private final int MAX_PARALLEL_GAMES = 5;
    private final int MAX_PLAYERS = 4; 
    private final int TIME_INTERVAL = 2;
    private final Queue queue = new Queue(4, 1000);
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
            System.out.println("New connection from ");
            socket = this.serverSocket.accept();
            Runnable client = () -> {
                try {
                    handleClient();
                } catch (IOException e) {
                    System.out.println("Server Error: " + e.getMessage());
                }
            };
            this.threadsPlayers.execute(client);
            System.out.println("Connection accepted");

    }
    public void handleClient() throws IOException {
        try {
            Player player = null;
            System.out.println("Handling client...");
            socket.socket().setSoTimeout(this.TIMEOUT);
            InputStream input = socket.socket().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.socket().getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            String choice = reader.readLine();
            switch (choice) {
                case "login":
                    player = attemptLogin(reader, writer);
                    break;
                case "register":
                    player = attemptRegister(reader, writer);
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    writer.println("Invalid choice");
                    break;
            }
            if (player != null){
                if (player.getCurrentGame() != -1){
                    System.out.println("Hnadle reconnection");
                    System.out.println("Player " + player.getName() + " is in the lobby");
                } else {
                    System.out.println("Player " + player.getName() + " is in game " + player.getCurrentGame());
                }
                System.out.println("Player " + player.getName() + " connected");
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
    void AddToqueue(PrintWriter writer,BufferedReader reader, Player player, SocketChannel socket) {
        ArrayList<Pair<Player, SocketChannel>> players = null;
        try {
            switch (reader.readLine()){
                case "ranked":
                    queue.AddPlayerToRanked(player, socket);
                    players =  queue.getRankedGamePlayers();
                    break;
                case "casual":
                    queue.AddPlayerToCasual(player, socket);
                    players = queue.getCasualGamePlayers();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    writer.println("Invalid choice");
                    break;
            }
            if (players != null){
                //Game game = new Game(players);
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
    Player attemptLogin(BufferedReader reader, PrintWriter writer) throws IOException {
        String username = "";
        Player player = null;
        while (true){
            username = reader.readLine();
            player = this.database.getPlayerByName(username);
            if (player != null){
                System.out.println("username found");
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
        return player;
    }
    Player attemptRegister(BufferedReader reader, PrintWriter writer) throws IOException {
        String username = "";
        Player player = null;
        while (true){
            username = reader.readLine();
            player = this.database.getPlayerByName(username);
            if (Objects.isNull(player)){
                writer.println("username not found");
                break;
            }
            writer.println("username found");
        }
        player = new Player(1, username, 1000.0, 0, 0.0, 1.0);
        String password = reader.readLine();
        this.database.createPlayer(player, password);
        writer.println("register successful");
        return player;
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
