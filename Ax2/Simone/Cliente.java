package com.company;

import java.util.Random;

import static java.lang.Thread.*;

public class Cliente implements Runnable{
    private final String name; // nome cliente
    private final long serviceTimeMs; //tempo di servizio stimato in ms
    private static final long MAX_ATTESA = 10000;

    public Cliente(String name){
        this.name = name;
        Random r = new Random();
        this.serviceTimeMs = Math.abs(r.nextLong() % MAX_ATTESA);
    }

    public String getName(){
        return new String(name);
    }

    public void run(){
        try {
            System.out.println(Thread.currentThread().getName() + " - Cliente " + name +
                    " servito per " + serviceTimeMs + "ms");
            sleep(serviceTimeMs); //simulo tramite sleep del thread l'operazione di servire un cliente
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " - Cliente " + name + " interrotto!");
            return;
        }
        System.out.println(Thread.currentThread().getName() + " - Cliente " + name + " uscito dall'ufficio!");
    }
}
