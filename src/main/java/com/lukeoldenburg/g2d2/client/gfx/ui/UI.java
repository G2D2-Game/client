package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public class UI extends UIElement {
	public UI() {
		super();
	}

	@Override
	public void draw(Graphics2D g2) {
		if (!visible) return;
		children.sort((a, b) -> {
			if (a.renderPriority < b.renderPriority) return 1;
			if (a.renderPriority > b.renderPriority) return -1;
			return 0;
		});

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
	}

	@Override
	public int getWidth(Graphics2D g2) {
		return 0;
	}

	@Override
	public int getHeight(Graphics2D g2) {
		return 0;
	}

	@Override
	public boolean contains(Graphics2D g2, Point point) {
		for (UIElement uiElement : children)
			if (uiElement.contains(g2, point)) return true;
		return false;
	}
}