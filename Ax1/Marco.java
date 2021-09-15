package com.company;

/**
 * Assignment 1
 */

class ApproximatedPiRunnable implements Runnable {

    double tolerance;

    public ApproximatedPiRunnable(double tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public void run() {
        int odd = 1;
        double app_pi = 0;
        int i = 0;

        while (true) {
            app_pi += Math.pow(-1, i) * 4/odd;
            odd += 2;

            i++;

            // check if th difference is tolerated
            if (Math.abs(app_pi - Math.PI) <= this.tolerance) {
                System.out.println("Exited with accuracy reached");
                System.out.println("[PI]: " + Math.PI);
                System.out.println("[Approximation] [" + i + "]: " + app_pi);
                return;
            }

            if (Thread.currentThread().isInterrupted()) {
                // cleanup and stop execution
                System.out.println("Exited with timeout");
                System.out.println("[PI]: " + Math.PI);
                System.out.println("Approximation [" + i + "]: " + app_pi);
                break;
            }
        }
    }
}

public class Main {

    public static void main(String[] args) {

        long interval = Long.valueOf(args[0]);
        double tolerance = Double.valueOf(args[1]);

        System.out.println(tolerance);

        Thread t = new Thread(new ApproximatedPiRunnable(tolerance));

        t.start();

        try {
            t.join(interval);
        } catch (InterruptedException e) {
            System.out.println("Interruption Exception occurred");
        }

        // If interval expired, then interrupt the thread
        t.interrupt();

        return;
    }
}
