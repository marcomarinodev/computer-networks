package com.company;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Server {

    public static void main(String[] args) throws Exception {

       // if (badArgs(args)) return;

        DatagramSocket serverSock = new DatagramSocket(40000);
        byte[] buffer = new byte[100];
        DatagramPacket receiverPacket = new DatagramPacket(buffer, buffer.length);
        String mode;
        long seqnum;
        long delay;
        long finalDelay;

        while (true) {
            System.out.println("Listening...");
            for (int i = 0; i < 10; i++) {

                // Generating delay
                //Random ran = new Random(Integer.valueOf(args[1]));
                Random ran = new Random(123);

                serverSock.receive(receiverPacket);
                ByteArrayInputStream bais = new ByteArrayInputStream(receiverPacket.getData(),
                        0, receiverPacket.getLength());
                DataInputStream dis = new DataInputStream(bais);
                mode = dis.readUTF();

                serverSock.receive(receiverPacket);
                bais = new ByteArrayInputStream(receiverPacket.getData(),
                        0, receiverPacket.getLength());
                dis = new DataInputStream(bais);
                seqnum = dis.readLong();

                serverSock.receive(receiverPacket);
                bais = new ByteArrayInputStream(receiverPacket.getData(),
                        0, receiverPacket.getLength());
                dis = new DataInputStream(bais);

                sleep(Math.abs(ran.nextInt()) % 1000);

                finalDelay = System.currentTimeMillis() - dis.readLong();

                DatagramPacket req = new DatagramPacket(buffer, buffer.length, receiverPacket.getAddress(), receiverPacket.getPort());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);

                dos.writeUTF("Send it back");
                buffer = baos.toByteArray();
                req.setData(buffer, 0, buffer.length);
                req.setLength(buffer.length);
                serverSock.send(req);
                baos.reset();

                System.out.println(receiverPacket.getAddress() + ":" +
                        receiverPacket.getPort() + "> " + mode + " " + i + " " + seqnum + " ACTION: " +
                        "-" + " " + finalDelay + " ms");
            }
        }
    }

    private static boolean badArgs(String[] args) {
        if (args.length < 2) {
            System.out.println("You need to insert: server name and its port");
            return true;
        }

        if (checkPort(args[0])) {
            System.out.println("ERR -arg port");
            return true;
        }

        if (checkSeed(args[1])) {
            System.out.println("ERR -arg seed");
            return true;
        }
        return false;
    }

    private static boolean checkSeed(String arg) {
        return Integer.valueOf(arg) > 0;
    }

    private static boolean checkPort(String arg) {
        return Integer.valueOf(arg) > 1023;
    }
}
