package mypackage;

import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {

        Scanner s = new Scanner(System.in);

        int nProf = s.nextInt();
        int nUnder = s.nextInt();
        int nStud = s.nextInt();

        int k = ConcurrentUtils.generateRandInt(1, 5);

        Laboratory lab = new Laboratory(20, nProf + nUnder + nStud, k, nProf, nUnder, nStud);

        lab.executeUsers();

        assert(lab.availableWorkstations == 0);

        s.close();
    
    }
}
