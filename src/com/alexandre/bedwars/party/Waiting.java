package com.alexandre.bedwars.party;

import com.alexandre.core.players.GamePlayer;
import com.alexandre.core.players.GamePlayersRegistry;
import com.alexandre.core.utils.BroadcastUtils;
import com.alexandre.bedwars.Main;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.Locale;

public class Waiting {

	public static long time;
	private boolean step;
	private final int stepValue = 8;
	private final Party party;
	
	private final int task;
	
	private int last = 0;
	
	private float initial_gap = 20_000f;
	
	public Waiting(Party party) {
		this.party = party;
		Waiting.time = System.currentTimeMillis() + 10_000L;
		
		Waiting waiting = this;
		
		this.step = Bukkit.getOnlinePlayers().size() < this.stepValue;
		
		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), waiting::checkTime, 0L, 5L);
	}
	
	private void checkTime() {
		String state = "§fWaiting...";

    	if (Bukkit.getOnlinePlayers().size() < 4) {
    		Waiting.time = System.currentTimeMillis() + 10_000L;
    		this.step = true;
    		BroadcastUtils.actionBar("Waiting more players");
			BroadcastUtils.xp(0, 0f);
    	} else if (Bukkit.getOnlinePlayers().size() < this.stepValue && this.step) {
    		this.step = false;
    		Waiting.time = System.currentTimeMillis() + 30_000L;
    		this.initial_gap = 30_000f;
    	} else if (Bukkit.getOnlinePlayers().size() >= this.stepValue && !this.step) {
    		this.step = true;
    		this.initial_gap = 20_000f;
    		Waiting.time = System.currentTimeMillis() + 20_000L;
    	} else {
    		int startIn = Math.round((Waiting.time - System.currentTimeMillis()) / 1000f);
			state = "§fStart in §a" + startIn + "s";
    		
    		float gap = Waiting.time - System.currentTimeMillis();
    		BroadcastUtils.xp(startIn, gap / this.initial_gap);
    		
    		if (startIn != this.last) {
    			this.last = startIn;
    			
    			if (startIn > 0 && (startIn <= 5 || startIn == 10)) BroadcastUtils.title("%time%".replace("%time%", startIn + ""), "§7The game is starting", 0, 30, 5);
    			else if (startIn == 0) BroadcastUtils.title("", "§aHave fun!", 0, 30, 5);
    			
    			switch (startIn) {
					case 3:
						BroadcastUtils.sound(Sound.NOTE_PLING, 1f, 0.7f);
						BroadcastUtils.sound(Sound.NOTE_PIANO, 2f, 0.7f);
						BroadcastUtils.sound(Sound.NOTE_BASS, 1f, 0.7f);
						break;
						
					case 2:
						BroadcastUtils.sound(Sound.NOTE_PLING, 1f, 0.6f);
						BroadcastUtils.sound(Sound.NOTE_PIANO, 2f, 0.6f);
						BroadcastUtils.sound(Sound.NOTE_BASS, 1f, 0.6f);
						break;
						
					case 1:
						BroadcastUtils.sound(Sound.NOTE_PLING, 1f, 0.4f);
						BroadcastUtils.sound(Sound.NOTE_PIANO, 2f, 0.1f);
						BroadcastUtils.sound(Sound.NOTE_BASS, 1f, 0.3f);
						break;
						
					case 0:
						BroadcastUtils.sound(Sound.NOTE_PLING, 2f, 2f);
						break;
    			}
    		}
    	}

    	String mode = "Team " + Main.getMaxTeamMembers();
    	switch (Main.getMaxTeamMembers()) {
			case 1:
				mode = "Solo";
				break;

			case 2:
				mode = "Duo";
				break;

			case 3:
				mode = "Trio";
				break;

			case 4:
				mode = "Quartet";
				break;

			case 5:
				mode = "Quintet";
				break;
		}

    	for (GamePlayer player : GamePlayersRegistry.getPlayers()) {
    		player.setScoreboard("§e§lBED WARS",
					"",
					"§fMap: §a" + StringUtils.capitalize(Main.getParty().getMapName().toLowerCase(Locale.ROOT)),
					"§fPlayers: §a" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers(),
					"",
					state,
					"",
					"§fMode: §a" + mode,
					"",
					"§eplay.example.com");
		}
    	
    	if (System.currentTimeMillis() >= Waiting.time || this.party.isStop()) {
    		Bukkit.getScheduler().cancelTask(this.task);
    		this.party.start();
    	}
	}
	
}
