package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

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
			byte[] incomingData = "CiaoSonoMarco".getBytes(StandardCharsets.UTF_8);
			DatagramPacket request = new DatagramPacket(incomingData, incomingData.length, host, port);
			byte[] data = new byte[1024];
			DatagramPacket response = new DatagramPacket(data, data.length);

			socket.send(request);

			socket.receive(request);

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
