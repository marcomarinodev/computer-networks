public class Professore extends Utente{
    public Professore(Laboratorio laboratorio) {
        super(laboratorio);
    }

    public void run() {
        for (int i=0; i<nAccesses; i++)
            lab.bookAll(500);
    }
}
