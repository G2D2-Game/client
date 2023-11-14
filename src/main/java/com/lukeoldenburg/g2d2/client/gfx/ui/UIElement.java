package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class UIElement {
	public UIElement parentElement;
	public int x = 0;
	public int y = 0;
	public int width = 0;
	public int height = 0;
	public boolean visible = true;

	public abstract void draw(Graphics2D g2);

	public abstract void onClick(MouseEvent e);

	public abstract void refresh(Graphics2D g2);

	public abstract int getWidth(Graphics2D g2);

	public abstract int getHeight(Graphics2D g2);

	public boolean contains(MouseEvent e) {
		Point point = e.getPoint();
		Graphics2D g2 = (Graphics2D) e.getComponent().getGraphics();
		return point.getX() > x && point.getX() < x + getWidth(g2) && point.getY() > y && point.getY() < y + getHeight(g2);
	}
}