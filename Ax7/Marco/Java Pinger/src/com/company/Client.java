package com.company;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

		try (DatagramSocket socket = new DatagramSocket(0)) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			byte[] data = new byte[100];
			DatagramPacket req = new DatagramPacket(data, data.length, host, port);

			for (int i = 0; i < 10; i++) {
				long now = System.currentTimeMillis();

				dos.writeUTF("PING");
				data = baos.toByteArray();
				req.setData(data, 0, data.length);
				req.setLength(data.length);
				socket.send(req);
				baos.reset();

				dos.writeLong(1234);
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


//				System.out.println("PING " + i + " RTT: " + (System.currentTimeMillis() - now) + " ms");

			}

			System.out.println("--- PING Statistics ---");

		} catch (IOException ex) {
			ex.printStackTrace();
		}

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
