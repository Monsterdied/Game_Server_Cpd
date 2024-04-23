import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

class Tester {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        ThreadLocal<Client> clientLocal = new ThreadLocal<>();
        for (int i = 5; i < 30; i++) {
            int idFinal = i;
            //System.out.println("Thread " + idFinal + " is running");
            Runnable task = () -> {
                Client client = new Client("player" + idFinal, "password" + idFinal, false, false);
                client.connect("localhost", 8000);
                System.out.println("Thread " + idFinal + " is running");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread " + client.username + " is done");
                //client.main(new String[]{"localhost", "8000"});

            };
            executor.execute(task);

        }
        
        System.out.println("Hello World");
    }
}