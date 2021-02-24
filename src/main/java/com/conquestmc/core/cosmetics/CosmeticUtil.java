package com.conquestmc.core.cosmetics;

import com.conquestmc.core.cosmetics.profile.CosmeticProfile;
import com.conquestmc.foundation.CorePlayer;
import com.google.common.collect.Maps;

import java.util.Map;

public class CosmeticUtil {

	private static Map<String, Cosmetic> cosmetics = Maps.newHashMap();

	public static Cosmetic getActiveCosmetic(CorePlayer player) {
		return cosmetics.get(player.getAllProfiles().stream().filter(p -> p instanceof CosmeticProfile).findFirst().get().getString("activeCosmetic"));
	}

	public static void registerCosmetic(Cosmetic cosmetic) {
		cosmetics.put(cosmetic.getName(), cosmetic);
	}
}
