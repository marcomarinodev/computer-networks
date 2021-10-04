package mypackage;

public class User {
    private int id;
    int fromTime;
    int toTime;

    public User(int id, int fromTime, int toTime) {
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public void printInfo() {
        System.out.println("User#" + id);
    }

    public String getId() {
        return "[" + id + "]";
    }

    public void work(String startMessage, String endMessage, int ms) {
        // print start message
        System.out.println(getId() + startMessage);

        // doing work...
        ConcurrentUtils.sleep(ms);

        // print exit message
        System.out.println(getId() + endMessage);

    }

}

class UndergraduateRunnable extends User implements Runnable {

    public UndergraduateRunnable(int id, int fromTime, int toTime) { super(id, fromTime, toTime); }

    @Override
    public void run() {
        work("Working at thesis", "Work on thesis done",
        ConcurrentUtils.generateRandInt(fromTime, toTime));
    }
}

class ProfRunnable extends User implements Runnable {

    public ProfRunnable(int id, int fromTime, int toTime) { super(id, fromTime, toTime); }

    @Override
    public void run() {
        work("Network job start", "Network job done",
        ConcurrentUtils.generateRandInt(fromTime, toTime));
    }

}

class StudentRunnable extends User implements Runnable {

    public StudentRunnable(int id, int fromTime, int toTime) { super(id, fromTime, toTime); }

    @Override
    public void run() {
        work("Study start", "Study done",
        ConcurrentUtils.generateRandInt(fromTime, toTime));
    }

}