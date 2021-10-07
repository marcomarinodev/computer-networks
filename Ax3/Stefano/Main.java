import java.util.Random;

public class Main {

    public static void main(String[] args) {

        if (args.length != 3) {

            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StackTraceElement main = stack[stack.length - 1];
            String mainClass = main.getClassName();

            System.err.println(mainClass + "number of: students, undergraduates, teachers");
            System.exit(1);

        }

        long numberOfStudents = Long.parseLong(args[0]);
        long numberOfUndergraduate = Long.parseLong(args[1]);
        long numberOfTeachers = Long.parseLong(args[2]);

        Random generator = new Random();
        int k = generator.nextInt(10) + 1;

        Laboratory lab = new Laboratory();

        Thread thread;

        int i;

        for (i = 0; i < numberOfStudents; i++) {
            thread = new Thread( new User(User.UserTypes.STUDENT, lab, k));
            thread.start();
        }
        for (i = 0; i < numberOfUndergraduate; i++) {
            thread = new Thread( new User(User.UserTypes.UNDERGRADUATE, lab, k));
            thread.start();
        }
        for (i = 0; i < numberOfTeachers; i++) {
            thread = new Thread( new User(User.UserTypes.TEACHER, lab, k) );
            thread.start();
        }

    }
}