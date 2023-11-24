package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Container extends UIElement {
	private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 210);
	private static final Color BORDER_COLOR = new Color(255, 255, 255);
	public boolean lockedWidth, lockedHeight = false;

	public Container(int x, int y, int renderPriority) {
		super();
		this.x = x;
		this.y = y;
		this.renderPriority = renderPriority;
	}

	public Container(int x, int y, int width, int height, int renderPriority) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.renderPriority = renderPriority;
		lockedWidth = true;
		lockedHeight = true;
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
	}

	@Override
	public void onClick(MouseEvent e) {
		for (UIElement uiElement : children)
			if (uiElement.visible && uiElement.contains((Graphics2D) e.getComponent().getGraphics(), e.getPoint()))
				uiElement.onClick(e);
	}

	@Override
	public void onHover(Graphics2D g2, Point point) {
		for (UIElement uiElement : children)
			if (uiElement.visible && uiElement.contains(g2, point)) uiElement.onHover(g2, point);
	}

	@Override
	public void refresh(Graphics2D g2) {
		for (UIElement uiElement : children)
			uiElement.refresh(g2);

		children.sort((a, b) -> {
			if (a.renderPriority < b.renderPriority) return 1;
			if (a.renderPriority > b.renderPriority) return -1;
			return 0;
		});

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
}