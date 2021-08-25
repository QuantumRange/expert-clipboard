package de.lupus.copyCAT.clip;

import de.lupus.copyCAT.clip.impl.FileClip;
import de.lupus.copyCAT.clip.impl.ImageClip;
import de.lupus.copyCAT.clip.impl.StringClip;

import java.awt.datatransfer.DataFlavor;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.*;

public class ClipItem {

	private LocalDateTime dateTime;
	private final ClipType<?> obj;
	private final ClipItemType type;

	public ClipItem(LocalDateTime dateTime, ClipType<?> obj, ClipItemType type) {
		this.obj = obj;
		this.type = type;
		this.dateTime = dateTime;
	}

	public enum ClipItemType {

		STRING("Text", DataFlavor.stringFlavor, StringClip::new),
		FILE("File", DataFlavor.javaFileListFlavor, FileClip::new),
		IMAGE("Image", DataFlavor.imageFlavor, ImageClip::new);

		private final String displayName;
		private final DataFlavor flavor;
		private final Function<Object, ClipType<?>> toClip;

		ClipItemType(String displayName, DataFlavor flavor, Function<Object, ClipType<?>> toClip) {
			this.displayName = displayName;
			this.flavor = flavor;
			this.toClip = toClip;
		}

		public String getDisplayName() {
			return displayName;
		}

		public DataFlavor getFlavor() {
			return flavor;
		}

		public Function<Object, ClipType<?>> getToClip() {
			return toClip;
		}
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public ClipItemType getType() {
		return type;
	}

	public ClipType<?> getObj() {
		return obj;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClipItem clipItem = (ClipItem) o;
		return Objects.equals(getDateTime(), clipItem.getDateTime()) && Objects.equals(getObj(), clipItem.getObj()) && getType() == clipItem.getType();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getDateTime(), getObj(), getType());
	}

	@Override
	public String toString() {
		return "ClipItem{" +
				"dateTime=" + dateTime +
				", obj=" + obj +
				", type=" + type +
				'}';
	}
}
