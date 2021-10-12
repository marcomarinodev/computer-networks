package com.company;

/**
 * Classe Studente estende Utente(quindi è un task) ed eredita da questa il metodo run
 */
public class Studente extends Utente{

    //costruttore
    public Studente(String nome, LaboratorioMarzotto l){
        super(nome, l);
    }
}