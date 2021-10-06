import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utente implements Runnable{
    protected Laboratorio lab;
    protected int nAccesses;

    public Utente(Laboratorio l) {
        nAccesses = ThreadLocalRandom.current().nextInt(1, 11);
        lab = l;
    }

    public void run() {}
}
