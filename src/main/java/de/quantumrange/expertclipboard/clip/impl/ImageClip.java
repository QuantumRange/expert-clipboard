package de.quantumrange.expertclipboard.clip.impl;

import de.quantumrange.expertclipboard.clip.ClipType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageClip extends ClipType<BufferedImage> {

	public ImageClip(BufferedImage data) {
		super(DataFlavor.imageFlavor, data);

		if (data != null) {
			BufferedImage newImg = new BufferedImage(data.getWidth(), data.getHeight(), BufferedImage.TYPE_INT_RGB);

			Graphics2D g2d = newImg.createGraphics();
			g2d.drawImage(data, 0, 0, data.getWidth(), data.getHeight(), null);
			g2d.dispose();

			setData(newImg);
		}
	}
	public ImageClip(Object data) {
		this((BufferedImage) data);
	}

	@Override
	public void render(Graphics2D g2d, int width, int height) {
		int cw = (int) ((double) width * (getData().getWidth() / (double) getData().getHeight()));

		g2d.drawImage(getData(), 0, 0, cw, height, null);
	}

	@Override
	public void writeToFile(File file) {
		try {
			ImageIO.write(getData(), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readFromFile(File file) {
		try {
			setData(ImageIO.read(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHeight() {
		return getData().getHeight();
	}
}
