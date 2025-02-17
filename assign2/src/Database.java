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
                return new Player(rs.getInt("id"), rs.getString("name"), rs.getDouble("money"), rs.getInt("current_game"), rs.getDouble("curr_bet"), rs.getDouble("bet_multiplier"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    
    public Player getPlayerByName(String name) {
        if(name == null) return null;
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select * from Player where name = '" + name + "';";
            System.out.println(q1);
            ResultSet rs = stmt.executeQuery(q1);
            System.out.println(rs);
            if (rs.next()) {
                Player player = new Player(rs.getInt("id"), rs.getString("name"), rs.getDouble("money"), rs.getInt("current_game"), rs.getDouble("curr_bet"), rs.getDouble("bet_multiplier"));
                return player;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
        public List<Player> getPlayersInGame(Integer id) {
        if(id == null) return null;
        try {
            checkConnection();
            
            Statement stmt = this.conn.createStatement();
            String q1 = "select * from Player where current_game = " +id+ ";";
            System.out.println(q1);
            ResultSet rs = stmt.executeQuery(q1);
            System.out.println(rs);
            List<Player> players = new ArrayList<Player>();
            while (rs.next()) {
                Player player = new Player(rs.getInt("id"), rs.getString("name"), rs.getDouble("money"), rs.getInt("current_game"), rs.getDouble("curr_bet"), rs.getDouble("bet_multiplier"));
                players.add(player);
            }
            return players;
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public int getLatestGameId(){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "SELECT MAX(id) AS max_id FROM Game;";
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return rs.getInt("max_id");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public String getPlayerPassword(String name) {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select * from Player where name = '" + name + "';";
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
    public int getLastPlayerId(){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "SELECT MAX(id) AS max_id FROM Player;";
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return rs.getInt("max_id");
            } else {
                return -1;
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
            System.out.println(q1);
            stmt.executeUpdate(q1);
            player.setId(getLastPlayerId());
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
    public boolean updateGame(Game game){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "update Game set SET curr_round = " + game.curr_round +" where id = " + game.gameID + ";";
            stmt.executeUpdate(q1);
            return true;
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    public int addGame() {
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "insert into Game (running) values (true)";
            stmt.executeUpdate(q1);
            return getLatestGameId();
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

    public String getName(Integer id){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select name from Player where id = " + id;
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return rs.getString("name");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    
    public boolean setName(Integer id, String name){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "update Player set name = '" + name + "' where id = " + id;
            stmt.executeUpdate(q1);
            return true;
        } catch(SQLException e) {
            throw new Error("Problem", e);
        }
    }
    
    public String getPassword(Integer id){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select password from Player where id = " + id;
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
    
    public boolean setPassword(Integer id, Integer password){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "update Player set password = " + password + " where id = " + id;
            stmt.executeUpdate(q1);
            return true;
        } catch(SQLException e) {
            throw new Error("Problem", e);
        }
    }

    public double getMoney(Integer id){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select money from Player where id = " + id;
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return rs.getInt("money");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }

    public boolean setMoney(Integer id, Double money){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "update Player set money = " + money + " where id = " + id;
            stmt.executeUpdate(q1);
            return true;
        } catch(SQLException e) {
            throw new Error("Problem", e);
        }
    }
    
    public int getPlayerGame(Integer id){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select current_game from Player where id = " + id;
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return rs.getInt("current_game");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
    
    public boolean setPlayerGame(Integer id, Integer game){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "update Player set current_game = " + game + " where id = " + id;
            stmt.executeUpdate(q1);
            return true;
        } catch(SQLException e) {
            throw new Error("Problem", e);
        }
    }

    public double getBet(Integer id){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select curr_bet from Player where id = " + id;
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return rs.getInt("curr_bet");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }

    public boolean setBet(Integer id, Double bet){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "update Player set curr_bet = " + bet + " where id = " + id;
            stmt.executeUpdate(q1);
            return true;
        } catch(SQLException e) {
            throw new Error("Problem", e);
        }
    }

    public double getMultiplier(Integer id){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "select bet_multiplier from Player where id = " + id;
            ResultSet rs = stmt.executeQuery(q1);
            if (rs.next()) {
                return rs.getInt("bet_multiplier");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }

    public boolean setMultiplier(Integer id, Double multiplier){
        try {
            checkConnection();
            Statement stmt = this.conn.createStatement();
            String q1 = "update Player set bet_multiplier = " + multiplier + " where id = " + id;
            stmt.executeUpdate(q1);
            return true;
        } catch(SQLException e) {
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
