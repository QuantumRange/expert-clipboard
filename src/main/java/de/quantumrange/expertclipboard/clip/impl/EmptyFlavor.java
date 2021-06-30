package de.quantumrange.expertclipboard.clip.impl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class EmptyFlavor implements Transferable {

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[0];
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		throw new UnsupportedFlavorException(flavor);
	}

}
