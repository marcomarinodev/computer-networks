package com.company;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Producer implements Runnable {

    // root directory
    private String startingDir;

    // list of directories that will be visited
    private LinkedList<String> dirToVisit;

    // flag that indicates if the producer still has to insert some dirs(true in that case, false otherwise)
    private AtomicBoolean active;

    // constructor
    public Producer(LinkedList<String> l, String startDir, AtomicBoolean a) {
        startingDir = startDir;
        dirToVisit = l;
        active = a;
    }

    // method that checks if rootpath exists: in case it doesn't, throws an exception
    private void checkRootPathExistance(String rootPath){
        File act = new File(rootPath); // rootpath file descriptor

        if(!act.exists()) // if rootpath does not exists, throw an exception
            throw new IllegalArgumentException(rootPath + " does not exists!");
    }

    // method that recursively visits FS from rootpath and adds in a LinkedList all the subdirectories
    private void visitFSTree(String rootPath) {
        File act = new File(rootPath); // rootpath file descriptor

        FileFilter ff = new FileFilter() { // file filter
            @Override
            public boolean accept(File file) {
                return file.isDirectory(); // accepts file only if it represents a directory
            }
        };

        File[] dirsToAdd = act.listFiles(ff); // filtering directory files

        if(dirsToAdd == null) // if this dir does not contain any subdirectories, return
            return;

        for(File s : dirsToAdd) { // adding directories to be visited(except this and its parent)
            try {
                if(!s.getName().equals(act.getCanonicalFile().getName()) &&
                        !s.getName().equals(act.getCanonicalFile().getParentFile().getName())){
                    synchronized (dirToVisit) {
                        try {
                            dirToVisit.add(s.getCanonicalPath()); // adding a canonical subdirectory path
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        dirToVisit.notify(); // waking up a consumer
                    }
                    try {
                        visitFSTree(s.getCanonicalPath()); // recursive visit
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void run() {
        try {
            checkRootPathExistance(startingDir); // checks rootpath existance
            synchronized (dirToVisit){
                dirToVisit.addFirst(startingDir); // if rootpath exists, add it to the list
                dirToVisit.notify();
            }
            visitFSTree(startingDir); // starting the visit
            synchronized (dirToVisit) { // insert rootpath as the first dir to be visited, if no errors occured
                active.set(false); // producer has no more dirs to insert
                dirToVisit.notifyAll(); // wake up every consumer to observe the state change
                System.out.println("Producer has no more dirs to insert");
            }
        }catch (Exception e){ // display error message
            System.out.println(e.getMessage());
        }
    }
}
