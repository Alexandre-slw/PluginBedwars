package com.alexandre.bedwars.event;

import com.alexandre.core.players.GamePlayer;
import com.alexandre.core.players.GamePlayersRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InventoryEvent implements Listener {

	@EventHandler
	public void onInventoryClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getPlayer().getInventory().getItemInHand() == null) return;
		if (event.getPlayer().getInventory().getItemInHand().getItemMeta() == null) return;

		GamePlayer player = GamePlayersRegistry.getPlayer(event.getPlayer());
		if (player == null) return;

		String name = event.getPlayer().getInventory().getItemInHand().getItemMeta().getDisplayName();
		this.action(name, player);
	}
	
	public void action(String name, GamePlayer player) {
		if (name == null) return;


	}
	
}
