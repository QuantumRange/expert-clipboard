package de.quantumrange.expertclipboard.clip;

import de.quantumrange.expertclipboard.FileUtil;
import de.quantumrange.expertclipboard.clip.impl.FileClip;
import de.quantumrange.expertclipboard.clip.impl.ImageClip;
import de.quantumrange.expertclipboard.clip.impl.StringClip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.*;

public class ClipItem {

	private final ClipType<?> obj;
	private final ClipItemType type;

	public ClipItem(ClipType<?> obj, ClipItemType type) {
		this.obj = obj;
		this.type = type;
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
		return Objects.equals(obj, clipItem.obj) && type == clipItem.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(obj, type);
	}

	@Override
	public String toString() {
		return "ClipItem{" +
				", obj=" + obj +
				", type=" + type +
				'}';
	}
}
