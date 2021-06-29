package de.quantumrange.expertclipboard;

import de.quantumrange.expertclipboard.clip.Clipboard;
import de.quantumrange.expertclipboard.frame.InfoFrame;
import de.quantumrange.expertclipboard.frame.KeyListener;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.awt.*;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

	public static InfoFrame frame;

	public static void main(String[] args) {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);

		frame = new InfoFrame(1000, 100);

		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(e -> Clipboard.update());
			GlobalScreen.addNativeKeyListener(new KeyListener());
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

	}

}
