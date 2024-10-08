package com.lukeoldenburg.g2d2.client.gfx.ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public class CheckBox extends UIElement {
	private boolean checked = false;

	public CheckBox(String id, int renderPriority, VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment) {
		super(id, renderPriority, verticalAlignment, horizontalAlignment);
		width = 20;
		height = 20;
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		g2.drawRect(x, y, width, height);
		if (checked) g2.fillRect(x, y, width, height);
	}

	@Override
	public void onClick(MouseEvent e) {
		checked = !checked;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}