package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;

public class Container extends UIElement {
	private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 200);
	private static final Color BORDER_COLOR = new Color(255, 255, 255);
	private boolean lockedWidth, lockedHeight;

	public Container(String id, int renderPriority, VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment) {
		super(id, renderPriority, verticalAlignment, horizontalAlignment);
	}

	@Override
	public void draw(Graphics2D g2) {
		if (!visible) return;
		g2.setColor(BACKGROUND_COLOR);
		g2.fillRoundRect(x, y, width + 1, height + 1, 35, 35);

		g2.setColor(BORDER_COLOR);
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);

		super.draw(g2);
	}

	@Override
	public void refresh(Graphics2D g2) {
		super.refresh(g2);
		UIElement child = children.get(0);
		if (child.getVerticalAlignment() != null) {
			switch (child.getVerticalAlignment()) {
				case TOP -> child.y = y + 20;
				case CENTER -> child.y = y + (height / 2) - (child.getHeight(g2) / 2);
				case BOTTOM -> child.y = y + height - child.getHeight(g2) - 20;
			}
		}
		if (child.getHorizontalAlignment() != null) {
			switch (child.getHorizontalAlignment()) {
				case LEFT -> child.x = x + 20;
				case CENTER -> child.x = x + (width / 2) - (child.getWidth(g2) / 2);
				case RIGHT -> child.x = x + width - child.getWidth(g2) - 20;
			}
		}
	}

	@Override
	public int getWidth(Graphics2D g2) {
		if (lockedWidth) return width;
		UIElement child = children.get(0);
		if (child.visible) width = child.getWidth(g2) + 40;
		return width;
	}

	@Override
	public int getHeight(Graphics2D g2) {
		if (lockedHeight) return height;
		UIElement child = children.get(0);
		if (child.visible) height = child.getHeight(g2) + 40;
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