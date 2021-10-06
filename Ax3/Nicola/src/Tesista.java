public class Tesista extends Utente {
    private int computer;

    public Tesista(Laboratorio laboratorio, int computer) {
        super(laboratorio);
        this.computer = computer;
    }

    public int getComputer() {
        return computer;
    }

    public void run() {
        Computer[] computers;

        for (int i=0; i<nAccesses; i++) {
            lab.bookComputer(computer, 250);
        }
    }
}
