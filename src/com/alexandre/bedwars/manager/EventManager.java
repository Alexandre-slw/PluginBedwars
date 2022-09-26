package com.alexandre.bedwars.manager;

import com.alexandre.bedwars.Main;
import com.alexandre.bedwars.event.EventCancel;
import com.alexandre.bedwars.event.EventJoin;
import com.alexandre.bedwars.event.GameEvents;
import com.alexandre.bedwars.event.InventoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class EventManager {

    public void registers() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new EventCancel(), Main.getInstance());
        pm.registerEvents(new EventJoin(), Main.getInstance());
        pm.registerEvents(new InventoryEvent(), Main.getInstance());
        pm.registerEvents(new GameEvents(), Main.getInstance());
    }

}
