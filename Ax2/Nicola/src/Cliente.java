import java.util.concurrent.ThreadLocalRandom;

public class Cliente implements Runnable{
    public Cliente() {

    }

    public void run() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 501));
            System.out.println("Thread " + Thread.currentThread().getName() + " has finished");
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getName() + " was interrupted while sleeping");
            e.printStackTrace();
        }
    }
}
