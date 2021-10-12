package com.company;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe computer rappresenta una singola postazione
 */
public class Computer {
    private ReentrantLock lockPc; //lock singolo computer
    private Condition tesistQueue; //coda tesisti in attesa di tale pc
    private boolean isOccupied; //false sse libero, true altrimenti

    //costruttore
    public Computer(){
        lockPc = new ReentrantLock();
        tesistQueue = lockPc.newCondition();
        isOccupied = false;
    }

    //ritorna riferimento a lock del pc
    public ReentrantLock getLockPc() {
        return lockPc;
    }

    //ritorna riferimento a CV del pc
    public Condition getTesistQueue() {
        return tesistQueue;
    }

    //ritorna status occuopato/libero di pc
    public boolean isOccupied() {
        return isOccupied;
    }

    //ritorna status occuopato/libero di pc
    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}
