package com.conquestmc.core.cosmetics;

import com.google.common.collect.Lists;
import net.md_5.bungee.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;
import java.util.UUID;

public class SupermanJump extends Gadget {

	private List<UUID> players = Lists.newArrayList();

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void play(Player player) {

	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {
		if (players.contains(event.getPlayer().getUniqueId())) {
			return;
		}
	}
}
