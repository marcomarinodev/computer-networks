import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {

        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            exit(1);
        }
        String localHostName = address.getHostName();

        Scanner scanner = new Scanner(System.in);
        String string = scanner.nextLine();

        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress(localHostName, 80)) ) {

            ByteBuffer buffer = ByteBuffer.allocate( (string + " echoed by server").length() );
            buffer.put(string.getBytes(StandardCharsets.UTF_8));buffer.flip();
            channel.write(buffer);
            buffer.flip();
            buffer.clear();
            channel.read(buffer);
            String receivedString = new String(buffer.array(), StandardCharsets.UTF_8);
            System.out.printf("%s\n", receivedString);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
