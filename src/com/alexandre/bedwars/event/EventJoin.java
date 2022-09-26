package com.alexandre.bedwars.event;

import com.alexandre.bedwars.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class EventJoin implements Listener {
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		Main.getInstance().onJoin(event.getPlayer());
    }

}
