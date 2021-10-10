package mypackage;

import java.util.concurrent.ThreadLocalRandom;

public class ConcurrentUtils {
    
    private ConcurrentUtils() {}

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch(InterruptedException e) { System.out.println("" + e); }
    }

    public static int generateRandInt(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    public static void main(String[] args) {
        System.out.println("Ciao");
    }

}
