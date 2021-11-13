import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws IOException {

        // file creation
        Path filePath = Paths.get(System.getProperty("user.home") + "/Desktop" + "/BankAccounts.json");
        try {
            Files.createFile(filePath);
        }
        catch (FileAlreadyExistsException ignored) { }
        catch (IOException e) {
            e.printStackTrace();
        }

        FileChannel channel = FileChannel.open(filePath, StandardOpenOption.WRITE, StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        // I build a GsonBuilder with custom serializer and deserializer to
        // serialize and deserialize LocalDateTime objects
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();

        builder.registerTypeAdapter( LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (localDateTime, type, jsonSerializationContext) ->
                        new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) );

        builder.registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (jsonElement, type, jsonDeserializationContext) -> {
                    try{
                        return LocalDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } catch (DateTimeParseException e) {
                        return LocalDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
                    }
        });

        Gson gson = builder.create();

        String[] names = {"Valerio", "Luca", "Michele", "Federico", "Gabriele"};
        LocalDateTime date;
        Random r = new Random();

        String bracket = "[";
        byte[] bracketBytes = bracket.getBytes(StandardCharsets.UTF_8);
        buffer.put(bracketBytes);
        String newLine = "\n";
        byte[] newLineBytes = newLine.getBytes(StandardCharsets.UTF_8);
        buffer.put(newLineBytes);

        // file initialization
        for (int j = 0; j < 5; j++) {

            BankAccount account = new BankAccount(names[j]);

            for (int i = 0; i < 5; i++) {

                int year = (r.nextInt() % 3) + 2019;
                int month = r.nextInt(12) + 1;
                int day;
                if (month == Month.FEBRUARY.getValue())
                    day = r.nextInt(28) + 1;
                else if (month == Month.NOVEMBER.getValue() || month == Month.APRIL.getValue() ||
                        month == Month.JUNE.getValue() || month == Month.SEPTEMBER.getValue())
                    day = r.nextInt(30) + 1;
                else
                    day = r.nextInt(31) + 1;
                int hour = r.nextInt(24);
                int minute = r.nextInt(60);
                date = LocalDateTime.of(year, month, day, hour, minute);
                String dateS = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                int purpose = r.nextInt(5);
                FinancialRecord record = new FinancialRecord(FinancialRecord.PaymentPurpose.values(purpose), dateS);
                account.addFinancialRecord(record);

            }

            String jsonAccount = gson.toJson(account);
            byte[] byteAccount = jsonAccount.getBytes(StandardCharsets.UTF_8);
            if (j != 0) {
                String comma = ",";
                byte[] commaBytes = comma.getBytes(StandardCharsets.UTF_8);
                buffer.put(commaBytes);
            }
            buffer.put(byteAccount);
            buffer.flip();
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            buffer.clear();

        }

        buffer.put(newLineBytes);
        bracket = "]";
        bracketBytes = bracket.getBytes(StandardCharsets.UTF_8);
        buffer.put(bracketBytes);
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        buffer.clear();

        // end of file initialization

        // start of the count
        AtomicInteger bankTransferCount = new AtomicInteger(0);
        AtomicInteger creditCount = new AtomicInteger(0);
        AtomicInteger bulletinCount = new AtomicInteger(0);
        AtomicInteger f24Count = new AtomicInteger(0);
        AtomicInteger atmCount = new AtomicInteger(0);

        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        FileInputStream inputStream = new FileInputStream(filePath.toString());
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        reader.beginArray();
        while (reader.hasNext()) {
            BankAccount account = new Gson().fromJson(reader, BankAccount.class);
            Counter counter = new Counter(bankTransferCount, creditCount, bulletinCount, f24Count, atmCount, account);
            threadPool.submit(counter);
        }
        //end of the count

        threadPool.shutdown();
        channel.close();
        inputStream.close();
        filePath.toFile().deleteOnExit();

        System.out.println("bank transfer: " + bankTransferCount);
        System.out.println("credit: " + creditCount);
        System.out.println("bulletin: " + bulletinCount);
        System.out.println("F24: " + f24Count);
        System.out.println("ATM: " + atmCount);

    }

}