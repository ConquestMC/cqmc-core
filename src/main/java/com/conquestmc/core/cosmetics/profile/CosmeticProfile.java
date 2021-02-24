package com.conquestmc.core.cosmetics.profile;

import com.conquestmc.foundation.player.FProfile;
import com.google.common.collect.Maps;

import java.util.Map;

public class CosmeticProfile extends FProfile {

	public CosmeticProfile() {
		super("cosmetics", Maps.newHashMap());
	}
}
