package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class UIElement {
	public ArrayList<UIElement> children = new ArrayList<>();
	public UIElement parentElement;
	public int x = 0;
	public int y = 0;
	public int width = 0;
	public int height = 0;
	public boolean visible = true;
	public int renderPriority = 0;

	public abstract void draw(Graphics2D g2);

	public abstract void onClick(MouseEvent e);

	public abstract void onHover(Graphics2D g2, Point point);

	public abstract void refresh(Graphics2D g2);

	public abstract int getWidth(Graphics2D g2);

	public abstract int getHeight(Graphics2D g2);

	public boolean contains(Graphics2D g2, Point point) {
		return point.getX() > x && point.getX() < x + getWidth(g2) && point.getY() > y && point.getY() < y + getHeight(g2);
	}
}