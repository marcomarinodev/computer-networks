package com.company;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe Computer rappresenta una postazione del laboratorio
 */
public class Computer {
    private AtomicBoolean isOccupied; //false sse libero, true altrimenti
    private AtomicInteger nTesistAttesaPc; // numero tesisti in attesa di this

    //costruttore
    public Computer(){
        isOccupied = new AtomicBoolean(false);
        nTesistAttesaPc = new AtomicInteger(0);
    }

    //ritorna status occupato/libero del computer this
    public boolean isOccupied() {
        return isOccupied.get();
    }

    //imposta status computer this
    public void setOccupied(boolean occupied) {
        isOccupied.set(occupied);
    }

    //ritorna numero tesisti, entrati nel laboratorio, in attesa di pc this
    public int getnTesistAttesaPc() {
        return nTesistAttesaPc.get();
    }

    //imposta numero tesisti, entrati nel laboratorio, in attesa di pc this
    public void setnTesistAttesaPc(int nTesistAttesaPc) {
        this.nTesistAttesaPc.set(nTesistAttesaPc);
    }
}
