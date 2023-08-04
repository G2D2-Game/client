package com.lukeoldenburg.g2d2.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class ClientSocket extends WebSocketClient {
	public ClientSocket(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		Client.getLogger().info("new connection opened");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		Client.getLogger().info("closed with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(String message) {
		Client.getLogger().info("received message: " + message);
	}

	@Override
	public void onMessage(ByteBuffer message) {
		Client.getLogger().info("received ByteBuffer");
	}

	@Override
	public void onError(Exception ex) {
		Client.getLogger().error("", ex);
	}
}