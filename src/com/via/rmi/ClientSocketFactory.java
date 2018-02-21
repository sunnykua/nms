package com.via.rmi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

public class ClientSocketFactory implements RMIClientSocketFactory {

	protected int timeout;

	public ClientSocketFactory(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(host, port), timeout);
		return socket;
	}
}