
import java.util.List;
import java.io.*;
import java.net.*;
import java.util.Date;
public class Game {

    private List<Socket> userSockets;
    private Integer gameID;
    private Date crachedTime = null;
    public Game(int players,int gameID, List<Socket> userSockets) {
        this.gameID = gameID;
        this.userSockets = userSockets;
    }
    public void start() {

        System.out.println("Starting game with " + userSockets.size() + " players");

    }
    public Integer getGameID() {
        return this.gameID;
    }
    public Date getCrachedTime() {
        return this.crachedTime;
    }
}