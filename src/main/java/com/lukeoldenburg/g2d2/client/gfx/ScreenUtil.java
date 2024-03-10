package com.lukeoldenburg.g2d2.client.gfx;

import com.lukeoldenburg.g2d2.client.Client;
import com.lukeoldenburg.g2d2.server.level.Coordinate;

import javax.swing.*;
import java.awt.*;

public class ScreenUtil {
	public static final int TILE_SIZE = 16;
	public static final int COLS = 640 / TILE_SIZE / 2;
	public static final int ROWS = 360 / TILE_SIZE / 2;
	public static int width;
	public static int centerWidth;
	public static int height;
	public static int centerHeight;
	public static int scale;
	public static int scaledTileSize;

	public static int getLeftBound(Coordinate playerCoordinate) {
		Point playerPoint = coordinateToPoint(playerCoordinate, playerCoordinate);
		int leftBound = getUncorrectedLeftBound(playerCoordinate);
		if (playerPoint.getX() > centerWidth)
			leftBound -= (int) ((playerPoint.getX() - centerWidth) / scaledTileSize);
		return leftBound;
	}

	public static int getRightBound(Coordinate playerCoordinate) {
		Point playerPoint = coordinateToPoint(playerCoordinate, playerCoordinate);
		int rightBound = getUncorrectedRightBound(playerCoordinate);
		if (playerPoint.getX() < centerWidth)
			rightBound += (int) ((centerWidth - playerPoint.getX()) / scaledTileSize);
		return rightBound;
	}

	public static int getUpperBound(Coordinate playerCoordinate) {
		Point playerPoint = coordinateToPoint(playerCoordinate, playerCoordinate);
		int upperBound = getUncorrectedUpperBound(playerCoordinate);
		if (playerPoint.getY() > centerHeight)
			upperBound -= (int) ((playerPoint.getY() - centerHeight) / scaledTileSize);
		return upperBound;
	}

	public static int getLowerBound(Coordinate playerCoordinate) {
		Point playerPoint = coordinateToPoint(playerCoordinate, playerCoordinate);
		int lowerBound = getUncorrectedLowerBound(playerCoordinate);
		if (playerPoint.getY() < centerHeight)
			lowerBound += (int) ((centerHeight - playerPoint.getY()) / scaledTileSize);
		return lowerBound;
	}

	public static boolean isInBounds(Coordinate coordinate, Coordinate playerCoordinate) {
		return coordinate.getX() >= getLeftBound(playerCoordinate) && coordinate.getX() <= getRightBound(playerCoordinate) && coordinate.getY() >= getUpperBound(playerCoordinate) && coordinate.getY() <= getLowerBound(playerCoordinate);
	}

	public static Point coordinateToPoint(Coordinate coordinate, Coordinate playerCoordinate) {
		Point playerPoint = getPlayerPoint(playerCoordinate);
		return new Point((int) (playerPoint.getX() + ((coordinate.getX() - playerCoordinate.getX()) * scaledTileSize)), (int) (playerPoint.getY() + ((coordinate.getY() - playerCoordinate.getY()) * scaledTileSize)));
	}

	public static Coordinate pointToCoordinate(Point point, Coordinate playerCoordinate) {
		Point playerPoint = getPlayerPoint(playerCoordinate);
		return new Coordinate(Double.parseDouble(String.format("%.3f", playerCoordinate.getX() + ((point.getX() - playerPoint.getX()) / scaledTileSize))), Double.parseDouble(String.format("%.3f", playerCoordinate.getY() + ((point.getY() - playerPoint.getY()) / scaledTileSize))));
	}

	public static Point getPlayerPoint(Coordinate playerCoordinate) {
		int playerScreenX = width / 2;
		if (getUncorrectedLeftBound(playerCoordinate) == 0) {
			playerScreenX = 0;
			playerScreenX -= (int) ((0 - playerCoordinate.getX()) * scaledTileSize);
			playerScreenX = Math.min(playerScreenX, width / 2);
		}

		if (getUncorrectedRightBound(playerCoordinate) == Client.getLevel().getSize()) {
			playerScreenX = width;
			playerScreenX -= (int) ((Client.getLevel().getSize() - playerCoordinate.getX()) * scaledTileSize);
			playerScreenX = Math.max(playerScreenX, width / 2);
		}

		int playerScreenY = height / 2;
		if (getUncorrectedUpperBound(playerCoordinate) == 0) {
			playerScreenY = 0;
			playerScreenY -= (int) ((0 - playerCoordinate.getY()) * scaledTileSize);
			playerScreenY = Math.min(playerScreenY, height / 2);
		}

		if (getUncorrectedLowerBound(playerCoordinate) == Client.getLevel().getSize()) {
			playerScreenY = height;
			playerScreenY -= (int) ((Client.getLevel().getSize() - playerCoordinate.getY()) * scaledTileSize);
			playerScreenY = Math.max(playerScreenY, height / 2);
		}

		return new Point(playerScreenX, playerScreenY);
	}

	private static int getUncorrectedLeftBound(Coordinate playerCoordinate) {
		return Math.max((int) (playerCoordinate.getX() - (COLS / 2) - 1), 0);
	}

	private static int getUncorrectedRightBound(Coordinate playerCoordinate) {
		return Math.min((int) (playerCoordinate.getX() + (COLS / 2) + 1), Client.getLevel().getSize());
	}

	private static int getUncorrectedUpperBound(Coordinate playerCoordinate) {
		return Math.max((int) (playerCoordinate.getY() - (ROWS / 2) - 2), 0);
	}

	private static int getUncorrectedLowerBound(Coordinate playerCoordinate) {
		return Math.min((int) (playerCoordinate.getY() + (ROWS / 2) + 2), Client.getLevel().getSize());
	}

	public static void initializeScreenData(GamePanel gp, int width, int height) {
		DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		if (width == dm.getWidth() && height == dm.getHeight()) {
			Client.getGameFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
			Client.getGameFrame().setUndecorated(true);
		}

		if (width <= dm.getWidth() && height <= dm.getHeight()) {
			ScreenUtil.width = width;
			centerWidth = width / 2;
			ScreenUtil.height = height;
			centerHeight = height / 2;
			scale = width / 640 * 2;
			scaledTileSize = TILE_SIZE * scale;
			gp.setPreferredSize(new Dimension(width, height));
			Client.getConfig().addProperty("resolution", width + "x" + height);
		}
	}
}