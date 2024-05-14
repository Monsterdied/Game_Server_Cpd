import java.util.List;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;


public class Game implements Runnable{

    private ArrayList<Pair<Player,SocketChannel>> players;
    private Integer gameID;
    private Date crashedTime = null;

    
    public double multiplier;
    private Random random;
    public int rounds = 3;
    public String requestString = "";

    public Game(ArrayList<Pair<Player,SocketChannel>> players, Database database) {
        this.players = players;
        this.random = new Random();
    }


    public Date getCrashedTime() {
        return this.crashedTime;
    }
    
    public void run() {
        try{
            System.out.println("Players: ");

            for (Pair<Player, SocketChannel> pair : players) {
                Player player = pair.getKey();
                System.out.println("Name: " + player.getName() + ", Money: " + player.getMoney() + ", Bet: " + player.getCurrBet() + ", Bet: " + player.getBetMultiplier());
                this.requestString += "Name: " + player.getName() + ", Money: " + player.getMoney() + ", Bet: " + player.getCurrBet() + ", Bet: " + player.getBetMultiplier();
                this.requestString += "\n";
                Connections.sendRequest(pair.getValue(), "startgame");
            }
            
            AllPlayersRequest(this.requestString);


        
            for (int i = 1; i <= rounds; i++) {
                this.requestString = "";
                System.out.println("Round " + i);
                this.requestString += "Round " + i;
                AllPlayersRequest(this.requestString + "/" + this.rounds);
                playRound();   
                Thread.sleep(500);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AllPlayersRequest(String request){
        for(Pair<Player, SocketChannel> pair : players){
            Player player = pair.getKey();
            try{
                Connections.sendRequest(pair.getValue(), request);
                System.out.println("Semding " + request);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void playRound() {
        multiplier = 1.0;


        for(Pair<Player, SocketChannel> pair : players){
            Player player = pair.getKey();

            this.askPlayerInfo(player, pair.getValue());


        }

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
        
        for(Pair<Player, SocketChannel> pair : players){
            
            Player player = pair.getKey();

            if(player.getBetMultiplier()>multiplier){
                System.out.println(player.getName() + " bet " + player.getCurrBet() + "with multiplier " + player.getBetMultiplier() + " but the multiplier was " + multiplier);
                System.out.println(player.getName() + " lost " + player.getCurrBet());
                player.setMoney(player.getMoney() - player.getCurrBet());
            }
            else if(player.getBetMultiplier()<=multiplier){
                System.out.println(player.getName() + " bet " + player.getCurrBet() + " with multiplier " + player.getBetMultiplier());
                System.out.println(player.getName() + " won " + player.getCurrBet() * player.getBetMultiplier());
                player.setMoney(player.getBetMultiplier() * player.getCurrBet() + player.getMoney());
            }
            System.out.println("Player " + player.getName() + " has " + player.getMoney() + " money");
            
        }
    }

    private void askPlayerInfo(Player player,SocketChannel socket){

        System.out.println("Sent request to " + player.getName() + " to enter new bet and multiplier");
    
        try{
        } catch (Exception e){
            e.printStackTrace();
        }

    }


}