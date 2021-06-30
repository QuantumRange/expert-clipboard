package de.quantumrange.expertclipboard;

import de.quantumrange.expertclipboard.clip.Clipboard;
import de.quantumrange.expertclipboard.frame.*;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.util.Random;
import java.util.TimerTask;
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
