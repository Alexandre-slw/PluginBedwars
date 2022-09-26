package com.alexandre.bedwars.players;

import com.alexandre.bedwars.utils.TimeMessage;
import com.alexandre.core.TimerPlugin;
import com.alexandre.core.api.inventory.ItemBuilder;
import com.alexandre.core.players.GamePlayer;
import com.alexandre.core.utils.Title;
import com.alexandre.bedwars.Main;
import com.alexandre.bedwars.gui.GuiShop;
import com.alexandre.bedwars.players.team.BedwarsTeam;
import com.alexandre.bedwars.utils.EntityUtils;
import com.alexandre.bedwars.utils.Reset;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Locale;

public class BedwarsPlayer extends GamePlayer {

    private BedwarsTeam team = null;

    private final ItemBuilder helmet = new ItemBuilder(Material.LEATHER_HELMET);
    private final ItemBuilder chestPlate = new ItemBuilder(Material.LEATHER_CHESTPLATE);
    private final ItemBuilder leggings = new ItemBuilder(Material.LEATHER_LEGGINGS);
    private final ItemBuilder boots = new ItemBuilder(Material.LEATHER_BOOTS);

    private final ItemBuilder sword = new ItemBuilder(Material.WOOD_SWORD);

    private ItemBuilder shears = null;

    private long respawnTime = 0;
    private long invisiblyTime = 0;

    private int lastNumberSent = 0;

    private boolean invisible = false;

    private int pickaxeLevel = 0;
    private int axeLevel = 0;

    private boolean alive;

    public BedwarsPlayer(GamePlayer player) {
        super(player);
        if (this.getTeam() != null) this.alive = this.getTeam().isBedSafe() && !this.getTeam().isEliminated();
        else this.alive = true;
    }

    public void update() {
        if (this.invisible && !this.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            this.setInvisible(false);
        } else if (!this.invisible && this.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            this.setInvisible(true);
        }

        this.sendScoreboard();

        if (this.respawnTime > System.currentTimeMillis()) {
            int second = Math.round((this.respawnTime - System.currentTimeMillis()) / 1000.0F);
            if (second != this.lastNumberSent) {
                this.lastNumberSent = second;
                Title.sendTitle(this.getPlayer(), second > 0 ? "§cYou died!" : "", second > 0 ? "Respawn in " + second : "Welcome back!", 0, 30, 5);
            }
        } else if (this.respawnTime != 0) {
            this.respawnTime = 0;
            this.downgradeTools();
            this.respawn();
        }
    }

    public void waitRespawn() {
        TimerPlugin.getTimerApi().syncTimers();

        if (this.team != null && this.team.isBedSafe()) {
            this.lastNumberSent = 0;
            this.respawnTime = System.currentTimeMillis() + 5_500L;
        } else if (this.team != null) {
            this.alive = false;
            Title.sendTitle(this.getPlayer(), "§cGame over!", "", 0, 60, 10);
        }
        Reset.resetPlayer(this.getPlayer());
        this.getPlayer().setGameMode(GameMode.SPECTATOR);
        this.getPlayer().teleport(new Location(this.getPlayer().getWorld(), Main.getParty().getSpawnX(), Main.getParty().getSpawnY(), Main.getParty().getSpawnZ()));
    }

    public void respawn() {
        if (this.getPlayer().isDead() || this.getPlayer().getHealth() <= 0.0D) this.getPlayer().spigot().respawn();
        Reset.resetPlayer(this.getPlayer());

        this.getPlayer().getInventory().clear();

        this.applyArmors();

        this.getPlayer().getInventory().addItem(this.sword.unbreakable().flags(ItemFlag.HIDE_UNBREAKABLE).build());
        if (this.pickaxeLevel > 0) this.getPlayer().getInventory().addItem(GuiShop.getItemBuilder(GuiShop.getPICKAXES().get(Math.min(Math.max(this.pickaxeLevel - 1, 0), GuiShop.getPICKAXES().size() - 1)), this).unbreakable().flags(ItemFlag.HIDE_UNBREAKABLE).build());
        if (this.axeLevel > 0) this.getPlayer().getInventory().addItem(GuiShop.getItemBuilder(GuiShop.getAXES().get(Math.min(Math.max(this.axeLevel - 1, 0), GuiShop.getAXES().size() - 1)), this).unbreakable().flags(ItemFlag.HIDE_UNBREAKABLE).build());
        if (this.shears != null) this.getPlayer().getInventory().addItem(this.shears.unbreakable().flags(ItemFlag.HIDE_UNBREAKABLE).build());

        this.setInvisiblyTime(System.currentTimeMillis() + 2_000L);
        this.getPlayer().teleport(this.getTeam().getSpawnPoint());
    }

    public void applyArmors() {
        this.getPlayer().getInventory().setHelmet(this.helmet.unbreakable().flags(ItemFlag.HIDE_UNBREAKABLE).armorColor(this.getTeam().getColor().getColor()).build());
        this.getPlayer().getInventory().setChestplate(this.chestPlate.unbreakable().flags(ItemFlag.HIDE_UNBREAKABLE).armorColor(this.getTeam().getColor().getColor()).build());
        this.getPlayer().getInventory().setLeggings(this.leggings.unbreakable().flags(ItemFlag.HIDE_UNBREAKABLE).armorColor(this.getTeam().getColor().getColor()).build());
        this.getPlayer().getInventory().setBoots(this.boots.unbreakable().flags(ItemFlag.HIDE_UNBREAKABLE).armorColor(this.getTeam().getColor().getColor()).build());
    }

    public void downgradeTools() {
        if (this.pickaxeLevel > 1) this.pickaxeLevel--;
        if (this.axeLevel > 1) this.axeLevel--;
    }

    public int countItemInInventory(Material type) {
        int amount = 0;
        for (ItemStack item : this.getPlayer().getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() != type) continue;

            amount += item.getAmount();
        }
        return amount;
    }

    public void removeItem(Material type, int amount) {
        for (ItemStack item : this.getPlayer().getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() != type) continue;

            int itemAmount = item.getAmount();
            int newAmount = Math.max(0, itemAmount - amount);
            if (newAmount >= 1) item.setAmount(newAmount);
            else this.getPlayer().getInventory().removeItem(item);

            amount -= itemAmount - newAmount;
            if (amount <= 0) break;
        }
    }

    public void sendScoreboard() {
        ArrayList<String> lines = new ArrayList<>();

        lines.add("");
        for (TimeMessage message : Main.getParty().getMessages()) {
            if (message.getTime() > Main.getParty().getTicks()) {
                String name = "";
                switch (message.getId()) {
                    case 0:
                        name = "Diamond II";
                        break;

                    case 1:
                        name = "Emerald II";
                        break;

                    case 2:
                        name = "Diamond III";
                        break;

                    case 3:
                        name = "Emerald III";
                        break;

                    case 4:
                        name = "Bed gone";
                        break;

                    case 5:
                        name = "Sudden death";
                        break;

                    case 6:
                        name = "End of the game";
                        break;
                }
                int seconds = (int) Math.ceil((message.getTime() - Main.getParty().getTicks()) / 20.0F);
                lines.add(name + " in §a" + String.format("%s:%02d", seconds / 60, seconds % 60));
                break;
            }
        }

        lines.add("");
        for (BedwarsTeam team : Main.getParty().getTeams()) {
            String icon = "§a✔";
            if (team.isEliminated()) icon = "§c✖";
            else if (!team.isBedSafe()) icon = "§a" + team.getAliveMembers().size();

            String letter = team.getColor().getTextColor() + team.getColor().name().charAt(0);

            lines.add(letter + " §f" +  StringUtils.capitalize(team.getColor().name().toLowerCase(Locale.ROOT)) + ": " + icon + (team == this.getTeam() ? " §7YOU" : ""));
        }

        lines.add("");
        lines.add("§eplay.example.com");

        this.setScoreboard("§e§lBED WARS", lines);
    }

    public void removeAllItemEndsWith(String end) {
        for (ItemStack item : this.getPlayer().getInventory().getContents()) {
            if (item == null) continue;
            if (!item.getType().name().toLowerCase(Locale.ROOT).endsWith(end)) continue;

            this.getPlayer().getInventory().removeItem(item);
        }
    }

    public boolean isInvisible() {
        return this.invisiblyTime > System.currentTimeMillis();
    }

    public long getInvisiblyTime() {
        return this.invisiblyTime;
    }

    public void setInvisiblyTime(long invisiblyTime) {
        this.invisiblyTime = invisiblyTime;
    }

    public void setTeam(BedwarsTeam team) {
        this.team = team;
    }

    public BedwarsTeam getTeam() {
        return this.team;
    }

    public ItemBuilder getHelmet() {
        return this.helmet;
    }

    public ItemBuilder getChestPlate() {
        return this.chestPlate;
    }

    public ItemBuilder getLeggings() {
        return this.leggings;
    }

    public ItemBuilder getBoots() {
        return this.boots;
    }

    public int getPickaxeLevel() {
        return this.pickaxeLevel;
    }

    public void setPickaxeLevel(int pickaxeLevel) {
        this.pickaxeLevel = pickaxeLevel;
    }

    public int getAxeLevel() {
        return this.axeLevel;
    }

    public void setAxeLevel(int axeLevel) {
        this.axeLevel = axeLevel;
    }

    public ItemBuilder getShears() {
        return this.shears;
    }

    public void setShears(ItemBuilder shears) {
        this.shears = shears;
    }

    public ItemBuilder getSword() {
        return this.sword;
    }

    public void setInvisible(boolean invisible) {
        if (this.invisible == invisible) return;

        this.invisible = invisible;

        if (this.invisible) {
            EntityUtils.hideArmor(this);
        } else {
            EntityUtils.showArmor(this);
        }
    }

    public boolean isAlive() {
        return this.alive && this.getPlayer().isOnline();
    }
}
