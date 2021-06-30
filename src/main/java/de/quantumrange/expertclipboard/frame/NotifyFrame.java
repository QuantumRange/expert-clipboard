package de.quantumrange.expertclipboard.frame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotifyFrame extends JWindow {

	private static final int NOTIFY_HEIGHT = 35,
							 NOTIFY_SPACE = 10;
	private final List<Notify> notifies;
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

		slideUp = new Animation(-(NOTIFY_HEIGHT + NOTIFY_SPACE), 0, 500, val -> {
			panel.offsetY = val;
		}, success -> {
			if (notifies.isEmpty()) {
				setLocation(50_000, 50_000);
			}
		});
		slideUp.start();

		synchronized (notifies) {
			notifies.add(0, notify);
		}

		if (empty) {
			panel.repaint();
		}
	}

	private Point calculatePerfectPosition() {
		Point point = MouseInfo.getPointerInfo().getLocation();

//		return new Point(point.x - (panel.getWidth() / 2), point.y + 15);
		return new Point(point.x, point.y + 15);
	}

	protected void updateMouseWindowPosition() {
		setLocation(calculatePerfectPosition());
	}

	private class NotifyPanel extends JPanel {

		private int offsetY;

		public NotifyPanel(int width, int height) {
			this.offsetY = -NOTIFY_HEIGHT;

			setOpaque(false);
			setPreferredSize(new Dimension(width, height));

			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

//			g2d.setColor(Color.RED);
//			g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

			List<Notify> list = new ArrayList<>(notifies).stream().filter(notify -> !notify.isDone()).collect(Collectors.toList());

			synchronized (notifies) {
				notifies.clear();
				notifies.addAll(list);
			}

			if (slideUp != null) {
				if (slideUp.isDone()) {
					slideUp.stop();
					slideUp = null;
				} else slideUp.update();
			}

			int y = 0;

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 18));

			for (Notify notify : list) {
				notify.render(g2d, 0, offsetY + y, getWidth(), NOTIFY_HEIGHT);

				y += NOTIFY_HEIGHT + NOTIFY_SPACE;
			}

			updateMouseWindowPosition();
			if (!notifies.isEmpty()) repaint();
		}

	}
}
