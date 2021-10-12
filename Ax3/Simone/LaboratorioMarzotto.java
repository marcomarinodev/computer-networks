package com.company;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LaboratorioMarzotto {
    private ReentrantLock lockLab; //lock per accesso a laboratorio
    private Computer computers[]; //array di postazioni
    private Condition professorQueue; //coda di attesa per i professori che devono occupare il lab
    private Condition studentQueue; //coda studenti per accedere ad un pc qualsiasi nel lab
    private int occupied; //contatore del numero di pc occupati in un dato momento
    private static final long MAX_T_UTILIZZO = 1000; //tempo in ms di massimo di utilizzo di pc

    //costruttore
    public LaboratorioMarzotto(){
        lockLab = new ReentrantLock();
        professorQueue = lockLab.newCondition();
        studentQueue = lockLab.newCondition();
        computers = new Computer[20];
        for(int i = 0; i < computers.length; i++)
            computers[i] = new Computer();
        occupied = 0;
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

    //ritorna numero di postazione del lab
    public int getNumPcs(){
        return computers.length;
    }

    //metodo di attesa
    private void MySleep(){
        try {
            Thread.sleep(Math.abs(new Random().nextLong() % MAX_T_UTILIZZO)); //utilizzo pcs per X ms
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Verifica esistenza thread che ha lockato
     * un pc. true se esiste, false altrimenti
     */
    private boolean existPcLocked(){
        for(Computer c : computers){
            if(c.getLockPc().isLocked())
                return true;
        }
        return false;
    }

    /**
     * Verifica se esiste un tesista, già nel laboratorio, che richiede pc i
     */
    private boolean existWaitingForPcTesist(){
        for(Computer c : computers){
            if(c.getLockPc().getWaitQueueLength(c.getTesistQueue()) > 0)
                return true; // se trovo pc con tesisti in attesa, ritorno true
        }
        return false;
    }

    //metodo di accesso di un professore
    private void accessProfessore(String s){
        lockLab.lock(); // tenta accesso a stato condiviso lab
        System.out.println(s + " guarda disponibilità pc");
        while(occupied != 0 || existPcLocked()){ //se lab non vuoto o qualcun'altro ha lock su un pc
            try {
                System.out.println(s + " attende");
                professorQueue.await(); //attendo
            } catch (InterruptedException e) {
                e.printStackTrace();
                lockLab.unlock();
                return;
            }
        }
        System.out.println(s + " ha occupato il laboratorio");
        occupied = computers.length; // prof occupa tutti i pc
        for(Computer c: computers) {
            c.getLockPc().lock();
            c.setOccupied(true);
        }

        MySleep(); //lavoro

        boolean areThereWaitingPcStudents = existWaitingForPcTesist(); // variabile che mi dice se ci sono tesisti nel laboratorio
                                                                        // in attesa di pc

        if(lockLab.getWaitQueueLength(professorQueue) > 0) { //se ho professore in attesa di entrare nel lab
            System.out.println(s + " ha svegliato altro professore");
            professorQueue.signal(); //segnalo un professore in coda
        }else { //altrimenti
            if(areThereWaitingPcStudents){ // se ci sono tesisti che attendono, all'interno del lab, un determinato pc
                for(int i = 0; i < computers.length; i++) {
                    computers[i].getTesistQueue().signal(); // per ogni pc, sveglio un tesista in attesa di esso
                }
                System.out.println(s + " sveglia eventuali tesisti in attesa per un determinato pc");
            }else{ // altrimenti
                System.out.println(s + " ha svegliato altri studenti o tesisti");
                studentQueue.signalAll(); //segnalo studenti in attesa di accedere al lab
            }
        }

        for(Computer c : computers){ //libero tutti i pc
            c.setOccupied(false);
            c.getLockPc().unlock();
        }

        occupied = 0; //resetto numero posti occupati
        System.out.println(s + " ha lasciato il laboratorio");
        lockLab.unlock();
    }

    //metodo di accesso di un tesista
    private void accessTesista(String s, int i){
        lockLab.lock(); // tesista prova ad accedere a lab
        computers[i].getLockPc().lock();
        lockLab.unlock(); // una volta acceduto si dirige verso il pc i
        System.out.println(s + " guarda per disponibilità pc " + i);
        while(computers[i].isOccupied()){ //se pc i è occupato, tesista attende nella coda del pcs
            try {
                System.out.println(s + " attende pc " + i);
                computers[i].getTesistQueue().await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                computers[i].getLockPc().unlock();
                return;
            }
        }
        computers[i].setOccupied(true); //tesista occupa pc
        System.out.println(s + " ha occupato pc " + i);
        computers[i].getLockPc().unlock();
        lockLab.lock();
        occupied++; //incremento numero pc occupati
        lockLab.unlock();

        MySleep(); //lavoro

        computers[i].getLockPc().lock();
        lockLab.lock();
        computers[i].setOccupied(false); //libero pc i
        System.out.println(s + " rilascia pc " + i);
        occupied--; //incremento numero pc occupati

        if(occupied == 0){//se sono ultimo occupante
            if(lockLab.getWaitQueueLength(professorQueue) > 0){ // se ho prof in attesa
                System.out.println(s + " sveglia i prof in attesa");
                professorQueue.signal(); //ne risveglio uno
            }else{ // altrimenti
                // se ho tesisti in attesa su un determinato pc
                if(computers[i].getLockPc().getWaitQueueLength(computers[i].getTesistQueue()) > 0) {
                    System.out.println(s + " sveglia tesisti in attesa su pc " + i);
                    computers[i].getTesistQueue().signal(); //ne sveglio uno
                }
            }
        }else{ // altrimenti
            // se ho tesisti in attesa su un determinato pc
            if(computers[i].getLockPc().getWaitQueueLength(computers[i].getTesistQueue()) > 0) {
                System.out.println(s + " sveglia tesisti in attesa su pc " + i);
                computers[i].getTesistQueue().signal(); //ne sveglio uno
            }
        }

        System.out.println(s + " esce dal laboratorio");
        computers[i].getLockPc().unlock();
        lockLab.unlock();
    }

    /**
     * Ritorna indice di pc libero, se esiste, altrimenti -1.
     * In caso di successo, vi acquisisce la lock
     */
    private int findFreePc(){
        int i = 0;
        boolean trovato = false; //

        while(i < getNumPcs()){
            if(computers[i].getLockPc().isLocked()){
                i++;
                continue;
            }
            computers[i].getLockPc().lock();
            if(!computers[i].isOccupied())
                return i;
            computers[i].getLockPc().unlock();
            i++;
        }
        return -1;
    }

    //metodo di accesso al lab per lo studente
    private void accessStudente(String s){
        lockLab.lock(); //tenta accesso al lab
        System.out.println(s + " guarda disponibilità pc");
        int i;

        while((i = findFreePc()) == -1){ //finchè non ho pc libero
            try {
                System.out.println(s + " attende");
                studentQueue.await(); //attendo
            } catch (InterruptedException e) {
                e.printStackTrace();
                lockLab.unlock();
                return;
            }
        }
        occupied++; //incremento occupanti lab
        computers[i].setOccupied(true); //occupo pc i
        System.out.println(s + " occupa pc " + i);
        computers[i].getLockPc().unlock();
        lockLab.unlock();

        MySleep(); //lavoro

        lockLab.lock();
        occupied--; //decremento occupanti
        computers[i].getLockPc().lock();
        computers[i].setOccupied(false); //libero pc
        System.out.println(s + " rilascia pc " + i);

        if(occupied == 0){ //se sono ultimo occupante
            if(lockLab.getWaitQueueLength(professorQueue) > 0){ // se ho prof in attesa
                System.out.println(s + " sveglia i prof in attesa");
                professorQueue.signal(); //ne sveglio uno
            }else{ //altrimenti
                // se ho tesisti in attesa su un determinato pc
                if(computers[i].getLockPc().getWaitQueueLength(computers[i].getTesistQueue()) > 0) {
                    System.out.println(s + " sveglia tesisti in attesa su pc " + i);
                    computers[i].getTesistQueue().signal(); //ne sveglio uno
                }
            }
        }else{ // non sono l'ultimo occupante
            // se ho tesisti in attesa su un determinato pc
            if(computers[i].getLockPc().getWaitQueueLength(computers[i].getTesistQueue()) > 0) {
                System.out.println(s + " sveglia tesisti in attesa su pc " + i);
                computers[i].getTesistQueue().signal(); //ne sveglio uno
            }
        }
        
        System.out.println(s + " esce dal laboratorio");
        computers[i].getLockPc().unlock();
        lockLab.unlock();
    }
}
