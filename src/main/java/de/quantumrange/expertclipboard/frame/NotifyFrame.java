package de.quantumrange.expertclipboard.frame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotifyFrame extends JWindow {

	private List<Notify> notifies;
	private final NotifyPanel panel;
	private Animation slideUp;

	public NotifyFrame(int width, int height) throws HeadlessException {
		this.notifies = new ArrayList<>();
		this.slideUp = null;

		setSize(width, height);
		add(panel = new NotifyPanel(width, height));
		pack();
		setAlwaysOnTop(true);
		setFocusable(false);
		setBackground(new Color(0, 0, 0, 0));
		setLocation(calculatePerfectPosition());
		setVisible(true);
	}

	public void add(Notify notify) {
		boolean empty = notifies.isEmpty();

		slideUp = new Animation(-40, 0, 500, val -> {
			panel.offsetY = val;
		}, success -> { });
		slideUp.start();

		notifies.add(notify);

		if (empty) {
			panel.repaint();
		}
	}

	private Point calculatePerfectPosition() {
		Point point = MouseInfo.getPointerInfo().getLocation();

		return new Point(point.x - (panel.getWidth() / 2), point.y + 15);
	}

	protected void updateMouseWindowPosition() {
		setLocation(calculatePerfectPosition());
	}

	private class NotifyPanel extends JPanel {

		private int offsetY;

		public NotifyPanel(int width, int height) {
			this.offsetY = -40;

			setOpaque(false);
			setPreferredSize(new Dimension(width, height));

			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			notifies = notifies.stream().filter(notify -> !notify.isDone()).collect(Collectors.toList());

			if ()

			for ()

			if (!notifies.isEmpty()) repaint();
		}

	}
}
