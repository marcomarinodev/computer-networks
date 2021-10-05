import java.util.concurrent.*;

public class PostOffice {

    private static final int numOfBranches = 4;

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StackTraceElement main = stack[stack.length - 1];
            String mainClass = main.getClassName();

            System.err.println(mainClass + " Usage: openingTime (duration of opening of the post office)," +
                    " operator waiting time");
            System.exit(1);
        }

        long operatorWaitingTime = Long.parseLong(args[1]);

        ArrayBlockingQueue<Person> firstRoom = new ArrayBlockingQueue<>(32, true);
        ArrayBlockingQueue<Runnable> secondRoom = new ArrayBlockingQueue<>(16);

        ThreadPoolExecutor pool = new ThreadPoolExecutor(numOfBranches,
                numOfBranches,
                operatorWaitingTime,
                TimeUnit.SECONDS,
                secondRoom,
                new MyRejectedExecutionException() );

        pool.allowCoreThreadTimeOut(true);

        Thread producer = new Thread( new Producer(firstRoom) );

        long start = System.currentTimeMillis();
        long end = start + Long.parseLong(args[0]) * 1000;

        Person person = null;

        producer.start();

        while ( System.currentTimeMillis() < end ) {

            try {
                person = firstRoom.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (person == null)
                break;

            pool.execute(person);

        }

        producer.interrupt();

        while( firstRoom.size() > 0 ) {

            person = firstRoom.remove();
            pool.execute(person);

        }

        pool.shutdown();

        System.exit(0);
    }
}
