import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) {

        if (args.length != 1) {

            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StackTraceElement main = stack[stack.length - 1];
            String mainClass = main.getClassName();

            System.err.println("Usage" + mainClass + "filepath: absolute/relative directory path");
            System.exit(1);

        }

        Path path = Paths.get(args[0]);
        Path dirPath = null;

        try {
            dirPath = path.toRealPath();
        } catch (NoSuchFileException f) {
            System.err.println("The file specified in the program arguments does not exist\n");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime runtime = Runtime.getRuntime();
        int numberOfProcessors = runtime.availableProcessors();

        LinkedList<String> list = new LinkedList<>();

        AtomicBoolean end = new AtomicBoolean(false);

        Thread producer = new Thread( new Producer(list, dirPath, end) );
        producer.start();
        
        Thread[] consumers = new Thread[numberOfProcessors];

        for (int i = 0; i < numberOfProcessors; i++) {
            consumers[i] = new Thread( new Consumer(list, end) );
            consumers[i].start();
        }

        try {
            producer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        for (int i = 0; i < numberOfProcessors; i++) {
            try {
                consumers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.exit(0);
    }
}
