package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class UI {
	public List<UIElement> children = new ArrayList<>();

	public void draw(Graphics2D g2) {
		children.forEach(uiElement -> {
			if (uiElement.visible) uiElement.draw(g2);
		});
	}

	public void onClick(MouseEvent e) {
		children.forEach(uiElement -> {
			if (uiElement.visible && uiElement.contains(e)) uiElement.onClick(e);
		});
	}
}