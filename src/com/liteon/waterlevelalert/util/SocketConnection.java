package com.liteon.waterlevelalert.util;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import android.util.Log;

public class SocketConnection implements Closeable {

	private String host;
	private int port;
	private Socket socket;
	private int connectionTimeout = 10000;
	private int soTimeout = 10000;
	private boolean broken = false;
	private DataOutputStream outputStream;
	private DataInputStream inputStream;

	public SocketConnection() {
	}

	public SocketConnection(final String host, final int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void close() throws IOException {
		disconnect();
	}

	public void disconnect() {
		if (isConnected()) {
			try {
				inputStream.close();
				if (!socket.isClosed()) {
					outputStream.close();
					socket.close();
				}
			} catch (IOException ex) {
				broken = true;
			}
		}
	}

	public boolean isConnected() {
		return socket != null && socket.isBound() && !socket.isClosed() && socket.isConnected()
				&& !socket.isInputShutdown() && !socket.isOutputShutdown();
	}

	public void connect() {
		if (!isConnected()) {
			try {
				socket = new Socket();
				socket.setReuseAddress(true);
				// Will monitor the TCP connection is valid
				socket.setKeepAlive(true);
				// Socket buffer Whether closed, to ensure timely delivery of
				// data
				socket.setTcpNoDelay(true);
				// Control calls close () method
				socket.setSoLinger(true, 0);
				socket.connect(new InetSocketAddress(host, port), connectionTimeout);
				socket.setSoTimeout(soTimeout);
				outputStream = new DataOutputStream(socket.getOutputStream());
				inputStream = new DataInputStream(socket.getInputStream());
			} catch (IOException ex) {
				broken = true;
				ex.printStackTrace();
				Log.e("Socket", ex.getMessage());
			}
		}
	}

	public boolean isBroken() {
		return broken;
	}

	protected void flush() {
		try {
			outputStream.flush();
		} catch (IOException ex) {
			broken = true;
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public void setTimeoutInfinite() {
		try {
			if (!isConnected()) {
				connect();
			}
			socket.setSoTimeout(0);
		} catch (SocketException ex) {
			broken = true;
		}
	}

	public void rollbackTimeout() {
		try {
			socket.setSoTimeout(soTimeout);
		} catch (SocketException ex) {
			broken = true;
		}
	}

	public void sendCommand(final byte[] args) {
		try {
			outputStream.write(args);
		} catch (IOException ex) {
			broken = true;
		}
	}

	public int getReply() {
		flush();
		return readWithCheckingBroken();
	}

	public int readWithCheckingBroken() {
		int new_status = -1;
		try {
			int count = inputStream.available();
            byte[] bs = new byte[count];
            inputStream.read(bs);
            int TCP_HEADER_SIZE = 6; 
            if ( count > TCP_HEADER_SIZE) {
	            int data_size = bs[TCP_HEADER_SIZE - 1];
	            int value = bs[TCP_HEADER_SIZE + data_size -1];
	            new_status = value;
            }
		} catch (IOException exc) {
			broken = true;
		}
        return new_status;
	}
}
