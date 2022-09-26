package com.alexandre.bedwars.elements;

import com.alexandre.bedwars.utils.SimpleAnimationDouble;
import com.alexandre.bedwars.utils.NumberUtils;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpecialOreGenerator {

    private final long spawnTime;
    private long lastSpawn = 0L;
    private final Location location;
    private final Material material;
    private final ArmorStand armorStandItem;
    private final ArmorStand armorStandTime;
    private final ArmorStand armorStandName;
    private final ArmorStand armorStandCategory;

    private double modifier = 1.0F;

    private int tick = 0;
    private double lastY;
    private int tier = 1;

    private final SimpleAnimationDouble animation = new SimpleAnimationDouble(3500L, 0.0F, 1.0F, SimpleAnimationDouble.Type.CUBIC_EASE_IN_OUT);

    public SpecialOreGenerator(long spawnTime, Location location, String name, Material material, Material helmet) {
        this.spawnTime = spawnTime;
        this.location = location;
        this.material = material;

        this.armorStandItem = location.getWorld().spawn(new Location(this.location.getWorld(), this.location.getX(), this.location.getY() + 0.5F + 1.0F, this.location.getZ()), ArmorStand.class);
        this.armorStandItem.setGravity(false);
        this.armorStandItem.setVisible(false);
        this.armorStandItem.setHelmet(new ItemStack(helmet));
        this.armorStandItem.setSmall(true);

        this.armorStandTime = location.getWorld().spawn(new Location(this.location.getWorld(), this.location.getX(), this.location.getY() + 0.5F + 1.0F, this.location.getZ()), ArmorStand.class);
        this.armorStandTime.setGravity(false);
        this.armorStandTime.setVisible(false);
        this.armorStandTime.setSmall(true);

        this.armorStandName = location.getWorld().spawn(new Location(this.location.getWorld(), this.location.getX(), this.location.getY() + 0.5F + 1.4F, this.location.getZ()), ArmorStand.class);
        this.armorStandName.setGravity(false);
        this.armorStandName.setVisible(false);
        this.armorStandName.setCustomName(name);
        this.armorStandName.setCustomNameVisible(true);
        this.armorStandName.setSmall(true);

        this.armorStandCategory = location.getWorld().spawn(new Location(this.location.getWorld(), this.location.getX(), this.location.getY() + 0.5F + 1.8F, this.location.getZ()), ArmorStand.class);
        this.armorStandCategory.setGravity(false);
        this.armorStandCategory.setVisible(false);
        this.armorStandCategory.setCustomName("§eTier §cI");
        this.armorStandCategory.setCustomNameVisible(true);
        this.armorStandCategory.setSmall(true);

        this.lastY = this.armorStandItem.getLocation().getY();
    }

    public void clean() {
        if (this.armorStandItem != null) this.armorStandItem.remove();
        if (this.armorStandTime != null) this.armorStandTime.remove();
        if (this.armorStandName != null) this.armorStandName.remove();
        if (this.armorStandCategory != null) this.armorStandCategory.remove();
    }

    public void update() {
        if (this.armorStandItem != null) {
            if (this.animation.isFinished()) this.animation.reverse();
            this.tick++;
            double newY = this.location.getY() + 0.5F + 0.7 + 0.3 * this.animation.getValue();
            if (this.tick >= 20) {
                Location location = this.armorStandItem.getLocation();
                float newYaw = (float) this.animation.getValue() * 480.0F;
                PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(
                        this.armorStandItem.getEntityId(),
                        MathHelper.floor(location.getX() * 32.0),
                        MathHelper.floor(newY * 32.0),
                        MathHelper.floor(location.getZ() * 32.0),
                        (byte) ((int)(newYaw * 256.0F / 360.0F)),
                        (byte) 0,
                        false);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }
                this.tick = 0;
                this.lastY = newY;
            } else {
                float newYaw = (float) this.animation.getValue() * 480.0F;

                PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                        this.armorStandItem.getEntityId(),
                        (byte) 0,
                        (byte) (newY * 32.0 - this.lastY * 32.0),
                        (byte) 0,
                        (byte) ((int)(newYaw * 256.0F / 360.0F)),
                        (byte) 0,
                        true);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }
                if ((byte) (newY * 32.0 - this.lastY * 32.0) != 0) this.lastY = newY;
            }
        }

        if (this.armorStandTime != null) {
            this.armorStandTime.setCustomName("§eSpawn in §c" + Math.round((this.lastSpawn + this.spawnTime * this.modifier - System.currentTimeMillis()) / 1000.0F) + " §eseconds");
            this.armorStandTime.setCustomNameVisible(true);
        }

        this.generate();
    }

    public void resetTime() {
        this.lastSpawn = System.currentTimeMillis();
    }

    public void generate() {
        if (this.lastSpawn + this.spawnTime * this.modifier > System.currentTimeMillis()) return;
        this.lastSpawn = System.currentTimeMillis();

        int amount = 0;
        for (Entity entity : this.getLocation().getWorld().getNearbyEntities(this.getLocation(), 1, 1, 1)) {
            if (!(entity instanceof Item)) continue;
            Item item = (Item) entity;
            if (item.getItemStack().getType() != this.getMaterial()) continue;

            amount += item.getItemStack().getAmount();
        }

        if (amount >= 8) return;

        Item item = this.location.getWorld().dropItem(this.location, new ItemStack(this.material));
        item.setVelocity(item.getVelocity().setX(0).setZ(0));
    }

    public void upgrade() {
        this.tier++;
        if (this.armorStandCategory != null) {
            this.armorStandCategory.setCustomName("§eTier §c"+ NumberUtils.toRomainNumber(this.tier));
        }
        this.modifier *= 0.75;
    }

    public Location getLocation() {
        return this.location;
    }

    public Material getMaterial() {
        return this.material;
    }
}
