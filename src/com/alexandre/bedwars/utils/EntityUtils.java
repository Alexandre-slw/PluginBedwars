package com.alexandre.bedwars.utils;

import com.alexandre.bedwars.players.BedwarsPlayer;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityUtils {

    public static void setNoAI(Entity entity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) tag = new NBTTagCompound();
        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        nmsEntity.f(tag);
    }

    public static void hideArmor(BedwarsPlayer player) {
        PacketPlayOutEntityEquipment helmet = new PacketPlayOutEntityEquipment(player.getPlayer().getEntityId(), 4, new ItemStack((Item) null));
        PacketPlayOutEntityEquipment chestplate = new PacketPlayOutEntityEquipment(player.getPlayer().getEntityId(), 3, new ItemStack((Item) null));
        PacketPlayOutEntityEquipment leggings = new PacketPlayOutEntityEquipment(player.getPlayer().getEntityId(), 2, new ItemStack((Item) null));
        PacketPlayOutEntityEquipment boots = new PacketPlayOutEntityEquipment(player.getPlayer().getEntityId(), 1, new ItemStack((Item) null));
        for (Player p : player.getPlayer().getWorld().getPlayers()) {
            if (p == player.getPlayer()) continue;
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(helmet);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(chestplate);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(leggings);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(boots);
        }
    }

    public static void showArmor(BedwarsPlayer player) {
        player.applyArmors();
        PacketPlayOutEntityEquipment helmet = new PacketPlayOutEntityEquipment(player.getPlayer().getEntityId(), 4, CraftItemStack.asNMSCopy(player.getPlayer().getInventory().getHelmet()));
        PacketPlayOutEntityEquipment chestplate = new PacketPlayOutEntityEquipment(player.getPlayer().getEntityId(), 3, CraftItemStack.asNMSCopy(player.getPlayer().getInventory().getChestplate()));
        PacketPlayOutEntityEquipment leggings = new PacketPlayOutEntityEquipment(player.getPlayer().getEntityId(), 2, CraftItemStack.asNMSCopy(player.getPlayer().getInventory().getLeggings()));
        PacketPlayOutEntityEquipment boots = new PacketPlayOutEntityEquipment(player.getPlayer().getEntityId(), 1, CraftItemStack.asNMSCopy(player.getPlayer().getInventory().getBoots()));
        for (Player p : player.getPlayer().getWorld().getPlayers()) {
            if (p == player.getPlayer()) continue;
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(helmet);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(chestplate);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(leggings);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(boots);
        }
    }
}
