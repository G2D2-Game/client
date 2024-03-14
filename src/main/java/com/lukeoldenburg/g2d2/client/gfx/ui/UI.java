package com.lukeoldenburg.g2d2.client.gfx.ui;

import com.lukeoldenburg.g2d2.client.Client;
import com.lukeoldenburg.g2d2.client.gfx.ScreenUtil;

import java.awt.*;
import java.util.ArrayList;

public class UI extends UIElement {
	private static final ArrayList<UIElement> hoveredElements = new ArrayList<>();

	public UI() {
		super();
		width = ScreenUtil.width - 1;
		height = ScreenUtil.height - 1;
	}

	public static ArrayList<UIElement> getHoveredElements() {
		return hoveredElements;
	}

	@Override
	public void draw(Graphics2D g2) {
		if (!visible) return;
		super.draw(g2);
	}

	@Override
	public void refresh(Graphics2D g2) {
		visible = (boolean) Client.getStateInfo().get("ui_visible");
		super.refresh(g2);
		for (UIElement uiElement : children) {
			if (uiElement.getVerticalAlignment() != null) {
				switch (uiElement.getVerticalAlignment()) {
					case TOP -> uiElement.y = y + 20;
					case CENTER -> uiElement.y = y + (height / 2) - (uiElement.getHeight(g2) / 2);
					case BOTTOM -> uiElement.y = y + height - uiElement.getHeight(g2) - 20;
				}
			}
			if (uiElement.getHorizontalAlignment() != null) {
				switch (uiElement.getHorizontalAlignment()) {
					case LEFT -> uiElement.x = x + 20;
					case CENTER -> uiElement.x = x + (width / 2) - (uiElement.getWidth(g2) / 2);
					case RIGHT -> uiElement.x = x + width - uiElement.getWidth(g2) - 20;
				}
			}
		}
	}
}