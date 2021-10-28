import java.util.concurrent.ThreadLocalRandom;

public class User implements Runnable {

    public enum UserTypes {
        STUDENT,
        UNDERGRADUATE,
        TEACHER
    }

    private final UserTypes type;
    private final int numOfAccesses;
    private final Laboratory lab;

    public User(UserTypes type, Laboratory lab, int numOfAccesses) {

        this.numOfAccesses = numOfAccesses;
        this.type = type;
        this.lab = lab;

    }


    @Override
    public void run() {

        switch(type) {

            case STUDENT -> {
                for (int i = 0; i < numOfAccesses; i++) {
                    lab.bookStudent();
                    try {
                        Thread.sleep( ThreadLocalRandom.current().nextInt(100, 200) );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            case UNDERGRADUATE -> {
                for (int i = 0; i < numOfAccesses; i++) {
                    int iThComputer = ThreadLocalRandom.current().nextInt(0, 20);
                    lab.bookUndergraduate(iThComputer);
                    try {
                        Thread.sleep( ThreadLocalRandom.current().nextInt(100, 200) );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            case TEACHER -> {
                for (int i = 0; i < numOfAccesses; i++) {
                    lab.bookTeacher();
                    try {
                        Thread.sleep( ThreadLocalRandom.current().nextInt(100, 200) );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }
}