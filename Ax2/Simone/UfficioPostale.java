package com.company;

import java.util.Random;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class UfficioPostale {
    private int n_sportelli; //numero sportelli attivi
    private int capacità_coda_sportelli; //capacità coda di accesso agli sportelli
    private ExecutorService sportelli; //thread pool simulante gli sportelli sempre attivi
    private LinkedBlockingQueue<Cliente> clienti; //lista iniziale di clienti entrati nell'ufficio
    private static final long MAX_ATTESA_SALA = 1000;

    public UfficioPostale(int n_sportelli, int capacità, int n_clienti){
        init_clienti(n_clienti); //faccio entrare tutti i clienti nell'ufficio
        this.n_sportelli = n_sportelli;
        this.capacità_coda_sportelli = capacità;
        sportelli = new ThreadPoolExecutor(n_sportelli, n_sportelli, 0,
                TimeUnit.SECONDS , new ArrayBlockingQueue<Runnable>(capacità_coda_sportelli),
                new WaitUntilQueueNotFull());
        // in ultimo parametro creo coda per accedere agli sportelli, di capacità limitata
    }

    private void init_clienti(int n_clienti){
        clienti = new LinkedBlockingQueue<>(Integer.MAX_VALUE); //sala d'attesa di capacità illimitata

        //Ipotesi che tutti i clienti da servire siano già presenti in sala d'attesa
        for (int i = 0; i < n_clienti; i++){
            String name = "Cliente " + i;
            try {
                clienti.put(new Cliente(name));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void officeActivity(){
        Random r = new Random();

        System.out.println("Ufficio postale aperto");
        System.out.println("Clienti totali: " + clienti.size());
        System.out.println("Sportelli attivi: " + n_sportelli);
        System.out.println("Capacità coda per accesso agli sportelli: " + capacità_coda_sportelli);

        /*
        * Provo a far accedere i clienti dalla sala d'attesa(coda clienti)
        * alla coda per gli sportelli se c'è spazio altrimenti attendo.
        * In quanto so che non arriveranno ulteriori clienti(non a caso
        * sono stati fatti entrari nell'inizializzazione dell'ufficio), una volta che
        * questi sono passati dalla sala d'attesa alla coda degli sportelli
        * posso avviare la procedura di terminazione GRADUALE che
        * non accetta ulteriori clienti(task) e chiude l'ufficio(il thread pool executor)
        * dopo aver servito i clienti della coda degli sportelli.
        * */
        while(!clienti.isEmpty()){
            try {
                Cliente c = clienti.take();
                sportelli.execute(c);
                long attesa = Math.abs(r.nextLong() % MAX_ATTESA_SALA);
                sleep(attesa);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        if(Thread.currentThread().isInterrupted())
            sportelli.shutdownNow();
        else
            sportelli.shutdown();

        while(!sportelli.isTerminated()){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
        System.out.println("Ufficio postale chiuso");
    }
}
