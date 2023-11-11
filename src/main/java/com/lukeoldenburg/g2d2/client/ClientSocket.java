package com.lukeoldenburg.g2d2.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;

public class ClientSocket extends WebSocketClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientSocket.class);

	public ClientSocket(URI serverUri, Map<String, String> httpHeaders) {
		super(serverUri, httpHeaders);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		LOGGER.info("Connected to server at: " + getURI().getHost() + ":" + getURI().getPort());
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		LOGGER.info("Closed with exit code " + code + " because " + reason);
	}

	@Override
	public void onMessage(String message) {
	}

	@Override
	public void onMessage(ByteBuffer message) {
	}

	@Override
	public void onError(Exception ex) {
		LOGGER.error("A websocket error occurred: ", ex);
	}
}