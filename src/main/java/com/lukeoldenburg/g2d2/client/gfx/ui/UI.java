package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class UI extends UIElement {
	private static final ArrayList<UIElement> hoveredElements = new ArrayList<>();

	public UI() {
		super();
	}

	@Override
	public void draw(Graphics2D g2) {
		if (!visible) return;
		for (UIElement uiElement : children)
			if (uiElement.visible) uiElement.draw(g2);
	}

	@Override
	public void onClick(MouseEvent e) {
		super.onClick(e);
	}

	@Override
	public void onHover(Graphics2D g2, Point point) {
		super.onHover(g2, point);
	}

	@Override
	public void refresh(Graphics2D g2) {
		super.refresh(g2);
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

	public static ArrayList<UIElement> getHoveredElements() {
		return hoveredElements;
	}
}