package de.quantumrange.expertclipboard.clip.impl;

import de.quantumrange.expertclipboard.FileUtil;
import de.quantumrange.expertclipboard.Main;
import de.quantumrange.expertclipboard.clip.ClipType;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileClip extends ClipType<List<File>> {

	public static final int WIDTH = 50,
							SPACE_BETWEEN = 25,
							HEIGHT = 60;

	public FileClip(List<File> data) {
		super(DataFlavor.javaFileListFlavor, data);
	}

	public FileClip(Object data) {
		this(data == null ? null : (List<File>) data);
	}

	@Override
	public void render(Graphics2D g2d, int width, int height) {
		int x = 20;
		int y = SPACE_BETWEEN;

		for (File f : getData()) {
			String[] endSplit = f.getName().split("\\.");
			String nameEnding = endSplit.length == 1 ? "" : endSplit[endSplit.length - 1];

			g2d.setColor(new Color(0, 0, 0, 50));

			if (f.isDirectory()) {
				Polygon p = new Polygon();

				p.addPoint(x, y);
				p.addPoint(x, y + WIDTH);
				p.addPoint(x + HEIGHT, y + WIDTH);
				p.addPoint(x + HEIGHT, y + 10);
				p.addPoint(x + HEIGHT - 20, y + 10);
				p.addPoint(x + HEIGHT - 20, y);

				g2d.fillPolygon(p);

				g2d.setColor(new Color(0, 0, 0, 25));

				Polygon p1 = new Polygon();

				p1.addPoint(x + HEIGHT, y);
				p1.addPoint(x + HEIGHT, y + 10);
				p1.addPoint(x + HEIGHT - 20, y + 10);
				p1.addPoint(x + HEIGHT - 20, y);

				g2d.fillPolygon(p1);
			} else {
				Polygon p = new Polygon();

				p.addPoint(x, y);
				p.addPoint(x, y + HEIGHT);
				p.addPoint(x + WIDTH, y + HEIGHT);
				p.addPoint(x + WIDTH, y + 10);
				p.addPoint(x + WIDTH - 20, y);

				g2d.fillPolygon(p);

				Polygon p1 = new Polygon();

				p1.addPoint(x + WIDTH, y + 10);
				p1.addPoint(x + WIDTH - 20, y);
				p1.addPoint(x + WIDTH - 20, y + 10);

				g2d.setColor(Color.BLACK);
				g2d.fillPolygon(p1);

				g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 15));

				g2d.drawString(nameEnding, x + (WIDTH / 2) - (g2d.getFontMetrics().stringWidth(nameEnding) / 2),
						y + (HEIGHT / 2) + (g2d.getFont().getSize() / 2));
			}

			g2d.setColor(new Color(0, 0, 0));

			String name = f.getName().substring(0, f.getName().length() - (nameEnding.length() == 0 ? 0 :
					nameEnding.length() + 1));

			g2d.setFont(new Font(Font.DIALOG, Font.ITALIC, Math.max(1, 15 - (name.length() / 3))));
			g2d.drawString(name, x + (WIDTH / 2) - (g2d.getFontMetrics().stringWidth(name) / 2),
					y + (f.isDirectory() ? WIDTH : HEIGHT) + SPACE_BETWEEN - 5);

			x += WIDTH + SPACE_BETWEEN;
			if (x + 70 > width) {
				x = 20;
				y += HEIGHT + SPACE_BETWEEN;
			}
		}
	}

	@Override
	public void writeToFile(File file) {
		FileUtil.write(file, getData().stream()
				.map(File::toString)
				.collect(Collectors.joining("\n")));
	}

	@Override
	public void readFromFile(File file) {
		String content = FileUtil.read(file);

		String[] split = content.split("\n");
		setData(new ArrayList<>(split.length));
		for (String l : split) {
			if (l.isBlank()) continue;
			getData().add(new File(l));
		}
	}

	@Override
	public int getHeight() {
		int pxl = WIDTH + SPACE_BETWEEN + 20;

		return (getData().size() * pxl / 450) * (HEIGHT + SPACE_BETWEEN * 2) + (HEIGHT + SPACE_BETWEEN * 2);
	}
}
