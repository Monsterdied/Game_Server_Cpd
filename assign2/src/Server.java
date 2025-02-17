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
        this.databaseLock.lock();
        List<Player> players = database.getPlayersInGame(0);
        this.databaseLock.unlock();
        for (Player player : players) {
            System.out.println("Player " + player.getName() + " is in the lobby");
        }
    }
    
    //Start Server
    public void start() throws IOException {

        this.serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(this.port));
        System.out.println("\nServer started on port " + this.port + " with mode " + this.mode + "\n");

    }

    //Run Server
    public void run() throws IOException {
        Runnable task = () -> {
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
        while (true){
            System.out.println("Current Queue Size: " + this.queue.rankedQueue.size() + " " + this.queue.casualQueue.size());
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                System.out.println("Server Error: " + e.getMessage());
            }

        }
    }
    public void acceptConnections() throws IOException {
            SocketChannel socket = this.serverSocket.accept();
            Runnable client = () -> {
                try {
                    handleClient(socket);
                } catch (IOException e) {
                    System.out.println("Server Error: " + e.getMessage());
                }
            };
            this.threadsPlayers.execute(client);
            System.out.println("Connection accepted");

    }
    public void handleClient(SocketChannel socket) throws IOException {
        try {
            Player player = null;
            System.out.println("\nNew Connection Received, Handling client...");
            socket.socket().setSoTimeout(this.TIMEOUT);
            String choice = Connections.receiveResponse(socket);
            System.out.println("Received Choice from the Client. The selected Choice was: " + choice + "!\n");
            switch (choice) {
                case "login":
                    System.out.println("Client chose to login!\n");
                    Connections.sendRequest(socket, "Login");
                    System.out.println("Ateempting loging!\n");
                    player = attemptLogin(socket);
                    break;
                case "register":
                    Connections.sendRequest(socket, "Register");
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
                //if (player.getCurrentGame() == -1){
                System.out.println("\nPlayer "+ player.getName()  +" is choosing the type of queue!");
                this.queueLock.lock();
                boolean inQueue = this.queue.CheckIfPlayerInQueueAndUpdate(player, socket);
                this.queueLock.unlock();
                if (!inQueue){
                    Connections.sendRequest(socket, "Type of game:");
                    AddToqueue( player, socket);                  
                }else{
                    System.out.println("Player " + player.getName() + " is already in the queue");
                    Connections.sendRequest(socket, "Player Reconnected to queue");
                }
                /*} else {
                    System.out.println("Player " + player.getName() + " is in the game");
                    databaseLock.lock();
                    player.setCurrentGame(-1);
                    database.updatePlayer(player);
                    databaseLock.unlock();
                    //System.out.println("Handle reconnection");
                    //System.out.println("Player " + player.getName() + " is in the lobby");
                }*/
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
    void AddToqueue(Player player, SocketChannel socket) {
        ArrayList<Pair<Player, SocketChannel>> players = null;
            String reading = Connections.receiveResponse(socket);
            System.out.println("\nThe Player " + player.getName() + " choose the " + reading + " queue!");
            switch (reading){
                case "RANKED":
                    System.out.println("\nAdding Player " + player.getName() + " to ranked queue!");
                    this.queueLock.lock();
                    this.queue.AddPlayerToRanked(player, socket);
                    players =  this.queue.getRankedGamePlayers();
                    this.queueLock.unlock();
                    break;
                case "NORMAL":
                    System.out.println("\nAdding Player " + player.getName() + " to normal queue!");
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
                System.out.println("Game is starting with " + players.size() + " players:");
                for (Pair<Player, SocketChannel> p : players){
                    System.out.println("Player " + p.getKey().getName() + " with money " + p.getKey().getMoney() + " !");
                }
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
            System.out.println("Waiting for the Client's Username: ");
            username = Connections.receiveResponse(socket);
            if(username.equals("") || username == null){
                System.out.println("Client's Username was empty!\n");
                return null;
            }
            this.databaseLock.lock();
            player = this.database.getPlayerByName(username);
            this.databaseLock.unlock();
            if (player != null){
                System.out.println("\nClient's Username was found in the database: " + username + "!\n");
                Connections.sendRequest(socket, "username found");
                break;
            }
            Connections.sendRequest(socket, "username not found");
        }
        while (true){
            String password = Connections.receiveResponse(socket);
            if(password.equals("") || password == null){
                return null;
            }
            this.databaseLock.lock();
            String passwordHash = this.database.getPlayerPassword(username);
            this.databaseLock.unlock();
            if (password.equals(passwordHash)){
                System.out.println("Client's Password was correct!\n");
                Connections.sendRequest(socket, "password correct");
                break;
            }
            Connections.sendRequest(socket, "password incorrect");
        }
        String answer = Connections.receiveResponse(socket);//Ack login
        return player;
    }
    Player attemptRegister(SocketChannel socket) throws IOException {
        String username = "";
        Player player = null;
        while (true){
            username = Connections.receiveResponse(socket);
            if(username.equals("") || username == null){
                return null;
            }
            this.databaseLock.lock();
            player = this.database.getPlayerByName(username);
            this.databaseLock.unlock();
            if (Objects.isNull(player)){
                Connections.sendRequest(socket, "username not found");
                System.out.println("\nClient defined it's new username as: " + username + "!\n");
                break;
            }
            Connections.sendRequest(socket,"username found");
        }
        player = new Player(1, username, 1000.0, -1, 0.0, 1.0);
        String password = Connections.receiveResponse(socket);
        System.out.println("Client defined it's new password!\n");
        this.databaseLock.lock();
        this.database.createPlayer(player, password);
        this.databaseLock.unlock();
        Connections.sendRequest(socket, "register successful");
        String answer = Connections.receiveResponse(socket);//Ack register
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
