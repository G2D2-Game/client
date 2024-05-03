package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;

public class HorizontalStack extends UIElement {
	public HorizontalStack(String id, int renderPriority, VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment) {
		super(id, renderPriority, verticalAlignment, horizontalAlignment);
	}

	@Override
	public void refresh(Graphics2D g2) {
		super.refresh(g2);
		int currentX = x;
		for (UIElement uiElement : children) {
			if (!uiElement.visible) continue;
			uiElement.x = currentX;
			if (uiElement.getVerticalAlignment() != null) {
				switch (uiElement.getVerticalAlignment()) {
					case TOP -> uiElement.y = y;
					case CENTER -> uiElement.y = y + (height / 2) - (uiElement.getHeight(g2) / 2);
					case BOTTOM -> uiElement.y = y + height - uiElement.getHeight(g2);
				}
			}
			currentX += uiElement.getWidth(g2) + 10;
		}

		getWidth(g2);
		getHeight(g2);
	}

	@Override
	public int getWidth(Graphics2D g2) {
		width = 0;
		for (UIElement uiElement : children)
			if (uiElement.visible) width += uiElement.getWidth(g2) + 10;

		return width - 10;
	}

	@Override
	public int getHeight(Graphics2D g2) {
		height = 0;
		for (UIElement uiElement : children)
			if (uiElement.visible) height = Math.max(height, uiElement.getHeight(g2));

		return height;
	}
}