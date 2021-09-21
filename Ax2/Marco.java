
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;

/**
 * Task defined as a person. This task will be performed by the thread pool
 */
class PersonRunnable implements Runnable {

    private int id;

    public PersonRunnable(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        // A person do something once he arrives at the door
        // ? To model the fact that a person takes different compunting time, I use random
        Random ran = new Random();
        int timeInterval = ran.nextInt(500);

        sleep(timeInterval);

        System.out.println("I'm {" + id + "} client, I'd like to...");
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch(InterruptedException e) { System.out.println(e); }
    } 
    
}

/**
 * Producer class
 */
class Producer implements Runnable {

    int peopleQuantity;
    BlockingQueue<Integer> blockingQueue = null;

    public Producer(int peopleQuantity, BlockingQueue<Integer> queue) {
        this.peopleQuantity = peopleQuantity;
        this.blockingQueue = queue;
    }

    public void run() {

        for (int i = 0; i < this.peopleQuantity; i++) {
            
            Random ran = new Random();
            int interval = ran.nextInt(20);

            this.insertInQueue(interval);

        }

        System.out.println("Finished Run");
    }

    private void insertInQueue(long element) {
        try {
            this.blockingQueue.put((int) element);
        } catch(InterruptedException e) { System.out.println("Producer was interrupted!"); }
    }

}

/**
 * Post office class.
 */
public class App {

    static Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        
        Scanner scanner = new Scanner(System.in);
        int doorsNumber = scanner.nextInt();
        int clientsNumber = scanner.nextInt();
        int k = scanner.nextInt();

        // Getting the log manager with configuration
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
        } catch (SecurityException | IOException e1) { e1.printStackTrace(); }

        // Setting logger level and logger handler
        logger.setLevel(Level.FINE);
        logger.addHandler(new ConsoleHandler());

        // TODO: Read the article about logging

        // Create main queue, due to the fact that there's no limit for the entrance queue
        // I'm going to use a LinkedBlockingQueue
        BlockingQueue<Integer> entranceQueue = new LinkedBlockingQueue<>(); 

        // Create thread pool
        ExecutorService service = Executors.newFixedThreadPool(doorsNumber);

        // Let's create the produce that will bring clients into our office
        Producer producer = new Producer(clientsNumber, entranceQueue);
        Thread producerThread = new Thread(producer);

        // It puts clientsNumber elements inside the queue
        // * I'm modeling the fact that each client enters the post office (entrance)
        producerThread.start();

        // Wait until the entrance has at least k people
        while (entranceQueue.size() < k);

        // Taking groups of k elements
        while (entranceQueue.size() >= k) {
            
            System.out.println("Picking a " + k + "-group from the entrance room");
            for (int i = 0; i < k; i++) {
                int idPerson = entranceQueue.take();
                System.out.println("* " + idPerson);
                service.execute(new PersonRunnable(idPerson));
            }
        }

        // Remaining entrance people
        while (!entranceQueue.isEmpty()) {
            int idPerson = entranceQueue.take();
            service.execute(new PersonRunnable(idPerson));
        }

        producerThread.join();

        service.shutdown();
        try {
            if (!service.awaitTermination(10000, TimeUnit.MILLISECONDS))
                service.shutdownNow();
        } catch (InterruptedException e) { service.shutdownNow(); }

    }
}
