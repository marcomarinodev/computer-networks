import java.util.concurrent.ArrayBlockingQueue;

public class Producer implements Runnable {

    private final ArrayBlockingQueue<Person> firstRoom;

    public Producer(ArrayBlockingQueue<Person> firstRoom) {

        this.firstRoom = firstRoom;

    }

    @Override
    public void run() {

        while ( !Thread.currentThread().isInterrupted() ) {

            Person newArrival = new Person();
            try {
                this.firstRoom.put(newArrival);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

    }

}
