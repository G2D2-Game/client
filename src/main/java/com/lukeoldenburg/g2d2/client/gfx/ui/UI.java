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
	}
}