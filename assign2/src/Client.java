import java.security.SecureRandom;
import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicReference;
import java.nio.channels.SocketChannel;
public class Client {
    SocketChannel socket; 
    static boolean loggedIn = false;
    private int TimeRetry = 5;
    private String password;
    public String username;
    public static void main(String[] args) {
            Client client = new Client();
        
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        while (true){
            client.connect(hostname, port);
            System.out.println("Connection Lost, retrying in "+ client.TimeRetry + " seconds");
            try {
                Thread.sleep(client.TimeRetry * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (client.TimeRetry < 60){
                client.TimeRetry *= 2;
            }
        }
    }

    public void connect(String hostname, int port){
        try  {
            this.socket = SocketChannel.open();
            socket.connect(new InetSocketAddress(hostname, port));
            this.TimeRetry = 5;
            if (!loggedIn){
                
                this.welcomeMenu();
            }else{
                this.reconnection();
            }

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
    public void reconnection(){
        Connections.sendRequest(this.socket, "login");
        Connections.sendRequest(this.socket, this.username);
        System.out.println(this.username);
        try{
            String answer = Connections.receiveResponse(this.socket);
            System.out.println(answer);
            if (! answer.equals("username found")){
                System.out.println("Username not found, please try again");
                this.loggedIn = false;
                welcomeMenu();
            }
            Connections.sendRequest(this.socket,this.password);
            answer = Connections.receiveResponse(this.socket);
            if (! answer.equals("password correct")){
                System.out.println("Password Wrong, please try again");
                this.loggedIn = false;
                welcomeMenu();
            }        
            System.out.println("Relogin Successful");
        }catch(Exception e){
            System.out.println(e.getMessage());
        };
    }
    public void welcomeMenu(){
            Scanner scanner = new Scanner(System.in); 
            System.out.println("Connected to the server.");
            System.out.println("Welcome to the Crash Game!");
            System.out.println("Do u wish to login or register?");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            try{
                int choice = scanner.nextInt();
                scanner.nextLine(); //não comer /n

            switch (choice) {
                case 1:
                    attemptLogin(scanner);
                    break;
                case 2:
                    attemptRegister(scanner);
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
            choose_Queue_Type(scanner);
            }catch(Exception e){
                System.out.println(e.getMessage());
                };
            
        }
    public void WaitStartGame(){
        try{
            System.out.println("Waiting for game to start");
            String answer = Connections.receiveResponse(this.socket);
            if (answer.equals("startgame")){
                System.out.println("Game is starting");
                PlayingGame();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    static Double getChoiceWithTimeout(int range, int timeout) {
        Callable<Double> k = () -> new Scanner(System.in).nextDouble();
        Long start = System.currentTimeMillis();
        Double choice = 0.0;
        boolean valid;
        ExecutorService l = Executors.newFixedThreadPool(1);
        Future<Double> g;
        g = l.submit(k);
        done: while (System.currentTimeMillis() - start < timeout * 1000) {
            do {
                valid = true;
                if (g.isDone()) {
                    try {
                        choice = g.get();
                        if (choice >= 0 && choice <= range) {
                            break done;
                        } else {
                            throw new IllegalArgumentException();
                        }
                    } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
                        System.out.println("Wrong choice, you have to pick an integer between 0 - " + range);
                        g = l.submit(k);
                        valid = false;
                    }
                }
            } while (!valid);
        }

        g.cancel(true);
        return choice;
    }
    public void handleBetAndMultiplierIo(){
        try{
            System.out.println("Playing Round");
            String answer = Connections.receiveResponse(this.socket);
            System.out.println("Server: " + answer);
            System.out.print("Enter your bet: ");
            int playerBet = getChoiceWithTimeout(1000000, 10).intValue();
            System.out.println("Player Bet: " + playerBet);
            Connections.sendRequest(this.socket,String.valueOf(playerBet));
            System.out.println("Bet Sent Waiting For Multiplier response");
            answer = Connections.receiveResponse(this.socket);
            if(answer.startsWith("Selected bet: ")){
                System.out.println("Server: " + answer);
                System.out.print("Select multiplier: ");
                double playerMultiplier = getChoiceWithTimeout(1000000, 10);
                System.out.println("Player Multiplier: " + playerMultiplier);
                Connections.sendRequest(this.socket,String.valueOf(playerMultiplier));
                answer = Connections.receiveResponse(this.socket);
                System.out.println("Server: " + answer);
            }else{
                System.out.println("Server: " + answer);
                System.out.println("Invalid Bet, please try again Next Round");
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void PlayRound(){
        handleBetAndMultiplierIo();
        System.out.println("Round is begining Client Status");
        HandleMidRound();
        HandleEndRound();

    }
    public void HandleEndRound(){
        String answer = Connections.receiveResponse(this.socket);
        System.out.println(answer);
        }

    public void HandleMidRound() {
        String startString = Connections.receiveResponse(this.socket);
        System.out.println(startString);
        long startTime =Long.parseLong(startString);
        long timeSinceStart = Instant.now().getEpochSecond() - startTime;
        System.out.println("Time since start: " + timeSinceStart);
        Thread.Builder builder = Thread.ofVirtual();
        AtomicReference<String> input = new AtomicReference<>("Not Y");
        Thread t = builder.start(() -> readInput(input));
        boolean bailed = false;
        while (true) {
            String response = Connections.hasResponse(this.socket);
            if (response != null) {
                System.out.println("");
                if(response.equals("Crashed!!!")){
                    System.out.println("crashed");
                    break;
                }else if(response.startsWith("Won Bet: ")){
                    System.out.println(response);
                }else if(response.startsWith("Lost Bet,")){
                    System.out.println(response);
                }else if(response.startsWith("Round Ended")){
                    System.out.println(response);
                    break;// TODO ONLY BREAK IF ROUND ENDED
                }
            }
            timeSinceStart = Instant.now().getEpochSecond() - startTime;
            double currMultiplier = timeSinceStart*0.2 + 1;
            
            // Print status update with backspace to overwrite previous characters
            if(!bailed){
                System.out.print("\r" +" "+ String.format("%,.2f", currMultiplier) + "Select Y to Bail:");
            }else{
                System.out.print("\r" +" "+ String.format("%,.2f", currMultiplier) + "Bailed");
            }
            // Read a single character
            if(input.get().equals("Y") && !bailed){
                bailed = true;
                Connections.sendRequest(this.socket,"" + currMultiplier);
            }
            try {
                Thread.sleep(400); // Sleep for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t.interrupt();
        System.out.println(); // Print newline after finishing
    }
    private static void readInput(AtomicReference<String> message) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
            // Update message based on user input (replace with your logic)
                if (input.equals("Y")) {
                    System.out.println("You pressed Y");
                    message.set("Y");
                    break;
                }else {
                    System.out.println("Invalid input");
                }
            }
        }
    }
    public void PlayingGame(){
        try{
            System.out.println("Game Started");
            
            String answerListPlayers = Connections.receiveResponse(this.socket);
            System.out.println("\nList of Players: \n" + answerListPlayers);

            int rounds = 3;
            for(int i = 1; i <= rounds; i++){
                String answerRound = Connections.receiveResponse(this.socket);
                System.out.println(answerRound);
                PlayRound();
            }

            while (true){

            }
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void attemptLogin(Scanner scanner) throws Exception{
        System.out.println("User Sent login request to server.");
        Connections.sendRequest(this.socket,"login");
        while (true){
            System.out.print("Enter your Username: ");
            String username = scanner.nextLine();
                Connections.sendRequest(this.socket,username);
                System.out.println("Username Selected is " + username);
                String answer = Connections.receiveResponse(this.socket);
                if (answer.equals("username found")){
                    this.username = username;
                    break;
                }
                System.out.println("Username not found, please try again");
            }
        while (true){
            System.out.print("Enter your Password: ");
            String password = scanner.nextLine();
            String hashedPassword = hashPassword(password);
            Connections.sendRequest(this.socket,hashedPassword);
            String answer = Connections.receiveResponse(this.socket);
            if (answer.equals("password correct")){
                this.password = hashedPassword;
                this.loggedIn = true;
                break;
            }
            System.out.println("Wrong Password. Please retry.");
        }
        System.out.println("Login Successful");
    }
    public void choose_Queue_Type(Scanner scanner) throws Exception{
        String response = Connections.receiveResponse(this.socket);
        System.out.println(response);
        switch(response){
            case "Type of game:":
                while (true){
                    System.out.println("Choose the type of queue you want to join");
                    System.out.println("1. Normal Queue (Player's rank doesn't matter)");
                    System.out.println("2. Ranked Queue (Player's rank matters)");
                    System.out.println("3. Exit");
                    String choice = scanner.nextLine();
                    if(choice.equals("1")){
                        Connections.sendRequest(this.socket,"NORMAL");
                        break;
                    }
                    if(choice.equals("2")){
                        Connections.sendRequest(this.socket,"RANKED");
                        break;
                    }
                    if(choice.equals("3")){
                        System.exit(0);
                    }
                    System.out.println("Invalid choice");
                }
                break;
            case "RANKED Reconnect":
                //TODO
                System.out.println("Reconnecting to Ranked Queue");
                break;
            case "NORMAL Reconnect":
                //TODO
                System.out.println("Reconnecting to Normal Queue");
                break;
            default:
                System.out.println("Invalid choice:" + response);
                break;
        }
        WaitStartGame();


    }
    public void attemptRegister(Scanner scanner) throws Exception{
        System.out.println("Sent Register request to server");
        Connections.sendRequest(this.socket,"register");
        while (true){
            System.out.print("Enter your Username: ");
            String username = scanner.nextLine();
                Connections.sendRequest(this.socket,username);
                String answer = Connections.receiveResponse(this.socket);
                if (answer.equals("username not found")){
                    this.username = username;
                    break;
                }
                System.out.println("Username found, please try again");
            }
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);
        Connections.sendRequest(this.socket,hashedPassword);
        String answer = Connections.receiveResponse(this.socket);
        if (answer.equals("register successful")){
            this.password = hashedPassword;
            System.out.println("Register Successful");
        }
        this.loggedIn = true;
    }
    public String hashPassword(String password){
        try {
            // Create a MessageDigest object for SHA-256 hashing
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Get the bytes of the password
            byte[] passwordBytes = password.getBytes();

            // Update the digest with the password bytes
            byte[] hashedBytes = digest.digest(passwordBytes);

            // Convert the hashed bytes to hexadecimal representation
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Handle the error appropriately
        }
    } 
}    

