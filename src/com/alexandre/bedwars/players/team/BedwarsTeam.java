package com.alexandre.bedwars.players.team;

import com.alexandre.bedwars.Main;
import com.alexandre.bedwars.elements.OreGenerator;
import com.alexandre.bedwars.players.BedwarsPlayer;
import com.alexandre.bedwars.utils.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class BedwarsTeam {

    private final CopyOnWriteArrayList<BedwarsPlayer> members = new CopyOnWriteArrayList<>();
    private final TeamColor color;

    private final int maxMembers;

    private final Location spawnPoint;
    private final Location generatorPoint;
    private final Location shopPoint;
    private final Location upgradePoint;

    private final OreGenerator generator;

    private boolean bedSafe = true;
    private boolean eliminated = false;

    private Villager shopVillager = null;
    private Blaze upgradeBlaze = null;
    private ArmorStand armorStandVillager = null;
    private ArmorStand armorStandBlaze = null;

    private int safeMinX = Integer.MAX_VALUE;
    private int safeMinY = Integer.MAX_VALUE;
    private int safeMinZ = Integer.MAX_VALUE;
    private int safeMaxX = Integer.MIN_VALUE;
    private int safeMaxY = Integer.MIN_VALUE;
    private int safeMaxZ = Integer.MIN_VALUE;

    public BedwarsTeam(TeamColor color, int maxMembers, FileConfiguration configuration) {
        this.color = color;
        this.maxMembers = maxMembers;

        int x = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".spawn.x", 0);
        int y = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".spawn.y", 130);
        int z = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".spawn.z", 0);
        int yaw = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".spawn.yaw", 0);
        this.spawnPoint = new Location(Bukkit.getWorlds().get(0), x + 0.5F, y, z + 0.5F, Math.round(yaw / 45.0F) * 45, 0);

        x = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".generator.x", x);
        y = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".generator.y", y);
        z = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".generator.z", z);
        this.generatorPoint = new Location(Bukkit.getWorlds().get(0), x + 0.5F, y, z + 0.5F);

        x = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".shop.x", x);
        y = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".shop.y", y);
        z = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".shop.z", z);
        yaw = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".shop.yaw", yaw);
        this.shopPoint = new Location(Bukkit.getWorlds().get(0), x + 0.5F, y, z + 0.5F, Math.round(yaw / 45.0F) * 45, 0);

        x = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".upgrade.x", x);
        y = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".upgrade.y", y);
        z = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".upgrade.z", z);
        yaw = configuration.getInt(this.color.name().toLowerCase(Locale.ROOT) + ".upgrade.yaw", yaw);
        this.upgradePoint = new Location(Bukkit.getWorlds().get(0), x + 0.5F, y, z + 0.5F, Math.round(yaw / 45.0F) * 45, 0);

        this.generator = new OreGenerator(configuration.getLong("generator.iron_spawn_time", 2_000L), configuration.getLong("generator.gold_spawn_time", 8_000L));

        this.setSafeZone(this.spawnPoint);
        this.setSafeZone(this.generatorPoint);
        this.setSafeZone(this.shopPoint);
        this.setSafeZone(this.upgradePoint);
        this.setSafeZone(this.getChest().getLocation());
    }

    public void init() {
        this.shopVillager = Bukkit.getWorlds().get(0).spawn(this.getShopPoint(), Villager.class);
        EntityUtils.setNoAI(this.shopVillager);
        this.shopVillager.setAdult();
        this.shopVillager.setProfession(Villager.Profession.PRIEST);
        this.shopVillager.setCanPickupItems(false);
        this.shopVillager.setMaxHealth(Double.MAX_VALUE);
        this.shopVillager.setHealth(this.shopVillager.getMaxHealth());

        this.upgradeBlaze = Bukkit.getWorlds().get(0).spawn(this.getUpgradePoint(), Blaze.class);
        EntityUtils.setNoAI(this.upgradeBlaze);
        this.upgradeBlaze.setMaxHealth(Double.MAX_VALUE);
        this.upgradeBlaze.setHealth(this.shopVillager.getMaxHealth());

        this.armorStandVillager = this.shopVillager.getWorld().spawn(new Location(this.shopVillager.getWorld(), this.shopVillager.getLocation().getX(), this.shopVillager.getLocation().getY() + 0.75F, this.shopVillager.getLocation().getZ()), ArmorStand.class);
        this.armorStandVillager.setGravity(false);
        this.armorStandVillager.setVisible(false);
        this.armorStandVillager.setSmall(true);
        this.armorStandVillager.setCustomName("§a§eITEMS SHOP");
        this.armorStandVillager.setCustomNameVisible(true);

        this.armorStandBlaze = this.upgradeBlaze.getWorld().spawn(new Location(this.upgradeBlaze.getWorld(), this.upgradeBlaze.getLocation().getX(), this.upgradeBlaze.getLocation().getY() + 0.75F, this.upgradeBlaze.getLocation().getZ()), ArmorStand.class);
        this.armorStandBlaze.setGravity(false);
        this.armorStandBlaze.setVisible(false);
        this.armorStandBlaze.setSmall(true);
        this.armorStandBlaze.setCustomName("§a§7UPGRADES - SOON");
        this.armorStandBlaze.setCustomNameVisible(true);

//        this.generator.resetTime();

        this.checkEliminated();
    }

    public Block getChest() {
        double min = Double.MAX_VALUE;
        Block chest = null;
        for (int x = -40; x < 80; x++) {
            for (int y = -40; y < 80; y++) {
                for (int z = -40; z < 80; z++) {
                    Location l = new Location(this.getSpawnPoint().getWorld(), this.getSpawnPoint().getX(), this.getSpawnPoint().getY(), this.getSpawnPoint().getZ()).add(x, y, z);
                    double distance = l.distance(this.getSpawnPoint());
                    if (distance > 40 || distance > min) continue;
                    if (l.getBlock() == null || l.getBlock().getType() != Material.CHEST) continue;
                    min = distance;
                    chest = l.getBlock();
                }
            }
        }
        return chest;
    }

    public void destroyBed() {
        double min = Double.MAX_VALUE;
        Block bed = null;
        for (int x = -40; x < 80; x++) {
            for (int y = -40; y < 80; y++) {
                for (int z = -40; z < 80; z++) {
                    Location l = new Location(this.getSpawnPoint().getWorld(), this.getSpawnPoint().getX(), this.getSpawnPoint().getY(), this.getSpawnPoint().getZ()).add(x, y, z);
                    double distance = l.distance(this.getSpawnPoint());
                    if (distance > 40 || distance > min) continue;
                    if (l.getBlock() == null || l.getBlock().getType() != Material.BED_BLOCK) continue;
                    min = distance;
                    bed = l.getBlock();
                }
            }
        }
        if (bed == null) return;
        Bukkit.getPluginManager().callEvent(new BlockBreakEvent(bed, null));
    }

    public boolean isInSafeZone(Location point) {
        if (point.getBlockX() < this.safeMinX || point.getBlockX() > this.safeMaxX) return false;
        if (point.getBlockY() < this.safeMinY || point.getBlockY() > this.safeMaxY) return false;
        if (point.getBlockZ() < this.safeMinZ || point.getBlockZ() > this.safeMaxZ) return false;
        return true;
    }

    public void setSafeZone(Location point) {
        this.safeMinX = Math.min(this.safeMinX, point.getBlockX() - 1);
        this.safeMinY = Math.min(this.safeMinY, point.getBlockY() - 1);
        this.safeMinZ = Math.min(this.safeMinZ, point.getBlockZ() - 1);
        this.safeMaxX = Math.max(this.safeMaxX, point.getBlockX() + 1);
        this.safeMaxY = Math.max(this.safeMaxY, point.getBlockY() + 4);
        this.safeMaxZ = Math.max(this.safeMaxZ, point.getBlockZ() + 1);
    }

    public void checkEliminated() {
        if (this.eliminated) return;
        boolean eliminated = this.getAliveMembers().size() <= 0;

        if (eliminated && this.isBedSafe()) this.destroyBed();
        this.eliminated = eliminated;
    }

    public void clean() {
        if (this.shopVillager != null) this.shopVillager.remove();
        if (this.upgradeBlaze != null) this.upgradeBlaze.remove();
        if (this.armorStandVillager != null) this.armorStandVillager.remove();
        if (this.armorStandBlaze != null) this.armorStandBlaze.remove();
    }

    public void update() {
        if (!Main.getParty().isStart()) return;
        this.generator.generate(this);

        for (BedwarsPlayer player : this.members) {
            player.update();
        }
    }

    public void addMember(BedwarsPlayer player) {
        this.getMembers().add(player);
        player.setTeam(this);
    }

    public CopyOnWriteArrayList<BedwarsPlayer> getMembers() {
        return this.members;
    }

    public CopyOnWriteArrayList<BedwarsPlayer> getAliveMembers() {
        CopyOnWriteArrayList<BedwarsPlayer> aliveMembers = new CopyOnWriteArrayList<>();
        for (BedwarsPlayer member : this.members) {
            if (!member.isAlive()) continue;
            aliveMembers.add(member);
        }
        return aliveMembers;
    }

    public int getMaxMembers() {
        return this.maxMembers;
    }

    public TeamColor getColor() {
        return this.color;
    }

    public Location getSpawnPoint() {
        return this.spawnPoint;
    }

    public Location getGeneratorPoint() {
        return this.generatorPoint;
    }

    public Location getShopPoint() {
        return this.shopPoint;
    }

    public Location getUpgradePoint() {
        return this.upgradePoint;
    }

    public boolean isBedSafe() {
        return this.bedSafe;
    }

    public void setBedSafe(boolean bedSafe) {
        this.bedSafe = bedSafe;
    }

    public boolean isEliminated() {
        return this.eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
}
