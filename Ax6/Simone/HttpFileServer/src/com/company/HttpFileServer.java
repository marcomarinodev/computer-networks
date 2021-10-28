package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpFileServer {
    private static final int PORT = 4536;
    private ServerSocket ls; // listen socket su porta PORT
    private static ConcurrentHashMap<String, String> acceptedExtensions; // mappa estensione - scopo
    private static final String START_DIR = "InitServerDir";
    private static final String ACCEPTED_EXTENSIONS_FILE = "AcceptedExtensions.txt";

    public HttpFileServer(){
        try {
            ls = new ServerSocket(PORT);
            System.out.println("Server in ascolto di nuove connessioni...");
            acceptedExtensions = new ConcurrentHashMap<>();
            initAcceptedExtensions(); // parso file per avere mappa estensione - content_type corrispondente
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public static HashMap<String, String> getAcceptedExtensionsCopy(){
        HashMap<String, String> res = new HashMap<>();

        for(String k : acceptedExtensions.keySet())
                res.put(k, acceptedExtensions.get(k));
        return res;
    }

    //Metodo per parsare file dove ho estensioni accettate(OK)
    private void initAcceptedExtensions() throws IOException {
        Scanner scan = new Scanner(new FileInputStream(new File(ACCEPTED_EXTENSIONS_FILE)));

        while(scan.hasNext()){
            String line = scan.nextLine();
            String[] token = line.split(":");
            if(token.length < 2)
                throw new IOException("Errore durante il parsing del file " + ACCEPTED_EXTENSIONS_FILE);

            String content_type = new String(token[0]);
            String ext = new String(token[1]);
            acceptedExtensions.put(ext, content_type);
        }
        scan.close();
    }

    public static File searchFile(String filename){
        String path = START_DIR + "/" + filename;

        File fd = new File(path);

        if(fd.exists())
            return fd;
        else
            return null;
    }



    public void activity() throws IOException {
        if(ls == null) {
            System.out.println("Server non attivo(probabile errore in apertura listen socket)");
            return;
        }

        Socket as = null;
        ExecutorService es = Executors.newCachedThreadPool(); // thread pooling per sfruttare concorrenza

        while(true){
            try {
                as = ls.accept(); // attendo nuove connessioni
                System.out.println("Ho un nuovo client connesso");
                es.submit(new RequestExecutor(as)); // servo richiesta
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                if(as != null && !as.isClosed())
                    as.close();
                break;
            }
        }

        try {
            es.shutdown();
            ls.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
