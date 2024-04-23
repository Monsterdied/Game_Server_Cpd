public class Player{
    private int id;
    private String name;
    private double money;
    private int current_game;
    private double curr_bet;

    public Player(int id, String name, double money, int current_game, double curr_bet) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.current_game = current_game;
        this.curr_bet = curr_bet;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public double getMoney() {
        return this.money;
    }

    public int getCurrentGame() {
        return this.current_game;
    }

    public double getCurrBet() {
        return this.curr_bet;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setCurrentGame(int current_game) {
        this.current_game = current_game;
    }

    public void setCurrBet(double curr_bet) {
        this.curr_bet = curr_bet;
    }
}