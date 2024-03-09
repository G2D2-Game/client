package com.lukeoldenburg.g2d2.client.gfx;

import com.lukeoldenburg.g2d2.client.Client;
import com.lukeoldenburg.g2d2.client.InputHandler;
import com.lukeoldenburg.g2d2.client.gfx.ui.Container;
import com.lukeoldenburg.g2d2.client.gfx.ui.Text;
import com.lukeoldenburg.g2d2.client.gfx.ui.UI;
import com.lukeoldenburg.g2d2.client.gfx.ui.UIElement;
import com.lukeoldenburg.g2d2.server.entity.Entity;
import com.lukeoldenburg.g2d2.server.level.Coordinate;
import com.lukeoldenburg.g2d2.server.level.Level;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable {
	public static Font font = null;
	static {
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(UI.class.getResourceAsStream("/font/HelvetiPixel.ttf")));

		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	private final UI ui = new UI();
	private Thread renderThread;
	private final InputHandler inputHandler = new InputHandler();
	private BufferedImage grassImage;
	private BufferedImage waterImage;

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
			e.printStackTrace();
		}

		// DEBUG INFO
		Container debugContainer = new Container("debug_container", ui, 0, 10, 10) {
			@Override
			public void refresh(Graphics2D g2) {
				super.refresh(g2);
				visible = Client.isDebugMode();
			}
		};
		debugContainer.addChild(new Text("debug_text", debugContainer, 0, 0, 0) {
			@Override
			public void refresh(Graphics2D g2) {
				super.refresh(g2);
				setText("DEBUG INFO\n"
						+ "Version: " + Client.VERSION + "\n"
						+ "Steam ID: " + Client.getConfig().get("steamId").getAsLong() + "\n"
						+ "Resolution: " + Client.getConfig().get("resolution").getAsString() + "\n"
						+ "OpenGL: " + Client.getConfig().get("opengl").getAsBoolean() + "\n"
						+ "FPS: " + Client.getConfig().get("maxFps").getAsInt() + "\n"
						+ "Level Seed: " + Client.getLevel().getSeed() + "\n"
						+ "Level Size: " + Client.getLevel().getSize() + "\n"
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
		debugContainer.addChild(new Text("ui_info_text", debugContainer, 0, 0, 0) {
			@Override
			public void refresh(Graphics2D g2) {
				super.refresh(g2);
				String text = "HOVERED ELEMENTS\n";
				for (UIElement element : UI.hoveredElements) {
					text += "Element: " + element.getId() + "\n";
					if (parentElement != null) text += "Parent Element: " + element.getParentElement().getId() + "\n";
					else text += "Parent Element: null\n";
					text += "Render Priority: " + element.getRenderPriority() + "\n";
					text += "Point: java.awt.Point[x=" + element.getX() + "," + element.getY() + "]\n";
					text += "Dimensions: " + element.getWidth(g2) + "x" + element.getHeight(g2) + "\n";
					String children = "Children: [";
					for (UIElement child : element.getChildren()) {
						children += child.getId() + ", ";
					}
					children = children.substring(0, children.length() - 2);
					if (element.getChildren().size() > 0) text += children + "]\n\n";
					else text += "Children: []\n\n";
				}
				setText(text);
			}
		});
		ui.addChild(debugContainer);

		// PANEL
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(inputHandler);
		this.addMouseListener(inputHandler);
		this.setFocusable(true);
		startRenderThread();
	}

	@Override
	public void paintComponent(Graphics g) {
		Level level = Client.getLevel();
		if (level == null || !level.isLoaded()) return;
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Coordinate playerCoordinate = Client.getMyself().getCoordinate();

		// LEVEL
		short[][] tiles = level.getBgTiles();
		for (int x = ScreenUtil.getLeftBound(playerCoordinate); x < ScreenUtil.getRightBound(playerCoordinate); x++) {
			for (int y = ScreenUtil.getUpperBound(playerCoordinate); y < ScreenUtil.getLowerBound(playerCoordinate); y++) {
				Point tilePoint = ScreenUtil.coordinateToPoint(new Coordinate(x, y), playerCoordinate);
				if (tiles[x][y] == 3) g2.drawImage(grassImage, (int) tilePoint.getX(), (int) tilePoint.getY(), null);
				else g2.drawImage(waterImage, (int) tilePoint.getX(), (int) tilePoint.getY(), null);
			}
		}

		// ENTITIES
		g2.setColor(Color.white);
		for (Entity entity : Client.getEntities()) {
			if (!ScreenUtil.isInBounds(entity.getCoordinate(), playerCoordinate)) continue;
			Coordinate entityCoordinate = entity.getCoordinate();
			Point entityPoint = ScreenUtil.coordinateToPoint(entityCoordinate, playerCoordinate);
			if (entity == Client.getMyself())
				g2.fillRect((int) entityPoint.getX() - ScreenUtil.scaledTileSize / 2, (int) entityPoint.getY() - ScreenUtil.scaledTileSize / 2, ScreenUtil.scaledTileSize, ScreenUtil.scaledTileSize);
			else
				g2.fillRect((int) entityPoint.getX(), (int) entityPoint.getY(), ScreenUtil.scaledTileSize, ScreenUtil.scaledTileSize);
		}

		// UI
		UI.hoveredElements = new ArrayList<>();
		for (UIElement uiElement : ui.getChildren())
			if (getMousePosition() != null && uiElement.isVisible() && uiElement.contains(g2, getMousePosition()))
				uiElement.onHover(g2, getMousePosition());
		ui.refresh(g2);
		ui.draw(g2);

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

			if (timer >= 1000000000) timer = 0;
		}
	}

	public void startRenderThread() {
		renderThread = new Thread(this);
		renderThread.start();
	}

	public UI getUi() {
		return ui;
	}
}