package com.lukeoldenburg.g2d2.client;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Objects;

public class Client {
	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private static JsonObject config;

	public static Logger getLogger() {
		return logger;
	}

	public static JsonObject getConfig() {
		return config;
	}

	public static void main(String[] args) {
		logger.info("Loading...");
		registerHooks();
		config = Objects.requireNonNullElse(JsonFile.read(Path.of("config.json"), JsonObject.class), new JsonObject());
		if (config.has("opengl") && config.get("opengl").getAsBoolean()) {
			System.setProperty("sun.java2d.opengl", "True");

		} else {
			System.setProperty("sun.java2d.opengl", "False");
		}

		setLookAndFeel();
		JFrame gameFrame = new JFrame();
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setResizable(false);
		gameFrame.setTitle("G2D2");

		GamePanel gamePanel = new GamePanel();
		gameFrame.add(gamePanel);

		gameFrame.pack();
		gameFrame.setLocationRelativeTo(null);
		gameFrame.setVisible(true);
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private static void registerHooks() {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> logger.error("There was an uncaught exception in thread {}", thread.getName(), throwable));
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Saving...");
			if (JsonFile.write(Path.of("config.json"), config)) logger.info("Successfully saved config.json");
			else logger.error("Failed to save config.json");
		}));
	}
}