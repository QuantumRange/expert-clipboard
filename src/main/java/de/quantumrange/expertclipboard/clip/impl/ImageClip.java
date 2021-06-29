package de.quantumrange.expertclipboard.clip.impl;

import de.quantumrange.expertclipboard.clip.ClipItem;
import de.quantumrange.expertclipboard.clip.ClipType;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.time.LocalDateTime;

public class ImageClip extends ClipType<Image> {

	public ImageClip(Image data) {
		super(DataFlavor.imageFlavor, data);
	}

	@Override
	public void render(Graphics2D g2d, int width, int height) {
		// TODO
	}
}
