package com.alexandre.bedwars.utils;

import com.alexandre.bedwars.players.BedwarsPlayer;
import com.alexandre.bedwars.players.BedwarsPlayersRegistry;
import com.alexandre.bedwars.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Reset {

	public static void resetPlayers() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			resetPlayer(player);
		}
	}
	
	public static void resetPlayer(Player player) {
		BedwarsPlayer bedwarsPlayer = BedwarsPlayersRegistry.getBedwarsPlayer(player);

		if (Main.getParty().isStart()) player.setGameMode(GameMode.SURVIVAL);
		else player.setGameMode(GameMode.ADVENTURE);
		if (bedwarsPlayer == null) player.getInventory().clear();
		player.setMaxHealth(20);
		if (player.isDead() || player.getHealth() <= 0.0D) player.spigot().respawn();
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
		player.setExp(0);
		player.setLevel(0);
		player.setDisplayName(player.getName());
		player.setPlayerListName(player.getName());
//		player.setWalkSpeed(0.2f);

		if (bedwarsPlayer == null) return;

		bedwarsPlayer.removeGui();
		bedwarsPlayer.open(null);
		player.getInventory().clear();

		if (bedwarsPlayer.getTeam() != null) {
			String c = bedwarsPlayer.getTeam().getColor().getTextColor();
			bedwarsPlayer.getPlayer().setDisplayName(c + "[§l" + bedwarsPlayer.getTeam().getColor().name() + "" + c + "] §r" + bedwarsPlayer.getPlayer().getName());
			bedwarsPlayer.getPlayer().setPlayerListName(c + "§l" + bedwarsPlayer.getTeam().getColor().name().charAt(0) + " " + c + bedwarsPlayer.getPlayer().getName());
		}
	}
	
}
