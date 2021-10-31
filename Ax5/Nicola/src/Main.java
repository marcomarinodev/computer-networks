import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int consumers = Integer.parseInt(args[0]);
        Thread[] threads = new Thread[consumers];

        // Creo una coda da passare sia ai produttori che ai consumatori
        List<String> list = new LinkedList<>();

        // Faccio partire il produttore
        Producer p = new Producer(list, "Test");
        Thread t = new Thread(p);

        t.start();

        // Faccio partire i consumatori
        for (int i=0; i<consumers; i++) {
            threads[i] = new Thread(new Consumer(list));
            threads[i].start();
        }

        for (int i=0; i<consumers; i++) {
            threads[i].join();
        }
    }
}
