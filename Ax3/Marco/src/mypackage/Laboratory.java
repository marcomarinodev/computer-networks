package mypackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.*;

public class Laboratory {

    final int size;
    int availableWorkstations;
    final Lock roomLock;
    public Computer[] computers;

    final Condition studCond;
    final Condition underCond;
    final Condition profCond;

    int profsInPending;
    int undergradInPending;
    int studentsInPending;

    final int nProf;
    final int nUnder;
    final int nStud;

    List<Runnable> usersRunnable;
    final int k;

    private void initLab() {
        for (int i = 0; i < size; i++) {
            computers[i] = new Computer();
        }
    }

    public Laboratory(int size, int nUsers, int k, int nProf, int nUnder, int nStud) {
        this.size = size;
        availableWorkstations = size;
        roomLock = new ReentrantLock();
        computers = new Computer[size];
        initLab();

        studCond = roomLock.newCondition();
        underCond = roomLock.newCondition();
        profCond = roomLock.newCondition();

        profsInPending = 0;
        undergradInPending = 0;
        studentsInPending = 0;

        usersRunnable = new ArrayList<>(nUsers);
        this.k = k;

        this.nProf = nProf;
        this.nUnder = nUnder;
        this.nStud = nStud;
    }

    /**
     * This function shuffle up the usersRunnable queue
     * @throws InterruptedException
     */
    public void executeUsers() throws InterruptedException {
        
        for (int i = 0; i < nProf; i++) {
            usersRunnable.add(new ProfRunnable(i * 2, 1000, 5000, this, -1));
        }

        for (int i = 0; i < nUnder; i++) {
            usersRunnable.add(new UndergraduateRunnable(i * 3, 1000, 2500, this, 4));
        }

        for (int i = 0; i < nStud; i++) {
            usersRunnable.add(new StudentRunnable(i * 4, 500, 2000, this, -1));
        }

        Collections.shuffle(usersRunnable);

        List<Thread> threads = new ArrayList<>(usersRunnable.size());

        for (int i = 0; i < k; i++) {
            for (Runnable userRunnable : usersRunnable) {
                Thread userThread = new Thread(userRunnable);
                threads.add(userThread);
    
                userThread.start();
    
                ConcurrentUtils.sleep(500);
            }
        }

    }

    public void studentGet(StudentRunnable student, String startMex, String endMex, int ms) throws InterruptedException {

        int chosenIndex = -1;

        System.out.println(student.getId() + " Student get");

        // read laboratory workstations in order to know
        // which workstation is free
        roomLock.lock();
        try {

            System.out.println(student.getId() + " Student access LOCK");

            // not available workstations
            while (availableWorkstations == 0 || profsInPending > 0) {
                studentsInPending++;
                studCond.await();   
                studentsInPending--;          
            }

            // func to get free workstation position
            for (int i = 0; i < computers.length; i++) {
                if (!computers[i].occupied) {
                    chosenIndex = i;
                    System.out.println(student.getId() + " Chosen Index for ["  + chosenIndex + "]");
                    break;
                }
            }

            computers[chosenIndex].computerLock.lock();
            computers[chosenIndex].occupied = true;

            System.out.println(student.getId() + " Student studying...");

            availableWorkstations--;

        } finally {
            roomLock.unlock();
        }

        ConcurrentUtils.sleep(ms);

        // asks for lock
        roomLock.lock();
        try {
            computers[chosenIndex].computerLock.unlock();
            computers[chosenIndex].occupied = false;
        
            availableWorkstations++;

            System.out.println("profs in queue? ->" + (profsInPending != 0));

            if (profsInPending != 0)
                profCond.signal();
            else if (undergradInPending != 0)
                underCond.signalAll();
            else if (studentsInPending != 0)
                studCond.signalAll();

            System.out.println(student.getId() + " Student done");

        } finally {
            roomLock.unlock();
        }
    }

    /**
     * If this function is called, undergraduate tries to get a certain pc
     * in the room.
     * @param index: computer index
     */
    public void undergraduateGet(UndergraduateRunnable under, int index, String startMex, String endMex, int ms) throws InterruptedException {

        System.out.println(under.getId() + " Undergrad get");

        // read laboratory workstations in order to know
        // which workstation is free
        roomLock.lock();
        try {

            // not available workstations
            while (availableWorkstations == 0 || profsInPending > 0 || computers[index].occupied) {
                System.out.println(under.getId() + " Undergrad [DENIED] access LOCK");
                undergradInPending++;
                underCond.await();  
                undergradInPending--;           
            }

            System.out.println(under.getId() + " Undergrad access LOCK");

            computers[index].computerLock.lock();
            try {

                System.out.println(under.getId() + " Undergrad access PC LOCK");

                computers[index].occupied = true;
    
                System.out.println(under.getId() + " Undergrad writing...");
            } finally {
                computers[index].computerLock.unlock();
            }
            
            availableWorkstations--;

        } finally {
            System.out.println(under.getId() + " Undergrad unlocked LOCK");
            roomLock.unlock();
        }

        ConcurrentUtils.sleep(ms);

        System.out.println(under.getId() + " Undergrad almost finished...");

        // asks for lock
        roomLock.lock();

        try {

            computers[index].occupied = false;
        
            availableWorkstations++;

            System.out.println("profs in queue? ->" + (profsInPending != 0));

            if (profsInPending != 0)
                profCond.signal();
            else if (undergradInPending != 0) {
                underCond.signalAll();
            }
            else if (studentsInPending != 0)
                studCond.signalAll();

            System.out.println(under.getId() + " Undergrad done");

        } finally {
            System.out.println(under.getId() + " Undergrad unlocked LOCK2");
            roomLock.unlock();
        }
    }

    public void profGet(ProfRunnable prof, String startMessage, String endMessage, int ms) throws InterruptedException {

        System.out.println(prof.getId() + " Prof get");

        // read laboratory workstations in order to know
        // which workstation is free
        roomLock.lock();
        try {

            System.out.println(prof.getId() + " Prof access LOCK");

            // not available workstations
            while (availableWorkstations != size) {
                System.out.println(prof.getId() + " Prof stopped because not all workstations are available");
                profsInPending++;
                profCond.await(); 
                profsInPending--;            
            }

            for (Computer pc : this.computers) {
                pc.computerLock.lock();
                pc.occupied = true;
            }

            System.out.println(prof.getId() + " Prof networking...");

            availableWorkstations = 0;

        } finally {
            roomLock.unlock();
        }

        ConcurrentUtils.sleep(ms);

        System.out.println(prof.getId() + " Prof almost finished...");

        // asks for lock
        roomLock.lock();
        try {
            for (Computer pc : this.computers) {
                pc.computerLock.unlock();
                pc.occupied = false;
            }
        
            availableWorkstations = size;

            System.out.println("profs in queue? ->" + (profsInPending != 0));

            if (profsInPending != 0)
                profCond.signal();
            else if (undergradInPending != 0)
                underCond.signalAll();
            else if (studentsInPending != 0)
                studCond.signalAll();

            System.out.println(prof.getId() + " Prof done");

        } finally {
            System.out.println(prof.getId() + " Prof unlocking...");
            roomLock.unlock();
        }

    }
    
}
