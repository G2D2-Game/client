package com.lukeoldenburg.g2d2.client.gfx;

import com.lukeoldenburg.g2d2.client.Client;
import com.lukeoldenburg.g2d2.client.InputHandler;
import com.lukeoldenburg.g2d2.client.gfx.ui.Container;
import com.lukeoldenburg.g2d2.client.gfx.ui.Text;
import com.lukeoldenburg.g2d2.client.gfx.ui.UI;
import com.lukeoldenburg.g2d2.server.level.Coordinate;
import com.lukeoldenburg.g2d2.server.level.Level;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable {
	public UI ui = new UI();
	public Container debugContainer = new Container(10, 10);
	public Font font;
	Thread renderThread;
	InputHandler inputHandler = new InputHandler();
	BufferedImage grassImage;
	BufferedImage waterImage;

	public GamePanel() {
		// RESOLUTION
		switch (Client.getConfig().get("resolution").getAsString()) {
			// 720p (HD)
			default -> ScreenUtil.initializeScreenData(this, 1280, 720);
			// 1080p (Full HD)
			case "1920x1080" -> ScreenUtil.initializeScreenData(this, 1920, 1080);
			// 1440p (QHD)
			case "2560x1440" -> ScreenUtil.initializeScreenData(this, 2560, 1440);
			// 2160p (4K UHD)
			case "3840x2160" -> ScreenUtil.initializeScreenData(this, 3840, 2160);
			// 4320p (8K UHD)
			case "7680x4320" -> ScreenUtil.initializeScreenData(this, 7680, 4320);
		}

		// TILE IMAGES
		try {
			grassImage = ImageUtil.getCompatibleImage(ImageUtil.getScaledImage(ImageIO.read(new File("grass.png"))));
			waterImage = ImageUtil.getCompatibleImage(ImageUtil.getScaledImage(ImageIO.read(new File("water.png"))));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// UI
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(UI.class.getResourceAsStream("/font/HelvetiPixel.ttf")));

		} catch (FontFormatException | IOException | NullPointerException e) {
			throw new RuntimeException(e);
		}

		debugContainer.children.add(new Text(debugContainer, "", font.deriveFont(30f), Color.white) {
			@Override
			public void refresh(Graphics2D g2) {
				super.refresh(g2);
				setText("Resolution: " + Client.getConfig().get("resolution").getAsString() + "\n"
						+ "FPS: " + Client.getConfig().get("maxFps").getAsInt() + "\n"
						+ "OpenGL: " + Client.getConfig().get("opengl").getAsBoolean() + "\n"
						+ "Steam ID: " + Client.getConfig().get("steamId").getAsLong() + "\n"
						+ "Level Size: " + Client.getLevel().getSize() + "\n"
						+ "Level Seed: " + Client.getLevel().getSeed() + "\n"
						+ "Left Bound: " + ScreenUtil.getLeftBound(Client.getMyself().getCoordinate()) + "\n"
						+ "Right Bound: " + ScreenUtil.getRightBound(Client.getMyself().getCoordinate()) + "\n"
						+ "Upper Bound: " + ScreenUtil.getUpperBound(Client.getMyself().getCoordinate()) + "\n"
						+ "Lower Bound: " + ScreenUtil.getLowerBound(Client.getMyself().getCoordinate()) + "\n"
						+ "Player Point: " + ScreenUtil.getPlayerPoint(Client.getMyself().getCoordinate()) + "\n"
						+ "Player Coordinate: " + Client.getMyself().getCoordinate() + "\n"
						+ "Mouse Point: " + Objects.requireNonNullElse(Client.getGameFrame().getMousePosition(), new Point(0, 0)) + "\n"
						+ "Mouse Coordinate: " + ScreenUtil.pointToCoordinate(Objects.requireNonNullElse(Client.getGameFrame().getMousePosition(), new Point(0, 0)), Client.getMyself().getCoordinate()));
			}
		});
		debugContainer.lockedWidth = true;
		debugContainer.width = 635;
		ui.children.add(debugContainer);

		// PANEL
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(inputHandler);
		this.addMouseListener(inputHandler);
		this.setFocusable(true);
		startRenderThread();
	}

	public void startRenderThread() {
		renderThread = new Thread(this);
		renderThread.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Level level = Client.getLevel();
		if (level != null && level.isLoaded()) {
			Coordinate playerCoordinate = Client.getMyself().getCoordinate();
			Point playerPoint = ScreenUtil.getPlayerPoint(playerCoordinate);

			// LEVEL
			short[][] tiles = level.getBgTiles();
			for (int x = ScreenUtil.getLeftBound(playerCoordinate); x < ScreenUtil.getRightBound(playerCoordinate); x++) {
				for (int y = ScreenUtil.getUpperBound(playerCoordinate); y < ScreenUtil.getLowerBound(playerCoordinate); y++) {
					Point tilePoint = ScreenUtil.coordinateToPoint(new Coordinate(x, y), playerCoordinate);
					if (tiles[x][y] == 3) {
						g2.drawImage(grassImage, (int) tilePoint.getX(), (int) tilePoint.getY(), null);

					} else {
						g2.drawImage(waterImage, (int) tilePoint.getX(), (int) tilePoint.getY(), null);
					}
				}
			}

			// ENTITIES
			g2.setColor(Color.white);
			Client.getEntities().forEach((entity -> {
				if (ScreenUtil.isInBounds(entity.getCoordinate(), playerCoordinate)) {
					Coordinate entityCoordinate = entity.getCoordinate();
					Point entityPoint = ScreenUtil.coordinateToPoint(entityCoordinate, playerCoordinate);
					if (entity == Client.getMyself()) {
						g2.fillRect((int) entityPoint.getX() - ScreenUtil.scaledTileSize / 2, (int) entityPoint.getY() - ScreenUtil.scaledTileSize / 2, ScreenUtil.scaledTileSize, ScreenUtil.scaledTileSize);

					} else {
						g2.fillRect((int) entityPoint.getX(), (int) entityPoint.getY(), ScreenUtil.scaledTileSize, ScreenUtil.scaledTileSize);
					}
				}
			}));

			// UI
			ui.refresh(g2);
			ui.draw(g2);
		}

		g2.dispose();
	}

	@Override
	public void run() {
		double drawInterval = 1000000000 / Client.getConfig().get("maxFps").getAsFloat();
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;

		while (renderThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;

			if (delta >= 1) {
				repaint();
				delta--;
			}

			if (timer >= 1000000000) {
				timer = 0;
			}
		}
	}
}