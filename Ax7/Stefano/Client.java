import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Client {

    public static void main(String[] args) {

        if (args.length != 2) {

            StackTraceElement[] stack =Thread.currentThread().getStackTrace();
            StackTraceElement main = stack[stack.length -1];
            String mainClass = main.getClassName();

            System.err.println("Usage" + mainClass + "serverName port");
            System.exit(1);

        }

        String serverName = args[0];
        int port = Integer.parseInt(args[1]);

        if (port < 1 || port > 65535) {

            StackTraceElement[] stack =Thread.currentThread().getStackTrace();
            StackTraceElement main = stack[stack.length -1];
            String mainClass = main.getClassName();

            System.err.println(mainClass + "ERR -arg 1");
            System.exit(1);

        }

        try (DatagramSocket socket = new DatagramSocket()) {

            socket.setSoTimeout(2000);
            InetAddress host = InetAddress.getByName(serverName);

            ByteArrayOutputStream arrayOutStream = new ByteArrayOutputStream();
            DataOutputStream outStream = new DataOutputStream(arrayOutStream);

            byte[] data = new byte[20];

            DatagramPacket request = new DatagramPacket(data, data.length, host, port);
            DatagramPacket reply = new DatagramPacket(data, data.length, host, port);

            long currentRTT, minRTT = Long.MAX_VALUE, maxRTT = Long.MIN_VALUE;
            double avgRTT = 0;
            int numReceived = 0;

            for (int i = 0; i < 10; i++) {

                ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault());
                long millis = date.toInstant().toEpochMilli();
                long start = System.nanoTime();

                // sending the request
                outStream.writeBytes("PING" + " " + i + " " + millis + "\n");
                data = arrayOutStream.toByteArray();
                request.setData(data);
                request.setLength(data.length);
                socket.send(request);
                arrayOutStream.reset();

                // receiving the reply
                try {
                    socket.receive(reply);
                } catch (SocketTimeoutException e) { }

                long end = System.nanoTime();

                currentRTT = (end - start) / 1000000;

                if (currentRTT < minRTT)
                    minRTT = currentRTT;
                if (currentRTT > maxRTT)
                    maxRTT = currentRTT;
                avgRTT += currentRTT;

                String s = new String(data, StandardCharsets.UTF_8);
                if (currentRTT >= 2000) {
                    System.out.println(s + "RTT: *");
                }
                else {
                    numReceived++;
                    System.out.println(s + "RTT: " + currentRTT);
                }

            }

            avgRTT = avgRTT / 10;
            System.out.println("--------- PING STATISTICS ---------");
            double percentage = (1 - (double) numReceived / 10) * 100;
            System.out.println("10 packets transmitted, " + numReceived + " packets received, "
                                + percentage + "% packet loss");
            System.out.printf("round-trip (ms) min/avg/max = %d/%.2f/%d", minRTT, avgRTT, maxRTT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
