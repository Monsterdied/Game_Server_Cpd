import java.security.SecureRandom;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            System.out.println("Connection Lost, retrying in "+ client.TimeRetry + "seconds");
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
                    System.out.println("Invalid choice");
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
            if (answer.equals("start")){
                System.out.println("Game is starting");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void attemptLogin(Scanner scanner) throws Exception{
        System.out.println("Sent login request to server");
        Connections.sendRequest(this.socket,"login");
        while (true){
            System.out.print("Enter your Username: ");
            String username = scanner.nextLine();
                Connections.sendRequest(this.socket,username);
                System.out.println(username);
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
            System.out.println(answer);
            if (answer.equals("password correct")){
                this.password = hashedPassword;
                this.loggedIn = true;
                break;
            }
            System.out.println("Answer Wrong retry :");
        }
        System.out.println("Login Successful");
    }
    public void choose_Queue_Type(Scanner scanner) throws Exception{
        System.out.println("testing");
        String response = Connections.receiveResponse(this.socket);
        System.out.println(response);
        switch(response){
            case "Type of game:":
                while (true){
                    System.out.println("Choose the type of queue you want to join");
                    System.out.println("1. Normal Queue");
                    System.out.println("2. Ranked Queue");
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

