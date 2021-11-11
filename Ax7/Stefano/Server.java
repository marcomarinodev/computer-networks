import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Sever {

    public static void main(String[] args) {

        int port;
        long seed = 0;

        if (args.length != 1) {

            if (args.length == 2) {
                seed = Long.parseLong(args[1]);
            }
            else {
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                StackTraceElement main = stack[stack.length - 1];
                String mainClass = main.getClassName();

                System.err.println("Usage" + mainClass + "port [seed]");
                System.exit(1);
            }

        }

        port = Integer.parseInt(args[0]);

        if (port < 1 || port > 65535) {

            StackTraceElement[] stack =Thread.currentThread().getStackTrace();
            StackTraceElement main = stack[stack.length -1];
            String mainClass = main.getClassName();

            System.err.println(mainClass + "ERR -arg 1");
            System.exit(1);

        }

        try ( DatagramSocket socket = new DatagramSocket(port) ) {

            byte[] data = new byte[20];
            DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
            Random r = new Random(seed);

            for (int i = 0; i < 10; i++) {

                socket.receive(receivedPacket);
                data = receivedPacket.getData();
                byte[] rawIP = receivedPacket.getAddress().getAddress();
                StringBuilder ipAddress = new StringBuilder();
                int j = 4;
                for (byte B : rawIP) {
                    ipAddress.append(B & 0xFF);
                    if (--j > 0) {
                        ipAddress.append(".");
                    }
                }
                System.out.println( "CLIENT IP: " +  ipAddress
                        + " CLIENT PORT: " + receivedPacket.getPort() );
                String s = new String(data, StandardCharsets.UTF_8);
                System.out.println(s);
                int latency = r.nextInt(200) + 50;
                if (i == 0 || i % 3 != 0) {
                    try {
                        Thread.sleep(latency);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    socket.send(receivedPacket);
                    System.out.println("PING delayed by " + latency + " ms");
                }
                else
                    System.out.println("Ping not sent");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
