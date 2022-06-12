package de.quantumrange.expertClipboard;

import de.quantumrange.expertClipboard.clip.Clipboard;
import de.quantumrange.expertClipboard.frame.HistoryFrame;
import de.quantumrange.expertClipboard.frame.InfoFrame;
import de.quantumrange.expertClipboard.frame.NotifyFrame;
import de.quantumrange.expertClipboard.listener.KeyListener;
import de.quantumrange.expertClipboard.listener.MouseListener;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

	public static InfoFrame frame;
	public static NotifyFrame notifyFrame;
	public static HistoryFrame historyFrame;
	public static Thread timer;

	public static void main(String[] args) {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);

		frame = new InfoFrame(1000, 450);
		notifyFrame = new NotifyFrame(400, 1500);
		historyFrame = new HistoryFrame(520, 400);

		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(e -> Clipboard.update());
			GlobalScreen.addNativeKeyListener(new KeyListener());
			GlobalScreen.addNativeMouseMotionListener(new MouseListener());
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		timer = new Thread(() -> {
			while (true) {
				Clipboard.testClipboard();

				try {
					Thread.sleep(50);
				} catch (InterruptedException ignore) { }
			}
		});
		timer.start();
	}

}
