package mypackage;

import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Laboratory lab = new Laboratory(20);

        /*
        Scanner s = new Scanner(System.in);

        int nProf = s.nextInt();
        int nUnder = s.nextInt();
        int nStud = s.nextInt();
        */

        // Simulate the stream of users
        Thread user = new Thread (new UndergraduateRunnable(0, 1000, 2000, lab, 5));
        Thread user1 = new Thread (new UndergraduateRunnable(1, 1000, 2000, lab, 5));
        Thread user2 = new Thread (new UndergraduateRunnable(2, 1000, 2000, lab, 5));
        Thread user3 = new Thread (new UndergraduateRunnable(3, 1000, 2000, lab, 3));
        Thread user4 = new Thread (new UndergraduateRunnable(4, 1000, 2000, lab, 3));
        Thread user5 = new Thread (new UndergraduateRunnable(5, 1000, 2000, lab, 1));
        Thread user6 = new Thread (new UndergraduateRunnable(6, 1000, 2000, lab, 8));

        user.start();
        user1.start();
        user2.start();
        user3.start();
        user4.start();
        user5.start();
        user6.start();

        user.join();
        user1.join();
        user2.join();
        user3.join();
        user4.join();
        user5.join();
        user6.join();
    
    }
}
