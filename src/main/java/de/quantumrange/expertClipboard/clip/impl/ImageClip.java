package de.quantumrange.expertClipboard.clip.impl;

import de.quantumrange.expertClipboard.clip.ClipType;

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
		this(data == null ? null : (BufferedImage) data);
	}

	@Override
	public void render(Graphics2D g2d, int width, int height) {
		int cw = (int) ((double) height * (getData().getWidth() / (double) getData().getHeight()));

		g2d.drawImage(getData(), (width / 2) - (cw / 2), 0, cw, height, null);
	}

	@Override
	public void writeToFile(File file) {
		try {
			BufferedImage image = getData();

			BufferedImage exportImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

			Graphics2D g2d = exportImage.createGraphics();
			g2d.drawImage(image, image.getWidth(), image.getHeight(), null);
			g2d.dispose();

			ImageIO.write(exportImage, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readFromFile(File file) {
		try {
			BufferedImage data = ImageIO.read(file);

			BufferedImage newImg = new BufferedImage(data.getWidth(), data.getHeight(), BufferedImage.TYPE_INT_RGB);

			Graphics2D g2d = newImg.createGraphics();
			g2d.drawImage(data, 0, 0, data.getWidth(), data.getHeight(), null);
			g2d.dispose();

			setData(newImg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHeight() {
		return getData().getHeight();
	}
}
