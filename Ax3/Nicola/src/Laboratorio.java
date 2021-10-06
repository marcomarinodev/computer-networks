import java.util.AbstractQueue;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Laboratorio {

    private Computer[] computers;
    private ReentrantReadWriteLock computersLock;

    public Laboratorio(int nComputers) {
        computers = new Computer[nComputers];
        computersLock = new ReentrantReadWriteLock();

        for (int i=0; i<nComputers; i++) {
            computers[i] = new Computer();
        }
    }

    public Computer[] readComputers() {
        Computer[] ret;

        computersLock.readLock().lock();
        ret = computers.clone();
        computersLock.readLock().unlock();

        return ret;
    }

    public void askComputer(long time) {
        // Nello studente, il sistema di locking e unlockin è volutamente poco efficiente
        // al fine di ridurre la priorità degli studenti sui tesisti
        boolean done = false;

        System.out.println("Studente in coda");

        while (!done) {
            computersLock.readLock().lock();

            for (Computer c : computers) {
                // Dormo per dare la possibilità ai tesisti di avere priorità
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Se il computer non è prenotato e non ho finito
                if (!c.isOccupied() && !done) {
                    computersLock.readLock().unlock();

                    // Locko la struttura
                    computersLock.writeLock().lock();
                    // Prenoto il computer
                    c.use(time);
                    computersLock.writeLock().unlock();

                    System.out.println(Thread.currentThread().getName() + ": Studente ha terminato");

                    done = true;
                }
            }
            if (!done)
                computersLock.readLock().unlock();
        }
    }

    public void bookComputer(int i, long time) {
        System.out.println("Tesista in coda");
        computersLock.writeLock().lock();
        computers[i].use(time);
        computersLock.writeLock().unlock();

        System.out.println(Thread.currentThread().getName() + ": Tesista ha terminato");
    }

    public void bookAll(long time) {
        System.out.println("Professore in coda");
        // Blocca l'accesso in scrittura a tutti i computer
        computersLock.writeLock().lock();

        // Prenota tutti i computer, se necessario aspetta che chi ce l'ha già finisca
        for (Computer value : computers) {
            value.use();
        }

        // Aspetta
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Libera i computer
        for (Computer value : computers) {
            value.unuse();
        }

        // Restituisci l'accesso al laboratorio
        computersLock.writeLock().unlock();
        System.out.println(Thread.currentThread().getName() + ": Professore ha terminato");
    }

    public static void main(String[] args) {
        Laboratorio lab = new Laboratorio(20);
        Thread t;

        for (int i=0; i<Integer.parseInt(args[0]); i++) {
            Professore p = new Professore(lab);
            t = new Thread(p);
            t.start();
        }

        for (int i=0; i<Integer.parseInt(args[1]); i++) {
            Tesista te = new Tesista(lab, ThreadLocalRandom.current().nextInt(0, 20));
            t = new Thread(te);
            t.start();
        }

        for (int i=0; i<Integer.parseInt(args[2]); i++) {
            Studente s = new Studente(lab);
            t = new Thread(s);
            t.start();
        }
    }
}
