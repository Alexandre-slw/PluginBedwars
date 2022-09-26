package com.alexandre.bedwars.players;

import com.alexandre.core.players.GamePlayersRegistry;
import org.bukkit.entity.Player;

public class BedwarsPlayersRegistry {

    public static BedwarsPlayer getBedwarsPlayer(Player player) {
        return (BedwarsPlayer) GamePlayersRegistry.getPlayer(player);
    }

}
