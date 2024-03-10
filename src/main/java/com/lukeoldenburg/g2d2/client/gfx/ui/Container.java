package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;

public class Container extends UIElement {
	private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 210);
	private static final Color BORDER_COLOR = new Color(255, 255, 255);
	private boolean lockedWidth, lockedHeight;

	public Container(String id, UIElement parentElement, int renderPriority, int x, int y) {
		super(id, parentElement, renderPriority, x, y);
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(BACKGROUND_COLOR);
		g2.fillRoundRect(x, y, width + 1, height + 1, 35, 35);

		g2.setColor(BORDER_COLOR);
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);

		for (UIElement uiElement : children)
			if (uiElement.visible) uiElement.draw(g2);
		super.draw(g2);
	}

	@Override
	public void refresh(Graphics2D g2) {
		super.refresh(g2);
		children.sort((a, b) -> Integer.compare(b.renderPriority, a.renderPriority));

		int currentX = x + 20;
		int currentY = y + 20;
		for (UIElement uiElement : children) {
			if (!uiElement.visible) continue;
			uiElement.x = currentX;
			uiElement.y = currentY;

			currentY += uiElement.getHeight(g2) + 10;
		}

		getWidth(g2);
		getHeight(g2);
	}

	@Override
	public int getWidth(Graphics2D g2) {
		if (lockedWidth) return width;
		width = 0;
		for (UIElement uiElement : children)
			if (uiElement.visible) width = Math.max(width, uiElement.getWidth(g2));

		width += 40;
		return width;
	}

	@Override
	public int getHeight(Graphics2D g2) {
		if (lockedHeight) return height;
		height = 0;
		for (UIElement uiElement : children)
			if (uiElement.visible) height += uiElement.getHeight(g2) + 10;

		height += 20;
		return height;
	}

	public void lockWidth(int width) {
		lockedWidth = true;
		this.width = width;
	}

	public void unlockWidth() {
		lockedWidth = false;
	}

	public void lockHeight(int height) {
		lockedHeight = true;
		this.height = height;
	}

	public void unlockHeight() {
		lockedHeight = false;
	}
}