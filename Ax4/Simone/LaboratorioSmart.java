package com.company;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class LaboratorioSmart {
    private static final long MAX_T_UTILIZZO = 1000; // tempo(in ms) max di utilizzo di un pc o di lab
    private Computer[] computers; //array di postazioni(con lock implicite su ognuna), la cui CV intrinseca è coda dei prof in attesa
    private Object mutexCodaStudTesisti; //oggetto la cui CV intrinseca rappresenta coda studenti/tesisti
    private AtomicInteger occupied; // numero posti occupati al momento
    private AtomicInteger nProfAttesa; // numero prof attesa di this.computers

    //costruttore
    public LaboratorioSmart(){
        computers = new Computer[20];
        for(int i = 0; i < computers.length; i++)
            computers[i] = new Computer();
        occupied = new AtomicInteger(0);
        nProfAttesa = new AtomicInteger(0);
        mutexCodaStudTesisti = new Object();
    }

    /*
        Metodo di accesso al laboratorio. Distingue il tipo di utente attuale
        ed invoca metodo corrispopndente
     */
    public void accessLab(Utente u){
        if(u instanceof Professore)
            accessProfessore(u.getNome());
        else if(u instanceof Studente)
            accessStudente(u.getNome());
        else if(u instanceof Tesista)
            accessTesista(u.getNome(), ((Tesista) u).getToUsePc());
    }

    //ritorna numero di postazioni totali
    public int getNumPcs(){
        return computers.length;
    }

    //metodo non rientrante di attesa
    private void MySleep(){
        try {
            Thread.sleep(Math.abs(new Random().nextLong() % MAX_T_UTILIZZO)); //utilizzo pcs per X ms
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Metodo di accesso del professore
     */
    private void accessProfessore(String s){
        synchronized (computers){ // acquisisco lock implicita su struttura di tutti i pc
            System.out.println(s + " è entrato nel laboratorio");
            nProfAttesa.set(nProfAttesa.get() + 1); //incremento preventivamente il numero di prof in attesa
            while(occupied.get() != 0){ // se il laboratorio non è uuoto, attendo
                try {
                    System.out.println(s + " attende che tutti i pc si liberino");
                    computers.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    nProfAttesa.set(nProfAttesa.get() - 1);
                    return;
                }
            }
            nProfAttesa.set(nProfAttesa.get() - 1); //diminuisco prof in attesa
            occupied.set(computers.length);
            System.out.println(s + " occupa tutti i pc");
            for(Computer c : computers){
                synchronized (c){
                    c.setOccupied(true); //occupo tutti i pc, acquisendovi la lock implicita, e ne modifico lo status
                }
            }
        }

        MySleep(); //attesa

        synchronized (computers){
            occupied.set(0);
            System.out.println(s + " rilascia tutti i pc");
            for(Computer c : computers){ //libero tutti i pc
                synchronized (c){
                    c.setOccupied(false); //vado a modificare status di ogni pc acquisendovi lock implicita
                }
            }
            if(nProfAttesa.get() > 0){ // se ho prof in attesa
                System.out.println(s + " sveglia uno dei prof in attesa");
                computers.notify(); // sveglio un professore
            }else{ //se non ho prof in attesa
                AtomicInteger count = new AtomicInteger(0); //contatore di code tesisti per pc su cui ho fatto notify
                for(int i = 0; i < computers.length; i++){
                    synchronized (computers[i]){
                        if(computers[i].getnTesistAttesaPc() > 0) { // se ho tesista in attesa per un determinato pc
                            System.out.println(s + " sveglia uno dei tesisti in attesa per pc " + i);
                            computers[i].notify(); //sveglio tesisti in attesa per un determinato pc
                            count.set(count.get() + 1);
                        }
                    }
                }

                if(count.get() == 0){ // se non ho svegliato alcun tesista dentro il lab, in attesa di pc
                    synchronized (mutexCodaStudTesisti){
                        System.out.println(s + " sveglia utenti non prof fuori dal lab");
                        mutexCodaStudTesisti.notifyAll(); //sveglio tutti gli studenti/tesisti che stanno fuori dal lab
                    }
                }
            }
        }
    }

    /**
     * Metodo per accesso di un tesista
     */
    private void accessTesista(String s, int i){
        synchronized (computers[i]){ // acquisisco lock implicita pc
            System.out.println(s + " richiede pc" + i);
            computers[i].setnTesistAttesaPc(computers[i].getnTesistAttesaPc() + 1); //incremento preventivamente
                                                                                    // numero tesisti in attesa pc i
            while(computers[i].isOccupied() || nProfAttesa.get() > 0){ //se pc i è occupato od ho prof in attesa, attendo
                try {
                    System.out.println(s + " attende pc" + i);
                    computers[i].wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    computers[i].setnTesistAttesaPc(computers[i].getnTesistAttesaPc() - 1);
                    return;
                }
            }
            computers[i].setnTesistAttesaPc(computers[i].getnTesistAttesaPc() - 1);
            computers[i].setOccupied(true); //modifico status pc i
            System.out.println(s + " occupa pc " + i);
            synchronized (computers) {
                occupied.set(occupied.get() + 1); //incremento occupanti lab
            }
        }

        MySleep(); //svolgo lavoro

        synchronized (computers[i]){
            computers[i].setOccupied(false); // rilascia pc i
            System.out.println(s + " rilascia pc " + i);
            synchronized (computers){
                occupied.set(occupied.get() - 1); //decremento occupanti lab
                if(occupied.get() == 0 && nProfAttesa.get() > 0){ //sono ultimo occupante ed ho prof in attesa,
                    System.out.println(s + " sveglia prof in attesa");
                    computers.notify(); // ne sveglio uno
                }else if(computers[i].getnTesistAttesaPc() > 0){ //altrimenti, ho tesista in attesa per pc i,
                    System.out.println(s + " sveglia tesista in attesa per pc " + i);
                    computers[i].notify();  // ne sveglio uno
                }else{ //altrimenti
                    synchronized (mutexCodaStudTesisti){
                        System.out.println(s + " sveglio utenti non prof fuori dal lab");
                        mutexCodaStudTesisti.notifyAll(); //sveglio eventuali tesisti/studenti fuori dal lab
                    }
                }
            }
        }
    }

    /**
     * Ritorna indice di pc libero, se lo trova, -1 altrimenti.
     */
    private int findFreePc(){
        for(int i = 0; i < getNumPcs(); i++){
            synchronized (computers[i]){
                if(!computers[i].isOccupied())
                    return i;
            }
        }
        return -1;
    }

    /*
        Metodo di accesso per lo studente
     */
    private void accessStudente(String s){
        int i;

        synchronized (mutexCodaStudTesisti){ //acquisisco lock implicita
            while((i = findFreePc()) == -1 || nProfAttesa.get() > 0){ // finchè non ho un pc libero, attendo
                try {
                    System.out.println(s + " attende pc libero");
                    mutexCodaStudTesisti.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            occupied.set(occupied.get() + 1); //incremento occupanti lab
            synchronized (computers[i]){
                computers[i].setOccupied(true); //occupo pc
            }
            System.out.println(s + " occupa pc " + i);
        }

        MySleep(); //attesa

        synchronized (computers[i]){
            computers[i].setOccupied(false); // rilascia pc i
            System.out.println(s + " rilascia pc " + i);
            synchronized (computers){
                occupied.set(occupied.get() - 1); //decremento occupanti lab
                if(occupied.get() == 0 && nProfAttesa.get() > 0){ //sono ultimo occupante ed ho prof in attesa,
                    System.out.println(s + " sveglia prof in attesa");
                    computers.notify(); // ne sveglio uno
                }else if(computers[i].getnTesistAttesaPc() > 0){ //altrimenti, ho tesista in attesa per pc i,
                    System.out.println(s + " sveglia tesista in attesa per pc " + i);
                    computers[i].notify();  // ne sveglio uno
                }else{ //altrimenti
                    synchronized (mutexCodaStudTesisti){
                        System.out.println(s + " sveglio utenti non prof fuori dal lab");
                        mutexCodaStudTesisti.notifyAll(); //sveglio eventuali tesisti/studenti fuori dal lab
                    }
                }
            }
        }
    }
}
