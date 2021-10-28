package com.company;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class RequestExecutor implements Runnable{
    private Socket s; // socket di comunicazione con il client

    public RequestExecutor(Socket sock){
        if(sock == null)
            throw new NullPointerException();
        this.s = sock;
    }

    /**
     * Per formatto Http Request il target della richiesta sta
     * nella prima linea quindi lavoro su quella(Domanda: è possibile
     * che restino dei dati nell'input Stream del socket che mi danno noia
     * anche dopo la shutdownInput?)
     */
    private String getRequest(Socket s) {
        Scanner scan = null;
        try {
            scan = new Scanner(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String res;

        res = new String(scan.nextLine());

        return res;
    }

    @Override
    public void run() {
        String req = getRequest(s); // ottengo prima linea richiesta(dove so che sta il target)

        System.out.println("Richiesta arrivata = " + req);

        String filename = req.split(" ")[1].substring(1); //recupero nome file(vedi formato prima linea di Http request)

        String fileExtension = new String(filename.substring(filename.lastIndexOf('.') + 1)); //ottengo estensione file
        String ct = HttpFileServer.getAcceptedExtensionsCopy().get(fileExtension); // ottengo eventuale content-type corrispondente

        File toSend = HttpFileServer.searchFile(filename); // cerco il file name in START_DIR(cartella predefinita)

        if(toSend == null || ct == null) { // file inesistente od estensione non accettata
            System.out.println("File " + filename + " inesistente");
            try (DataOutputStream osr = new DataOutputStream(s.getOutputStream());){
                String response = "HTTP/1.0 404 Not Found";
                osr.writeBytes(response);
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        int c;

        //mi preparo per invio del file al mittente della richiesta

        try(DataOutputStream osr = new DataOutputStream(s.getOutputStream());
            DataInputStream isr = new DataInputStream((new FileInputStream(toSend)));){

            long fileLength = toSend.length();

            // byte[] content = new byte[Math.toIntExact(fileLength)];

            //costruisco header Http response
            String statusLine = "HTTP/1.0 200 OK";
            String date = "Date: " + new Date().toString();
            String lastModified = "Last-Modified: " + new Date(toSend.lastModified()).toString();
            String contentLength = "Content-Length: " + fileLength;
            String contentType = "Content-Type: " + ct; // da implementare riconoscimento content type da estensione
            String conn = "Connection: Closed" + "\r\n\r\n";

            String response = statusLine + "\r\n" + date + "\r\n" + lastModified + "\r\n" +
                    contentLength + "\r\n" + contentType + "\r\n" + conn;

            osr.writeBytes(response); // scrivo header

            while((c = isr.read()) != -1){ // scrivo file byte a byte
                try {
                    osr.writeByte(c);
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }

            //chiudo stream aperti
            osr.flush();
            isr.close();
            osr.close();
            //s.shutdownOutput();
            s.close(); //connessione non persistente
            System.out.println("Termino esecuzione richiesta");
            System.out.println("Client disconnesso");
        }catch(IOException ex){
            if(!s.isClosed()) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            System.out.println("Errore!");
        }
    }
}
