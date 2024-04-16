import java.security.SecureRandom;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Client {
    Socket socket; 
    PrintWriter writer;
    BufferedReader reader;
    public static void main(String[] args) {
        Client client = new Client();
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        try  {
            client.socket = new Socket(hostname, port);
            OutputStream output = client.socket.getOutputStream();
            client.writer = new PrintWriter(output, true);
            InputStream input = client.socket.getInputStream();
            client.reader = new BufferedReader(new InputStreamReader(input));
            client.welcomeMenu();
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
        
        
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
            }catch(Exception e){
                System.out.println(e.getMessage());
                };
        }
    public void attemptLogin(Scanner scanner) throws Exception{
        System.out.println("Sent login request to server");
        writer.println("login");
        while (true){
            System.out.print("Enter your Username: ");
            String username = scanner.nextLine();
                writer.println(username);
                String answer = reader.readLine();
                if (answer.equals("username found")){
                    break;
                }
                System.out.println("Username not found, please try again");
            }
        while (true){
            System.out.print("Enter your Password: ");
            String password = scanner.nextLine();
            writer.println(hashPassword(password));
            String answer = reader.readLine();
            if (answer.equals("password correct")){
                break;
            }
            System.out.println("Answer Wrong retry :");
        }
    }
    public void attemptRegister(Scanner scanner) throws Exception{
        System.out.println("Sent Register request to server");
        writer.println("register");
        while (true){
            System.out.print("Enter your Username: ");
            String username = scanner.nextLine();
                writer.println(username);
                String answer = reader.readLine();
                if (answer.equals("username not found")){
                    break;
                }
                System.out.println("Username found, please try again");
            }
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();
        writer.println(hashPassword(password));
        String answer = reader.readLine();
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

