package com.alexandre.bedwars;

import com.alexandre.bedwars.party.Party;
import com.alexandre.bedwars.players.BedwarsPlayer;
import com.alexandre.bedwars.players.BedwarsPlayerModifier;
import com.alexandre.bedwars.players.BedwarsPlayersRegistry;
import com.alexandre.bedwars.utils.BlockOverride;
import com.alexandre.bedwars.utils.Reset;
import com.alexandre.core.players.GamePlayersRegistry;
import com.alexandre.bedwars.manager.EventManager;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    private static int maxTeamMembers = 1;
    private static int minPlayers = 1;

    private static Party party;

    @Override
    public void onEnable() {
        Main.instance = this;

        for (Block block : Block.REGISTRY) {
            if (block == Blocks.END_STONE) continue;
            if (block == Blocks.WOOL) continue;
            if (block == Blocks.HARDENED_CLAY) continue;
            if (block == Blocks.PLANKS) continue;
            if (block == Blocks.OBSIDIAN) continue;
            if (block == Blocks.LADDER) continue;
            if (block == Blocks.TNT) continue;
            if (block == Blocks.BED) continue;
            new BlockOverride(block).set("durability", 2000.0F * 3);
        }

        GamePlayersRegistry.setModifier(new BedwarsPlayerModifier());

        this.saveDefaultConfig();
        Main.maxTeamMembers = this.getConfig().getInt("team_members", 1);
        Main.minPlayers = this.getConfig().getInt("min_players", 4);

        new EventManager().registers();

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.onJoin(player);
        }

        Main.party = new Party();
    }

    @Override
    public void onDisable() {
        if (Main.getParty() != null) Main.getParty().endParty();
        for (Block block : Block.REGISTRY) {
            new BlockOverride(block).revertAll();
        }
    }

    public void onJoin(Player player) {
        player.getWorld().setAutoSave(false);
        player.getWorld().setDifficulty(Difficulty.EASY);
        player.teleport(new Location(player.getWorld(), Main.getParty().getSpawnX() + 0.5F, Main.getParty().getSpawnY() + 1, Main.getParty().getSpawnZ() + 0.5F));
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());

        Reset.resetPlayer(player);

        if (Main.getParty().isStart()) {
            BedwarsPlayer bedwarsPlayer = BedwarsPlayersRegistry.getBedwarsPlayer(player);
            if (bedwarsPlayer == null) return;
            bedwarsPlayer.waitRespawn();
        }
    }

    public static Main getInstance() {
        return Main.instance;
    }

    public static Party getParty() {
        return Main.party;
    }

    public static int getMaxTeamMembers() {
        return Main.maxTeamMembers;
    }

    public static int getMinPlayers() {
        return Main.minPlayers;
    }
}
