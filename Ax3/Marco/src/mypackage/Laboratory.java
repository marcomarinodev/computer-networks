package mypackage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.*;

public class Laboratory {

    final int size;
    int availableWorkstations;
    final Lock roomLock;
    public Computer[] computers;
    final Condition isNotFull;
    final ReentrantReadWriteLock workstationsLock;
    final Lock roomReadLock;

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
        workstationsLock = new ReentrantReadWriteLock();
        roomReadLock = workstationsLock.readLock();
    }

    public void studentGet(String startMex, String endMex, int ms) throws InterruptedException {

        int chosenIndex = -1;

        System.out.println("Student get");

        enterLock();

        // read laboratory workstations in order to know
        // which workstation is free
        try {
            roomReadLock.lock();

            for (int i = 0; i < computers.length; i++) {

                if (!computers[i].occupied) {
                    chosenIndex = i;
                    System.out.println("Chosen Index for ["  + chosenIndex + "]");
                    break;
                }
                
            }

        } finally {
            roomReadLock.unlock();
        }

        assert(chosenIndex != -1);

        // System.out.println("Chosen Index for ["  + chosenIndex + "]");

        usePlatform(chosenIndex, startMex, endMex, ms);

    }

    /**
     * If this function is called, undergraduate tries to get a certain pc
     * in the room.
     * @param index: computer index
     */
    public void undergraduateGet(int index, String startMex, String endMex, int ms) throws InterruptedException {
        
        System.out.println("Undergraduate get");

        enterLock();

        usePlatform(index, startMex, endMex, ms);

    }

    public void profGet(String startMessage, String endMessage, int ms) throws InterruptedException {
        
        System.out.println("Professor get");

        try {
            roomLock.lock();

            while (availableWorkstations == 0) {
                System.out.println("Waiting for a workstation");
                isNotFull.await();
            }

            System.out.println(startMessage);

            ConcurrentUtils.sleep(ms);

            System.out.println(endMessage);

        } finally {
            // unlock the global lock
            roomLock.unlock();
        }

    }

    public void enterLock() throws InterruptedException {
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
    }

    public void usePlatform(int chosenIndex, String startMex, String endMex, int ms) throws InterruptedException {
        try {
            computers[chosenIndex].computerLock.lock();

            // Waiting until the desired workstation is not occupied
            while (Boolean.TRUE.equals(computers[chosenIndex].occupied)) {
                computers[chosenIndex].notOccupied.await();
            }
                
            // set this workstation as occupied
            computers[chosenIndex].occupied = true;
            availableWorkstations++;

            System.out.println(startMex);

            ConcurrentUtils.sleep(ms);

            System.out.println(endMex);

            // set this workstation as not occupied
            computers[chosenIndex].occupied = false;

            // update available workstations
            availableWorkstations--;

            // signaling the next user that needs this workstation
            computers[chosenIndex].notOccupied.signal();
            

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
            computers[chosenIndex].computerLock.unlock();
        }
    }
    
}
