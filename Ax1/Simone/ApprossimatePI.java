package PIApprox;

public class ApprossimatePI implements Runnable{
    private final double accuracy;
    private double PIApprox;

    public ApprossimatePI(double accuracy) {
        this.accuracy = accuracy;
        this.PIApprox = 4.0000;
    }

    public void run(){
        int i = 3;
        int p = 1;

        while(Math.abs(this.PIApprox - Math.PI) >= accuracy && !Thread.currentThread().isInterrupted()){
                this.PIApprox += (4.0000 / ((Math.pow(-1, p)) * i));
                p++;
                i = i + 2;
        }
        if(Math.abs(this.PIApprox - Math.PI) < accuracy)
            System.out.println("Exiting due to accuracy limit");
        else if(Thread.currentThread().isInterrupted())
            System.out.println("Exiting due to interruption");
        System.out.println("Approximated PI = " + this.PIApprox);
    }
}
