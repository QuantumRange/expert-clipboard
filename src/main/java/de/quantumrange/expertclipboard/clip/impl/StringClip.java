package de.quantumrange.expertclipboard.clip.impl;

import de.quantumrange.expertclipboard.util.FileUtil;
import de.quantumrange.expertclipboard.clip.ClipType;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;

public class StringClip extends ClipType<String> {

	public StringClip(String data) {
		super(DataFlavor.stringFlavor, data);
	}

	public StringClip(Object data) {
		this(data == null ? "" : data.toString());
	}

	@Override
	public void render(Graphics2D g2d, int width, int height) {
		int y = 0;
		g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));

		for (String line : getData().split("\n")) {
			g2d.drawString(line, 0, y + 17);
			y += 20;

			if (y > height) break;
		}
	}

	@Override
	public void writeToFile(File file) {
		FileUtil.write(file, getData());
	}

	@Override
	public void readFromFile(File file) {
		setData(FileUtil.read(file));
	}

	@Override
	public int getHeight() {
		return getData().split("\n").length * 20;
	}
}
