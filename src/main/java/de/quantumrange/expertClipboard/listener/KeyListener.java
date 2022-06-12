package de.quantumrange.expertClipboard.listener;

import de.quantumrange.expertClipboard.Main;
import de.quantumrange.expertClipboard.frame.Notify;
import de.quantumrange.expertClipboard.clip.Clipboard;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import static org.jnativehook.NativeInputEvent.*;

public class KeyListener implements NativeKeyListener {

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {

	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {

	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		System.out.println("Press: " + e.getKeyCode() + " with " + e.getModifiers());

		if (e.getModifiers() == (ALT_L_MASK | SHIFT_L_MASK)) {
			if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
				Main.frame.close();
				Notify.success("Close GUI");
			}
			else if (e.getKeyCode() == NativeKeyEvent.VC_Z) {
				Clipboard.undo();
			} else if (e.getKeyCode() == NativeKeyEvent.VC_R) {
				Clipboard.redo();
			} else if (e.getKeyCode() == NativeKeyEvent.VC_H) {
				Main.frame.close();
				if (Main.historyFrame.isOpen()) Main.historyFrame.close();
				else Main.historyFrame.open(Clipboard.currentSlot);
			} else Main.frame.open();

			int num = e.getKeyCode() - 1;

			if (num > 0 && num <= 9) {
				Main.frame.updateSelected(num);
				Main.historyFrame.close();
			}
		} else if (e.getKeyCode() == NativeKeyEvent.VC_F5) {
			try {
				GlobalScreen.unregisterNativeHook();
				System.exit(0);
			} catch (NativeHookException ignored) { }
		} else {
			Main.historyFrame.pressKey(e.getKeyCode());
		}
	}
}
