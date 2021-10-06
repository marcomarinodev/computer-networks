public class Studente extends Utente {
    public Studente(Laboratorio laboratorio) {
        super(laboratorio);
    }

    public void run() {
        Computer[] computers;

        for (int i=0; i<nAccesses; i++) {
             lab.askComputer(100);
        }
    }
}
