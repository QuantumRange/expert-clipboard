package de.quantumrange.expertClipboard.listener;

import de.quantumrange.expertClipboard.Main;
import de.quantumrange.expertClipboard.frame.InfoFrame;
import de.quantumrange.expertClipboard.clip.Clipboard;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

import java.awt.*;

public class MouseListener implements NativeMouseMotionListener {

    @Override
    public void nativeMouseMoved(NativeMouseEvent event) {
        if (!Main.frame.isVisible()) return;

        Point loc = Main.frame.getLocationOnScreen();
        Dimension size = Main.frame.getSize();
        for (int i = 0; i < Clipboard.slotIndexes.length; i++) {
            Polygon polygon = InfoFrame.InfoPanel.getPolygon((int) ((size.getWidth() / 2) - (450 / 2)), i);

            Point point = event.getPoint();
            point.translate(-loc.x, -loc.y);

            if (polygon.contains(point)) {
                if (Clipboard.currentSlot != i) {
                    Clipboard.switchToClipboard(i);
                    Main.frame.updateSelected(i + 1);
                }
            }
        }
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

    }
}
