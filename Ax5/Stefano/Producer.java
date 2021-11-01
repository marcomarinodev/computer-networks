import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Producer implements Runnable{

    private final LinkedList<String> list;
    private final Path dirPath;
    private final AtomicBoolean end;

    Producer(LinkedList<String> list, Path dirPath, AtomicBoolean end) {

        this.list = list;
        this.dirPath = dirPath;
        this.end = end;

    }

    private void dirWalk(File file) {

        if (file == null)
            return;

        File[] dirs = file.listFiles(File::isDirectory);

        if (dirs != null) {
            for (File dir : dirs) {
                dirWalk(dir);
                synchronized (list) {
                    list.addLast(dir.getPath());
                    list.notifyAll();
                }
            }
        }

    }

    @Override
    public void run() {

        File dir = dirPath.toFile();

        synchronized (list) {
            list.addLast(dir.getPath());
            list.notifyAll();
        }
        dirWalk(dir);
        synchronized (list) {
            end.set(true);
            list.notifyAll();
        }

    }
}
