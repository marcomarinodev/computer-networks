package com.company;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class App {

    public static String FILE_PATH = "src/com/company/Data.json";
    public static int wireTransferCounter = 0;
    public static int accreditationCounter = 0;
    public static int postalCounter = 0;
    public static int f24Counter = 0;
    public static int bancomatCounter = 0;

    public static void main(String[] args) throws Exception {

        List<BankAccount> bankAccounts = BankAccount.generateBankAccounts(5);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ByteBuffer byteBuffer = ByteBuffer.allocate(16 * 1024);
        WritableByteChannel dest = Channels.newChannel(new FileOutputStream(FILE_PATH));
        FileInputStream inputStream = new FileInputStream(FILE_PATH);
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));

        // Serialize
        serialize(bankAccounts, gson, byteBuffer, dest);

        // Deserialize
        deserialize(gson, reader);

        // Frequencies
        System.out.println(
            "Wire Transfer: " + wireTransferCounter + "\n" +
            "Accreditation: " + accreditationCounter + "\n" +
            "Postal: " + postalCounter + "\n" +
            "F24: " + f24Counter + "\n" +
            "Bancomat: " + bancomatCounter + "\n"
        );
    }

    private static void deserialize(Gson gson, JsonReader reader) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        reader.beginArray();
        while(reader.hasNext()) {
            BankAccount ba = gson.fromJson(reader, BankAccount.class);
            
            for (Transaction transaction: ba.transactions) {
                Runnable worker = new WorkerThread(transaction.causal);
                executor.execute(worker);   
            }

        }
        reader.endArray();

        executor.shutdown();

        while (!executor.isTerminated()) {}
    }

    private static void serialize(List<BankAccount> bankAccounts, Gson gson, ByteBuffer byteBuffer,
            WritableByteChannel dest) throws IOException {
        byteBuffer.put("[\n".getBytes());
        for (int i = 0; i < bankAccounts.size(); i++) {
            // Serialize json object
            String json = gson.toJson(bankAccounts.get(i));

            // Using NIO
            if (i < bankAccounts.size() - 1) 
                byteBuffer.put((json + ",\n").getBytes());
            else 
                byteBuffer.put((json).getBytes());

            byteBuffer.flip();

            while (byteBuffer.hasRemaining()) {
                dest.write(byteBuffer);
            }

            byteBuffer.clear();
        }

        byteBuffer.put("\n]".getBytes());
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            dest.write(byteBuffer);
        }
    }
}
