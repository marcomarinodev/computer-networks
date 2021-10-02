package mypackage;

import java.util.concurrent.locks.*;

public class Laboratory {

    final int size;
    int availableRooms;
    final Lock roomLock;
    public Computer[] computers;
    final Condition isEmpty;


    public Laboratory(int size) {
        this.size = size;
        availableRooms = 0;
        roomLock = new ReentrantLock();
        computers = new Computer[size];
        isEmpty = roomLock.newCondition();
    }

    /**
     * If this function is called, a user tries to get a certain pc
     * in the room.
     * @param index: computer index
     */
    public int get(int index) throws InterruptedException {
        computers[index].computerLock.lock();

        try {
            while (Boolean.TRUE.equals(computers[index].occupied))
                computers[index].notOccupied.await();
            
            computers[index].occupied = true;
            
            return index;
        
        } finally {
            computers[index].computerLock.unlock();
        }

    }
    
}
