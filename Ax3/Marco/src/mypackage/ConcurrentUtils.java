package mypackage;

public class ConcurrentUtils {
    
    private ConcurrentUtils() {}

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch(InterruptedException e) { System.out.println("" + e); }
    }

}
