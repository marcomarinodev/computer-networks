package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CustomService implements Runnable {

    private Socket socket;
    static final String NOT_FOUND_PAGE = "err.html";
    static final String currentPath = System.getProperty("user.dir");

    public CustomService(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Connected: " + socket);

        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        StringTokenizer tokenizer;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            dataOut = new BufferedOutputStream(socket.getOutputStream());

            // Analyze the request
            // Get -> /file.extension
            List<String> request = new LinkedList<>();
            String line;
            StringBuilder content = new StringBuilder();
            int i = 0;
            if ((line = in.readLine()) != null) {
                content.append(line);
            }

            tokenizer = new StringTokenizer(content.toString());
            while (i < 2 && tokenizer.hasMoreTokens()) {
                request.add(tokenizer.nextToken());
            }

            File file = new File(currentPath + "/resources" + request.get(1));
            byte[] fileData = readFileData(file, (int) file.length());

            out.println("HTTP/1.1 200 OK");
            out.println("Server: Marco's Java HTTP Server : 1.0");
            out.println("Date: " + new Date());
            out.println();
            out.flush();

            dataOut.write(fileData, 0, (int) file.length());
            dataOut.flush();

        } catch (FileNotFoundException err) {
            handleFileNotFound(out, dataOut);

        } catch (IOException e) {
            System.out.println("Server error: " + e);
        } finally {
            closeConn(in, out, dataOut);
        }

    }

    private void closeConn(BufferedReader in, PrintWriter out, BufferedOutputStream dataOut) {
        try {
            in.close();
            out.close();
            dataOut.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing stream: " + e.getMessage());
        }

        System.out.println("Connection closed");
    }

    private void handleFileNotFound(PrintWriter out, BufferedOutputStream dataOut) {
        try {
            File file = new File(currentPath + "/resources/" + NOT_FOUND_PAGE);
            byte[] fileData = readFileData(file, (int) file.length());

            out.println("HTTP/1.1 404 Not Found");
            out.println("Server: Marco's Java HTTP Server : 1.0");
            out.println("Date: " + new Date());
            out.println();
            out.flush();

            dataOut.write(fileData, 0, (int) file.length());
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidHttp(List<String> request) {
        if (request.size() <= 0)
            return false;

        if (request.get(0).equals("GET"))
            return true;

        return false;
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }

}

public class Main {

    public final static int PORT = 6789;

    public static void main(String[] args) throws Exception {

        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            ServerSocket listener = new ServerSocket(PORT);
            System.out.println("Server is running on " + localAddress + " with port: " + PORT);

            ExecutorService pool = Executors.newFixedThreadPool(20);

            while (true) {
                System.out.println("Listening...");
                pool.execute(new CustomService(listener.accept()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
