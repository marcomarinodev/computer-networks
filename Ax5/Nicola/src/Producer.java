import java.io.File;
import java.util.List;

public class Producer implements Runnable{
    private List<String> list;
    private String dirPath;

    public Producer(List<String> list, String dirPath) {
        this.list = list;
        this.dirPath = dirPath;
    }

    public void run() {
        visitDirectory(dirPath);
    }

    private void visitDirectory(String path) {
        File dir = new File(path);

        if (dir.isFile()) {
            synchronized (list) {
                list.add(path);
                return;
            }
        }

        File[] files = dir.listFiles();

        for (int i=0; i<files.length; i++) {
            visitDirectory(files[i].getPath());
        }
    }
}
