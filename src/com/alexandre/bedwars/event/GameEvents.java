package com.alexandre.bedwars.event;

import com.alexandre.core.utils.BroadcastUtils;
import com.alexandre.core.utils.Title;
import com.alexandre.bedwars.Main;
import com.alexandre.bedwars.elements.SpecialOreGenerator;
import com.alexandre.bedwars.gui.GuiShop;
import com.alexandre.bedwars.players.BedwarsPlayer;
import com.alexandre.bedwars.players.BedwarsPlayersRegistry;
import com.alexandre.bedwars.players.team.BedwarsTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.material.Bed;

public class GameEvents implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!Main.getParty().isStart()) return;
        if (event.getTo().getY() > 0) return;
        if (event.getPlayer().getHealth() > 0) event.getPlayer().damage(event.getPlayer().getHealth());
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        BroadcastUtils.chat(event.getPlayer().getDisplayName() + "§r: §7" + event.getMessage());
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!Main.getParty().isStart()) return;
        boolean found = false;
        for (BedwarsTeam team : Main.getParty().getTeams()) {
            if (team.getGeneratorPoint().distance(event.getItem().getLocation()) < 1) {
                found = true;
                break;
            }
        }
        if (!found) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == event.getPlayer()) continue;
            if (player.getLocation().distance(event.getPlayer().getLocation()) > 2) continue;
            player.getInventory().addItem(event.getItem().getItemStack());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (!Main.getParty().isStart()) return;
        if (event.getBlock().getType() == Material.TNT) {
            event.setCancelled(true);
            event.getPlayer().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
            return;
        }

        if (event.getBlock().getLocation().getBlockY() > Main.getParty().getBuildLimit()) {
            event.getPlayer().getPlayer().sendMessage("§eYou can not place blocks here!");
            event.setCancelled(true);
            return;
        }

        BedwarsPlayer player = BedwarsPlayersRegistry.getBedwarsPlayer(event.getPlayer());
        if (player == null) return;

        for (BedwarsTeam team : Main.getParty().getTeams()) {
            if (team.isInSafeZone(event.getBlock().getLocation())) {
                player.getPlayer().sendMessage("§eYou can not place blocks here!");
                event.setCancelled(true);
                break;
            }
        }

        for (SpecialOreGenerator generator : Main.getParty().getGenerators()) {
            if (generator.getLocation().distance(event.getBlock().getLocation()) < 3) {
                player.getPlayer().sendMessage("§eYou can not place blocks here!");
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!Main.getParty().isStart()) return;
        event.setDroppedExp(0);
        event.getDrops().clear();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            BedwarsPlayer player = BedwarsPlayersRegistry.getBedwarsPlayer(event.getEntity());
            if (player == null) return;
            player.waitRespawn();
        }, 1L);

        BedwarsPlayer player = BedwarsPlayersRegistry.getBedwarsPlayer(event.getEntity());
        if (player == null) return;
        if (player.getTeam() != null) {
            event.setDeathMessage(event.getDeathMessage().replace(event.getEntity().getName(), player.getTeam().getColor().getTextColor() + event.getEntity().getName() + "§r"));
            if (!player.getTeam().isBedSafe()) event.setDeathMessage("§f§lFINAL KILL > §r" + event.getDeathMessage());
        }

        if (event.getEntity().getKiller() == null) return;

        BedwarsPlayer killer = BedwarsPlayersRegistry.getBedwarsPlayer(event.getEntity().getKiller());

        if (killer == null) return;
        if (player.getTeam() == null || killer.getTeam() == null) return;

        if (player.getTeam().isBedSafe()) event.setDeathMessage(player.getTeam().getColor().getTextColor() + player.getPlayer().getName() + "§r killed by " + killer.getTeam().getColor().getTextColor() + killer.getPlayer().getName());
        else event.setDeathMessage("§f§lFINAL KILL > §r" + player.getTeam().getColor().getTextColor() + player.getPlayer().getName() + "§r killed by " + killer.getTeam().getColor().getTextColor() + killer.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (!Main.getParty().isStart()) return;
        if (event.getItem().getType() != Material.POTION) return;
        if (event.getItem().getData().getData() != 14) return;

        BedwarsPlayer player = BedwarsPlayersRegistry.getBedwarsPlayer(event.getPlayer());
        if (player == null) return;

        player.setInvisible(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!Main.getParty().isStart()) return;
        if (event.getBlock().getType() != Material.BED_BLOCK) return;
        event.setCancelled(true);

        BedwarsPlayer player = BedwarsPlayersRegistry.getBedwarsPlayer(event.getPlayer());

        double min = Double.MAX_VALUE;
        BedwarsTeam bedwarsTeam = null;
        for (BedwarsTeam team : Main.getParty().getTeams()) {
            double distance = event.getBlock().getLocation().distance(team.getSpawnPoint());
            if (distance > min || distance > 40) continue;
            min = distance;
            bedwarsTeam = team;
        }
        if (bedwarsTeam == null || bedwarsTeam.isEliminated()) return;

        if (player != null && player.getTeam() == bedwarsTeam) {
            player.getPlayer().sendMessage("§cYou can not destroy your own bed!");
        } else {
            Bed bed = (Bed) event.getBlock().getType().getNewData(event.getBlock().getData());

            if (bed.isHeadOfBed()) {
                Location l = event.getBlock().getLocation();
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        if (x == 0 && z == 0) continue;
                        Location location = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).add(x, 0, z);
                        if (location.getBlock() == null || location.getBlock().getType() != Material.BED_BLOCK) continue;
                        location.getBlock().setType(Material.AIR);
                    }
                }
            }
            event.getBlock().setType(Material.AIR);

            bedwarsTeam.setBedSafe(false);
            for (BedwarsPlayer member : bedwarsTeam.getMembers()) {
                Title.sendTitle(member.getPlayer(), "§cBed destroyed!", "§fYou will not respawn anymore", 0, 100, 20);
            }

            if (player != null) BroadcastUtils.chat("\n§f§lBED DESTRUCTION > " + bedwarsTeam.getColor().getTextColor() + bedwarsTeam.getColor().name() + " §fbed destroyed by " + player.getTeam().getColor().getTextColor() + event.getPlayer().getName() + "\n");
        }
    }

    @EventHandler
    public void onHitPlayer(EntityDamageByEntityEvent event) {
        if (!Main.getParty().isStart()) return;
        if (event.getEntity() instanceof Villager || event.getEntity() instanceof Blaze) {
            event.setCancelled(true);
            return;
        }
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
        BedwarsPlayer victim = BedwarsPlayersRegistry.getBedwarsPlayer((Player) event.getEntity());
        BedwarsPlayer damager = BedwarsPlayersRegistry.getBedwarsPlayer((Player) event.getDamager());

        if (victim == null || damager == null) return;

        if (victim.getTeam() == damager.getTeam() || victim.isInvisible()) {
            event.setCancelled(true);
        } else {
            victim.setInvisible(false);
        }
    }


    @EventHandler
    public void onPlayerOpenChest(PlayerInteractEvent event) {
        if (!Main.getParty().isStart()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        event.setCancelled(true);

        BedwarsPlayer player = BedwarsPlayersRegistry.getBedwarsPlayer(event.getPlayer());
        if (player == null) return;

        double min = Double.MAX_VALUE;
        BedwarsTeam bedwarsTeam = null;
        for (BedwarsTeam team : Main.getParty().getTeams()) {
            double distance = event.getClickedBlock().getLocation().distance(team.getSpawnPoint());
            if (distance > min || distance > 30) continue;
            min = distance;
            bedwarsTeam = team;
        }
        if (bedwarsTeam == null) return;

        if (player.getTeam() == bedwarsTeam || bedwarsTeam.isEliminated()) {
            event.setCancelled(false);
        } else {
            player.getPlayer().sendMessage("§cYou can not open this chest until this team is eliminated.");
        }
    }

    @EventHandler
    public void onPlayerOpenShop(PlayerInteractEntityEvent event) {
        if (!Main.getParty().isStart()) return;
        if (event.getRightClicked() == null) return;
        if (event.getRightClicked().getType() != EntityType.VILLAGER) return;
        event.setCancelled(true);

        BedwarsPlayer player = BedwarsPlayersRegistry.getBedwarsPlayer(event.getPlayer());
        if (player == null) return;
        player.open(new GuiShop(player));
    }
}
