package dev.inspectitems.client.anim;

import java.util.ArrayList;
import java.util.List;

/**
 * A value over normalized time (0..1) through keyframes, using cubic Hermite interpolation.
 * <p>
 * {@link #smooth} keys are monotone-smooth: velocity is continuous, there is no overshoot between
 * keys, and every peak/valley comes to rest. For punch, use {@link #builder()}:
 * <ul>
 *   <li>{@code k}      normal smooth key</li>
 *   <li>{@code launch} leaves the key with a burst of speed (kick off the hand)</li>
 *   <li>{@code hit}    arrives at full speed and rebounds at full speed (impact / catch)</li>
 *   <li>{@code land}   arrives at full speed and stops dead</li>
 *   <li>{@code cruise} constant speed through the key (good for spins)</li>
 * </ul>
 * Pure Java, no Minecraft types.
 */
public final class Track {
	public static final Track ZERO = new Track(new float[] {0f, 1f}, new float[] {0f, 0f}, new float[] {0f, 0f}, new float[] {0f, 0f});

	/** Tangent multiplier meaning "work it out from the neighbours". */
	static final float AUTO = -1f;

	private final float[] t;
	private final float[] v;
	private final float[] mIn;  // tangent arriving at each key (dv/dt)
	private final float[] mOut; // tangent leaving each key

	private Track(float[] t, float[] v, float[] mIn, float[] mOut) {
		this.t = t;
		this.v = v;
		this.mIn = mIn;
		this.mOut = mOut;
	}

	/** Keys are time,value pairs with increasing time. */
	public static Track smooth(float... keys) {
		if (keys.length < 4 || keys.length % 2 != 0) {
			throw new IllegalArgumentException("Track needs at least two time,value pairs");
		}
		Builder b = new Builder();
		for (int i = 0; i < keys.length; i += 2) {
			b.k(keys[i], keys[i + 1]);
		}
		return b.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public float at(float time) {
		int last = t.length - 1;
		if (time <= t[0]) {
			return v[0];
		}
		if (time >= t[last]) {
			return v[last];
		}
		int i = 1;
		while (time > t[i]) {
			i++;
		}
		float h = t[i] - t[i - 1];
		float s = (time - t[i - 1]) / h;
		float s2 = s * s;
		float s3 = s2 * s;
		return (2f * s3 - 3f * s2 + 1f) * v[i - 1]
			+ (s3 - 2f * s2 + s) * h * mOut[i - 1]
			+ (-2f * s3 + 3f * s2) * v[i]
			+ (s3 - s2) * h * mIn[i];
	}

	public static final class Builder {
		private final List<float[]> keys = new ArrayList<>(); // t, v, inMul, outMul

		private Builder() {}

		public Builder k(float time, float value) {
			return k(time, value, AUTO, AUTO);
		}

		/** Explicit tangent multipliers: 0 = ease to rest, 1 = secant speed, 2 = quadratic ease-in/out. */
		public Builder k(float time, float value, float inMul, float outMul) {
			keys.add(new float[] {time, value, inMul, outMul});
			return this;
		}

		public Builder launch(float time, float value) { return k(time, value, AUTO, 2f); }
		public Builder hit(float time, float value) { return k(time, value, 2f, 2f); }
		public Builder land(float time, float value) { return k(time, value, 2f, 0f); }
		public Builder cruise(float time, float value) { return k(time, value, 1f, 1f); }

		public Track build() {
			int n = keys.size();
			if (n < 2) {
				throw new IllegalArgumentException("Track needs at least two keys");
			}
			float[] t = new float[n];
			float[] v = new float[n];
			for (int i = 0; i < n; i++) {
				t[i] = keys.get(i)[0];
				v[i] = keys.get(i)[1];
				if (i > 0 && t[i] <= t[i - 1]) {
					throw new IllegalArgumentException("Track times must increase");
				}
			}
			float[] d = new float[n - 1];
			for (int i = 0; i < n - 1; i++) {
				d[i] = (v[i + 1] - v[i]) / (t[i + 1] - t[i]);
			}
			float[] mIn = new float[n];
			float[] mOut = new float[n];
			for (int i = 0; i < n; i++) {
				float inMul = keys.get(i)[2];
				float outMul = keys.get(i)[3];
				float auto = 0f;
				if (i > 0 && i < n - 1) {
					float a = d[i - 1];
					float b = d[i];
					auto = (a * b <= 0f) ? 0f : 2f * a * b / (a + b);
				}
				mIn[i] = (inMul == AUTO || i == 0) ? auto : inMul * d[i - 1];
				mOut[i] = (outMul == AUTO || i == n - 1) ? auto : outMul * d[i];
			}
			return new Track(t, v, mIn, mOut);
		}
	}
}
