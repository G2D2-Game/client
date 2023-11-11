package com.lukeoldenburg.g2d2.client.gfx;

import com.lukeoldenburg.g2d2.client.Client;
import com.lukeoldenburg.g2d2.client.InputHandler;
import com.lukeoldenburg.g2d2.server.entity.Entity;
import com.lukeoldenburg.g2d2.server.level.Coordinate;
import com.lukeoldenburg.g2d2.server.level.Level;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {
	Thread renderThread;
	InputHandler inputHandler = new InputHandler();
	BufferedImage grassImage;
	BufferedImage waterImage;

	public GamePanel() {
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

		try {
			grassImage = ImageUtil.getCompatibleImage(ImageUtil.getScaledImage(ImageIO.read(new File("grass.png"))));
			waterImage = ImageUtil.getCompatibleImage(ImageUtil.getScaledImage(ImageIO.read(new File("water.png"))));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

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

			// PLAYER
			g2.setColor(Color.white);
			int offset = ScreenUtil.scaledTileSize / 2;
			g2.fillRect((int) (playerPoint.getX() - offset), (int) (playerPoint.getY() - offset), ScreenUtil.scaledTileSize, ScreenUtil.scaledTileSize);

			// ENTITIES
			for (Entity entity : Client.getEntities()) {
				if (entity != Client.getMyself() && ScreenUtil.isInBounds(entity.getCoordinate(), playerCoordinate)) {
					Coordinate entityCoordinate = entity.getCoordinate();
					Point entityPoint = ScreenUtil.coordinateToPoint(entityCoordinate, playerCoordinate);
					g2.setColor(Color.red);
					g2.fillRect((int) entityPoint.getX(), (int) entityPoint.getY(), ScreenUtil.scaledTileSize, ScreenUtil.scaledTileSize);
				}
			}
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