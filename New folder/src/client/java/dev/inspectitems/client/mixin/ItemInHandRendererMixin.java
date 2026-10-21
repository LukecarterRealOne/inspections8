package dev.inspectitems.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.inspectitems.client.InspectConfig;
import dev.inspectitems.client.InspectController;
import dev.inspectitems.client.anim.Frame;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Shadow
	public abstract void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext context,
									PoseStack poseStack, SubmitNodeCollector collector, int light);

	@Shadow
	protected abstract void renderPlayerArm(PoseStack poseStack, SubmitNodeCollector collector, int light,
											float equippedProgress, float swingProgress, HumanoidArm arm);

	private static boolean inspectitems$hideHands() {
		return InspectConfig.get().hideHandsOutsideInspect && !InspectController.isActive();
	}

	/** Empty-hand arm: only visible while inspecting. */
	@Inject(method = "renderPlayerArm", at = @At("HEAD"), cancellable = true)
	private void inspectitems$hidePlayerArm(PoseStack poseStack, SubmitNodeCollector collector, int light,
											float equippedProgress, float swingProgress, HumanoidArm arm,
											CallbackInfo ci) {
		if (inspectitems$hideHands()) {
			ci.cancel();
		}
	}

	/** Arm drawn under a held map: same rule. */
	@Inject(method = "renderMapHand", at = @At("HEAD"), cancellable = true)
	private void inspectitems$hideMapHand(PoseStack poseStack, SubmitNodeCollector collector, int light,
										  HumanoidArm arm, CallbackInfo ci) {
		if (inspectitems$hideHands()) {
			ci.cancel();
		}
	}

	/** While inspecting, draw the main-hand item and the hand ourselves and hide the off hand. */
	@Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
	private void inspectitems$renderInspect(AbstractClientPlayer player, float partialTick, float pitch,
											InteractionHand hand, float swingProgress, ItemStack stack,
											float equipProgress, PoseStack poseStack,
											SubmitNodeCollector collector, int light, CallbackInfo ci) {
		if (!InspectController.isActive()) {
			return;
		}
		ci.cancel();
		if (hand == InteractionHand.OFF_HAND || stack.isEmpty()) {
			return;
		}

		HumanoidArm arm = player.getMainArm();
		float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
		ItemDisplayContext context = arm == HumanoidArm.RIGHT
			? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
			: ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
		Frame f = InspectController.frame(partialTick);
		InspectConfig cfg = InspectConfig.get();

		// Item: vanilla resting spot, then the animation frame rotating about the grip.
		poseStack.pushPose();
		poseStack.translate(side * 0.56F, -0.52F, -0.72F);
		poseStack.translate(-side * f.inward(), f.up(), f.depth());
		poseStack.translate(side * cfg.pivotX, cfg.pivotY, 0.0F);
		poseStack.mulPose(Axis.ZP.rotationDegrees(f.roll() * side));
		poseStack.mulPose(Axis.YP.rotationDegrees(f.yaw() * side));
		poseStack.mulPose(Axis.XP.rotationDegrees(f.pitch()));
		if (cfg.squashStretch) {
			poseStack.scale(f.scale(), f.scale(), f.scale());
		}
		poseStack.translate(-side * cfg.pivotX, -cfg.pivotY, 0.0F);
		this.renderItem(player, stack, context, poseStack, collector, light);
		poseStack.popPose();

		// Hand: vanilla arm pose, shifted onto the grip and following the item's movement.
		poseStack.pushPose();
		poseStack.translate(
			side * cfg.handOffsetX - side * f.inward() * cfg.handFollow,
			cfg.handOffsetY + f.up() * cfg.handFollow,
			cfg.handOffsetZ + f.depth() * cfg.handFollow);
		this.renderPlayerArm(poseStack, collector, light, 0.0F, 0.0F, arm);
		poseStack.popPose();
	}
}
