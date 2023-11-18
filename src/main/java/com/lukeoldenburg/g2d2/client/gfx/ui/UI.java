package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class UI {
	public List<UIElement> children = new ArrayList<>();
	public boolean visible = true;

	public void draw(Graphics2D g2) {
		if (!visible) return;
		children.sort((a, b) -> {
			if (a.renderPriority < b.renderPriority) return 1;
			if (a.renderPriority > b.renderPriority) return -1;
			return 0;
		});

		for (UIElement uiElement : children)
			if (uiElement.visible) uiElement.draw(g2);
	}

	public void onClick(MouseEvent e) {
		for (UIElement uiElement : children)
			if (uiElement.visible && uiElement.contains(e)) uiElement.onClick(e);
	}

	public void refresh(Graphics2D g2) {
		for (UIElement uiElement : children)
			if (uiElement.visible) uiElement.refresh(g2);
	}
}