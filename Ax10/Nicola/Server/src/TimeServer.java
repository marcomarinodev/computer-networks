import java.io.IOException;
import java.net.*;
import java.util.Date;

public class TimeServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length < 1) {
            System.err.println("Indirizzo di multicast mancante");
            return;
        }

        InetAddress address = InetAddress.getByName(args[0]);
        if (!address.isMulticastAddress()) {
            System.err.println("L'indirizzo fornito non è un indirizzo multicast");
            return;
        }

        DatagramSocket socket = new DatagramSocket(6000);

        while (true) {
            Date date = new Date();
            byte[] dateString = date.toString().getBytes();
            DatagramPacket dp = new DatagramPacket(dateString, dateString.length, address, 6666);

            System.out.println("Sent " + new String(dateString));

            socket.send(dp);
            Thread.sleep(1000);
        }
    }
}
