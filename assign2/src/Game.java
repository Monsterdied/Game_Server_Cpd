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
    public int getGameID() {
        return gameID;
    }
    public Date getCrachedTime() {
        return crashedTime;
    }
    
    public void run() {
        try{
            System.out.println("Starting game " + gameID);
            System.out.println("Players: ");
            for (Player player : players) {
                System.out.println(player.getName() + " " + player.getMoney() + " " + player.getCurrBet() + " " + player.getBetMultiplier());
            }

            for (int i = 0; i < rounds; i++) {
                System.out.println("Round " + i);
                playRound();
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playRound() {
        multiplier = 1.0;
        while(true){
            
            multiplier += 0.1;
            System.out.println("Multiplier: " + String.format("%.1f", multiplier));
            
            if(random.nextDouble() < 0.1){
                System.out.println("Crashed!");
                break;
            }

            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        for(Player player : players){
            if(player.getBetMultiplier()>multiplier){
                System.out.println(player.getName() + " lost " + player.getCurrBet());
                player.setMoney(player.getMoney() - player.getCurrBet());
            }
            else if(player.getBetMultiplier()<=multiplier){
                System.out.println(player.getName() + " won " + player.getCurrBet());
                player.setMoney(player.getBetMultiplier() * player.getCurrBet() + player.getMoney());
            }
            System.out.println("Player " + player.getName() + " has " + player.getMoney() + " money");
            
        }
    }
    

    public static void main(String[] args) {
            Player player1 = new Player(1,"Joao",100,1,30,1.3);
            Player player2 = new Player(2,"Maria",100,1,30,1.1);

            List<Player> players = new ArrayList<Player>();
            players.add(player1);
            players.add(player2);
            Game game = new Game(1, players);
            game.run();
    }
    
}