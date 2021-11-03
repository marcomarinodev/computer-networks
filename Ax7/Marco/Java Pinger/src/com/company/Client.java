package com.company;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

class Main {

	public static void main(String[] args) {

		InetAddress host;
		int port;

		if (args.length < 2) {
			System.out.println("You need to insert: server name and its port");
			return;
		}

		host = checkHostname(args[0]);
		port = checkPort(args[1]);

		if (host == null) {
			System.out.println("ERR -arg hostname");
			return;
		}

		if (port == -1) {
			System.out.println("ERR -arg port");
			return;
		}

		int received = 0;
		int totalDelay = 0;
		double minRTT = Double.POSITIVE_INFINITY;
		double maxRTT = Double.NEGATIVE_INFINITY;
		double avgRTT = 0;

		for (int i = 0; i < 10; i++) {
			try (DatagramSocket socket = new DatagramSocket(52477)) {

				socket.setSoTimeout(2000);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				byte[] data = new byte[100];
				DatagramPacket req = new DatagramPacket(data, data.length, host, port);
				DatagramPacket receiverPacket = new DatagramPacket(data, data.length);

				long now = System.currentTimeMillis();

				dos.writeUTF("PING");
				data = baos.toByteArray();
				req.setData(data, 0, data.length);
				req.setLength(data.length);
				socket.send(req);
				baos.reset();

				dos.writeLong(i);
				data = baos.toByteArray();
				req.setData(data, 0, data.length);
				req.setLength(data.length);
				socket.send(req);
				baos.reset();

				dos.writeLong(System.currentTimeMillis());
				data = baos.toByteArray();
				req.setData(data, 0, data.length);
				req.setLength(data.length);
				socket.send(req);
				baos.reset();

				ByteArrayInputStream bais = new ByteArrayInputStream(receiverPacket.getData(),
						0, receiverPacket.getLength());
				DataInputStream dis = new DataInputStream(bais);
				socket.receive(receiverPacket);
				// bais = new ByteArrayInputStream(receiverPacket.getData(),
						// 0, receiverPacket.getLength());
				// dis = new DataInputStream(bais);
				// String response = dis.readUTF();

				long delta = System.currentTimeMillis() - now;
 				System.out.println("PING " + i + " RTT: " + delta + " ms");
				received++;
				totalDelay += delta;
				minRTT = Math.min(minRTT, delta);
				maxRTT = Math.max(maxRTT, delta);
				avgRTT = totalDelay / received;

			} catch (IOException ex) {
				System.out.println("PING " + i + " RTT: *");
			}
		}

		System.out.println("--- PING Statistics ---");
		System.out.println("10 packets transmitted, " + received + " packets received, " +
				(100 - (received * 10)) + "% packet loss");
		System.out.println("round-trip (ms) min/avg/max = " + (int) minRTT + "/" + avgRTT + "/" +
				(int) maxRTT);

	}

	private static int checkPort(String port) {
		return (Integer.valueOf(port) > 1023) ? Integer.valueOf(port) : -1;
	}

	private static InetAddress checkHostname(String hostname) {
		try {
			InetAddress address = InetAddress.getByName(hostname);
			return address;
		} catch (UnknownHostException e) {
			return null;
		}
	}


}
