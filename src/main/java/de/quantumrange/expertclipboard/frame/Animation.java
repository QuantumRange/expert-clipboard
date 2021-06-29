package de.quantumrange.expertclipboard.frame;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Animation {

	private final int from, diff;
	private long time, startTime;
	private final Consumer<Integer> consumer;
	private final Consumer<Boolean> complete;

	public Animation(int from, int to, long time, Consumer<Integer> consumer, Consumer<Boolean> complete) {
		this.from = from;
		this.diff = to - from;
		this.time = time;
		this.consumer = consumer;
		this.complete = complete;
	}

	public void start() {
		long time = System.currentTimeMillis();
		this.startTime = time;
		this.time += time;
	}

	public void stop() {
		this.complete.accept(true);
	}

	public boolean isDone() {
		return System.currentTimeMillis() >= time;
	}

	public void update() {
		long timeDiff = time - startTime;

		float progress = (System.currentTimeMillis() - startTime) / (float) timeDiff;

		consumer.accept(calcFadeInOut(progress, from, diff));
	}

	public static int calcFadeInOut(float x, int from, int diff) {
		double animation = x < 0.5 ?
				4 * x * x * x :
				1 - Math.pow(-2 * x + 2, 3) / 2;
		return (int) (from + (diff * animation));
	}

}
