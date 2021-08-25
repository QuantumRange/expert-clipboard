package de.lupus.copyCAT.frame;

import de.lupus.copyCAT.clip.ClipItem;
import de.lupus.copyCAT.clip.Clipboard;
import de.lupus.copyCAT.util.FrameUtil;
import org.jnativehook.keyboard.NativeKeyEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class HistoryFrame extends JWindow {

	public static final Color BACKGROUND_COLOR  = new Color(88, 101, 242),
			SELECTED_COLOR  = new Color(72, 81, 177),
			EMPTY_COLOR     = new Color(120, 130, 245);

	private final HistoryPanel panel;
	private boolean isOpen;

	private int currentSlot, maxHistory, selected, selectedPixel, previewPixel;
	private Animation[] animations;
	private final BufferedImage[] previews;

	public HistoryFrame(int width, int height) throws HeadlessException {
		this.animations = new Animation[3];
		this.isOpen = false;
		this.previews = new BufferedImage[Clipboard.MAX_HISTORY];

		setSize(width, height);
		add(panel = new HistoryPanel(width, height));
		pack();
		setAlwaysOnTop(true);
		setFocusable(false);
		setBackground(new Color(0, 0, 0, 0));
		setLocation(calculatePerfectPosition());
		setVisible(false);
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void open(int id) {
		if (Clipboard.getLast(id) == null) return;

		this.selected = 0;
		this.selectedPixel = 0;
		this.currentSlot = id;
		this.previewPixel = 0;
		this.isOpen = true;
		ClipItem[] items = Clipboard.slots[id];

		for (int i = 0; i < Clipboard.MAX_HISTORY; i++) {
			int index = Clipboard.slotIndexes[id] - i;
			if (index < 0) index += (Clipboard.MAX_HISTORY - 1);

			if (items[index] != null) {
				System.out.println("Found save: " + i + " with index: " + index);
				maxHistory++;
				if (maxHistory == 8) break;
			} else break;
		}

		reloadPreview(items);

		animations[0] = new Animation(0, getHeight(), 1000, value -> panel.renderSize.height =
				value,
				success -> { }, Animation.AnimationType.FADE_IN_OUT);
		animations[0].start();

		setVisible(true);
		panel.repaint();
	}

	private void reloadPreview(ClipItem[] items) {
		for (int i = 0; i < previews.length; i++) {
			if (items[i] != null) {
				BufferedImage img = new BufferedImage(getWidth() - 100, getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics2D = img.createGraphics();

				graphics2D.setColor(Color.WHITE);
				graphics2D.fillRect(0, 0, getWidth() - 100, getHeight());
				graphics2D.setColor(Color.BLACK);
				items[i].getObj().render(graphics2D, getWidth() - 100, Math.min(items[i].getObj().getHeight(), getHeight()));
				graphics2D.dispose();

				previews[i] = img;
			} else previews[i] = null;
		}
	}

	public void pressKey(int i) {
		if (isOpen) {
			if (i == NativeKeyEvent.VC_ESCAPE) {
				close();
			} else if (i == NativeKeyEvent.VC_UP || i == NativeKeyEvent.VC_DOWN) {
				if (i == NativeKeyEvent.VC_UP) {
					selected = (selected - 1) % maxHistory;

					if (selected < 0) {
						selected = maxHistory - 1;
					}
				} else selected = (selected + 1) % maxHistory;

				animations[1] = new Animation(selectedPixel, selected * 50, 300, val -> selectedPixel = val,
						success -> {
						}, Animation.AnimationType.EASE_IN_OUT_ELASTIC);
				animations[1].start();

				animations[2] = new Animation(previewPixel, selected * getHeight(), 300, val -> previewPixel = val,
						success -> {
						}, Animation.AnimationType.EASE_IN_OUT_ELASTIC);
				animations[2].start();
			}
		}
	}

	public void close() {
		animations[0] = new Animation(panel.renderSize.height, 0, 1000, value -> panel.renderSize.height =
				value,
				success -> {
					setVisible(false);
					this.isOpen = false;
				}, Animation.AnimationType.FADE_IN_OUT);
		animations[0].start();
	}

	private Point calculatePerfectPosition() {
		Point point = MouseInfo.getPointerInfo().getLocation();
		return new Point(point.x - getWidth() - 50, point.y - getHeight() / 2);
	}

	protected void updateMouseWindowPosition() {
		setLocation(calculatePerfectPosition());
	}

	private class HistoryPanel extends JPanel {

		protected Dimension renderSize;

		public HistoryPanel(int width, int height) {
			this.renderSize = new Dimension(width, 0);
			setOpaque(false);
			setPreferredSize(new Dimension(width, height));

			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setClip(new Rectangle2D.Double(0, 0, renderSize.width, renderSize.height));

			FrameUtil.updateAnimations(animations);

			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Polygon p = new Polygon();

			int value0 = 6; // Size of the little riffle in the corner

			p.addPoint(value0, 0);
			p.addPoint(0, value0);

			p.addPoint(0, renderSize.height - value0);
			p.addPoint(value0, renderSize.height);

			p.addPoint(renderSize.width - value0, renderSize.height);
			p.addPoint(renderSize.width, renderSize.height - value0);

			p.addPoint(renderSize.width, value0);
			p.addPoint(renderSize.width - value0, 0);

			g2d.setClip(p);

			// Render Left Bar
			g2d.setColor(BACKGROUND_COLOR);
			g2d.fillRect(0, 0, 100, renderSize.height);

			// Render Right Bar
			g2d.setColor(Color.WHITE);
			g2d.fillRect(100, 0, renderSize.width - 100, renderSize.height);

			g2d.setColor(EMPTY_COLOR);
			g2d.fillRect(0, maxHistory * 50, 100, renderSize.height);

			g2d.setColor(SELECTED_COLOR);
			g2d.fillRect(0, selectedPixel, 100, 50);

			// Render preview
			int i = Clipboard.slotIndexes[currentSlot] - selected;
			if (i < 0) i += Clipboard.MAX_HISTORY - 1;

			BufferedImage image = previews[i];

			if (image == null) {
				reloadPreview(Clipboard.slots[currentSlot]);
				image = previews[i];
			}

			g2d.drawImage(image, 100,
					renderSize.height / 2 - image.getHeight() / 2,
					renderSize.width,
					image.getHeight(), null);

			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 17));

			ClipItem[] items = Clipboard.slots[currentSlot];

			for (int y = 0; y < maxHistory; y++) {
				int ind = Clipboard.slotIndexes[currentSlot] - y;
				while (ind < 0) ind += Clipboard.MAX_HISTORY - 1;
				ClipItem item = items[ind];

				if (item == null) continue;

				String oldString = item.getDateTime().toString();

				LocalDateTime now = LocalDateTime.now();

				long sec  = item.getDateTime().until(now, ChronoUnit.SECONDS);
				long min  = item.getDateTime().until(now, ChronoUnit.MINUTES);
				long hour = item.getDateTime().until(now, ChronoUnit.HOURS);
				long days = item.getDateTime().until(now, ChronoUnit.DAYS);

				if (days != 0) oldString = "%d days old".formatted(days);
				else if (hour != 0) oldString = "%d hour old".formatted(hour);
				else if (min != 0) oldString = "%d min old".formatted(min);
				else if (sec != 0) oldString = "%d sec old".formatted(sec);

				g2d.drawString(oldString, 10, (int) ((y * 50) + (g2d.getFont().getSize() * 1.5)));
			}

			updateMouseWindowPosition();
			if (isOpen) repaint();
		}

	}
}
