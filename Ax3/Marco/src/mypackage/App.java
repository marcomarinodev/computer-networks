package mypackage;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class App {
    public static void main(String[] args) throws Exception {
        Laboratory lab = new Laboratory(20);

        /*
        Scanner s = new Scanner(System.in);

        int nProf = s.nextInt();
        int nUnder = s.nextInt();
        int nStud = s.nextInt();
        */

        BlockingQueue<Thread> profsQueue = new ArrayBlockingQueue<>(3);
        BlockingQueue<Thread> undergradsQueue = new ArrayBlockingQueue<>(3);
        BlockingQueue<Thread> studentsQueue = new ArrayBlockingQueue<>(3);

        // Simulate the stream of users
        Thread prof1 = new Thread (new ProfRunnable(-1, 500, 10000, lab, -1));
        Thread prof2 = new Thread (new ProfRunnable(-2, 1000, 3000, lab, -1));
        Thread prof3 = new Thread (new ProfRunnable(-3, 1000, 3000, lab, -1));


        Thread user = new Thread (new UndergraduateRunnable(0, 2000, 4000, lab, 5));
        Thread user1 = new Thread (new UndergraduateRunnable(1, 3000, 5000, lab, 5));
        Thread user2 = new Thread (new UndergraduateRunnable(2, 1000, 2000, lab, 5));
        // Thread user3 = new Thread (new UndergraduateRunnable(3, 1000, 2000, lab, 3));
        // Thread user4 = new Thread (new UndergraduateRunnable(4, 1000, 2000, lab, 3));
        // Thread user5 = new Thread (new UndergraduateRunnable(5, 1000, 2000, lab, 1));
        // Thread user6 = new Thread (new UndergraduateRunnable(6, 1000, 2000, lab, 8));
        Thread stud = new Thread (new StudentRunnable(7, 5000, 6000, lab, -1));
        Thread stud2 = new Thread (new StudentRunnable(8, 500, 2000, lab, -1));
        Thread stud3 = new Thread (new StudentRunnable(9, 500, 2000, lab, -1));
        


        studentsQueue.add(stud);
        studentsQueue.add(stud2);
        studentsQueue.add(stud3);



        stud.start();
        stud2.start();

        prof1.start();

        stud3.start();

        prof2.start();
        prof3.start();
        
        stud.join();
        stud2.join();
        stud3.join();

        prof1.join();
        prof2.join();
        prof3.join();

        assert(lab.availableWorkstations == 0);
    
    }
}
