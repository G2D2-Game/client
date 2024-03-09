package com.lukeoldenburg.g2d2.client.gfx.ui;

import com.lukeoldenburg.g2d2.client.Client;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class UIElement {
	public ArrayList<UIElement> children = new ArrayList<>();
	public String id;
	public UIElement parentElement;
	public int renderPriority = 0;
	public int x = 0;
	public int y = 0;
	public int width = 0;
	public int height = 0;
	public boolean visible = true;

	public UIElement(String id, UIElement parentElement, int renderPriority, int x, int y) {
		this.id = id;
		this.parentElement = parentElement;
		this.renderPriority = renderPriority;
		this.x = x;
		this.y = y;
	}

	public UIElement() {
	}

	public void draw(Graphics2D g2) {
		g2.setStroke(new BasicStroke(1));
		if (Client.getGamePanel().debugContainer.visible) {
			g2.drawLine(x, y, x + width, y);
			g2.drawLine(x, y, x, y + height);
			g2.drawLine(x, y + height, x + width, y + height);
			g2.drawLine(x + width, y, x + width, y + height);
		}
	}

	;

	public void onClick(MouseEvent e) {
		for (UIElement uiElement : children)
			if (uiElement.visible && uiElement.contains((Graphics2D) e.getComponent().getGraphics(), e.getPoint()))
				uiElement.onClick(e);
	}

	public void onHover(Graphics2D g2, Point point) {
		UI.hoveredElements.add(this);
		for (UIElement uiElement : children)
			if (uiElement.visible && uiElement.contains(g2, point)) uiElement.onHover(g2, point);
	}

	public void refresh(Graphics2D g2) {
		for (UIElement uiElement : children)
			uiElement.refresh(g2);
	}

	public abstract int getWidth(Graphics2D g2);

	public abstract int getHeight(Graphics2D g2);

	public boolean contains(Graphics2D g2, Point point) {
		try {
			return point.getX() > x && point.getX() < x + getWidth(g2) && point.getY() > y && point.getY() < y + getHeight(g2);

		} catch (NullPointerException e) {
			return false;
		}
	}

	public void addChild(UIElement child) {
		children.add(child);
		children.sort((a, b) -> {
			if (a.renderPriority < b.renderPriority) return 1;
			if (a.renderPriority > b.renderPriority) return -1;
			return 0;
		});
	}
}