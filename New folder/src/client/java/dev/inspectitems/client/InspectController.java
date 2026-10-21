package dev.inspectitems.client;

import dev.inspectitems.client.anim.Animations;
import dev.inspectitems.client.anim.Frame;
import dev.inspectitems.client.anim.InspectAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

/** Holds the state of the current inspection and drives it from client ticks. */
public final class InspectController {
	private static final Random RANDOM = new Random();

	private static boolean active;
	private static int ticks;
	private static int totalTicks = 1;
	private static InspectAnimation animation;
	private static InspectAnimation lastAnimation;
	private static ItemStack startStack = ItemStack.EMPTY;

	/** Ticks the animation is frozen for (impact hit-stop). */
	private static int hold;
	/** Blend from the previous pose into a freshly started animation (chaining). */
	private static Frame blendFrom = Frame.REST;
	private static int blendTick;
	private static int blendTotal;
	/** Quick blend back to rest when the inspect is cancelled by attack/use, instead of snapping. */
	private static boolean exiting;
	private static int exitTick;
	private static Frame exitFrom = Frame.REST;
	/** Last pose we rendered, the starting point for any blend. */
	private static Frame lastFrame = Frame.REST;

	private static final int EXIT_TICKS = 2;
	private static final int CHAIN_TICKS = 3;
	/** Pressing the key again after this much of the move chains straight into a new one. */
	private static final float CHAIN_FROM = 0.45f;

	private InspectController() {}

	public static boolean isActive() {
		return active;
	}

	public static void onClientTick(Minecraft mc) {
		boolean pressed = false;
		while (InspectItemsClient.inspectKey.consumeClick()) {
			pressed = true;
		}
		if (active && exiting) {
			if (++exitTick >= EXIT_TICKS) {
				stop();
			}
		} else if (active) {
			boolean chain = pressed && totalTicks > 0 && (float) ticks / totalTicks >= CHAIN_FROM;
			if (advance(mc) && chain) {
				start(mc);
			}
		} else if (pressed) {
			start(mc);
		}
	}

	private static void start(Minecraft mc) {
		LocalPlayer player = mc.player;
		if (player == null || mc.screen != null || player.isUsingItem()) {
			return;
		}
		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty() || stack.is(Items.FILLED_MAP)) {
			return;
		}
		String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		boolean chaining = active;
		animation = Animations.pick(Animations.categoryOf(path), RANDOM, lastAnimation);
		lastAnimation = animation;
		float speed = Math.max(0.25f, InspectConfig.get().animationSpeed);
		totalTicks = Math.max(1, Math.round(animation.ticks() / speed));
		ticks = 0;
		hold = 0;
		exiting = false;
		blendFrom = chaining ? lastFrame : Frame.REST;
		blendTick = 0;
		blendTotal = chaining ? CHAIN_TICKS : 0;
		startStack = stack.copy();
		active = true;
	}

	/** Returns true while the animation keeps running normally. */
	private static boolean advance(Minecraft mc) {
		LocalPlayer player = mc.player;
		if (player == null || mc.screen != null
			|| !ItemStack.isSameItemSameComponents(player.getMainHandItem(), startStack)) {
			stop();
			return false;
		}
		if (player.isUsingItem() || mc.options.keyAttack.isDown() || mc.options.keyUse.isDown()) {
			beginExit();
			return false;
		}
		if (hold > 0) {
			hold--;
			return true;
		}
		float from = (float) ticks / totalTicks;
		ticks++;
		if (blendTick < blendTotal) {
			blendTick++;
		}
		float to = (float) ticks / totalTicks;
		for (InspectAnimation.Cue cue : animation.cues()) {
			if (cue.time() > from && cue.time() <= to) {
				if (InspectConfig.get().sounds) {
					switch (cue.sfx()) {
						case SWISH -> player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.5f, cue.pitch());
						case THUD -> player.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 0.6f, cue.pitch());
					}
				}
				if (InspectConfig.get().hitStop && cue.hitStop() > 0) {
					hold = Math.max(hold, cue.hitStop());
				}
			}
		}
		if (ticks >= totalTicks) {
			stop();
			return false;
		}
		return true;
	}

	/** Ease back to the resting pose in a couple of ticks, so cancelling never snaps the item. */
	private static void beginExit() {
		exiting = true;
		exitTick = 0;
		exitFrom = lastFrame;
		hold = 0;
	}

	public static void stop() {
		active = false;
		exiting = false;
		hold = 0;
		blendTotal = 0;
		animation = null;
		startStack = ItemStack.EMPTY;
		lastFrame = Frame.REST;
	}

	private static float ease(float k) {
		return k * k * (3f - 2f * k);
	}

	public static Frame frame(float partialTick) {
		if (!active) {
			return Frame.REST;
		}
		Frame f;
		if (exiting) {
			float k = Mth.clamp((exitTick + partialTick) / EXIT_TICKS, 0f, 1f);
			f = Frame.lerp(exitFrom, Frame.REST, ease(k));
		} else if (animation == null) {
			return Frame.REST;
		} else {
			// During hit-stop the pose is frozen exactly on the impact frame.
			float pt = hold > 0 ? 0f : partialTick;
			float t = Mth.clamp((ticks + pt) / totalTicks, 0f, 1f);
			f = animation.sample(t);
			if (blendTick < blendTotal) {
				float k = Mth.clamp((blendTick + pt) / blendTotal, 0f, 1f);
				f = Frame.lerp(blendFrom, f, ease(k));
			}
		}
		lastFrame = f;
		return f;
	}
}
