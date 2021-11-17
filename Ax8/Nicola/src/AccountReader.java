import java.io.*;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.nio.*;
import java.util.concurrent.*;

public class AccountReader implements Runnable {
    private static ConcurrentHashMap<String, Integer> occurrences = new ConcurrentHashMap<>();
    private Account toRead;
    
    public AccountReader(Account toRead) {
        this.toRead = toRead;
    }

    @Override
    public void run() {
        toRead.putOccurrences(AccountReader.occurrences);
    }

    public static void createDump(int nAccounts) throws IOException {
        String[] names = {"Mario", "Franco", "Giovanni", "Antonia", "Marcella", "Piorlo"};
        ArrayList<Account> accounts = new ArrayList<Account>();

        // Genera account casuali
        for (int i=0; i<nAccounts; i++) {
            try {
                accounts.add(new Account(names[Math.round(new Random().nextFloat() * (names.length-1))]));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Salvali in JSON
        Gson gson = new Gson();
        String json = gson.toJson(accounts);

        // Salvali su file tramite canale
        byte[] strBytes = json.getBytes();
        FileOutputStream fout = new FileOutputStream("dump.json");
        FileChannel fc = fout.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);

        for (int i=0; i<strBytes.length; i++) {
            buffer.put(strBytes[i]);
        }
        buffer.flip();
        fc.write(buffer);

        fc.close();
        fout.close();
    }

    public static void main(String[] args) throws IOException {
        // Crea un threadpool di accountreader
        ExecutorService threadPool = Executors.newCachedThreadPool();

        if (args.length > 0) {
            AccountReader.createDump(Integer.parseInt(args[0]));
        }

        // Il thread principale legge il file di json tramite jsonstream
        FileInputStream inputStream = new FileInputStream("dump.json");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));

        reader.beginArray();

        // Il thread principale passa un oggetto alla volta al threadpool
        while (reader.hasNext()) {
            Account account = new Gson().fromJson(reader, Account.class);
            threadPool.execute(new AccountReader(account));
        }

        threadPool.shutdown();

        while (!threadPool.isTerminated()) {}

        for (String s : occurrences.keySet()) {
            System.out.println("Numero di occorrenze per " + s + ": " + occurrences.get(s));
        }
    }
}
