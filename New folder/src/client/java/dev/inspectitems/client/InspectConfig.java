package dev.inspectitems.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Config at config/inspectitems.json. Edit it, then restart the game. Missing keys keep these defaults. */
public final class InspectConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static InspectConfig instance = new InspectConfig();

	/** 1.0 = normal, 2.0 = twice as fast. */
	public float animationSpeed = 1.0f;
	/** Hide first-person hands whenever you are not inspecting. */
	public boolean hideHandsOutsideInspect = true;
	/** Swish and thud sounds on the big moves. */
	public boolean sounds = true;
	/** Freeze for a couple of ticks on big impacts (axe chop, mace slam, spear stab) for weight. */
	public boolean hitStop = true;
	/** Quick size pops on impacts and catches. */
	public boolean squashStretch = true;
	/** Hand placement while inspecting (X is mirrored for left-handed). Tune these if the hand is off the grip. */
	public float handOffsetX = -0.175f;
	public float handOffsetY = -0.04f;
	public float handOffsetZ = 0.42f;
	/** 0 = hand stays put, 1 = hand moves fully with the item. */
	public float handFollow = 0.6f;
	/** Point the item rotates around, relative to its center (X is mirrored). Around the grip feels most natural. */
	public float pivotX = 0.0f;
	public float pivotY = -0.10f;

	public static InspectConfig get() {
		return instance;
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("inspectitems.json");
		try {
			if (Files.exists(path)) {
				InspectConfig loaded = GSON.fromJson(Files.readString(path), InspectConfig.class);
				if (loaded != null) {
					instance = loaded;
				}
			}
			Files.writeString(path, GSON.toJson(instance));
		} catch (IOException | RuntimeException e) {
			System.err.println("[inspectitems] Could not read/write config, using defaults: " + e);
		}
	}
}
