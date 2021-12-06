import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        try ( ServerSocketChannel serverChannel = ServerSocketChannel.open();
              Selector selector = Selector.open() ) {

            serverChannel.configureBlocking(false);
            serverChannel.socket().bind( new InetSocketAddress(80) );
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            Map<SelectionKey, String> messages = new HashMap<>();

            while (true) {

                selector.select();

                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();

                while (iterator.hasNext()) {

                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            SocketChannel client = serverChannel.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        } else if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(2048);
                            channel.read(buffer);
                            String read = new String(buffer.array(), StandardCharsets.UTF_8);
                            read = read.trim();
                            messages.put(key, read + " echoed by server");
                        } else if (key.isWritable() && messages.containsKey(key)) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.write(ByteBuffer.wrap(messages.get(key).getBytes(StandardCharsets.UTF_8)));
                        }
                    } catch (IOException e) {
                        key.cancel();
                        key.channel().close();
                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
