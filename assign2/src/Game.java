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


public class Game implements Runnable{

    private List<Player> players;
    private Integer gameID;
    private Date crashedTime = null;

    
    public double multiplier;
    private Random random;
    public int rounds = 5;

    public Game(int gameID, List<Player> players) {
        this.gameID = gameID;
        this.players = players;
        this.random = new Random();
    }
    
    public void run() {
        try{
            System.out.println("Starting game " + gameID);
            System.out.println("Players: ");
            for (Player player : players) {
                System.out.println(player.getName());
            }

            for (int i = 0; i < rounds; i++) {
                System.out.println("Round " + i);
                multiplier = random.nextDouble() * 2;
                System.out.println("Multiplier: " + multiplier);
                for (Player player : players) {
                    player.setCurrBet(player.getCurrBet() * multiplier);
                    System.out.println(player.getName() + " bet: " + player.getCurrBet());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
            Player player1 = new Player(1,"Joao",100,-1,-1);
            Player player2 = new Player(2,"Maria",200,-1,-1);

            List<Player> players = new ArrayList<Player>();
            players.add(player1);
            players.add(player2);
            Game game = new Game(1, players);
            game.run();
    }
    
}