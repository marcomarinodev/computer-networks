package mypackage;
import java.util.concurrent.locks.*;

public class Computer {
    final Lock computerLock;
    final Condition notOccupied;
    Boolean occupied;

    public Computer() {
        occupied = false;
        computerLock = new ReentrantLock();
        notOccupied = computerLock.newCondition();
    }
}
