package de.quantumrange.expertclipboard.frame;

import de.quantumrange.expertclipboard.clip.ClipItem;
import de.quantumrange.expertclipboard.clip.Clipboard;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class InfoFrame extends JWindow {

	/**
	 * animationSate
	 * 0 = Closed
	 * 1 = Open
	 * 2 = Opened
	 * 3 = Close
	 */
	protected int originalWidth, originalHeight, animationState;
	/**
	 * 0 = Open/Close
	 * 1 = Extra Informations
	 * 2 = selectedElementSlider
	 */
	protected Animation[] animations;
	private final InfoPanel panel;
	private int selectedElement;

	public InfoFrame(int width, int height) throws HeadlessException {
		this.originalWidth = width;
		this.originalHeight = height;
		this.animationState = 0;
		this.animations = new Animation[3];

		setSize(width, height);
		add(panel = new InfoPanel(width, height));
		pack();
		setAlwaysOnTop(true);
		setFocusable(false);
		setBackground(new Color(0, 0, 0, 0));
		setLocation(calculatePerfectPosition());
		setVisible(true);
	}

	public void open() {
		if (animationState != 2 && animationState != 1) {
			if (animationState == 0) panel.repaint();
			animationState = 1;
			animations[0] = new Animation(
					panel.renderSize.width,
					originalWidth,
					1500,
					value -> panel.renderSize.width = value,
					success -> animationState = 2);
			animations[0].start();
		}
	}

	public void close() {
		if (animationState != 0 && animationState != 3) {
			animationState = 3;
			animations[0] = new Animation(
					panel.renderSize.width,
					0,
					1500,
					value -> panel.renderSize.width = value,
					success -> animationState = 0);
			animations[0].start();
		}
	}

	public void updateSelected(int selected) {
		int slot = selected - 1;

		if (this.selectedElement != slot) {
			Clipboard.switchToClipboard(slot);
		}

		animations[2] = new Animation(panel.sliderX, slot * 50, 500, val -> panel.sliderX = val, success -> { });
		animations[2].start();

		animations[1] = new Animation(
				panel.renderSize.height,
				Math.min(50 + Clipboard.getLast().getObj().getHeight(), 400),
				1500,
				value -> panel.renderSize.width = value,
				success -> animationState = 2);
		animations[1].start();

		this.selectedElement = slot;
	}

	private Point calculatePerfectPosition() {
		Point point = MouseInfo.getPointerInfo().getLocation();

		return new Point(point.x - (panel.renderSize.width / 2), point.y - panel.renderSize.height - 15);
	}

	protected void updateMouseWindowPosition() {
		setLocation(calculatePerfectPosition());
	}

	private class InfoPanel extends JPanel {

		protected int sliderX;
		protected Dimension renderSize;

		public InfoPanel(int width, int height) {
			this.renderSize = new Dimension(width, height);
			setOpaque(false);
			setPreferredSize(new Dimension(width, height));

			sliderX = 0;
			renderSize.width = 0;

			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setClip(new Rectangle2D.Double(0, 0, renderSize.width, renderSize.height));

			for (int i = 0; i < animations.length; i++) {
				if (animations[i] != null) {
					if (animations[i].isDone()) {
						animations[i].stop();
						animations[i] = null;
					} else animations[i].update();
				}
			}

			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));

			drawGUI(g2d, selectedElement);

			updateMouseWindowPosition();

			if (animationState != 0) repaint();
		}

		private void drawGUI(Graphics2D g2d, int selected) {
			int offsetX = (getWidth() / 2) - (450 / 2);

			for (int i = 0; i < 9; i++) {
				drawGUIBackground(g2d, offsetX + (i * 50));
			}

			int nX = offsetX + sliderX;
			g2d.setColor(selectedColor);
			drawBackground(g2d, nX);

			for (int i = 0; i < 9; i++) {
				drawGUIElement(g2d, i, String.valueOf(i + 1), offsetX + (i * 50));
			}
		}

		public static final Color backgroundColor = new Color(255, 255, 255),
								  textColor = new Color(0, 0, 0),
								  selectedColor = new Color(88, 101, 242);

		private static final int width = 50,
				                 height = 50,
				                 y = 0,
				                 offset = 15;

		private void drawGUIBackground(Graphics2D g2d, int x) {
			g2d.setColor(backgroundColor);
			drawBackground(g2d, x);
		}

		private void drawBackground(Graphics2D g2d, int x) {
			Polygon p = new Polygon();
			p.addPoint(x + offset, y);
			p.addPoint(x, y + height);
			p.addPoint(x + width, y + height);
			p.addPoint(x + width + offset, y);
			g2d.fillPolygon(p);
		}

		private void drawGUIElement(Graphics2D g2d, int slot, String name, int x) {



			g2d.setColor(textColor);
			g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
			drawCenteredText(g2d, name, x + offset - 5, y + 15, 50);
			g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 10));

			ClipItem last = Clipboard.getLast(slot);

			drawCenteredText(g2d, last == null ? "EMPTY" : last.getType().getDisplayName(), x, y + height - 5, 50);
		}

		@SuppressWarnings("SameParameterValue")
		private void drawCenteredText(Graphics2D g2d, String str, int x, int y, int width) {
			int strWidth = g2d.getFontMetrics().stringWidth(str);
			g2d.drawString(str, x + ((width / 2) - (strWidth / 2)), y);
		}

	}
}
