package com.company;

public class WorkerThread implements Runnable {

    Causal causal;

    WorkerThread(Causal causal) {
        this.causal = causal;
    }

    public void run() {
        update();
    }

    private synchronized void update() {

        switch(causal) {
            case WIRE_TRANSFER:
            App.wireTransferCounter++;
            break;

            case ACCREDITATION:
            App.accreditationCounter++;
            break;

            case POSTAL:
            App.postalCounter++;
            break;

            case F24:
            App.f24Counter++;
            break;

            case BANCOMAT:
            App.bancomatCounter++;
            break;

            default:
            break;
        }

    }
}
