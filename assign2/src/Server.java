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
import java.util.List;
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
    private final Queue queue = new Queue(2, 1000);
    Database database = new Database();
    private ReentrantLock databaseLock;
    private ReentrantLock queueLock;
    private ReentrantLock timeLock;
    //Constructor
    public Server(int port, int mode) throws IOException{
        this.queueLock = new ReentrantLock();
        this.databaseLock = new ReentrantLock();
        this.timeLock = new ReentrantLock();
        this.port = port;
        this.mode = mode;
        this.threadsGame = Executors.newFixedThreadPool(this.MAX_PARALLEL_GAMES);
        this.threadsPlayers = Executors.newFixedThreadPool(this.MAX_PLAYERS);
        
        int i = 1;
        this.databaseLock.lock();
        Player p = database.getPlayer(i);
        this.databaseLock.unlock();
        System.out.println("Players in database:");
        System.out.println("ID, Name, Money, Current Game, Current Bet");

        while(p != null) {
            System.out.println(p.getId() + ", " + p.getName() + ", " + p.getMoney() + ", " + p.getCurrentGame() + ", " + p.getCurrBet());
            i++;
            this.databaseLock.lock();
            p = this.database.getPlayer(i);
            this.databaseLock.unlock();
            
        }
        List<Player> players = database.getPlayersInGame(0);
        for (Player player : players) {
            System.out.println("Player " + player.getName() + " is in the lobby");
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
            //String choice = reader.readLine();
            System.out.println("Wainting choice: ");
            String choice = Connections.receiveResponse(socket);
            System.out.println("Choice: " + choice);
            switch (choice) {
                case "login":
                    player = attemptLogin(socket);
                    break;
                case "register":
                    player = attemptRegister(socket);
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    Connections.sendRequest(socket, "Invalid choice 1");
                    break;
            }
            if (player != null){
                if (player.getCurrentGame() == -1){
                    System.out.println("Handle new queue request");
                    this.queueLock.lock();
                    boolean inQueue = this.queue.CheckIfPlayerInQueueAndUpdate(player, socket);
                    this.queueLock.unlock();
                    if (!inQueue){
                        System.out.println("Player " + player.getName() + " is not in the queue");
                        Connections.sendRequest(socket, "Type of game:");
                        AddToqueue( player, socket);
                    }else{
                        System.out.println("Player " + player.getName() + " is in the queue");
                        Connections.sendRequest(socket, "Player Reconnected to queue");
                    }
                } else {
                    System.out.println("Handle reconnection");
                    System.out.println("Player " + player.getName() + " is in the lobby");
                }
                System.out.println("Player " + player.getName() + " connected");
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
    void AddToqueue(Player player, SocketChannel socket) {
        ArrayList<Pair<Player, SocketChannel>> players = null;
            System.out.println("Adding player to queue");
            String reading = Connections.receiveResponse(socket);
            System.out.println("Reading :" + reading);
            switch (reading){
                case "RANKED":
                    this.queueLock.lock();
                    queue.AddPlayerToRanked(player, socket);
                    players =  queue.getRankedGamePlayers();
                    this.queueLock.unlock();
                    break;
                case "NORMAL":
                    System.out.println("Adding player to casual queue");
                    this.queueLock.lock();
                    this.queue.AddPlayerToCasual(player, socket);
                    players = queue.getCasualGamePlayers();
                    this.queueLock.unlock();
                    break;
                default:
                    Connections.sendRequest(socket, "Invalid choice");
                    break;
            }
            if (players != null){
                System.out.println("Game is starting with: " + players.size());
                this.queueLock.lock();
                Game game = new Game(players,database,databaseLock,timeLock);
                this.threadsGame.execute(game);
                this.queueLock.unlock();
                
            }

    }
    Player attemptLogin(SocketChannel socket) throws IOException {
        String username = "";
        Player player = null;
        while (true){
            username = Connections.receiveResponse(socket);
            if(username.equals("")){
                return null;
            }
            this.databaseLock.lock();
            player = this.database.getPlayerByName(username);
            this.databaseLock.unlock();
            if (player != null){
                System.out.println("username found");
                Connections.sendRequest(socket, "username found");
                break;
            }
            Connections.sendRequest(socket, "username not found");
        }
        while (true){
            String password = Connections.receiveResponse(socket);
            this.databaseLock.lock();
            String passwordHash = this.database.getPlayerPassword(username);
            this.databaseLock.unlock();
            if(username.equals("")){
                return null;
            }
            if (password.equals(passwordHash)){
                Connections.sendRequest(socket, "password correct");
                break;
            }
            Connections.sendRequest(socket, "password incorrect");
        }
        return player;
    }
    Player attemptRegister(SocketChannel socket) throws IOException {
        String username = "";
        Player player = null;
        while (true){
            username = Connections.receiveResponse(socket);
            this.databaseLock.lock();
            player = this.database.getPlayerByName(username);
            this.databaseLock.unlock();
            if (Objects.isNull(player)){
                Connections.sendRequest(socket, "username not found");
                break;
            }
            Connections.sendRequest(socket,"username found");
        }
        player = new Player(1, username, 1000.0, -1, 0.0, 1.0);
        String password = Connections.receiveResponse(socket);
        this.databaseLock.lock();
        this.database.createPlayer(player, password);
        this.databaseLock.unlock();
        Connections.sendRequest(socket, "register successful");
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
