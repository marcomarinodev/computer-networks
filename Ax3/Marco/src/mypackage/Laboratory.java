package mypackage;

import java.util.concurrent.locks.*;

public class Laboratory {

    final int size;
    int availableWorkstations;
    final Lock roomLock;
    public Computer[] computers;
    final Condition isNotFull;

    private void initLab() {
        for (int i = 0; i < size; i++) {
            computers[i] = new Computer();
        }
    }

    public Laboratory(int size) {
        this.size = size;
        availableWorkstations = size;
        roomLock = new ReentrantLock();
        computers = new Computer[size];
        initLab();
        isNotFull = roomLock.newCondition();
    }

    /**
     * If this function is called, undergraduate tries to get a certain pc
     * in the room.
     * @param index: computer index
     */
    public void undergraduateGet(int index, String startMex, String endMex, int ms) throws InterruptedException {
        
        System.out.println("Undergraduate get");

        // reading the global lock and in particular if the room is full
        try {
            roomLock.lock();

            while (availableWorkstations == 0) {
                System.out.println("Waiting for a workstation");
                isNotFull.await();
            }

            System.out.println("I can enter the room and searching the workstation");

        } finally {
            // unlock the global lock
            roomLock.unlock();
        }

        try {
            computers[index].computerLock.lock();

            // Waiting until the desired workstation is not occupied
            while (Boolean.TRUE.equals(computers[index].occupied)) {
                System.out.println("This workstation is occupied");
                computers[index].notOccupied.await();
            }
                
            
            // set this workstation as occupied
            computers[index].occupied = true;
            availableWorkstations++;

            System.out.println(startMex);

            ConcurrentUtils.sleep(ms);

            System.out.println(endMex);

            // set this workstation as not occupied
            computers[index].occupied = false;

            // update available workstations

            // signaling the next user that needs this workstation
            computers[index].notOccupied.signal();
            

            try {
                roomLock.lock();

                System.out.println("Waking up a user (student/undergrad)");

                isNotFull.signal();
    
            } finally {
                // unlock the global lock
                roomLock.unlock();
            }

        
        } finally {

            // then unlock the workstation
            computers[index].computerLock.unlock();
        }

    }

    public void profGet(String startMessage, String endMessage, int ms) {
        return;
    }
    
}
