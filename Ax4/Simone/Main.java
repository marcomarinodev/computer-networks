package com.company;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        if(args.length < 3)
            throw new IllegalArgumentException("Program args length must be at least 3");
        int nStud = Integer.parseInt(args[0]);
        int nTesi = Integer.parseInt(args[1]);
        int nProf = Integer.parseInt(args[2]);

        if(nStud < 0 || nTesi < 0 || nProf < 0)
            throw new IllegalArgumentException("All the program args must be >= 0");

        LaboratorioSmart l = new LaboratorioSmart();

        Thread[] threads = new Thread[nStud + nTesi + nProf];
        int i = 0;

        //schedulazione shuffled di Prof, Studenti e Tesisti
        while(i < threads.length){
            int k = new Random().nextInt(3);
            switch(k){
                case 0: if(nStud > 0){
                    threads[i] = new Thread(new Studente("S" + i, l));
                    nStud--;
                    threads[i].start();
                    i++;
                } break;

                case 1: if(nTesi > 0){
                    threads[i] = new Thread(new Tesista("T" + i, l));
                    nTesi--;
                    threads[i].start();
                    i++;
                } break;

                case 2: if(nProf > 0){
                    threads[i] = new Thread(new Professore("P" + i, l));
                    nProf--;
                    threads[i].start();
                    i++;
                } break;
            }
        }

        try {
            for(int j = 0; j < threads.length; j++)
                threads[j].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
