package com.lukeoldenburg.g2d2.client.gfx.ui;

import com.lukeoldenburg.g2d2.client.gfx.GamePanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class Text extends UIElement {
	private String text = "";
	private Font font = GamePanel.font.deriveFont(30f);
	private Color color = Color.white;
	private boolean underlined = false;

	public Text(String id, UIElement parentElement, int renderPriority, int x, int y) {
		super(id, parentElement, renderPriority, x, y);
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setFont(font);
		g2.setColor(color);
		for (String line : text.split("\n")) {
			Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds(line, g2);
			g2.drawString(line, x, (int) (y + stringBounds.getHeight() / 2));
			if (underlined) {
				g2.setStroke(new BasicStroke(2));
				g2.drawLine(x, (int) (y + stringBounds.getHeight() / 2 + 3), (int) (x + stringBounds.getWidth()), (int) (y + stringBounds.getHeight() / 2 + 3));
			}

			y += (int) stringBounds.getHeight();
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
		getWidth(g2);
		getHeight(g2);
	}

	@Override
	public int getWidth(Graphics2D g2) {
		g2.setFont(font);
		this.width = 0;
		for (String line : text.split("\n"))
			this.width = Math.max(this.width, (int) g2.getFontMetrics().getStringBounds(line, g2).getWidth());

		return width;
	}

	@Override
	public int getHeight(Graphics2D g2) {
		g2.setFont(font);
		this.height = 0;
		for (String line : text.split("\n"))
			this.height += (int) g2.getFontMetrics().getStringBounds(line, g2).getHeight();

		return height;
	}

	@Override
	public boolean contains(Graphics2D g2, Point point) {
		try {
			return point.getX() > x && point.getX() < x + getWidth(g2) && point.getY() < y && point.getY() > y - getHeight(g2);

		} catch (NullPointerException e) {
			return false;
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

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

	public boolean isUnderlined() {
		return underlined;
	}

	public void setUnderlined(boolean underlined) {
		this.underlined = underlined;
	}
}