package PIApprox;

public class Main {
    public static void main(String[] args){
        if (args.length < 2)
            throw new IllegalArgumentException();

        System.out.println("Timeout = " + args[0] + "ms\nAccuracy = " + args[1]);
        long timeout = Long.parseLong(args[0]);
        double acc = Double.parseDouble(args[1]);
        ApprossimatePI api = new ApprossimatePI(acc);
        Thread t = new Thread(api);
        t.start();
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
