package mypackage;

import java.util.concurrent.locks.*;

public class Laboratory {

    final int size;
    Boolean availableRooms;
    final Lock roomLock;
    public Computer[] computers;
    final Condition isEmpty;


    public Laboratory(int size) {
        this.size = size;
        availableRooms = true;
        roomLock = new ReentrantLock();
        computers = new Computer[size];
        isEmpty = roomLock.newCondition();
    }

    /**
     * If this function is called, undergraduate tries to get a certain pc
     * in the room.
     * @param index: computer index
     */
    public void undergraduateGet(int index, UndergraduateRunnable ur) throws InterruptedException {
        computers[index].computerLock.lock();

        try {
            // Waiting until the desired workstation is not occupied
            while (Boolean.TRUE.equals(computers[index].occupied))
                computers[index].notOccupied.await();
            
            // set this workstation as occupied
            computers[index].occupied = true;

            

            // set this workstation as not occupied
            computers[index].occupied = false;

            // signaling the next user that needs this workstation
            computers[index].notOccupied.signal();
            
            return;
        
        } finally {

            // then unlock the workstation
            computers[index].computerLock.unlock();
        }

    }

    public void profGet(String startMessage, String endMessage, int ms) {
        return;
    }
    
}
