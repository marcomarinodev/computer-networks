package com.company;

/*
* Classe professore estende Utente, quindi anch'essa rappresenta un task(eredita run dalla superclasse)
* */
public class Professore extends Utente{

    //costruttore
    public Professore(String nome, LaboratorioSmart l){
        super(nome, l);
    }
}
