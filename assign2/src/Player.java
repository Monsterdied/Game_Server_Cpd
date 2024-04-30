public class Player{
    private int id;
    private String name;
    private double money;
    private int current_game;
    private double curr_bet;
    private double bet_multiplier;

    public Player(int id, String name, double money, int current_game, double curr_bet, double bet_multiplier) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.current_game = current_game;
        this.curr_bet = curr_bet;
        this.bet_multiplier = bet_multiplier;
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
        if(curr_bet > this.money){
            this.curr_bet = this.money;
        }
        else{
            this.curr_bet = curr_bet;
        }
    }

    public double getBetMultiplier() {
        return this.bet_multiplier;
    }

    public void setBetMultiplier(double bet_multiplier) {
        this.bet_multiplier = bet_multiplier;
    }
}