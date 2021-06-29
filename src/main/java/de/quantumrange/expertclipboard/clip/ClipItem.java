package de.quantumrange.expertclipboard.clip;

import de.quantumrange.expertclipboard.FileUtil;
import de.quantumrange.expertclipboard.clip.impl.ImageSelection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.*;

public class ClipItem {

	private LocalDateTime createDate;
	private final Object obj;
	private final ClipItemType type;

	public ClipItem(LocalDateTime createDate, Object obj, ClipItemType type) {
		this.createDate = createDate;
		this.obj = obj;
		this.type = type;
	}

	public enum ClipItemType {

		STRING("Text",
				DataFlavor.stringFlavor,
				(obj, file) -> {
				String data = (String) obj;

				FileUtil.write(file, data);
				}, FileUtil::read, obj -> new StringSelection((String) obj)),
		IMAGE("Image",
				DataFlavor.imageFlavor,
				(obj, file) -> {
					Image data = (Image) obj;
					try {
						ImageIO.write((RenderedImage) data, "PNG", file);
					} catch (IOException ignore) { }
				}, file -> {
					try {
						return ImageIO.read(file);
					} catch (IOException ignore) { }
					return null;
				}, obj -> new ImageSelection((Image) obj));

		private final String displayName;
		private final DataFlavor type;
		private final BiConsumer<Object, File> save;
		private final Function<File, Object> load;
		private final Function<Object, Transferable> transferable;

		ClipItemType(String displayName, DataFlavor type, BiConsumer<Object, File> save, Function<File, Object> load,
		             Function<Object, Transferable> transferable) {
			this.displayName = displayName;
			this.type = type;
			this.save = save;
			this.load = load;
			this.transferable = transferable;
		}

		public Function<Object, Transferable> getTransferable() {
			return transferable;
		}

		public String getDisplayName() {
			return displayName;
		}

		public DataFlavor getType() {
			return type;
		}

		public BiConsumer<Object, File> getSave() {
			return save;
		}

		public Function<File, Object> getLoad() {
			return load;
		}
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public Object getObj() {
		return obj;
	}

	public ClipItemType getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClipItem clipItem = (ClipItem) o;
		return Objects.equals(createDate, clipItem.createDate) && Objects.equals(obj, clipItem.obj) && type == clipItem.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createDate, obj, type);
	}

	@Override
	public String toString() {
		return "ClipItem{" +
				"createDate=" + createDate +
				", obj=" + obj +
				", type=" + type +
				'}';
	}
}
