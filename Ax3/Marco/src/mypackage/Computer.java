package mypackage;
import java.util.concurrent.locks.*;

public class Computer {
    final Lock computerLock;
    Boolean occupied;

    public Computer() {
        occupied = false;
        computerLock = new ReentrantLock();
    }
}
