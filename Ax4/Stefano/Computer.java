import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Computer {

    private final Lock computerLock;
    private boolean occupied;

    public Computer() {
        this.occupied = false;
        this.computerLock = new ReentrantLock();
    }

    public void startUsing() {
        computerLock.lock();
        occupied = true;
    }

    public void stopUsing() {
        occupied = false;
        computerLock.unlock();
    }

    public void use(long time) {

        computerLock.lock();
        occupied = true;

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        occupied = false;
        computerLock.unlock();

    }

    public boolean isOccupied() {
        return this.occupied;
    }

}