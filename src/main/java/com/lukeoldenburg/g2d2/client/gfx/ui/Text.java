package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class Text extends UIElement {
	private String text;

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	private Font font;
	private Color color;

	public Text(int x, int y, UIElement parentElement, String text, Font font, Color color) {
		this.x = x;
		this.y = y;
		this.parentElement = parentElement;
		this.text = text;
		this.font = font;
		this.color = color;
	}

	public Text(UIElement parentElement, String text, Font font, Color color) {
		this.parentElement = parentElement;
		this.text = text;
		this.font = font;
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setFont(font);
		g2.setColor(color);
		for (String line : text.split("\n")) {
			int lineHeight = (int) g2.getFontMetrics().getStringBounds(line, g2).getHeight();
			g2.drawString(line, x, y + lineHeight / 2);
			y += lineHeight + 10;
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	@Override
	public void onClick(MouseEvent e) {
	}

	@Override
	public void refresh(Graphics2D g2) {
	}

	@Override
	public int getWidth(Graphics2D g2) {
		g2.setFont(font);
		this.width = 0;
		for (String line : text.split("\n")) {
			Rectangle2D bounds = g2.getFontMetrics().getStringBounds(line, g2);
			this.width = Math.max(this.width, (int) bounds.getWidth());
		}
		return width;
	}

	@Override
	public int getHeight(Graphics2D g2) {
		g2.setFont(font);
		this.height = 0;
		for (String line : text.split("\n")) {
			Rectangle2D bounds = g2.getFontMetrics().getStringBounds(line, g2);
			this.height += (int) (bounds.getHeight() + 10);
		}
		return height;
	}
}