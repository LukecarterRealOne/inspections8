package dev.inspectitems.client.anim;

import dev.inspectitems.client.anim.InspectAnimation.Sfx;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Catalog of inspect routines per item category. Add new ones here.
 * Tracks are (time, value) keys, time 0..1. Add a small opposite dip before a big move for
 * wind-up, and a small overshoot after it for snap. In-plane roll never changes distance to
 * the camera, so long items (spears) use roll and lunges instead of pitch/yaw flips.
 */
public final class Animations {
	public enum Category { SWORD, AXE, TOOL, MACE, SPEAR, GENERIC }

	private static final Map<Category, List<InspectAnimation>> CATALOG = new EnumMap<>(Category.class);

	// ---- Sword -------------------------------------------------------------------------
	public static final InspectAnimation SWORD_TWIRL = InspectAnimation.builder("Sword twirl", 36)
		.yaw(Track.smooth(0, 0, 0.10f, 0, 0.20f, -45, 0.46f, -45, 0.60f, 10, 0.70f, 0, 1, 0))
		.roll(Track.builder().k(0, 0).k(0.14f, 0).launch(0.20f, -18).k(0.50f, 372, 1.4f, 1.4f)
			.k(0.58f, 355).k(0.64f, 360).k(1, 360).build())
		.up(Track.smooth(0, 0, 0.28f, 0, 0.40f, 0.08f, 0.54f, 0, 1, 0))
		.pitch(Track.smooth(0, 0, 0.50f, 0, 0.56f, -14, 0.68f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.48f, 0, 0.55f, 0.06f, 0.68f, 0, 1, 0))
		.cue(0.22f, Sfx.SWISH, 1.3f).cue(0.48f, Sfx.SWISH, 1.7f)
		.build();

	public static final InspectAnimation SWORD_FLIP = InspectAnimation.builder("Sword flip", 34)
		.up(Track.builder().k(0, 0).k(0.12f, 0).launch(0.19f, -0.04f).k(0.38f, 0.34f)
			.hit(0.58f, -0.04f).k(0.68f, 0).k(1, 0).build())
		.pitch(Track.builder().k(0, 0).k(0.12f, 0).k(0.19f, 12, Track.AUTO, 1f)
			.k(0.58f, -372, 1.3f, 1.3f).k(0.66f, -356).k(0.72f, -360).k(1, -360).build())
		.yaw(Track.smooth(0, 0, 0.58f, 0, 0.68f, 30, 0.82f, 0, 1, 0))
		.roll(Track.smooth(0, 0, 0.58f, 0, 0.68f, -18, 0.82f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.56f, 0, 0.60f, 0.07f, 0.74f, 0, 1, 0))
		.cue(0.24f, Sfx.SWISH, 1.0f).impact(0.58f, Sfx.THUD, 1.7f, 1)
		.build();

	public static final InspectAnimation SWORD_COMBO = InspectAnimation.builder("Slash combo", 32)
		.yaw(Track.smooth(0, 0, 0.09f, 0, 0.16f, -45, 0.23f, 45, 0.30f, -40, 0.37f, 50, 0.46f, -22, 0.58f, 0, 1, 0))
		.roll(Track.smooth(0, 0, 0.09f, 0, 0.16f, 35, 0.23f, -35, 0.30f, 30, 0.37f, -45, 0.46f, 12, 0.58f, 0, 1, 0))
		.pitch(Track.smooth(0, 0, 0.09f, 0, 0.16f, -20, 0.23f, -10, 0.30f, -22, 0.37f, 16, 0.50f, 0, 1, 0))
		.depth(Track.builder().k(0, 0).k(0.30f, 0).hit(0.38f, -0.13f).k(0.56f, 0).k(1, 0).build())
		.scale(Track.smooth(0, 0, 0.30f, 0, 0.38f, 0.07f, 0.52f, 0, 1, 0))
		.cue(0.14f, Sfx.SWISH, 1.1f).cue(0.21f, Sfx.SWISH, 1.2f)
		.cue(0.28f, Sfx.SWISH, 1.3f).impact(0.37f, Sfx.SWISH, 0.9f, 1)
		.build();

	// ---- Axe ---------------------------------------------------------------------------
	public static final InspectAnimation AXE_TOSS = InspectAnimation.builder("Axe toss", 38)
		.up(Track.builder().k(0, 0).k(0.12f, 0).launch(0.18f, -0.05f).k(0.40f, 0.42f)
			.hit(0.62f, -0.06f).k(0.74f, 0).k(1, 0).build())
		.pitch(Track.builder().k(0, 0).k(0.12f, 0).k(0.18f, 14, Track.AUTO, 1f)
			.k(0.62f, -736, 1.5f, 1.5f).k(0.70f, -716).k(0.76f, -720).k(1, -720).build())
		.depth(Track.smooth(0, 0, 0.18f, 0, 0.40f, -0.12f, 0.62f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.60f, 0, 0.64f, 0.08f, 0.78f, 0, 1, 0))
		.cue(0.22f, Sfx.SWISH, 0.9f).impact(0.62f, Sfx.THUD, 1.2f, 2)
		.build();

	public static final InspectAnimation AXE_CHOP = InspectAnimation.builder("Axe chop", 34)
		.pitch(Track.builder().k(0, 0).k(0.10f, 0).k(0.26f, -84).k(0.32f, -84)
			.k(0.38f, 50, 2f, 2f).k(0.46f, 32).k(0.52f, 38).k(0.74f, 0).k(1, 0).build())
		.up(Track.builder().k(0, 0).k(0.10f, 0).k(0.26f, 0.12f).k(0.32f, 0.12f)
			.k(0.38f, -0.13f, 2f, 2f).k(0.52f, -0.08f).k(0.76f, 0).k(1, 0).build())
		.depth(Track.builder().k(0, 0).k(0.32f, 0).hit(0.38f, -0.18f).k(0.52f, -0.10f).k(0.78f, 0).k(1, 0).build())
		.scale(Track.smooth(0, 0, 0.26f, -0.03f, 0.38f, 0.08f, 0.52f, 0, 1, 0))
		.cue(0.33f, Sfx.SWISH, 0.8f).impact(0.38f, Sfx.THUD, 0.7f, 2)
		.build();

	public static final InspectAnimation AXE_WINDMILL = InspectAnimation.builder("Axe windmill", 36)
		.roll(Track.builder().k(0, 0).k(0.10f, 0).launch(0.20f, -22).k(0.64f, 742, 1.4f, 1.4f)
			.k(0.72f, 716).k(0.78f, 720).k(1, 720).build())
		.pitch(Track.smooth(0, 0, 0.20f, -14, 0.68f, -14, 0.84f, 0, 1, 0))
		.up(Track.smooth(0, 0, 0.20f, 0, 0.42f, 0.10f, 0.68f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.62f, 0, 0.68f, 0.06f, 0.80f, 0, 1, 0))
		.cue(0.24f, Sfx.SWISH, 0.8f).cue(0.42f, Sfx.SWISH, 1.0f).cue(0.64f, Sfx.SWISH, 1.4f)
		.build();

	// ---- Pickaxe / shovel / hoe / misc tools ------------------------------------------------
	public static final InspectAnimation TOOL_TWIRL = InspectAnimation.builder("Tool twirl", 30)
		.roll(Track.builder().k(0, 0).k(0.12f, 0).launch(0.20f, -14).k(0.58f, 374, 1.4f, 1.4f)
			.k(0.66f, 358).k(0.72f, 360).k(1, 360).build())
		.yaw(Track.smooth(0, 0, 0.58f, 0, 0.70f, 50, 0.88f, 0, 1, 0))
		.up(Track.smooth(0, 0, 0.16f, 0, 0.36f, 0.10f, 0.60f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.56f, 0, 0.62f, 0.06f, 0.76f, 0, 1, 0))
		.cue(0.24f, Sfx.SWISH, 1.2f).cue(0.58f, Sfx.SWISH, 1.6f)
		.build();

	// ---- Mace --------------------------------------------------------------------------
	public static final InspectAnimation MACE_SLAM = InspectAnimation.builder("Mace slam", 42)
		.pitch(Track.builder().k(0, 0).k(0.12f, 0).k(0.34f, -105).k(0.44f, -105)
			.k(0.50f, 46, 2f, 2f).k(0.56f, 28).k(0.62f, 38).k(0.80f, 0).k(1, 0).build())
		.up(Track.builder().k(0, 0).k(0.12f, 0).k(0.34f, 0.28f).k(0.44f, 0.28f)
			.k(0.50f, -0.22f, 2f, 2f).k(0.62f, -0.12f).k(0.82f, 0).k(1, 0).build())
		.depth(Track.builder().k(0, 0).k(0.44f, 0).hit(0.50f, -0.16f).k(0.82f, 0).k(1, 0).build())
		.roll(Track.smooth(0, 0, 0.50f, 0, 0.54f, 9, 0.58f, -8, 0.62f, 6, 0.66f, -4, 0.72f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.34f, -0.04f, 0.50f, 0.09f, 0.64f, 0, 1, 0))
		.cue(0.45f, Sfx.SWISH, 0.6f).impact(0.50f, Sfx.THUD, 0.6f, 3)
		.build();

	public static final InspectAnimation MACE_SPIN = InspectAnimation.builder("Mace windmill", 38)
		.roll(Track.builder().k(0, 0).k(0.12f, 0).launch(0.22f, -18).k(0.72f, 724, 1.4f, 1.4f)
			.k(0.80f, 714).k(0.86f, 720).k(1, 720).build())
		.pitch(Track.smooth(0, 0, 0.22f, -20, 0.70f, -20, 0.86f, 0, 1, 0))
		.up(Track.smooth(0, 0, 0.22f, 0, 0.46f, 0.10f, 0.72f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.70f, 0, 0.74f, 0.07f, 0.88f, 0, 1, 0))
		.cue(0.30f, Sfx.SWISH, 0.7f).impact(0.74f, Sfx.THUD, 0.8f, 1)
		.build();

	// ---- Spear / trident (kept further from the camera, roll and lunge only) -------------------
	public static final InspectAnimation SPEAR_LUNGE = InspectAnimation.builder("Spear lunge", 34)
		.baseDepth(-0.20f)
		.depth(Track.builder().k(0, 0).k(0.12f, 0).k(0.30f, 0.07f).hit(0.40f, -0.38f)
			.k(0.52f, -0.35f).k(0.82f, 0).k(1, 0).build())
		.pitch(Track.builder().k(0, 0).k(0.12f, 0).k(0.30f, 12).hit(0.40f, -8).k(0.52f, -6).k(0.82f, 0).k(1, 0).build())
		.yaw(Track.builder().k(0, 0).k(0.12f, 0).k(0.30f, -20).hit(0.40f, 8).k(0.52f, 6).k(0.82f, 0).k(1, 0).build())
		.scale(Track.smooth(0, 0, 0.30f, -0.04f, 0.40f, 0.08f, 0.56f, 0, 1, 0))
		.cue(0.34f, Sfx.SWISH, 0.8f).impact(0.40f, Sfx.THUD, 1.3f, 1)
		.build();

	public static final InspectAnimation STAFF_TWIRL = InspectAnimation.builder("Staff twirl", 36)
		.baseDepth(-0.20f)
		.roll(Track.builder().k(0, 0).k(0.12f, 0).launch(0.20f, 14).k(0.70f, -740, 1.4f, 1.4f)
			.k(0.78f, -714).k(0.84f, -720).k(1, -720).build())
		.pitch(Track.smooth(0, 0, 0.20f, 8, 0.74f, 8, 0.88f, 0, 1, 0))
		.inward(Track.smooth(0, 0, 0.12f, 0, 0.35f, 0.07f, 0.60f, -0.07f, 0.80f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.68f, 0, 0.72f, 0.05f, 0.86f, 0, 1, 0))
		.cue(0.24f, Sfx.SWISH, 0.8f).cue(0.46f, Sfx.SWISH, 1.0f).cue(0.70f, Sfx.SWISH, 1.3f)
		.build();

	// ---- Anything else -----------------------------------------------------------------
	public static final InspectAnimation TURNTABLE = InspectAnimation.builder("Turntable", 32)
		.yaw(Track.builder().k(0, 0).k(0.10f, 0).launch(0.14f, -8).k(0.68f, 372, 1.4f, 1.4f)
			.k(0.78f, 358).k(0.84f, 360).k(1, 360).build())
		.pitch(Track.smooth(0, 0, 0.10f, 0, 0.28f, -18, 0.70f, -18, 0.86f, 0, 1, 0))
		.up(Track.smooth(0, 0, 0.10f, 0, 0.40f, 0.07f, 0.80f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.66f, 0, 0.72f, 0.05f, 0.86f, 0, 1, 0))
		.cue(0.22f, Sfx.SWISH, 1.4f)
		.build();

	public static final InspectAnimation TOSS = InspectAnimation.builder("Toss", 28)
		.up(Track.builder().k(0, 0).k(0.14f, 0).launch(0.20f, -0.04f).k(0.42f, 0.34f)
			.hit(0.66f, -0.04f).k(0.76f, 0).k(1, 0).build())
		.pitch(Track.builder().k(0, 0).k(0.14f, 0).k(0.20f, 10, Track.AUTO, 1f)
			.k(0.66f, -370, 1.4f, 1.4f).k(0.74f, -356).k(0.80f, -360).k(1, -360).build())
		.scale(Track.smooth(0, 0, 0.64f, 0, 0.68f, 0.07f, 0.82f, 0, 1, 0))
		.cue(0.24f, Sfx.SWISH, 1.5f).impact(0.66f, Sfx.THUD, 1.5f, 1)
		.build();

	public static final InspectAnimation CLOSE_LOOK = InspectAnimation.builder("Close look", 36)
		.yaw(Track.smooth(0, 0, 0.14f, 0, 0.32f, 38, 0.58f, -38, 0.78f, 0, 1, 0))
		.roll(Track.smooth(0, 0, 0.14f, 0, 0.32f, 10, 0.58f, -10, 0.78f, 0, 1, 0))
		.pitch(Track.smooth(0, 0, 0.14f, 0, 0.32f, -12, 0.58f, -12, 0.78f, 0, 1, 0))
		.inward(Track.smooth(0, 0, 0.14f, 0, 0.42f, -0.06f, 0.78f, 0, 1, 0))
		.depth(Track.smooth(0, 0, 0.14f, 0, 0.42f, 0.08f, 0.78f, 0, 1, 0))
		.scale(Track.smooth(0, 0, 0.14f, 0, 0.32f, 0.05f, 0.58f, 0.05f, 0.78f, 0, 1, 0))
		.cue(0.16f, Sfx.SWISH, 1.5f)
		.build();

	static {
		CATALOG.put(Category.SWORD, List.of(SWORD_TWIRL, SWORD_FLIP, SWORD_COMBO));
		CATALOG.put(Category.AXE, List.of(AXE_TOSS, AXE_CHOP, AXE_WINDMILL));
		CATALOG.put(Category.TOOL, List.of(TOOL_TWIRL, AXE_TOSS, AXE_WINDMILL));
		CATALOG.put(Category.MACE, List.of(MACE_SLAM, MACE_SPIN));
		CATALOG.put(Category.SPEAR, List.of(SPEAR_LUNGE, STAFF_TWIRL));
		CATALOG.put(Category.GENERIC, List.of(TURNTABLE, TOSS, CLOSE_LOOK));
	}

	private Animations() {}

	public static List<InspectAnimation> all() {
		return CATALOG.values().stream().flatMap(List::stream).distinct().toList();
	}

	/** Random routine for the category, avoiding an immediate repeat of {@code last} when possible. */
	public static InspectAnimation pick(Category category, Random random, InspectAnimation last) {
		List<InspectAnimation> list = CATALOG.get(category);
		InspectAnimation pick = list.get(random.nextInt(list.size()));
		if (pick == last && list.size() > 1) {
			pick = list.get((list.indexOf(pick) + 1 + random.nextInt(list.size() - 1)) % list.size());
		}
		return pick;
	}

	/** Category from an item's registry path, e.g. "iron_sword" or "mace". Works for modded items by name. */
	public static Category categoryOf(String path) {
		if (path.equals("mace")) return Category.MACE;
		if (path.endsWith("_sword")) return Category.SWORD;
		if (path.endsWith("_axe")) return Category.AXE;
		if (path.endsWith("_spear") || path.equals("spear") || path.equals("trident")) return Category.SPEAR;
		if (path.endsWith("_pickaxe") || path.endsWith("_shovel") || path.endsWith("_hoe")
			|| path.equals("shears") || path.equals("fishing_rod") || path.equals("brush")) {
			return Category.TOOL;
		}
		return Category.GENERIC;
	}
}
