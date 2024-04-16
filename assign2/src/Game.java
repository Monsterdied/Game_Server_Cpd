import java.util.List;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;


public class Game {

    private List<Pair<Player, Socket>> players;
    private Integer gameID;
    private Date crashedTime = null;

    
    public double multiplier;
    private Random random;
    public int rounds;

    public Game(int gameID, List<Pair<Player,Socket>> players) {
        this.gameID = gameID;
        this.players = players;
        this.random = new Random();
    }
    
    public void start() {

        System.out.println("Starting game with " + players.size() + " players");
        for (Pair<Player,Socket> pair : players){
            Socket socket = pair.getValue();
            new Thread(() -> {
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }).start();
        }
    }
    

    public static void main(String[] args) {
        try {
            // Create some Player and Socket objects
            Player player1 = new Player(1,"Joao",100,-1,-1);
            Player player2 = new Player(2,"Maria",200,-1,-1);
            Socket socket1 = new Socket("localhost", 1234);
            Socket socket2 = new Socket("localhost", 1234);
    
            // Create Pair objects
            Pair<Player, Socket> pair1 = new Pair<>(player1, socket1);
            Pair<Player, Socket> pair2 = new Pair<>(player2, socket2);
    
            // Add the pairs to a list
            List<Pair<Player, Socket>> pairs = new ArrayList<>();
            pairs.add(pair1);
            pairs.add(pair2);
    
            // Create a Game and start it
            Game game = new Game(1, pairs);
            game.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}