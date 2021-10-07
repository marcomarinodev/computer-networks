import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.*;

public class Laboratory {

    private final Lock labLock;
    private final Computer[] computers;

    public Laboratory() {

        labLock = new ReentrantLock();
        computers = new Computer[20];
        for (int i = 0; i < 20; i++) {
            computers[i] = new Computer();
        }

    }

    public void bookStudent() {

        System.out.println("Student in the queue");

        for (Computer computer: computers) {

            try {
                Thread.sleep(70);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!computer.isOccupied()) {
                labLock.lock();
                long time = ThreadLocalRandom.current().nextLong(100, 300);
                computer.use(time);
                labLock.unlock();
                break;
            }
        }

        System.out.println("Student has finished");

    }

    public void bookUndergraduate(int i) {

        System.out.println("Undergraduate in the queue");

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        labLock.lock();
        long time = ThreadLocalRandom.current().nextLong(100, 300);
        computers[i].use(time);
        labLock.unlock();
        System.out.println("Undergraduate has finished");

    }

    public void bookTeacher() {

        System.out.println("Teacher in the queue");

        labLock.lock();

        for (Computer computer : computers) {
            computer.startUsing();
        }

        long time = ThreadLocalRandom.current().nextLong(100, 300);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Computer computer : computers) {
            computer.stopUsing();
        }

        labLock.unlock();

        System.out.println("Teacher has finished");

    }

}