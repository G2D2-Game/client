package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Slider extends UIElement {
	private int minValue;
	private int maxValue;
	protected int value;

	public Slider(String id, UIElement parentElement, int renderPriority, VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment, int x, int y, int minValue, int maxValue) {
		super(id, parentElement, renderPriority, verticalAlignment, horizontalAlignment, x, y);
		this.minValue = minValue;
		this.maxValue = maxValue;
		width = 100;
		height = 20;
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		g2.drawRect(x + 10, y, width - 20, height);
		double fillWidth = (double) (value - minValue) * (width - 20) / (maxValue - minValue);
		g2.fillRect(x + 10, y, (int) fillWidth, height);
		super.draw(g2);
	}

	@Override
	public void onClick(MouseEvent e) {
		value = (int) ((e.getPoint().getX() - x + 10) / (width - 20) * (maxValue - minValue) + 10);
		if (e.getPoint().getX() > x + 10 + width - 20) value = maxValue;
		if (e.getPoint().getX() < x + 10) value = minValue;
	}
}