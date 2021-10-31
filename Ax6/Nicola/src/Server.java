import java.io.*;
import java.net.*;
import java.util.Date;

public class Server implements Runnable{
    private Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        String path = "";
        char c;
        BufferedReader reader = null;
        PrintWriter pw = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());

            // Leggi la prima riga
            path = reader.readLine();
            // Rimuovi GET / all'inizio
            path = path.substring(5, path.length());
            // Rimuovi da " HTTP" in poi
            int httpIndex = path.indexOf("HTTP");
            path = path.substring(0, httpIndex - 1);

            // Carica il file
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(path));
            byte[] toSend = input.readAllBytes();
            input.close();

            pw.println("HTTP/1.1 200 OK");
            pw.println("Server: Test server");
            pw.println("Date: " + new Date());
            pw.println();
            pw.flush();

            // Invia il file
            for (int i = 0; i < toSend.length; i++)
                socket.getOutputStream().write(toSend[i]);

        } catch (FileNotFoundException e) {
            sendError("Couldn't find file " + path);
        } catch (IOException e) {
            System.err.println("Communication error ");
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                socket.close();
            }
            catch (IOException e) {
                System.out.println("Couldn't correctly close the connection");
            }

        }
    }

    private void sendError(String err) {
        try {
            (new PrintWriter(socket.getOutputStream())).println(err);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket acceptSocket = new ServerSocket(6789, 0, null);

        while (true) {
            Socket s = acceptSocket.accept();
            (new Thread(new Server(s))).start();
        }
    }
}
