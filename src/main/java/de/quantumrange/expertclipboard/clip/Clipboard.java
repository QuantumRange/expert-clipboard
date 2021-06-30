package de.quantumrange.expertclipboard.clip;

import de.quantumrange.expertclipboard.util.FileUtil;
import de.quantumrange.expertclipboard.Main;
import de.quantumrange.expertclipboard.clip.impl.EmptyFlavor;
import de.quantumrange.expertclipboard.frame.Notify;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

	static {
		load(new File("./clip/"));
	}

	public static void redo() {
		int index = (slotIndexes[currentSlot] + 1) % MAX_HISTORY;

		if (slots[currentSlot][index] != null) {
			slotIndexes[currentSlot] = index;
			setClipboard(getLast());
			Notify.success("Redo successfully");
		} else {
			Notify.error("You can't go back any further.");
		}
	}

	public static void undo() {
		int index = slotIndexes[currentSlot] - 1;
		if (index < 0) {
			index = MAX_HISTORY - 1;
		}

		if (slots[currentSlot][index] != null) {
			slotIndexes[currentSlot] = index;
			setClipboard(getLast());
			Notify.success("Undo successfully");
		} else {
			Notify.error("You can't go back any further.");
		}
	}

	public static synchronized void testClipboard() {
		ClipItem item = getClipboard();

		if (item != null && getLast() == null) {
			update();
			return;
		}

		if (item == null) return;

		if (item.getType() != getLast().getType()) {
			update();
			return;
		}

		if (item.getType() == ClipItem.ClipItemType.IMAGE) return;

		if (!item.getObj().getData().equals(getLast().getObj().getData())) {
			update();
		}
	}

	public static void update() {
		if (!disableCopy) {
			ClipItem item = getClipboard();

			if (item == null) return;
			if (item.getType() != ClipItem.ClipItemType.IMAGE && getLast() != null) if (item.getObj().getData().equals(getLast().getObj().getData())) return;

			int i = slotIndexes[currentSlot];
			slotIndexes[currentSlot] = (i + 1) % MAX_HISTORY;

			slots[currentSlot][slotIndexes[currentSlot]] = item;

			Main.frame.updateClipboard();
			save(new File("./clip/"));
//			Notify.success("The %s was stored in clipboard.".formatted(getLast().getType().getDisplayName()));
		}
	}

	public static void switchToClipboard(int slot) {
		disableCopy = true;

//		Notify.success("It has been switched to clipboard %d.".formatted(slot + 1));
		ClipItem item = getLast(slot);
		currentSlot = slot;
		setClipboard(item);

		disableCopy = false;
	}

	public static ClipItem getClipboard() {
		try {
			java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			ClipItem item;

			for (ClipItem.ClipItemType type : ClipItem.ClipItemType.values()) {
				try {
					if (clipboard.isDataFlavorAvailable(type.getFlavor())) {
						item = new ClipItem(LocalDateTime.now(),
										type.getToClip().apply(clipboard.getContents(null).getTransferData(type.getFlavor())),
										type);
						return item;
					}
				} catch (IllegalStateException ignored) { }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setClipboard(ClipItem item) {
		if (item != null) {
			Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.setContents(item.getObj(), null);
		} else {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new EmptyFlavor(), null);
		}
	}

	public static ExecutorService executors;

	public static void save(File dir) {
		if (executors == null) executors = Executors.newFixedThreadPool(1);

		executors.execute(() -> {
			dir.mkdirs();

			File indexFile = new File(dir, "index.txt");

			FileUtil.write(indexFile, Arrays.stream(slotIndexes)
					.mapToObj(String::valueOf)
					.collect(Collectors.joining(":")));

			File dataFile = new File(dir, "data.txt");
			FileUtil.write(dataFile, Arrays.stream(slots)
					.map(clipItems -> Arrays.stream(clipItems)
							.map(clipItem -> clipItem == null ? "NULL" :
									clipItem.getType().name() + "|" + clipItem.getDateTime().toString())
							.collect(Collectors.joining(";")))
					.collect(Collectors.joining("\n")));

			File dataDir = new File(dir, "data/");
			dataDir.mkdirs();

			for (int x = 0; x < slots.length; x++) {
				for (int y = 0; y < slots[x].length; y++) {
					if (slots[x][y] != null) {
						slots[x][y].getObj().writeToFile(new File(dataDir, "%d-%d.dat".formatted(x, y)));
					}
				}
			}
		});
	}

	public static void load(File dir) {
		System.out.println("Load...");

		dir.mkdirs();

		File indexFile = new File(dir, "index.txt");

		if (!indexFile.exists()) return;

		String[] indexArgs = FileUtil.read(indexFile).split("\\:");

		for (int i = 0; i < indexArgs.length; i++) {
			slotIndexes[i] = Integer.parseInt(indexArgs[i]);
		}

		File dataFile = new File(dir, "data.txt");
		ClipItem.ClipItemType[][] clipItemTypes = new ClipItem.ClipItemType[9][MAX_HISTORY];
		LocalDateTime[][] clipItemDates = new LocalDateTime[9][MAX_HISTORY];
		String[] lines = FileUtil.read(dataFile).split("\n");

		for (int x = 0; x < lines.length; x++) {
			String[] entries = lines[x].split(";");

			for (int y = 0; y < entries.length; y++) {
				if (entries[y].equals("NULL")) clipItemTypes[x][y] = null;
				else {
					String[] split = entries[y].split("\\|");
					String dat = split[0];
					String date = split[1];

					clipItemDates[x][y] = LocalDateTime.parse(date);
					clipItemTypes[x][y] = ClipItem.ClipItemType.valueOf(dat);
				}
			}
		}

		File dataDir = new File(dir, "data/");
		dataDir.mkdirs();

		for (int x = 0; x < slots.length; x++) {
			for (int y = 0; y < slots[x].length; y++) {
				File file = new File(dataDir, "%d-%d.dat".formatted(x, y));
				if (file.exists()) {
					ClipItem.ClipItemType t = clipItemTypes[x][y];

					ClipType<?> type = t.getToClip().apply(null);

					type.readFromFile(file);

					slots[x][y] = new ClipItem(clipItemDates[x][y], type, t);
				} else slots[x][y] = null;
			}
		}

		System.out.println("Loaded!");
	}

}
