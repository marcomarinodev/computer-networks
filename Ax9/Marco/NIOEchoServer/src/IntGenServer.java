import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
 
public class IntGenServer {

    public static int PORT = 1919;

    public static void main(String[] args) {
        
        System.out.println("Listening for message to echo on port " + PORT);

        ServerSocketChannel serverChannel;
        Selector selector;
        Map<SelectionKey, String> messagesToPerform = new HashMap<>();

        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            extracted(serverChannel, selector);
        } catch (IOException ex) { ex.printStackTrace(); return; }

        while (true) {
            try {
                selector.select();
            } catch (IOException ex) { ex.printStackTrace(); return; }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey currentKey = iterator.next();
                // remove key from selected set (ready keys), but not from Registered Set
                iterator.remove();

                try {
                    if (currentKey.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) currentKey.channel();
                        SocketChannel client = server.accept();

                        System.out.println("Accepted connection from " + client);

                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        
                    } else if (currentKey.isReadable()) {
                        
                        // Saving the incoming message (request)
                        SocketChannel clientChannel = (SocketChannel) currentKey.channel();
                        // Server is agnostic about the size of the message
                        ByteBuffer messageBuffer = ByteBuffer.allocate(256);

                        clientChannel.read(messageBuffer);

                        String readableMessage = new String(messageBuffer.array());
                        
                        messagesToPerform.put(currentKey, readableMessage);

                    } else if (currentKey.isWritable() && messagesToPerform.containsKey(currentKey)) {

                        // Response
                        SocketChannel clientChannel = (SocketChannel) currentKey.channel();
                        ByteBuffer responseAsBuffer = ByteBuffer.wrap(messagesToPerform.get(currentKey).getBytes());

                        clientChannel.write(responseAsBuffer);
                    }


                } catch (IOException ex) {
                    currentKey.cancel();

                    try {
                        currentKey.channel().close();
                    } catch (IOException closeEx) {}
                }
            }
        }

    }

    private static void extracted(ServerSocketChannel serverChannel, Selector selector) throws IOException, ClosedChannelException {
        
        ServerSocket serverSocket = serverChannel.socket();

        serverSocket.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
}