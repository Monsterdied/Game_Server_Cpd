import java.nio.channels.SocketChannel;
import java.io.*;
import java.net.*;
import java.util.*; 
public class Queue{
    private List<Pair<Player, SocketChannel>> rankedQueue;
    private List<Pair<Player, SocketChannel>> casualQueue;
    private final int players_per_game;
    private final double Max_Disparity_Money;
    public Queue(int players_per_game , int Max_Disparity_Money) {
        this.Max_Disparity_Money = Max_Disparity_Money;
        this.players_per_game = players_per_game;
        this.rankedQueue = new ArrayList<Pair<Player, SocketChannel>>();
        this.casualQueue = new ArrayList<Pair<Player, SocketChannel>>();
    }
    public boolean CheckIfPlayerInQueueAndUpdate(Player player , SocketChannel socket) {
        for (int i = 0; i < this.rankedQueue.size(); i++){
            Pair<Player, SocketChannel> p = this.rankedQueue.get(i);
            if (p.getKey().getName().equals(player.getName())) {
                this.rankedQueue.set(i, new Pair<Player, SocketChannel>(player, socket));
                return true;
            }
        }
        for (int i = 0; i < this.casualQueue.size(); i++){
            Pair<Player, SocketChannel> p = this.casualQueue.get(i);
            if (p.getKey().getName().equals(player.getName())) {
                this.casualQueue.set(i, new Pair<Player, SocketChannel>(player, socket));
                return true;
            }
        }
        return false;
    }
    public void sortRankedQueue() {
        Collections.sort(this.rankedQueue , new Comparator<Pair<Player, SocketChannel>>() {
            @Override
            public int compare(Pair<Player, SocketChannel> p1, Pair<Player, SocketChannel> p2) {
                return (int) (p1.getKey().getMoney() - p2.getKey().getMoney());
            }
        });
    }
    public void AddPlayerToRanked(Player player , SocketChannel socket) {
        this.rankedQueue.add(new Pair<Player, SocketChannel>(player, socket));
        sortRankedQueue();
    }

    public void AddPlayerToCasual(Player player , SocketChannel socket) { 
        this.casualQueue.add(new Pair<Player, SocketChannel>(player, socket));
    }

    public ArrayList<Pair<Player, SocketChannel>> getRankedGamePlayers() {
        //TODO need to make sure that the players are still there make a ping function
        for (int i = 0; i < this.rankedQueue.size() - this.players_per_game + 1; i++){
            Pair<Player, SocketChannel> first = this.rankedQueue.get(i);
            Pair<Player, SocketChannel> last = this.rankedQueue.get(i+players_per_game - 1);
            if (last.getKey().getMoney() - first.getKey().getMoney() <= Max_Disparity_Money) {
                ArrayList<Pair<Player, SocketChannel>> players = new ArrayList<Pair<Player, SocketChannel>>();
                for (int j = 0; j < this.players_per_game; j++){
                    players.add(this.rankedQueue.get(i+j));
                }
                this.rankedQueue.subList(i, i+players_per_game).clear();
                return players;
            }
        }
        return null;
    }
    public ArrayList<Pair<Player, SocketChannel>> getCasualGamePlayers() {
        // #TODO need to make sure that the players are still there make a ping function
        System.out.println("Casual Queue Size: " + this.casualQueue.size());
        if(this.casualQueue.size() >= this.players_per_game) {
            System.out.println("Casual Game Started");
            ArrayList<Pair<Player, SocketChannel>> players = new ArrayList<Pair<Player, SocketChannel>>();
            for (int i = 0; i < this.players_per_game; i++){
                players.add(this.casualQueue.get(i));
            }
            this.casualQueue = new ArrayList<Pair<Player, SocketChannel>>();
            return players;
        }
        return null;
    }


}