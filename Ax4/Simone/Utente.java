package com.company;

import java.util.Random;

/*
* Ci sono 3 tipi di utente:
*   - Professori
*   - Tesisti
*   - Studenti
*
* Perciò il task Utente è astratto ed ulteriori dettagli sono delegati alle sottoclassi concrete
* */
public abstract class Utente implements Runnable{
    protected LaboratorioSmart l; //laboratorio al quale accedere
    private String nome; //nome utente(usato per stampa messaggi
    protected final static int MAX_ACCESS = 3; //max numero di accessi di un utente

    // costruttore
    public Utente(String nome, LaboratorioSmart l){
        this.l = l;
        this.nome = nome;
    }

    // restituisce il nome dell'utente
    public String getNome(){
        return nome;
    }

    @Override
    public void run(){
        int k = new Random().nextInt(MAX_ACCESS) + 1; // definisco numero di accessi al lab(tra 1 e MAX_ACCESS inclusi)
        System.out.println("N accessi che " + this.getNome() + " dovrà eseguire = " + k);

        for(int i = 0; i < k; i++) {
            l.accessLab(this); //funzione di accesso ed uscita dal laboratorio
            System.out.println(this.getNome() + " ha terminato " + (i + 1) + "° accesso!");
            try {
                Thread.sleep(Math.abs(new Random().nextLong() % 1000)); //attendo max 1 s prima di provare a rientrare
                                                                        // nel lab
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        System.out.println(this.getNome() + " terminato");
    }
}
