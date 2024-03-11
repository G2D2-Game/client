package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;

public class VerticalStack extends UIElement {
	public VerticalStack(String id, UIElement parentElement, int renderPriority, int x, int y) {
		super(id, parentElement, renderPriority, x, y);
	}

	@Override
	public void refresh(Graphics2D g2) {
		super.refresh(g2);
		children.sort((a, b) -> Integer.compare(b.renderPriority, a.renderPriority));

		int currentX = x;
		int currentY = y;
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
		width = 0;
		for (UIElement uiElement : children)
			if (uiElement.visible) width = Math.max(width, uiElement.getWidth(g2));

		return width;
	}

	@Override
	public int getHeight(Graphics2D g2) {
		height = 0;
		for (UIElement uiElement : children)
			if (uiElement.visible) height += uiElement.getHeight(g2) + 10;

		return height - 10;
	}
}