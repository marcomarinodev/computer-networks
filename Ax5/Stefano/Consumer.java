import java.io.File;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Consumer implements Runnable{

    private final LinkedList<String> list;
    private final AtomicBoolean end;

    Consumer(LinkedList<String> list, AtomicBoolean end) {

        this.list = list;
        this.end =end;

    }

    @Override
    public void run() {

        String dirName;
        boolean listIsEmpty;

        do {

            synchronized (list) {

                while (list.isEmpty()) {
                    try {
                        list.wait();
                        if (end.get())
                            break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

               try {
                   dirName = list.pop();
               } catch (NoSuchElementException e) {
                   break;
               }

            }

            File file = new File(dirName);
            File[] files = file.listFiles();

            if (files != null) {
                for (File f : files) {
                    System.out.println(f.getName());
                }
            }

            synchronized (list) {
                listIsEmpty = list.isEmpty();
            }

        } while (!end.get() || !listIsEmpty);

    }
}
