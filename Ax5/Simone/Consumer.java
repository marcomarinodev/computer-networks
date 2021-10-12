package com.company;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Consumer implements Runnable {

    // list of directories that will be visited
    private LinkedList<String> dirToVisit;

    // object to provide ordered print of file properties(directory, filename, length, last modified)
    private Object mutex;

    // flag that indicates if the consumer still has to extract some dirs(true in that case)
    private AtomicBoolean active;

    // constructor
    public Consumer(LinkedList<String> l, AtomicBoolean a) {
        dirToVisit = l;
        mutex = new Object();
        active = a;
    }

    // method that prints file properties
    private void printFileInfo(File f){
        synchronized (mutex){
            System.out.println("Directory: " + f.getParent());
            System.out.println("Filename: " + f.getName());
            System.out.println("File length: " + f.length());
            System.out.println("Last modified: " + new Date(f.lastModified()).toString() + "\n");
        }
    }

    // method that analyzes a single directory
    private void scanDir(String dir){
        System.out.println(Thread.currentThread().getName() + " scanning " + dir);
        File actDir = new File(dir);

        FileFilter ff = new FileFilter() { // file filter
            @Override
            public boolean accept(File file) {
                return !file.isDirectory(); // accepts file only if it does not represent a directory
            }
        };

        File[] dirsToAdd = actDir.listFiles(ff); // filtering directory files

        if(dirsToAdd == null) // if this dir does not contain any files, return
            return;

        for(File f : dirsToAdd) // printing all files in this directory
            printFileInfo(f);
    }

    @Override
    public void run() {
        String dir;
        boolean dirListEmpty = false;

        synchronized (dirToVisit) {
            dirListEmpty = !dirToVisit.isEmpty(); // initialize list emptiness condition
        }

        while(active.get() || dirListEmpty){ // while producer has dirs to visit || the list isn't empty
            synchronized (dirToVisit) { // synchronized access to shared queue of to be visited directories' names
                while (dirToVisit.isEmpty()) { // if there are not any directories to be visited, just wait
                    try {
                        dirToVisit.wait();

                        /**
                         *  If, when I wake up, producer stopped inserting dirs the termination
                         *  will depend on the emptiness of the dirs' list(no more on the flag
                         *  set by the producer). So consumer will continue scanning dirs until
                         *  the dirs' list will not be empty.
                         */
                        if(!active.get())
                            break;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                // if when I wake up , the list is empty, exit
                 if(dirToVisit.isEmpty())
                     break;
                dir = dirToVisit.pop(); // pop directory to analyze
                scanDir(dir); // scan extracted directory
                dirListEmpty = !dirToVisit.isEmpty(); // update list emptiness condition
            }
        }
    }
}
