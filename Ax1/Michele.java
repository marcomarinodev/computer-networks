import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Prendo in input da terminale prima accuratezza poi time-out
        Scanner input = new Scanner(System.in);
        System.out.println("Inserisci Accuratezza:");
        double accuracy = input.nextDouble();
        System.out.println("Inserisci Time-Out:");
        long time = input.nextLong();
        input.close();

        MyPiCalc newpi = new MyPiCalc(accuracy);
        newpi.start();
        try {
            newpi.join(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (newpi.isAlive()) newpi.interrupt();
    }
}

class MyPiCalc extends Thread{
    private double mypi;
    private double accuracy;

    public MyPiCalc(double accuracy) {
        mypi=0.0;
        this.accuracy=accuracy;
    }

    public void run() {
        int counter=0;
        double dis=1;
        while(Math.abs(Math.PI - mypi) >= accuracy && this.isInterrupted() == false) {
            if(counter%2==0) {
                mypi=mypi+(4/dis);
            }
            else {
                mypi=mypi-(4/dis);
            }
            counter++;
            dis=dis+2;
        }
        System.out.println("PI Finale:" + mypi);
        System.out.println("PI Reale:" + Math.PI);
        if(this.isInterrupted() == false){
            System.out.println("Uscito perchè ho raggiunto l'accuratezza in " + counter + " passi");
        }
        else {
            System.out.println("Uscito perchè raggiunto tempo limite, Ho effettuato " + counter +" passi ");
        }
    }
}