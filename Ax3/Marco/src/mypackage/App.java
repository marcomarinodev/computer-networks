package mypackage;

import java.util.Scanner;

enum UserType {PROF, TH, STUD}

class User {
    private int id;

    public User(int id) {
        this.id = id;
    }

    public void printInfo() {
        System.out.println("User#" + id);
    }

}

class ProfRunnable extends User implements Runnable {

    public ProfRunnable(int id) {
        super(id);
    }

    @Override
    public void run() {

    }

}

public class App {
    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);

        int nProf = s.nextInt();
        int nUnder = s.nextInt();
        int nStud = s.nextInt();

        // Simulate the stream of users


    }
}
