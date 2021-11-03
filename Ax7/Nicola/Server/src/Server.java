import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;

public class Server implements Runnable {
    private DatagramPacket clientPacket;
    private DatagramSocket socket;
    private Random r;

    public Server(DatagramPacket packet, Random r, DatagramSocket socket) {
        this.clientPacket = packet;
        this.r = r;
        this.socket = socket;
    }

    @Override
    public void run() {
        // Calculate the response delay
        float delay = r.nextFloat() * 1.5f;

        Server.printPacketData(clientPacket, true, delay);

        try {
            DatagramPacket response = new DatagramPacket(new byte[1], 1,
                    clientPacket.getAddress(), clientPacket.getPort());
            Thread.sleep((long) (delay * 1000));

            socket.send(response);
        }
        catch (InterruptedException e) {
            System.out.println("The server was shut down before it could send a ping");
        }
        catch (IOException e) {
            System.out.println("Error while trying to send datagram");
        }
    }

    private synchronized static void printPacketData(DatagramPacket packet, boolean accepted, float delay) {
        System.out.println("------PACKET RECEIVED-----");
        System.out.println("Client address: " + packet.getAddress().getHostAddress());
        System.out.println("Client port: " + packet.getPort());
        System.out.println("Ping content: " + new String(packet.getData()));

        System.out.println(accepted ? "Packet accepted, sending response with a delay of " + (int)(delay*1000) +
                " milliseconds..." : "Packet not accepted");
    }

    public static void main(String[] args) throws SocketException {
        // Check arguments
        if (args.length < 1) {
            System.out.println("ERR: -arg 1 | Missing port number");
            return;
        }
        // Received data
        byte[] data = new byte[2048];
        // Seed for the generator
        long seed = (new Date()).getTime();;
        // Random float generator
        Random random;

        // Socket
        DatagramSocket socket = null;
        try {
             socket = new DatagramSocket(Integer.parseInt(args[0]));
        }
        catch (NumberFormatException e) {
            System.out.println("ERR: -arg 1 | Port number is not an integer");
            return;
        }
        // Used to store the request of the client
        DatagramPacket request = new DatagramPacket(data, data.length);

        // Using the provided seed if possible, using the current date otherwise
        if (args.length == 2) {
            try {
                seed = Long.parseLong(args[1]);
            }
            catch (NumberFormatException e) {
                System.out.println("ERR: -arg 2 | Incorrect seed");
                return;
            }
        }

        random = new Random(seed);

        try {
            System.out.println("Server correctly initialized and currently running");
            while (true) {
                // Calculate the probability of replying
                float responseProb = random.nextFloat() * 100;

                socket.receive(request);

                if (responseProb < 75) {
                    (new Thread(new Server(request, random, socket))).start();
                }
                else {
                    Server.printPacketData(request, false, -1);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error while trying to receive datagram");
        }
    }
}
