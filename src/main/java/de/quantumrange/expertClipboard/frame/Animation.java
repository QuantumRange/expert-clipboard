package de.quantumrange.expertClipboard.frame;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.Math.pow;
import static java.lang.Math.sin;

public class Animation {

	private final int from, diff;
	private long time, startTime;
	private final Consumer<Integer> consumer;
	private final Consumer<Boolean> complete;
	private AnimationType type;

	public Animation(int from, int to, long time, Consumer<Integer> consumer, Consumer<Boolean> complete) {
		this(from, to, time, consumer, complete, AnimationType.EASE_IN_OUT_BACK);
	}

	public Animation(int from, int to, long time,Consumer<Integer> consumer, Consumer<Boolean> complete,
	                 AnimationType type) {
		this.from = from;
		this.diff = to - from;
		this.time = time;
		this.consumer = consumer;
		this.complete = complete;
		this.type = type;
	}

	public void start() {
		long time = System.currentTimeMillis();
		this.startTime = time;
		this.time += time;
	}

	public void stop() {
		consumer.accept(from + diff);
		this.complete.accept(true);
	}

	public boolean isDone() {
		return System.currentTimeMillis() >= time;
	}

	public void update() {
		long timeDiff = time - startTime;

		double progress = (System.currentTimeMillis() - startTime) / (float) timeDiff;

		consumer.accept((int) (from + (diff * type.function.apply(progress))));
	}

	public enum AnimationType {
		FADE_IN_OUT(x -> {
			return x < 0.5 ?
				4 * x * x * x :
				1 - Math.pow(-2 * x + 2, 3) / 2;
		}),
		EASE_IN_OUT_ELASTIC(x -> {
			double c5 = (2 * Math.PI) / 4.5;
			double sin = sin((20 * x - 11.125) * c5);

			return x == 0 ? 0
					: x == 1 ? 1 : x < 0.5
					? -(pow(2, 20 * x - 10) * sin) / 2
					: (pow(2, -20 * x + 10) * sin) / 2 + 1;
		}),
		EASE_IN_ELASTIC(x -> {
			double c4 = (2 * Math.PI) / 3;

			return x == 0 ? 0 : x == 1 ? 1 : -pow(2, 10 * x - 10) * sin((x * 10 - 10.75) * c4);
		}),
		EASE_OUT_BOUNCE(x -> {
			double n1 = 7.5625;
			double d1 = 2.75;

			if (x < 1 / d1) return n1 * x * x;
			else if (x < 2 / d1) return n1 * (x -= 1.5 / d1) * x + 0.75;
			else if (x < 2.5 / d1) return n1 * (x -= 2.25 / d1) * x + 0.9375;
			else return n1 * (x -= 2.625 / d1) * x + 0.984375;
		}),
		EASE_IN_OUT_BOUNCE(x -> {
			return x < 0.5
					? (1 - EASE_OUT_BOUNCE.function.apply(1 - 2 * x)) / 2
					: (1 + EASE_OUT_BOUNCE.function.apply(2 * x - 1)) / 2;
		}),
		EASE_IN_OUT_BACK(x -> {
			double c1 = 1.70158;
			double c2 = c1 * 1.525;

			return x < 0.5
					? (pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
					: (pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
		});

		private Function<Double, Double> function;

		AnimationType(Function<Double, Double> calc) {
			this.function = calc;
		}

		public Function<Double, Double> getCalc() {
			return function;
		}
	}

}
