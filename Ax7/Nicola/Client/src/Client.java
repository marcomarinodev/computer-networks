import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

public class Client {
    public static void main(String[] args) throws SocketException {
        byte[] toSend = new byte[2048];
        ByteArrayOutputStream baos;
        DataOutputStream stream;

        int nReceived = 0;
        int totRtts = 0;
        int minRtt = 3000, maxRtt = -1;


        try {
            InetAddress address = InetAddress.getByName(args[0]);
            DatagramSocket socket = new DatagramSocket(0);

            DatagramPacket sendPacket = new DatagramPacket(toSend, 2048, address, Integer.parseInt(args[1]));
            DatagramPacket receivePacket = new DatagramPacket(new byte[1], 1);

            socket.setSoTimeout(2000);

            for (int i=0; i<10; i++) {
                long startTime = (new Date()).getTime();
                long rtt;
                long endTime;

                baos = new ByteArrayOutputStream();
                stream = new DataOutputStream(baos);
                stream.writeBytes("PING " + i + " " + startTime);

                sendPacket.setData(baos.toByteArray());
                socket.send(sendPacket);

                try {
                    socket.receive(receivePacket);
                    nReceived++;

                    endTime = (new Date()).getTime();
                    rtt = endTime - startTime;
                    System.out.println("PING " + i + " " + startTime + "\nRTT: " + rtt);

                    minRtt = (int)Math.min(minRtt, rtt);
                    maxRtt = (int)Math.max(maxRtt, rtt);
                    totRtts += rtt;
                }
                catch (SocketTimeoutException e) {
                    System.out.println("*");
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("Host name couldn't be resolved");
        } catch (IOException e) {
            System.out.println("Error while preparing PING packet");
        }

        System.out.println("---- PING Statistics ----");
        System.out.println("10 packets transmitted, " + nReceived + " packets received, "
            + String.format(java.util.Locale.US, "%.2f", (100 - ((float)nReceived / 10)*100)) + "% packet loss, round trip (ms) min / avg / max = "
            + minRtt + " / " + String.format(java.util.Locale.US,"%.2f", ((float)totRtts / nReceived))
            + " / " + maxRtt);
    }
}
