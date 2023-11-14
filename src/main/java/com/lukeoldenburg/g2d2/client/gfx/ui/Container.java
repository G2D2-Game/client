package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Container extends UIElement {
	public List<UIElement> children = new ArrayList<>();
	public boolean lockedWidth, lockedHeight;

	public Container(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Container(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		lockedWidth = true;
		lockedHeight = true;
	}

	@Override
	public void draw(Graphics2D g2) {
		getWidth(g2);
		getHeight(g2);
		Color c = new Color(0, 0, 0, 210);
		g2.setColor(c);
		g2.fillRoundRect(x, y, width + 1, height + 1, 35, 35);

		c = new Color(255, 255, 255);
		g2.setColor(c);
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);

		int currentX = x + 20;
		int currentY = y + 20;
		for (UIElement uiElement : children) {
			if (uiElement.visible) {
				uiElement.x = currentX;
				uiElement.y = currentY;
				uiElement.draw(g2);
				currentY += uiElement.getHeight(g2) + 10;
			}
		}
	}

	@Override
	public void onClick(MouseEvent e) {
		children.forEach(uiElement -> {
			if (uiElement.visible && uiElement.contains(e)) uiElement.onClick(e);
		});
	}

	@Override
	public void refresh(Graphics2D g2) {
		children.forEach(uiElement -> uiElement.refresh(g2));
	}

	@Override
	public int getWidth(Graphics2D g2) {
		if (!lockedWidth) {
			width = 0;
			for (UIElement uiElement : children) {
				if (uiElement.visible) width = Math.max(width, uiElement.getWidth(g2));
			}

			width += 35;
		}

		return width;
	}

	@Override
	public int getHeight(Graphics2D g2) {
		if (!lockedHeight) {
			height = 0;
			for (UIElement uiElement : children) {
				if (uiElement.visible) height += uiElement.getHeight(g2) + 10;
			}

			height += 15;
		}

		return height;
	}
}