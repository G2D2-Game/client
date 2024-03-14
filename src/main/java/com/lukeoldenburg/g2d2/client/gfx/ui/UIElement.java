package com.lukeoldenburg.g2d2.client.gfx.ui;

import com.lukeoldenburg.g2d2.client.Client;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class UIElement {
	protected final ArrayList<UIElement> children = new ArrayList<>();
	protected String id;
	protected UIElement parentElement;
	protected int renderPriority = 0;
	protected VerticalAlignment verticalAlignment;
	protected HorizontalAlignment horizontalAlignment;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected boolean visible = true;

	public UIElement(String id, UIElement parentElement, int renderPriority, VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment, int x, int y) {
		this.id = id;
		this.parentElement = parentElement;
		this.renderPriority = renderPriority;
		this.verticalAlignment = verticalAlignment;
		this.horizontalAlignment = horizontalAlignment;
		this.x = x;
		this.y = y;
	}

	public UIElement() {
	}

	public void draw(Graphics2D g2) {
		for (UIElement uiElement : children)
			if (uiElement.visible) uiElement.draw(g2);

		if ((boolean) Client.getStateInfo().get("debug_mode")) {
			g2.setColor(Color.white);
			g2.setStroke(new BasicStroke(1));
			g2.drawRect(x, y, getWidth(g2), getHeight(g2));
		}
	}

	public void onClick(MouseEvent e) {
		for (UIElement uiElement : children)
			if (uiElement.visible && uiElement.contains((Graphics2D) e.getComponent().getGraphics(), e.getPoint()))
				uiElement.onClick(e);
	}

	public void onHover(Graphics2D g2, Point point) {
		UI.getHoveredElements().add(this);
		for (UIElement uiElement : children)
			if (uiElement.visible && uiElement.contains(g2, point)) uiElement.onHover(g2, point);
	}

	public void refresh(Graphics2D g2) {
		for (UIElement uiElement : children)
			uiElement.refresh(g2);
	}

	public boolean contains(Graphics2D g2, Point point) {
		try {
			return point.getX() > x && point.getX() < x + getWidth(g2) && point.getY() > y && point.getY() < y + getHeight(g2);

		} catch (NullPointerException e) {
			return false;
		}
	}

	public void addChild(UIElement child) {
		children.add(child);
		children.sort((a, b) -> Integer.compare(b.renderPriority, a.renderPriority));
	}

	public ArrayList<UIElement> getChildren() {
		return children;
	}

	public String getId() {
		return id;
	}

	public UIElement getParentElement() {
		return parentElement;
	}

	public int getRenderPriority() {
		return renderPriority;
	}

	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth(Graphics2D g2) {
		return width;
	}

	public int getHeight(Graphics2D g2) {
		return height;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}