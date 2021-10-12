package com.company;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) {
        if(args.length < 2)
            throw new IllegalArgumentException("There must be at least 2 arguments");

        LinkedList<String> l = new LinkedList<>();

        AtomicBoolean ab = new AtomicBoolean(true); // 'producer has dirs to insert' flag

        Thread t = new Thread( new Producer(l, args[0], ab));
        Consumer c = new Consumer(l, ab);

        int size = Integer.parseInt(args[1]); // consumer threads' number

        Thread[] threads = new Thread[size];

        t.start();

        for(int i = 0; i < threads.length; i++){ // consumers' initialization
            threads[i] = new Thread(c);
            threads[i].start();
        }

        for(int i = 0; i < threads.length; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        int remainingDirs = l.size();

        if(remainingDirs > 0){
            System.out.println("Still remaining " + remainingDirs + " dirs to visit"); // error situation
        }else{
            System.out.println("You printed the entire FS tree from " + args[0]); // successful situation
        }
    }
}
