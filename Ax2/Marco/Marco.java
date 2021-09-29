import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


class ConcurrentUtils {
    
    private ConcurrentUtils() {}

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch(InterruptedException e) { App.logger.info("" + e); }
    }

}

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
        int timeInterval = ran.nextInt(1000);

        ConcurrentUtils.sleep(timeInterval);

        App.logger.info("I'm {" + id + "} client, my job last " + timeInterval);
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
        int i = 0;

        for (i = 0; i < this.peopleQuantity; i++) {
            this.insertInQueue(i);
        }

        // Set a sleep to simulate the closing of the single thread of the pool
        // ConcurrentUtils.sleep(3000);
        
        /*
        while (true) {
            this.insertInQueue(i);
            i++;
            ConcurrentUtils.sleep(1500);
        }*/
    }

    private void insertInQueue(long element) {
        try {
            this.blockingQueue.put((int) element);
        } catch(InterruptedException e) { App.logger.info("Producer was interrupted!"); }
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
        Handler handler = new FileHandler("ass.log");

        // Setting logger level and logger handler
        handler.setFormatter(new XMLFormatter());
        logger.addHandler(handler);

        // Create main queue, due to the fact that there's no limit for the entrance queue
        // I'm going to use a LinkedBlockingQueue
        BlockingQueue<Integer> entranceQueue = new LinkedBlockingQueue<>(); 

        // Create thread pool
        BlockingQueue<Runnable> doorsQueue = new ArrayBlockingQueue<>(k);
        ExecutorService service = new ThreadPoolExecutor(doorsNumber, doorsNumber, 0, TimeUnit.MILLISECONDS, doorsQueue);

        // Let's create the produce that will bring clients into our office
        Producer producer = new Producer(clientsNumber, entranceQueue);
        Thread producerThread = new Thread(producer);

        // It puts clientsNumber elements inside the queue
        // * I'm modeling the fact that each client enters the post office (entrance)
        producerThread.start();

        // Wait until the entrance has at least a person
        while (entranceQueue.isEmpty());
        logger.info("Finished Run");

        // Remaining entrance people
        while (!entranceQueue.isEmpty()) {

            if (doorsQueue.size() < k) {
                int idPerson = entranceQueue.take();
                service.execute(new PersonRunnable(idPerson));
            }
            
        }

        producerThread.join();

        // Stop accepting new tasks
        // Waiting the termination of all threads including the tasks inside the queue
        service.shutdown();

        try {
            if (!service.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
                logger.info("Shutdown time out");
                service.shutdownNow();
            }

        } catch (InterruptedException e) { service.shutdownNow(); }

        assert(doorsQueue.isEmpty());

    }
}
