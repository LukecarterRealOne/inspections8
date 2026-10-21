package dev.inspectitems.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class InspectItemsClient implements ClientModInitializer {
	public static final String MOD_ID = "inspectitems";
	public static KeyMapping inspectKey;

	@Override
	public void onInitializeClient() {
		InspectConfig.load();

		KeyMapping.Category category = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(MOD_ID, "main"));
		inspectKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.inspectitems.inspect",
			InputConstants.Type.KEYSYM,
			InputConstants.KEY_G,
			category));

		ClientTickEvents.END_CLIENT_TICK.register(InspectController::onClientTick);
	}
}
