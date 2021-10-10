package mypackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Laboratory {

    final int size;
    int availableWorkstations;
    private ArrayList<Computer> computers;

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
            computers.add(new Computer());
        }
    }

    public Laboratory(int size, int nUsers, int k, int nProf, int nUnder, int nStud) {
        this.size = size;
        availableWorkstations = size;

        computers = new ArrayList<Computer>(Collections.synchronizedList(new ArrayList<Computer>()));
        initLab();

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

        System.out.println(k);

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

        System.out.println(student.getId() + " Student get");

        boolean booked = false;
        Computer[] _computers = this.computers.toArray(new Computer[0]);

        while (!booked) {
            for (Computer c: _computers) {

                // sleep to give priority to others
                ConcurrentUtils.sleep(300);

                synchronized (c) {
                    if (!c.occupied && !booked) {
                        c.computerLock.lock();
                        c.occupied = true;

                        ConcurrentUtils.sleep(ms);

                        c.occupied = false;
                        c.computerLock.unlock();

                        System.out.println(student.getId() + " Student done");

                        booked = true;
                    }
                }
            } 
        }

    }

    /**
     * If this function is called, undergraduate tries to get a certain pc
     * in the room.
     * @param index: computer index
     */
    public void undergraduateGet(UndergraduateRunnable under, int index, String startMex, String endMex, int ms) throws InterruptedException {

        System.out.println(under.getId() + " Undergrad get");

        synchronized (computers) {
            computers.get(index).computerLock.lock();
            computers.get(index).occupied = true;

            ConcurrentUtils.sleep(ms);

            computers.get(index).occupied = false;
            computers.get(index).computerLock.unlock();

            System.out.println(under.getId() + " Undergrad done");
        }

    }

    public void profGet(ProfRunnable prof, String startMessage, String endMessage, int ms) throws InterruptedException {

        System.out.println(prof.getId() + " Prof get");

        synchronized (computers) {
            for (Computer pc : this.computers) {
                pc.computerLock.lock();
                pc.occupied = true;
            }

            ConcurrentUtils.sleep(ms);

            for (Computer pc : this.computers) {
                pc.computerLock.unlock();
                pc.occupied = false;
            }
        }

        System.out.println(prof.getId() + " Prof done");

    }
    
}
