package com.lukeoldenburg.g2d2.client;

import com.google.gson.JsonPrimitive;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

public class InputHandler implements KeyListener, MouseListener {
	// KEYBOARD
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Client.fireAction(Objects.requireNonNullElse(Client.getConfig().get("keybinds").getAsJsonObject().get("K" + e.getKeyCode()), new JsonPrimitive("")).getAsString());
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	// MOUSE
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Client.getGamePanel().ui.onClick(e);
		Client.fireAction(Objects.requireNonNullElse(Client.getConfig().get("keybinds").getAsJsonObject().get("M" + e.getButton()), new JsonPrimitive("")).getAsString());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}