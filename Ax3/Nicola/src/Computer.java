import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Computer {
    private ReentrantLock lock;
    private boolean occupied;

    public Computer(){
        lock = new ReentrantLock();
    }

    public void use() {
        lock.lock();
        occupied = true;
    }

    public void unuse() {
        lock.unlock();
        occupied = false;
    }

    public void use(long time) {
        occupied = true;
        lock.lock();

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        occupied = false;
        lock.unlock();
    }

    public boolean isOccupied() {
        return occupied;
    }

}
