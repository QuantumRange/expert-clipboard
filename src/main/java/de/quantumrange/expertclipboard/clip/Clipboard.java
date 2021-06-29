package de.quantumrange.expertclipboard.clip;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDateTime;

public class Clipboard {

	public static final int MAX_HISTORY = 20;

	public static ClipItem[][] slots = new ClipItem[9][MAX_HISTORY];
	public static int[] slotIndexes = new int[9];
	public static int currentSlot = 0;
	public static boolean disableCopy = false;

	public static ClipItem getLast() {
		return slots[currentSlot][slotIndexes[currentSlot]];
	}

	public static ClipItem getLast(int slot) {
		return slots[slot][slotIndexes[slot]];
	}

	public static void update() {
		if (!disableCopy) {
			ClipItem item = getCurrent();

			if (item == null) return;
			int i = slotIndexes[currentSlot];
			slotIndexes[currentSlot] = (i + 1) % MAX_HISTORY;

			slots[currentSlot][slotIndexes[currentSlot]] = item;
		}
	}

	public static void switchToClipboard() {
		disableCopy = true;

		ClipItem item = getCurrent();
		setCurrent(item);

		disableCopy = false;
	}

	public static ClipItem getCurrent() {
		try {
			java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			ClipItem item = null;

			for (ClipItem.ClipItemType type : ClipItem.ClipItemType.values()) {
				if (clipboard.isDataFlavorAvailable(type.getType())) {
					item = new ClipItem(LocalDateTime.now(), clipboard.getContents(null).getTransferData(type.getType()), type);
				}
			}

			return item;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setCurrent(ClipItem item) {
		if (item != null) {
			Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.setContents(item.getType()
							.getTransferable()
							.apply(item.getObj()),null);
			System.out.println("Save: " + item);
		} else {
			System.out.println("Nope");
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
		}
	}

}
