import java.util.concurrent.*;

public class Ufficio extends Thread {

    private BlockingQueue<Cliente> codaClienti;
    private ExecutorService pool;

    private int ratePersone;

    public Ufficio(int rate, int maxCoda, int capienza) {
        codaClienti = new ArrayBlockingQueue<Cliente>(capienza);
        pool = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(maxCoda), new ThreadPoolExecutor.DiscardPolicy());
        ratePersone = rate;
    }

    public void addCliente(Cliente c) {
        try {
            codaClienti.put(c);
        } catch (InterruptedException e) {
            System.out.println("While adding client");
            e.printStackTrace();
        }
    }

    public void run() {
        // Aggiungo clienti finché il programma non viene interrotto
        while (!Thread.currentThread().isInterrupted()) {
            // Se ho abbastanza persone da aggiungere
            if (codaClienti.size() >= ratePersone) {
                // Le assegno tutte agli sportelli
                for (int i=0; i<ratePersone; i++) {
                    pool.submit(codaClienti.poll());
                }
            }
        }
    }

    public static void main(String[] args) {
        // L'ufficio fa entrare args[0] persone alla volta, ogni sportello accetta un massimo di args[1] persone in coda
        // e la sala grande ha una capienza massima di args[2] persone
        Ufficio u = new Ufficio(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        // Faccio partire l'ufficio
        u.start();

        // Aggiungo persone senza limite simulando un flusso continuo
        while (true) {
            u.addCliente(new Cliente());
        }
    }
}
