package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Spacer extends UIElement {
	public Spacer(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(Graphics2D g2) {
	}

	@Override
	public void onClick(MouseEvent e) {
	}

	@Override
	public void onHover(Graphics2D g2, Point point) {
	}

	@Override
	public void refresh(Graphics2D g2) {
	}

	@Override
	public int getWidth(Graphics2D g2) {
		return width;
	}

	@Override
	public int getHeight(Graphics2D g2) {
		return height;
	}
}
