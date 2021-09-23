import java.util.Objects;

public class PiCalculator implements Runnable{

    double accuracy;

    public PiCalculator(double accuracy) {

        this.accuracy = accuracy;

    }

    public double getAccuracy() {

        return this.accuracy;

    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if ( !(o instanceof PiCalculator) )
            return false;
        PiCalculator that = (PiCalculator) o;

        return Double.compare(that.getAccuracy(), this.accuracy) == 0;

    }

    @Override
    public int hashCode() {

        return Objects.hash(getAccuracy());

    }

    public String toString() {

        return Double.toString(this.accuracy);

    }

    @Override
    public void run() {

        int numerator = 4;
        double denominator = 1;
        int i = 1;

        double pi = 0;

        do {

            if ( (i & 1) == 0 )
                pi -= numerator / denominator;
            else
                pi += numerator / denominator;

            if ( Math.abs(pi - Math.PI) <= accuracy ) {
                System.out.println("Accuracy reached");
                break;
            }
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Time out");
                break;
            }

            denominator += 2;
            i++;

        } while (true);

        System.out.println(pi);

    }
}
