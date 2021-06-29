package de.quantumrange.expertclipboard.clip;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public abstract class ClipType<T> implements Transferable {

	private final DataFlavor favor;
	private final T data;

	public ClipType(DataFlavor favor, T data) {
		this.favor = favor;
		this.data = data;
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { favor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return this.favor.equals(flavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (!this.favor.equals(flavor)) throw new UnsupportedFlavorException(flavor);
		return data;
	}

	public abstract void render(Graphics2D g2d, int width, int height);

}
