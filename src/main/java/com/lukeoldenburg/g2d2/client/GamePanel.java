package com.lukeoldenburg.g2d2.client;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
	Thread renderThread;
	String resolution = "1280x720";
	float fps = 60;

	public GamePanel() {
		if (Client.getConfig().has("resolution")) resolution = Client.getConfig().get("resolution").getAsString();
		switch (resolution) {
			// 720p (HD)
			default -> this.setResolution(1280, 720);
			// 1080p (Full HD)
			case "1920x1080" -> this.setResolution(1920, 1080);
			// 1440p (QHD)
			case "2560x1440" -> this.setResolution(2560, 1440);
			// 2160p (4K UHD)
			case "3840x2160" -> this.setResolution(3840, 2160);
			// 4320p (8K UHD)
			case "7680x4320" -> this.setResolution(7680, 4320);
		}

		if (Client.getConfig().has("fps")) fps = Client.getConfig().get("fps").getAsFloat();

		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		startRenderThread();
	}

	public void setResolution(int width, int height) {
		DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		if (width < dm.getWidth() && height < dm.getHeight()) this.setPreferredSize(new Dimension(width, height));
		else this.setPreferredSize(new Dimension(1280, 720));
		Client.getConfig().addProperty("resolution", width + "x" + height);
	}

	public void startRenderThread() {
		renderThread = new Thread(this);
		renderThread.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.dispose();
	}

	@Override
	public void run() {
		double drawInterval = 1000000000 / fps;
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