package com.company;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class Server {

    public static void main(String[] args) throws Exception {

        DatagramSocket serverSock = new DatagramSocket(40000);
        byte[] buffer = new byte[128];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

        while (true) {
            System.out.println("Listening...");
            serverSock.receive(receivedPacket);
            String byteToString = new String(receivedPacket.getData(),
                    0, receivedPacket.getLength(), StandardCharsets.UTF_8);
            System.out.println(byteToString);
        }
    }
}
