package dev.inspectitems.client.anim;

import java.util.ArrayList;
import java.util.List;

/** A named inspect routine: shared raise/lower envelope plus per-axis keyframe tracks and sound cues. */
public record InspectAnimation(String name, int ticks, Track inward, Track up, Track depth,
							   Track pitch, Track yaw, Track roll, Track scale, float baseDepth, List<Cue> cues) {
	public enum Sfx { SWISH, THUD }

	/** hitStop: ticks the whole animation freezes on this cue (impact weight). 0 = none. */
	public record Cue(float time, Sfx sfx, float pitch, int hitStop) {}

	/** The item never gets closer to the camera than this (positive = toward camera). */
	public static final float MAX_DEPTH = 0.10f;

	private static final float RAISE_INWARD = 0.26f;
	private static final float RAISE_UP = 0.20f;
	private static final float RAISE_DEPTH = 0.06f;
	private static final float TILT_PITCH = -8f;
	private static final float TILT_YAW = 14f;

	/** Fraction of the animation spent snapping the item up, and settling it back down. */
	private static final float RAISE_END = 0.09f;
	private static final float LOWER_START = 0.86f;

	public Frame sample(float time) {
		float e = envelope(time);
		float d = Math.min(MAX_DEPTH, RAISE_DEPTH * e + baseDepth * e + depth.at(time));
		return new Frame(
			RAISE_INWARD * e + inward.at(time),
			RAISE_UP * e + up.at(time),
			d,
			TILT_PITCH * e + pitch.at(time),
			TILT_YAW * e + yaw.at(time),
			roll.at(time),
			1f + scale.at(time));
	}

	/**
	 * Snap up with a small overshoot (ease-out-back), settle down with a quintic smootherstep.
	 * Fast in, soft out: the item jumps to attention and glides home with no kink.
	 */
	private static float envelope(float t) {
		float raise = t >= RAISE_END ? 1f : easeOutBack(t / RAISE_END);
		return raise * (1f - smoother(LOWER_START, 1f, t));
	}

	private static float easeOutBack(float u) {
		final float c1 = 1.3f;
		final float c3 = c1 + 1f;
		float x = u - 1f;
		return 1f + c3 * x * x * x + c1 * x * x;
	}

	private static float smoother(float a, float b, float x) {
		float u = Math.max(0f, Math.min(1f, (x - a) / (b - a)));
		return u * u * u * (u * (u * 6f - 15f) + 10f);
	}

	public static Builder builder(String name, int ticks) {
		return new Builder(name, ticks);
	}

	public static final class Builder {
		private final String name;
		private final int ticks;
		private Track inward = Track.ZERO, up = Track.ZERO, depth = Track.ZERO;
		private Track pitch = Track.ZERO, yaw = Track.ZERO, roll = Track.ZERO, scale = Track.ZERO;
		private float baseDepth = 0f;
		private final List<Cue> cues = new ArrayList<>();

		private Builder(String name, int ticks) {
			this.name = name;
			this.ticks = ticks;
		}

		public Builder inward(Track t) { this.inward = t; return this; }
		public Builder up(Track t) { this.up = t; return this; }
		public Builder depth(Track t) { this.depth = t; return this; }
		public Builder pitch(Track t) { this.pitch = t; return this; }
		public Builder yaw(Track t) { this.yaw = t; return this; }
		public Builder roll(Track t) { this.roll = t; return this; }
		/** Size offset from 1.0 (0.08 = 8% bigger). Pop it on impacts, dip it on wind-ups. */
		public Builder scale(Track t) { this.scale = t; return this; }
		/** Hold the item further from the camera for the whole inspect (negative = away). Good for long items. */
		public Builder baseDepth(float d) { this.baseDepth = d; return this; }
		public Builder cue(float time, Sfx sfx, float pitch) { cues.add(new Cue(time, sfx, pitch, 0)); return this; }
		/** A cue that also freezes the animation for {@code hitStop} ticks, for weight. */
		public Builder impact(float time, Sfx sfx, float pitch, int hitStop) {
			cues.add(new Cue(time, sfx, pitch, hitStop));
			return this;
		}

		public InspectAnimation build() {
			return new InspectAnimation(name, ticks, inward, up, depth, pitch, yaw, roll, scale,
				baseDepth, List.copyOf(cues));
		}
	}
}
