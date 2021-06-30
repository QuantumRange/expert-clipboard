package de.quantumrange.expertclipboard.frame;

import de.quantumrange.expertclipboard.Main;
import de.quantumrange.expertclipboard.clip.Clipboard;
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
		System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " with Modifiers: " + e.getModifiers());

		if (e.getModifiers() == (ALT_L_MASK | SHIFT_L_MASK)) {
			if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) Main.frame.close();
			else if (e.getKeyCode() == NativeKeyEvent.VC_Z) {
				Clipboard.undo();
			} else if (e.getKeyCode() == NativeKeyEvent.VC_R) {
				Clipboard.redo();
			}

			else Main.frame.open();

			int num = e.getKeyCode() - 1;

			if (num > 0 && num <= 9) {
				Main.frame.updateSelected(num);
			}
		} else if (e.getKeyCode() == NativeKeyEvent.VC_TAB) {
			try {
				GlobalScreen.unregisterNativeHook();
				System.exit(0);
			} catch (NativeHookException ignored) { }
		}
	}
}
