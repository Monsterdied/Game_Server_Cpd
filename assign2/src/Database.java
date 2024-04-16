import java.sql.*;
import java.util.*;

public class Database {
    public static Connection conn;
    public Database() {
        // Load the SQLite JDBC driver
        // Define the JDBC URL
        String url = "jdbc:sqlite:testDatabase.db";

            try (Connection conn = DriverManager.getConnection(url)) {
                this.conn = conn;
                Statement stmt = this.conn.createStatement();
                System.out.println("Connection to SQLite has been established.");
                String q1 = "select * from Player";
                ResultSet rs = stmt.executeQuery(q1);
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + ", " + rs.getString("name") + ", " + rs.getInt("money") + ", " + rs.getInt("current_game") + ", " + rs.getInt("curr_bet"));
                }
            } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public Player getPlayer(Integer id) {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select * from Player where id = " + id;
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return new Player(rs.getInt("id"), rs.getString("name"), rs.getInt("money"), rs.getInt("current_game"), rs.getInt("curr_bet"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public Player getPlayerByName(String name) {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select * from Player where name = " + name;
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return new Player(rs.getInt("id"), rs.getString("name"), rs.getInt("money"), rs.getInt("current_game"), rs.getInt("curr_bet"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public String getPlayerPassword(String name) {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select * from Player where name = " + name;
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return rs.getString("password");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public boolean createPlayer(Player player,String password) {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "insert into Player (name, money, current_game, curr_bet, password) values ('" + player.getName() + "', " + player.getMoney() + ", " + player.getCurrentGame() + ", " + player.getCurrBet() + ", '" + password + "')";
            stmt.executeUpdate(q1);
            return true;
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public boolean updatePlayer(Player player) {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "update Player set name = '" + player.getName() + "', money = " + player.getMoney() + ", current_game = " + player.getCurrentGame() + ", curr_bet = " + player.getCurrBet() + " where id = " + player.getId();
            stmt.executeUpdate(q1);
            return true;
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public boolean addPlayer(Player player) {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "insert into Player (id, name, money, current_game, curr_bet) values (" + player.getId() + ", '" + player.getName() + "', " + player.getMoney() + ", " + player.getCurrentGame() + ", " + player.getCurrBet() + ")";
            stmt.executeUpdate(q1);
            return true;
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public boolean addGame(Game game) {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "insert into Game (id, players) values (" + ((game.getGameID()== null) ? "NULL" : game.getGameID() )+ ", " + game.getCrachedTime() + ")";
            stmt.executeUpdate(q1);
            return true;
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public List<Integer> getGame(Integer id) {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select * from Game where id = " + id;
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                List<Integer> game = new ArrayList<Integer>();
                game.add(rs.getInt("id"));
                game.add(rs.getInt("crashed_time"));
                return game;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public List<Integer> getGames(){
        checkConnection();
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select * from Game";
            ResultSet rs = stmt.executeQuery(q1);
            List<Integer> games = new ArrayList<Integer>();
            while (rs.next()) {
                games.add(rs.getInt("id"));
            }
            return games;
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public void close() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public void checkConnection() {
        try {
            if (this.conn.isClosed()){
                this.conn = DriverManager.getConnection("jdbc:sqlite:testDatabase.db");
            }
        } catch (SQLException e) {
            e.printStackTrace();
    // Handle the exception
        }
    }
}
