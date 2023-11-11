package com.lukeoldenburg.g2d2.client.gfx;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {
	/**
	 * Optimizes an image for the current graphics environment.
	 *
	 * @param image unoptimized image
	 * @return optimized image
	 * @see <a href="https://stackoverflow.com/questions/196890/java2d-performance-issues/197060#197060">Java2D Performance Issues</a>
	 */
	public static BufferedImage getCompatibleImage(BufferedImage image) {
		// obtain the current system graphical settings
		GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

		/*
		 * if image is already compatible and optimized for current system
		 * settings, simply return it
		 */
		if (image.getColorModel().equals(gfxConfig.getColorModel())) return image;

		// image is not optimized, so create a new image that is
		BufferedImage newImage = gfxConfig.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());

		// get the graphics context of the new image to draw the old image on
		Graphics2D g2 = newImage.createGraphics();

		// actually draw the image and dispose of context no longer needed
		g2.drawImage(image, 0, 0, null);
		g2.dispose();

		// return the new optimized image
		return newImage;
	}

	public static BufferedImage getScaledImage(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(ScreenUtil.scaledTileSize, ScreenUtil.scaledTileSize, image.getType());
		Graphics2D g2 = newImage.createGraphics();
		g2.drawImage(image, 0, 0, ScreenUtil.scaledTileSize, ScreenUtil.scaledTileSize, null);
		g2.dispose();
		return newImage;
	}
}