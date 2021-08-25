package de.quantumrange.expertclipboard.frame;

import de.quantumrange.expertclipboard.clip.ClipItem;
import de.quantumrange.expertclipboard.clip.Clipboard;
import de.quantumrange.expertclipboard.util.FrameUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class InfoFrame extends JWindow {

	public static final int TIMEOUT_TIME = 10_000;
	public static final Color backgroundColor = new Color(255, 255, 255),
			textColor = new Color(0, 0, 0),
			selectedColor = new Color(88, 101, 242);

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
	private long lastInteraction;

	public InfoFrame(int width, int height) throws HeadlessException {
		this.originalWidth = width;
		this.originalHeight = height;
		this.animationState = 0;
		this.animations = new Animation[3];
		lastInteraction = 0L;

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
		this.lastInteraction = System.currentTimeMillis();

		setVisible(true);

		if (animationState == 0) panel.repaint();

		animationState = 1;
		animations[0] = new Animation(
				panel.renderSize.width,
				originalWidth,
				1000,
				value -> panel.renderSize.width = value,
				success -> animationState = 2, Animation.AnimationType.EASE_IN_OUT_BOUNCE);
		animations[0].start();

		setLocation(calculatePerfectPosition());
	}

	public void close() {
		if (animationState != 0 && animationState != 3) {
			animationState = 3;
			animations[0] = new Animation(
					panel.renderSize.width,
					0,
					2000,
					value -> panel.renderSize.width = value,
					success -> {
						animationState = 0;
						setVisible(false);
					}, Animation.AnimationType.EASE_IN_OUT_BOUNCE);
			animations[0].start();
		}
	}

	public void updateClipboard() {
		animateHeight();
	}

	private void animateHeight() {
		if (animationState == 3) open();

		lastInteraction = System.currentTimeMillis();

		int height = Clipboard.getLast() != null ? Clipboard.getLast().getObj().getHeight() : 0;

		int prefHeight = Math.min(50 + height, 400);

		animations[1] = new Animation(
				panel.renderSize.height,
				prefHeight,
				150,
				value -> panel.renderSize.height = value,
				success -> animationState = 2);
		animations[1].start();
	}

	public void updateSelected(int selected) {
		this.lastInteraction = System.currentTimeMillis();

		int slot = selected - 1;

		if (this.selectedElement != slot) {
			Clipboard.switchToClipboard(slot);
		}

		animations[2] = new Animation(panel.sliderX, slot * 50, 50, val -> panel.sliderX = val, success -> { });
		animations[2].start();

		animateHeight();

		this.selectedElement = slot;
	}

	private Point calculatePerfectPosition() {
		Point point = MouseInfo.getPointerInfo().getLocation();

		return new Point(point.x - (panel.renderSize.width / 2), point.y - panel.renderSize.height - 15);
	}

	protected void updateMouseWindowPosition() {
		// setLocation(calculatePerfectPosition());
	}

	public class InfoPanel extends JPanel {

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

//			g2d.setColor(Color.RED);
//			g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

//			final int SPLIT = 15;
//
//			for (int i = 0; i < (getWidth() + getHeight()) / SPLIT; i++) {
//				g2d.drawLine(0, i * SPLIT, i * SPLIT, 0);
//				g2d.drawLine(0, getHeight() - (i * SPLIT), i * SPLIT, getHeight());
//			}

			g2d.setClip(new Rectangle2D.Double(0, 0, renderSize.width, renderSize.height));

			FrameUtil.updateAnimations(animations);

			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));

			drawGUI(g2d);

			updateMouseWindowPosition();

			if (lastInteraction + TIMEOUT_TIME < System.currentTimeMillis()) close();

			if (animationState != 0) repaint();
		}

		private void drawGUI(Graphics2D g2d) {
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

			drawMoreInformation(g2d, offsetX, 50, 450, renderSize.height - 50);
		}

		private void drawMoreInformation(Graphics2D g2d, int x, int y, int width, int height) {
			g2d.setColor(Color.WHITE);
			g2d.fillRect(x, y, width, height);

			ClipItem last = Clipboard.getLast();

			if (last != null && width > 0 && height > 0) {
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics2D = img.createGraphics();

				graphics2D.setColor(Color.WHITE);
				graphics2D.fillRect(0, 0, width, height);
				graphics2D.setColor(Color.BLACK);
				last.getObj().render(graphics2D, width, height);
				graphics2D.dispose();

				g2d.drawImage(img, x, y, width, height, null);
			}
		}


		private static final int width = 50,
				                 height = 50,
				                 y = 0,
				                 offset = 15;

		private void drawGUIBackground(Graphics2D g2d, int x) {
			g2d.setColor(backgroundColor);
			drawBackground(g2d, x);
		}

		private void drawBackground(Graphics2D g2d, int x) {
			g2d.fillPolygon(getPolygon(x));
		}

		public static Polygon getPolygon(int offset, int slot) {
			int x = offset + (slot * 50);
			return getPolygon(x);
		}

		private static Polygon getPolygon(int x) {
			Polygon p = new Polygon();
			p.addPoint(x + offset, y);
			p.addPoint(x, y + height);
			p.addPoint(x + width, y + height);
			p.addPoint(x + width + offset, y);

			return p;
		}

		private void drawGUIElement(Graphics2D g2d, int slot, String name, int x) {
			g2d.setColor(textColor);
			g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
			drawCenteredText(g2d, name, x + offset - 5, y + 15);
			g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 10));

			ClipItem last = Clipboard.getLast(slot);

			drawCenteredText(g2d, last == null ? "EMPTY" : last.getType().getDisplayName(), x, y + height - 5);
		}

		private void drawCenteredText(Graphics2D g2d, String str, int x, int y) {
			int strWidth = g2d.getFontMetrics().stringWidth(str);
			g2d.drawString(str, x + 25 - strWidth / 2, y);
		}

	}
}
