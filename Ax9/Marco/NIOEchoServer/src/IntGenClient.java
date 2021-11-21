import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class IntGenClient {
    public static int PORT = 1919;
    public static String baseURL = "localhost";

    public static void main(String[] args) {
    
        Scanner scanner = new Scanner(System.in);

        String expectedOutput = scanner.nextLine() + " echoed by server";

        try { scanner.close(); } catch (Exception e) {
            System.out.println("close scanner exception");
        }

        try {
            SocketAddress address = new InetSocketAddress(baseURL, PORT);
            SocketChannel clientChannel = SocketChannel.open(address);
            ByteBuffer buffer = ByteBuffer.allocate(expectedOutput.length());
        
            // Sending message
            buffer.put(expectedOutput.getBytes());
            buffer.flip();
            clientChannel.write(buffer);

            // Fipping and clearing the buffer from what we wrote on
            buffer.flip();
            buffer.clear();
            clientChannel.read(buffer);

            String effectiveOutput = new String(buffer.array());
            System.out.println(effectiveOutput);

            // Closing the channel
            clientChannel.close();
            
        } catch(IOException ex) { ex.printStackTrace(); } 

    }
}
