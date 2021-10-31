import java.util.Date;
import java.util.List;

public class Consumer implements Runnable{

    private List<String> list;

    public Consumer(List<String> list) {
        this.list = list;
    }

    public void run() {
        // Return after 3 seconds
        long time = (new Date()).getTime();
        long finishTime = time + 3000;

        while (time < finishTime) {
            synchronized (list) {
                if (list.size() > 0) {
                    System.out.println("Thread " + Thread.currentThread().getName() +
                        " found file: " + list.remove(0));
                }
            }
            time = (new Date()).getTime();
        }
    }
}
