package de.quantumrange.expertclipboard.clip.impl;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

public class ImageSelection implements Transferable
{
	private final Image image;

	public ImageSelection(Image image)
	{
		this.image = image;
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return DataFlavor.imageFlavor.equals(flavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (!DataFlavor.imageFlavor.equals(flavor)) throw new UnsupportedFlavorException(flavor);
		return image;
	}
}
