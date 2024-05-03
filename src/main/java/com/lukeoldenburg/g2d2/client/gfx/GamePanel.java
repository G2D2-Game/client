package com.lukeoldenburg.g2d2.client.gfx;

import com.lukeoldenburg.g2d2.client.Client;
import com.lukeoldenburg.g2d2.client.InputHandler;
import com.lukeoldenburg.g2d2.client.gfx.ui.CheckBox;
import com.lukeoldenburg.g2d2.client.gfx.ui.Container;
import com.lukeoldenburg.g2d2.client.gfx.ui.*;
import com.lukeoldenburg.g2d2.server.entity.Entity;
import com.lukeoldenburg.g2d2.server.level.Coordinate;
import com.lukeoldenburg.g2d2.server.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(GamePanel.class);
	public static Font font = null;
	private int frames = 0;
	private int lastFrames = 0;

	static {
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(UI.class.getResourceAsStream("/font/HelvetiPixel.ttf")));

		} catch (FontFormatException | IOException e) {
			LOGGER.error("Failed to load font", e);
		}
	}

	private final UI ui;
	private Thread renderThread;
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
			LOGGER.error("Failed to load tile images", e);
		}

		// UI
		ui = new UI();
		initializeSettingsContainer();
		initializeDebugContainer();

		// PANEL
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		InputHandler inputHandler = new InputHandler();
		this.addKeyListener(inputHandler);
		this.addMouseListener(inputHandler);
		this.setFocusable(true);
		startRenderThread();
	}

	@Override
	public void paintComponent(Graphics g) {
		frames++;
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
		UI.getHoveredElements().clear();
		for (UIElement uiElement : ui.getChildren())
			if (getMousePosition() != null && uiElement.isVisible() && uiElement.contains(g2, getMousePosition()))
				uiElement.onHover(g2, getMousePosition());
		ui.refresh(g2);
		ui.draw(g2);

		g2.dispose();
	}

	@Override
	public void run() {
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;

		while (renderThread != null) {
			double drawInterval = 1000000000 / Client.getConfig().get("maxFps").getAsFloat();
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;

			if (delta >= 1) {
				repaint();
				delta--;
			}

			if (timer >= 1000000000) {
				lastFrames = frames;
				frames = 0;
				timer = 0;
			}
		}
	}


	public void startRenderThread() {
		renderThread = new Thread(this);
		renderThread.start();
	}

	public void initializeSettingsContainer() {
		Container settingsContainer = new Container("settings_container", 0, VerticalAlignment.CENTER, HorizontalAlignment.CENTER) {
			@Override
			public void draw(Graphics2D g2) {
				g2.setColor(new Color(0, 0, 0, 160));
				g2.fillRect(0, 0, ScreenUtil.width, ScreenUtil.height);
				super.draw(g2);
			}

			@Override
			public void refresh(Graphics2D g2) {
				super.refresh(g2);
				visible = (boolean) Client.getStateInfo().get("settings_mode");
			}
		};
		settingsContainer.lockWidth(400);
		settingsContainer.lockHeight(200);
		// TODO fix alignment in settings panel
		// TODO display slider value as text
		// TODO fix hovered elements size
		VerticalStack settingsItems = new VerticalStack("settings_vs", 0, VerticalAlignment.TOP, HorizontalAlignment.CENTER);
		HorizontalStack openGlRow = new HorizontalStack("opengl_hs", 0, VerticalAlignment.TOP, HorizontalAlignment.LEFT);
		openGlRow.addChild(new Text("opengl_text", 0, VerticalAlignment.CENTER, HorizontalAlignment.LEFT, "OpenGL"));
		openGlRow.addChild(new CheckBox("opengl_checkbox", 0, VerticalAlignment.CENTER, HorizontalAlignment.LEFT) {
			@Override
			public void onClick(MouseEvent e) {
				super.onClick(e);
				Client.getConfig().addProperty("opengl", isChecked());
			}

			@Override
			public void refresh(Graphics2D g2) {
				setChecked(Client.getConfig().get("opengl").getAsBoolean());
			}
		});
		settingsItems.addChild(openGlRow);
		HorizontalStack maxFpsRow = new HorizontalStack("maxfps_hs", 0, VerticalAlignment.TOP, HorizontalAlignment.LEFT);
		maxFpsRow.addChild(new Text("maxfps_text", 0, VerticalAlignment.CENTER, HorizontalAlignment.LEFT, "Max FPS"));
		maxFpsRow.addChild(new Slider("maxfps_slider", 0, VerticalAlignment.CENTER, HorizontalAlignment.LEFT, 30, 120) {
			@Override
			public void onClick(MouseEvent e) {
				super.onClick(e);
				Client.getConfig().addProperty("maxFps", value);
			}

			@Override
			public void refresh(Graphics2D g2) {
				value = Client.getConfig().get("maxFps").getAsInt();
			}
		});
		settingsItems.addChild(maxFpsRow);
		settingsContainer.addChild(settingsItems);
		ui.addChild(settingsContainer);
	}

	public void initializeDebugContainer() {
		Container debugContainer = new Container("debug_container", 1, VerticalAlignment.TOP, HorizontalAlignment.LEFT) {
			@Override
			public void refresh(Graphics2D g2) {
				super.refresh(g2);
				visible = (boolean) Client.getStateInfo().get("debug_mode");
			}
		};
		VerticalStack debugStack = new VerticalStack("debug_vs", 0, VerticalAlignment.TOP, HorizontalAlignment.LEFT);
		debugStack.addChild(new Text("debug_text", 0, VerticalAlignment.TOP, HorizontalAlignment.LEFT, "") {
			@Override
			public void refresh(Graphics2D g2) {
				super.refresh(g2);
				setText("DEBUG INFO\n"
						+ "Version: " + Client.VERSION + "\n"
						+ "Steam ID: " + Client.getConfig().get("steamId").getAsLong() + "\n"
						+ "Resolution: " + Client.getConfig().get("resolution").getAsString() + "\n"
						+ "OpenGL: " + Client.getConfig().get("opengl").getAsBoolean() + "\n"
						+ "FPS: " + lastFrames + "\n"
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
		debugStack.addChild(new Text("ui_info_text", 0, VerticalAlignment.TOP, HorizontalAlignment.LEFT, "") {
			@Override
			public void refresh(Graphics2D g2) {
				super.refresh(g2);
				StringBuilder text = new StringBuilder("HOVERED ELEMENTS\n");
				for (UIElement element : UI.getHoveredElements()) {
					text.append("Element: ").append(element.getId()).append("\n");
					if (parentElement != null)
						text.append("Parent Element: ").append(element.getParentElement().getId()).append("\n");
					else text.append("Parent Element: null\n");
					text.append("Render Priority: ").append(element.getRenderPriority()).append("\n");
					text.append("Point: java.awt.Point[x=").append(element.getX()).append(",y=").append(element.getY()).append("]\n");
					text.append("Dimensions: ").append(element.getWidth(g2)).append("x").append(element.getHeight(g2)).append("\n");
					StringBuilder children = new StringBuilder("Children: [");
					for (UIElement child : element.getChildren()) {
						children.append(child.getId()).append(", ");
					}
					children = new StringBuilder(children.substring(0, children.length() - 2));
					if (!element.getChildren().isEmpty()) text.append(children).append("]\n\n");
					else text.append("Children: []\n\n");
				}
				setText(text.toString());
			}
		});
		debugContainer.addChild(debugStack);
		ui.addChild(debugContainer);
	}

	public UI getUi() {
		return ui;
	}
}