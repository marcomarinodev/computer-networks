package mypackage;

import java.util.concurrent.locks.*;

public class Laboratory {

    final int size;
    Boolean availableRooms;
    final Lock roomLock;
    public Computer[] computers;
    final Condition isEmpty;

    private void initLab() {
        for (int i = 0; i < size; i++) {
            computers[i] = new Computer();
        }
    }

    public Laboratory(int size) {
        this.size = size;
        availableRooms = true;
        roomLock = new ReentrantLock();
        computers = new Computer[size];
        initLab();
        isEmpty = roomLock.newCondition();
    }

    /**
     * If this function is called, undergraduate tries to get a certain pc
     * in the room.
     * @param index: computer index
     */
    public void undergraduateGet(int index, Thread user) throws InterruptedException {
        computers[index].computerLock.lock();

        try {
            // Waiting until the desired workstation is not occupied
            while (Boolean.TRUE.equals(computers[index].occupied))
                computers[index].notOccupied.await();
            
            // set this workstation as occupied
            computers[index].occupied = true;

            user.start();

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
