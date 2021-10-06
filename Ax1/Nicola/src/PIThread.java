import java.lang.Thread;

public class PIThread extends Thread{

    private double accuracy;

    private double currentPI;
    private int currentIter;

    public PIThread(double accuracy) {
        this.accuracy = accuracy;

        currentPI = 0;
        currentIter = 0;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (Math.abs(currentPI - Math.PI) >= accuracy) {
                currentPI += Math.pow(-1, currentIter) * 4 / (1 + 2 * currentIter);
                currentIter++;
            }
            else {
                return;
            }
        }
    }

    public double getPIApproximation() {
        return currentPI;
    }

    public static void main(String[] args) {
        PIThread piCalculator;
        double accuracy;
        long maxMillis;

        accuracy = Double.parseDouble(args[0]);
        maxMillis = Long.parseLong(args[1]);

        piCalculator = new PIThread(accuracy);
        piCalculator.start();

        try {
            Thread.sleep(maxMillis);
            piCalculator.interrupt();

            System.out.println("Approssimazione finale di PI: " + piCalculator.getPIApproximation());
        } catch (InterruptedException e) {
            System.out.println("Main interrotto prima della terminazione corretta del programma");
            e.printStackTrace();
        }
    }
}
