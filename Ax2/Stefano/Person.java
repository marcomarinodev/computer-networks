import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class Person implements Runnable {

    public static final AtomicLong ticketCounter = new AtomicLong(0L);
    private final long ticket;

    public Person() {

        this.ticket = ticketCounter.getAndIncrement();

    }

    @Override
    public void run() {

        int operationTime = ThreadLocalRandom.current().nextInt(1000, 3000);
        try {
            Thread.sleep(operationTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Person no. " + this.ticket + " has completed his operation");

    }

    public long getTicket() {

        return this.ticket;

    }

    public boolean equals(Object obj) {

        if (obj == null || getClass() != obj.getClass())
            return false;

        if (this == obj)
            return true;

        Person p = (Person) obj;
        return this.ticket == p.getTicket();

    }
}
