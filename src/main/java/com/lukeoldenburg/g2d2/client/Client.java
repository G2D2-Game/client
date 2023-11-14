package com.lukeoldenburg.g2d2.client;

import com.google.gson.JsonObject;
import com.lukeoldenburg.g2d2.client.gfx.GamePanel;
import com.lukeoldenburg.g2d2.client.gfx.ScreenUtil;
import com.lukeoldenburg.g2d2.client.gfx.ui.Text;
import com.lukeoldenburg.g2d2.server.JsonUtil;
import com.lukeoldenburg.g2d2.server.entity.Entity;
import com.lukeoldenburg.g2d2.server.entity.Player;
import com.lukeoldenburg.g2d2.server.level.Coordinate;
import com.lukeoldenburg.g2d2.server.level.Level;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client {
	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
	private static JsonObject config;
	private static Level level;
	private static ClientSocket socket;
	private static List<Entity> entities = new ArrayList<>();
	private static int myselfIndex;
	private static JFrame gameFrame;
	private static GamePanel gamePanel;

	public static JFrame getGameFrame() {
		return gameFrame;
	}

	public static GamePanel getGamePanel() {
		return gamePanel;
	}

	public static void setMyselfIndex() {
		entities.forEach(entity -> {
			if (entity instanceof Player) {
				if (((Player) entity).getSteamId() == config.get("steamId").getAsLong()) {
					Client.myselfIndex = entities.indexOf(entity);
				}
			}
		});
	}

	public static JsonObject getConfig() {
		return config;
	}

	public static ClientSocket getSocket() {
		return socket;
	}

	public static Level getLevel() {
		return level;
	}

	public static List<Entity> getEntities() {
		return entities;
	}

	public static void setEntities(List<Entity> entities) {
		Client.entities = entities;
	}

	public static void main(String[] args) {
		LOGGER.info("Loading...");
		registerHooks();
		loadConfig();
		if (config.get("opengl").getAsBoolean()) System.setProperty("sun.java2d.opengl", "True");
		else System.setProperty("sun.java2d.opengl", "False");

		setLookAndFeel();
		gameFrame = new JFrame();
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setResizable(false);
		gameFrame.setTitle("G2D2");
		gamePanel = new GamePanel();
		gameFrame.add(gamePanel);
		gameFrame.pack();
		gameFrame.setLocationRelativeTo(null);
		gameFrame.setVisible(true);
		LOGGER.info("Initialized window");

		// TESTING
		entities.add(new Player(new Coordinate(100, 100), 100, System.nanoTime(), "Player"));
		entities.add(new Player(new Coordinate(99, 99), 100, config.get("steamId").getAsLong(), config.get("name").getAsString()));
		entities.forEach((entity) -> {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				gamePanel.ui.children.add(new Text(0, 0, null, player.getName(), gamePanel.font.deriveFont(40f), Color.white) {
					long steamId = player.getSteamId();

					@Override
					public void refresh(Graphics2D g2) {
						super.refresh(g2);
						g2.setFont(getFont());
						Client.getEntities().forEach((entity) -> {
							if (((Player) entity).getSteamId() == steamId) {
								Coordinate coordinate = entity.getCoordinate();
								if (ScreenUtil.isInBounds(coordinate, Client.getMyself().getCoordinate())) {
									visible = true;
									x = (int) ScreenUtil.coordinateToPoint(coordinate, Client.getMyself().getCoordinate()).getX();
									y = (int) ScreenUtil.coordinateToPoint(coordinate, Client.getMyself().getCoordinate()).getY() - ScreenUtil.scaledTileSize / 4;
									x += ScreenUtil.scaledTileSize / 2 - g2.getFontMetrics().stringWidth(((Player) entity).getName()) / 2;
									if (entity == Client.getMyself()) {
										x -= ScreenUtil.scaledTileSize / 2;
										y -= ScreenUtil.scaledTileSize / 2;
									}

								} else {
									visible = false;
								}
							}
						});
					}
				});
			}
		});

		setMyselfIndex();
		level = new Level("level.bin.gz", 200, System.nanoTime());
		level.generateLevel();
//		try {
//			JsonObject server = config.get("servers").getAsJsonArray().get(0).getAsJsonObject();
//			Map<String, String> httpHeaders = new HashMap<>();
//			httpHeaders.put("password", server.get("password").getAsString());
//			httpHeaders.put("name", config.get("name").getAsString());
//			httpHeaders.put("steamId", config.get("steamId").getAsString());
//			socket = new ClientSocket(new URI(server.get("ip").getAsString()), httpHeaders);
//			socket.connect();
//
//		} catch (URISyntaxException e) {
//			LOGGER.error("Failed to connect to server", e);
//		}
	}

	private static void loadConfig() {
		config = Objects.requireNonNullElse(JsonUtil.readJsonFile("config.json", JsonObject.class), new JsonObject());
		if (!config.has("opengl")) config.addProperty("opengl", true);
		if (!config.has("resolution")) config.addProperty("resolution", "1280x720");
		if (!config.has("maxFps")) config.addProperty("maxFps", 60);
		if (!config.has("uiScale")) config.addProperty("uiScale", 1);
		if (!config.has("name")) config.addProperty("name", "Player");
		if (!config.has("steamId")) config.addProperty("steamId", System.nanoTime());
		if (!config.has("keybinds")) config.add("keybinds", new JsonObject());
		JsonObject keybinds = config.get("keybinds").getAsJsonObject();
		if (!keybinds.has("K114")) keybinds.addProperty("K114", "debug");
		if (!keybinds.has("K27")) keybinds.addProperty("K27", "settings");
		if (!keybinds.has("K67")) keybinds.addProperty("K67", "inventory");
		if (!keybinds.has("K70")) keybinds.addProperty("K70", "interact");
		if (!keybinds.has("M1")) keybinds.addProperty("M1", "use");
		if (!keybinds.has("K77")) keybinds.addProperty("K77", "minimap");
		if (!keybinds.has("K9")) keybinds.addProperty("K9", "info");
		if (!keybinds.has("K87")) keybinds.addProperty("K87", "up");
		if (!keybinds.has("K83")) keybinds.addProperty("K83", "down");
		if (!keybinds.has("K65")) keybinds.addProperty("K65", "left");
		if (!keybinds.has("K68")) keybinds.addProperty("K68", "right");
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) {
			LOGGER.error("Failed to set look and feel", e);
		}
	}

	private static void registerHooks() {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> LOGGER.error("There was an uncaught exception in thread {}", thread.getName(), throwable));
		Runtime.getRuntime().addShutdownHook(new Thread(Client::shutdown));
	}

	public static void fireAction(String action) {
		switch (action) {
			case "debug" -> {
				gamePanel.debugContainer.visible = !gamePanel.debugContainer.visible;
			}
			case "settings" -> {
			}
			case "inventory" -> {
			}
			case "interact" -> {
			}
			case "use" -> {
			}
			case "minimap" -> {
			}
			case "info" -> {
			}
			case "up" -> getMyself().getCoordinate().translate(0, -getMyself().getSpeed(), level.getSize());
			case "down" -> getMyself().getCoordinate().translate(0, getMyself().getSpeed(), level.getSize());
			case "left" -> getMyself().getCoordinate().translate(-getMyself().getSpeed(), 0, level.getSize());
			case "right" -> getMyself().getCoordinate().translate(getMyself().getSpeed(), 0, level.getSize());
		}
	}

	public static Player getMyself() {
		return (Player) entities.get(myselfIndex);
	}

	private static void shutdown() {
		LOGGER.info("Saving...");
		JsonUtil.writeJsonFile("config.json", config);
		LogManager.shutdown();
	}
}