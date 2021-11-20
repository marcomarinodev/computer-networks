import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.SocketChannel;

public class Client {
    private static final int port = 1234;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Expected string to echo");
            return;
        }

        // Connect to the server, prepare the buffer
        SocketAddress address = new InetSocketAddress("localhost", port);
        SocketChannel clientChannel = SocketChannel.open(address);
        ByteBuffer buffer = ByteBuffer.allocate(args[0].length() + " echoed by server".length());

        // Send the string
        buffer.put(args[0].getBytes());
        buffer.flip();
        clientChannel.write(buffer);

        // Receive the echoed string
        buffer.flip();
        buffer.clear();
        clientChannel.read(buffer);

        System.out.println(new String(buffer.array()));

        clientChannel.close();
    }
}
