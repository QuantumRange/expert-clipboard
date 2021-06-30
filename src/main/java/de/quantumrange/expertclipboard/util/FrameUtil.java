package de.quantumrange.expertclipboard.util;

import de.quantumrange.expertclipboard.frame.Animation;

public class FrameUtil {

	public static void updateAnimations(Animation[] animations) {
		for (int i = 0; i < animations.length; i++) {
			if (animations[i] != null) {
				if (animations[i].isDone()) {
					animations[i].stop();
					animations[i] = null;
				} else animations[i].update();
			}
		}
	}

}
