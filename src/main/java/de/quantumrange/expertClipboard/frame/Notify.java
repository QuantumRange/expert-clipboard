package de.quantumrange.expertClipboard.frame;

import de.quantumrange.expertClipboard.Main;

import java.awt.*;

public class Notify {

	private final String message;
	private final Color color;
	private final long startTime;

	public Notify(String message, Color color) {
		this.message = message;
		this.color = color;
		this.startTime = System.currentTimeMillis();
	}

	public void render(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.setColor(new Color(44, 47, 51));
		g2d.fillRect(x, y, width, height);
		g2d.setColor(color);
		int w1 = (int) (width * 0.02);
		g2d.fillRect(x, y, w1, height);

		g2d.setColor(Color.WHITE);
		g2d.drawString(message, w1 + 10, (int) (y + height * 0.7));
	}

	public boolean isDone() {
		return startTime + 3_000 < System.currentTimeMillis();
	}

	public static void error(String message) {
		Main.notifyFrame.add(new Notify(message, Color.RED));
	}

	public static void success(String message) {
		Main.notifyFrame.add(new Notify(message, Color.GREEN));
	}

}
