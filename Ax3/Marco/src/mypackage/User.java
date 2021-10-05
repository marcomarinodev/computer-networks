package mypackage;

public class User {
    int id;
    int fromTime;
    int toTime;
    Laboratory lab;
    int pos;

    public User(int id, int fromTime, int toTime, Laboratory lab, int pos) {
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.lab = lab;
        this.pos = pos;
    }

    public void printInfo() {
        System.out.println("User#" + id);
    }

    public String getId() {
        return "[" + id + "]";
    }

    public String message(String mex) {
        // print start message
        return getId() + mex;
    }

}

class UndergraduateRunnable extends User implements Runnable {

    public UndergraduateRunnable(
        int id,
        int fromTime,
        int toTime,
        Laboratory lab,
        int pos
    ) { super(id, fromTime, toTime, lab, pos); }

    @Override
    public void run() {
        int randomTime = ConcurrentUtils.generateRandInt(fromTime, toTime);

        try {
			lab.undergraduateGet(pos, message("Working at thesis"), message("Work on thesis done"), randomTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}

class StudentRunnable extends User implements Runnable {

    public StudentRunnable(int id, int fromTime, int toTime, Laboratory lab, int pos) { 
        super(id, fromTime, toTime, lab, pos); 
    }

    @Override
    public void run() {
        int randomTime = ConcurrentUtils.generateRandInt(fromTime, toTime);

        try {
			lab.studentGet(message("Studying..."), message("Study done"), randomTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}


class ProfRunnable extends User implements Runnable {

    public ProfRunnable(int id, int fromTime, int toTime, Laboratory lab, int pos) { 
        super(id, fromTime, toTime, lab, pos); 
    }

    @Override
    public void run() {
        int randomTime = ConcurrentUtils.generateRandInt(fromTime, toTime);

        try {
			lab.profGet(message("Studying..."), message("Study done"), randomTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}