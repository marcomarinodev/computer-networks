public class Pi {

    public static void main(String[] args) throws InterruptedException {

        if (args.length < 2) {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StackTraceElement main = stack[stack.length - 1];
            String mainClass = main.getClassName();

            System.err.println(mainClass + " Usage: " + "accuracy, maximum waiting time(milliseconds)");
            System.exit(1);
        }

        double accuracy = Double.parseDouble(args[0]);
        long maxWaitingTime = Long.parseLong(args[1]);

        Thread piCalculator = new Thread( new PiCalculator(accuracy) );
        piCalculator.start();
        Thread.sleep(maxWaitingTime);
        piCalculator.interrupt();

    }
}
