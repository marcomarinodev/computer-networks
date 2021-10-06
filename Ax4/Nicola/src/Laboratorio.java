import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Laboratorio {

    private ArrayList<Computer> computers;

    public Laboratorio(int nComputers) {
        computers = new ArrayList<Computer>(Collections.synchronizedList(new ArrayList<Computer>()));

        for (int i=0; i<nComputers; i++) {
            computers.add(new Computer());
        }
    }

    public void askComputer(long time) {
        // Nello studente, il sistema di locking e unlockin è volutamente poco efficiente
        // al fine di ridurre la priorità degli studenti sui tesisti
        boolean done = false;
        Computer[] computers = this.computers.toArray(new Computer[0]);

        System.out.println("Studente in coda");

        while (!done) {
            for (Computer c : computers) {
                // Dormo per dare la possibilità ai tesisti di avere priorità
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (c) {
                    // Se il computer non è prenotato e non ho finito
                    if (!c.isOccupied() && !done) {
                        // Prenoto il computer
                        c.use(time);
                        System.out.println(Thread.currentThread().getName() + ": Studente ha terminato");
                        done = true;
                    }
                }
            }
        }
    }

    public void bookComputer(int i, long time) {
        System.out.println("Tesista in coda");
        synchronized (computers) {
            computers.get(i).use(time);
        }

        System.out.println(Thread.currentThread().getName() + ": Tesista ha terminato");
    }

    public void bookAll(long time) {
        System.out.println("Professore in coda");

        synchronized (computers) {

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

            System.out.println(Thread.currentThread().getName() + ": Professore ha terminato");
        }
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
