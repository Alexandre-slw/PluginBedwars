package com.alexandre.bedwars.elements;

import com.alexandre.bedwars.players.BedwarsPlayer;
import com.alexandre.bedwars.players.team.BedwarsTeam;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class OreGenerator {

    private final long ironSpawnTime;
    private long lastSpawnIron = 0L;

    private final long goldSpawnTime;
    private long lastSpawnGold = 0L;

    private double modifier = 1.0F;

    public OreGenerator(long ironSpawnTime, long goldSpawnTime) {
        this.ironSpawnTime = ironSpawnTime;
        this.goldSpawnTime = goldSpawnTime;
    }

    public void generate(BedwarsTeam team) {
        if (team.isEliminated() || team.getMembers().size() <= 0) return;

        this.generateIron(team);
        this.generateGold(team);
    }

    private void generateIron(BedwarsTeam team) {
        if (this.lastSpawnIron + this.ironSpawnTime * this.modifier > System.currentTimeMillis()) return;
        this.lastSpawnIron = System.currentTimeMillis();

        BedwarsPlayer player = team.getMembers().get(0);

        int amount = 0;
        for (Entity entity : team.getGeneratorPoint().getWorld().getNearbyEntities(team.getGeneratorPoint(), 1, 1, 1)) {
            if (!(entity instanceof Item)) continue;
            Item item = (Item) entity;
            if (item.getItemStack().getType() != Material.IRON_INGOT) continue;

            amount += item.getItemStack().getAmount();
        }

        if (amount >= 128) return;

        Item item = player.getPlayer().getWorld().dropItem(team.getGeneratorPoint(), new ItemStack(Material.IRON_INGOT));
        item.setVelocity(item.getVelocity().setX(0).setZ(0));
    }

    private void generateGold(BedwarsTeam team) {
        if (this.lastSpawnGold + this.goldSpawnTime * this.modifier > System.currentTimeMillis()) return;
        this.lastSpawnGold = System.currentTimeMillis();

        BedwarsPlayer player = team.getMembers().get(0);

        int amount = 0;
        for (Entity entity : team.getGeneratorPoint().getWorld().getNearbyEntities(team.getGeneratorPoint(), 1, 1, 1)) {
            if (!(entity instanceof Item)) continue;
            Item item = (Item) entity;
            if (item.getItemStack().getType() != Material.GOLD_INGOT) continue;

            amount += item.getItemStack().getAmount();
        }

        if (amount >= 25) return;

        Item item = player.getPlayer().getWorld().dropItem(team.getGeneratorPoint(), new ItemStack(Material.GOLD_INGOT));
        item.setVelocity(item.getVelocity().setX(0).setZ(0));
    }

    public void resetTime() {
        this.lastSpawnIron = System.currentTimeMillis();
        this.lastSpawnGold = System.currentTimeMillis();
    }

}
