package dev.inspectitems.client.anim;

/**
 * One sampled pose of the held item, relative to its normal first-person resting spot.
 * inward: toward screen center, up: screen up, depth: toward the camera (negative = away),
 * pitch/yaw/roll: degrees, applied about the grip. scale: uniform size multiplier (1 = normal),
 * used for squash/stretch pops on impacts.
 */
public record Frame(float inward, float up, float depth, float pitch, float yaw, float roll, float scale) {
	public static final Frame REST = new Frame(0f, 0f, 0f, 0f, 0f, 0f, 1f);

	/** Linear blend, used to ease between poses when an inspect starts or is cancelled mid-move. */
	public static Frame lerp(Frame a, Frame b, float k) {
		return new Frame(
			a.inward + (b.inward - a.inward) * k,
			a.up + (b.up - a.up) * k,
			a.depth + (b.depth - a.depth) * k,
			a.pitch + (b.pitch - a.pitch) * k,
			a.yaw + (b.yaw - a.yaw) * k,
			a.roll + (b.roll - a.roll) * k,
			a.scale + (b.scale - a.scale) * k);
	}
}
