import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static final String toAppend = " echoed by server";
    private static final int port = 1234;

    public static String trim(String toTrim) {
        String ret = "";
        int i = 0;

        while (toTrim.charAt(i) != 0) {
            ret += toTrim.charAt(i);
            i++;
        }

        return ret;
    }

    public static void main(String[] args) throws IOException {
        HashMap<SelectionKey, String> requests = new HashMap<>();
        Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);

        server.bind(address);
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIt = readyKeys.iterator();

            while (keyIt.hasNext()) {
                SelectionKey currKey = keyIt.next();
                keyIt.remove();

                try {
                    if (currKey.isAcceptable()) {
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client.getLocalAddress());

                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    } else if (currKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) currKey.channel();
                        ByteBuffer dest = ByteBuffer.allocate(4096);
                        StringBuilder builder = new StringBuilder();

                        channel.read(dest);
                        builder.append(Server.trim(new String(dest.array())));
                        builder.append(toAppend);

                        requests.put(currKey, builder.toString());
                    } else if (currKey.isWritable() && requests.get(currKey) != null) {
                        SocketChannel channel = (SocketChannel) currKey.channel();
                        channel.write(ByteBuffer.wrap(requests.get(currKey).getBytes()));
                    }
                }
                catch (IOException e) {
                    currKey.cancel();
                    System.out.println("Closed connection");
                }
            }
        }

    }
}
