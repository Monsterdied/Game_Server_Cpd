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
    private ReentrantLock databaseLock;
    private ReentrantLock timeLock;
    private Database database;
    public Game(ArrayList<Pair<Player,SocketChannel>> players, Database database,ReentrantLock databaseLock,ReentrantLock timeLock) {
        this.database = database;
        this.databaseLock = databaseLock;
        this.timeLock = timeLock;
        this.players = players;
        this.random = new Random();
    }


    public Date getCrashedTime() {
        return this.crashedTime;
    }
    
    public void run() {
        try{
            for(Pair<Player, SocketChannel> pair : players){
                Player player = pair.getKey();
                player.setMoney(player.getMoney() + 30);//add 30 to the player money
                databaseLock.lock();
                database.updatePlayer(player);
                databaseLock.unlock();
            }
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
                System.out.println("Sending Request" + request);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
        //checks all players response and return a list of string where nulls are not responded players
    public ArrayList<String> CheckAllPlayersResponses(){
        ArrayList<String> responses = new ArrayList<String>();
        for(Pair<Player, SocketChannel> pair : players){
            Player player = pair.getKey();
            try{
                String response = Connections.hasResponse(pair.getValue());
                responses.add(response);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return responses;
    }
    //checks all players response an number defined by tries and sleeps for sleepTime between the tries this function uses CheckAllPlayersResponses
    /*public void AllPlayersResponseWithTries(int sleepTime,int tries){
        for(int i = 0 ; i < tries ; i++){
            ArrayList<String> responses = CheckAllPlayersResponses("bet Ammount");
            for(int j = 0 ; j < responses.size() ; j++){
                String response = responses.get(j);
                if(response == null){
                    continue;
                }
                Player player = players.get(j).getKey();
                int bet = Integer.parseInt(response);
                if(player.ge tMoney() < bet){
                    player.setCurrBet(player.getMoney());
                }
                else{
                    player.setCurrBet(bet);
                }
                Thread.sleep(sleepTime);
            }
        }
    }*/

    public void playRound() {
        multiplier = 1.0;
        this.askPlayersInfo();


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

    private void askPlayersInfo(){
        try{
            AllPlayersRequest("bet Ammount");
            Thread.sleep(10000);//wait 10 secs for the responses
            ArrayList<String> responses = CheckAllPlayersResponses();
            for(int j = 0 ; j < responses.size() ; j++){
                String response = responses.get(j);
                var pair = players.get(j);
                Player player = players.get(j).getKey();
                if(response == null){
                    player.setCurrBet(0);
                    Connections.sendRequest(pair.getValue(), "No bet selected.");
                }else{                                        
                    int bet = Integer.parseInt(response);
                    if(player.getMoney() < bet){
                        player.setCurrBet(0);
                        Connections.sendRequest(pair.getValue(), "Not enougth money to bet: " + bet+" User has: "+player.getMoney()+" money.");
                    }
                    else{
                        player.setCurrBet(bet);
                        Connections.sendRequest(pair.getValue(), "Selected bet: " + player.getCurrBet());
                    }
                }
                databaseLock.lock();
                database.updatePlayer(player);
                databaseLock.unlock();
            }
            Thread.sleep(10000);// wait 10 secs for the responses

            responses = CheckAllPlayersResponses();
            for(int j = 0 ; j < responses.size() ; j++){
                String response = responses.get(j);
                Player player = players.get(j).getKey();
                if(response == null){
                    player.setBetMultiplier(0.0);
                }else{
                    int multiplier = Integer.parseInt(response);
                    if(multiplier < 0.0){
                        player.setBetMultiplier(0.0);
                    }
                    else{
                        player.setBetMultiplier(multiplier);
                    }
                }

                databaseLock.lock();
                database.updatePlayer(player);
                databaseLock.unlock();
            }
            AllPlayersRequest("Round about to begin");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}