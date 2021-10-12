package com.company;

import java.util.Random;

/**
 * Classe Tesista estende Utente, quindi è un task ed eredita da questa il metodo run
 */
public class Tesista extends Utente{
    private int toUsePc; //indice del pc che il tesista deve utilizzare

    //costruttore
    public Tesista(String nome, LaboratorioSmart l){
        super(nome, l);
        setToUsePc();
    }

    //imposta indice del pc che il tesista this andrà ad utilizzare
    private void setToUsePc(){
        toUsePc = new Random().nextInt(l.getNumPcs());
    }

    //ritorna indice pc richiesto da tesista this
    public int getToUsePc(){
        return toUsePc;
    }
}
