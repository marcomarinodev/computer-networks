package com.company;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Thread.sleep;

/*
* Rejected executuon handler customizzato che tenta, ad intervalli di mezzo
* secondo la schedulazione di un task
* */
public class WaitUntilQueueNotFull implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
        if(threadPoolExecutor.isShutdown()) return;

        try {
            threadPoolExecutor.getQueue().put(runnable);
        } catch (InterruptedException e) {
            return;
        }
    }
}
