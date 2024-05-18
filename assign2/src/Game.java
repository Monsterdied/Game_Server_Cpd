import java.util.List;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.time.*; 
import java.util.Date;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;


public class Game implements Runnable{

    private ArrayList<Pair<Player,SocketChannel>> players;
    public Integer gameID;
    private Long crashedTime = null;

    
    public double multiplier;
    private Random random;
    public int rounds = 3;
    public int curr_round = 1;
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
        this.databaseLock.lock();
        this.gameID = database.addGame();
        this.databaseLock.unlock();
    }


    public Long getCrashedTime() {
        return this.crashedTime;
    }
    
    public void run() {
        try{

            System.out.println("\n Players in this game: ");

            for (Pair<Player, SocketChannel> pair : players) {
                Player player = pair.getKey();
                // add money to the player
                player.setMoney(player.getMoney() + 30);//add 30 to the player money
                player.setCurrentGame(this.gameID);
                databaseLock.lock();
                System.out.println("Name: " + player.getName() + ", Money: " + player.getMoney() + ", Bet: " + player.getCurrBet() + ", Bet: " + player.getBetMultiplier());
                database.updatePlayer(player);
                databaseLock.unlock();
                // print players information
                this.requestString += "Name: " + player.getName() + ", Money: " + player.getMoney() + ", Bet: " + player.getCurrBet() + ", Bet: " + player.getBetMultiplier();
                this.requestString += "\n";
                Connections.sendRequest(pair.getValue(), "startgame");
            }
            
            AllPlayersRequest(this.requestString);


        
            for (;curr_round <= rounds; curr_round++) {
                this.requestString = "";
                System.out.println("Round " + curr_round);
                this.requestString += "Round " + curr_round;
                AllPlayersRequest(this.requestString + "/" + this.rounds);
                playRound();
                   
                Thread.sleep(500);
            }
            for (Pair<Player, SocketChannel> pair : players) {
                Player player = pair.getKey();
                player.setCurrentGame(-1);
                databaseLock.lock();
                database.updatePlayer(player);
                databaseLock.unlock();
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
                System.out.println("Request sent to player " + player.getName() + " with request: \n" + request);
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
        this.askPlayersInfo();
        double multiplier = this.getMultiplier();
        timeLock.lock();
        this.crashedTime = Instant.now().getEpochSecond() + (long)(multiplier * 5);//0.5 seconds per 0.1 multiplier
        long currentTime = Instant.now().getEpochSecond();
        timeLock.unlock();

        AllPlayersRequest(""+currentTime);
        long startTime = currentTime;
        boolean crashed = false;
        while(currentTime < this.crashedTime + 2){//add 2 seconds to the time to make sure request with delay are processed
            System.out.println("Current Multiplier: " +  String.format("%.1f", (currentTime - startTime)*0.2));
            HandlePlayerUpdateOfBets(multiplier);
            if(currentTime >= this.crashedTime && !crashed){
                crashed = true;
                System.out.println("Crashed!!!");
                AllPlayersRequest("Crashed!!!");
            }
            try{
                Thread.sleep(300);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            timeLock.lock();
            currentTime = Instant.now().getEpochSecond();
            timeLock.unlock();
        }

        String resume = "";
        for(Pair<Player, SocketChannel> pair : players){
            System.out.println("\n");
            Player player = pair.getKey();
            String response = "";
            if(player.getBetMultiplier()>multiplier){
                response +=player.getName() + " bet " + player.getCurrBet() + " with multiplier " + player.getBetMultiplier() + " but the multiplier was " + multiplier + "\n";
                response += player.getName() + " lost " + player.getCurrBet();
                System.out.println(response);
                player.setMoney(player.getMoney() - player.getCurrBet());
            }
            else if(player.getBetMultiplier()<=multiplier){
                response +=player.getName() + " bet " + player.getCurrBet() + " with multiplier " + player.getBetMultiplier() + "\n";
                response +=player.getName() + " won " + player.getCurrBet() * player.getBetMultiplier();
                System.out.println(response);
                player.setMoney(player.getBetMultiplier() * player.getCurrBet() + player.getMoney());
            }
            resume += response + "\n";
            System.out.println("Player " + player.getName() + " has " + player.getMoney() + " money\n");
            
        }
        AllPlayersRequest(resume);
    }
    private void HandlePlayerUpdateOfBets(double crachedMultiplier){
        ArrayList<String> responses = CheckAllPlayersResponses();
        for(int j = 0 ; j < responses.size() ; j++){
            String response = responses.get(j);
            var pair = players.get(j);
            Player player = players.get(j).getKey();
            if(response == null){
                continue;
            }else{                                        
                Double multiplier = Double.parseDouble(response);
                System.out.println("Player " + player.getName() + " updated bet multiplier to: " + multiplier);
                if(crachedMultiplier >= multiplier){
                    player.setBetMultiplier(multiplier);
                    Connections.sendRequest(pair.getValue(), "Won Bet: " + player.getCurrBet() * player.getBetMultiplier());
                }
                else{
                    player.setBetMultiplier(multiplier);
                    Connections.sendRequest(pair.getValue(), "Lost Bet, You lost: " + player.getCurrBet() + " money.");
                }
            }
            databaseLock.lock();
            database.updatePlayer(player);
            databaseLock.unlock();
        }
    }
    private double getMultiplier(){
        double multiplier = 1.0;
        while(true){    
            multiplier += 0.1;
            System.out.println("Multiplier: " + String.format("%.1f", multiplier));
            
            if(random.nextDouble() < 0.1){
                System.out.println("Crashed!");
                break;
            }
        }
        System.out.println("Multiplier: " + String.format("%.1f", multiplier));
        return multiplier;
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
                    player.setBetMultiplier(1.0);
                }else{
                    Double multiplier = Double.parseDouble(response);
                    if(multiplier < 0.0){
                        player.setBetMultiplier(1.0);
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