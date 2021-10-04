package mypackage;

import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Laboratory lab = new Laboratory(20);

        Scanner s = new Scanner(System.in);

        int nProf = s.nextInt();
        int nUnder = s.nextInt();
        int nStud = s.nextInt();

        // Simulate the stream of users
        Thread user = new Thread (new UndergraduateRunnable(0, 1000, 2000));
        Thread user1 = new Thread (new UndergraduateRunnable(1, 1000, 2000));
        Thread user2 = new Thread (new UndergraduateRunnable(2, 1000, 2000));
        Thread user3 = new Thread (new UndergraduateRunnable(3, 1000, 2000));
        Thread user4 = new Thread (new UndergraduateRunnable(4, 1000, 2000));
        Thread user5 = new Thread (new UndergraduateRunnable(5, 1000, 2000));
        Thread user6 = new Thread (new UndergraduateRunnable(6, 1000, 2000));

        
        lab.undergraduateGet(5, user);
        lab.undergraduateGet(5, user1);
        lab.undergraduateGet(1, user2);
        lab.undergraduateGet(3, user3);
        lab.undergraduateGet(3, user4);
        lab.undergraduateGet(1, user5);
        lab.undergraduateGet(8, user6);
    }
}
