import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class TimeClient {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Indirizzo di multicast mancante");
            return;
        }
        InetAddress address = InetAddress.getByName(args[0]);
        MulticastSocket socket = new MulticastSocket(6666);
        byte[] buffer = new byte[1024];
        socket.joinGroup(address);

        for (int i=0; i<10; i++) {
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            socket.receive(dp);

            System.out.println("Received " + new String(dp.getData()));
        }
    }
}
