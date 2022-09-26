package com.alexandre.bedwars.event;

import com.alexandre.bedwars.Main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.ArrayList;
import java.util.Locale;

public class EventCancel implements Listener {

	private static final ArrayList<Location> blocks = new ArrayList();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
		if (Main.getParty().isStart()) return;
    	event.setCancelled(true);
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
		if (Main.getParty().isStart()) return;
    	event.setCancelled(true);
    }

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (Main.getParty().isStart()) {
			EventCancel.blocks.add(event.getBlock().getLocation());
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
		if (Main.getParty().isStart()) return;
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
		if (Main.getParty().isStart()) return;
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
		if (Main.getParty().isStart()) return;
    	event.setCancelled(true);
    }

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
    	event.setCancelled(true);
	}

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (Main.getParty().isStart()) {
			if (event.getItemDrop() == null || event.getItemDrop().getType() == null) return;
			event.setCancelled(
					event.getItemDrop().getItemStack().getType().name().toLowerCase(Locale.ROOT).contains("_sword") ||
					event.getItemDrop().getItemStack().getType().name().toLowerCase(Locale.ROOT).contains("_pickaxe") ||
					event.getItemDrop().getItemStack().getType().name().toLowerCase(Locale.ROOT).contains("_axe") ||
					event.getItemDrop().getItemStack().getType().name().toLowerCase(Locale.ROOT).contains("_boots") ||
					event.getItemDrop().getItemStack().getType().name().toLowerCase(Locale.ROOT).contains("_chestplate") ||
					event.getItemDrop().getItemStack().getType().name().toLowerCase(Locale.ROOT).contains("_leggings") ||
					event.getItemDrop().getItemStack().getType().name().toLowerCase(Locale.ROOT).contains("_helmet")
			);
			return;
		}
		event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		if (Main.getParty().isStart()) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
				if (event.getClickedBlock().getType() == Material.BED_BLOCK && !event.getPlayer().isSneaking()) event.setCancelled(true);
			}
			return;
		}
    	if (event.getItem() != null && (event.getItem().getType().toString().replace("_", "").equalsIgnoreCase("snowball") || event.getItem().getType() == Material.BOW)) {
			return;
		}

    	if (event.getAction() == Action.PHYSICAL) {
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

		event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (Main.getParty().isStart()) return;
    	event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }
    
	@EventHandler
    public void onExplode(EntityExplodeEvent event) {
		if (Main.getParty().isStart()) {
			event.blockList().removeIf(block -> !EventCancel.blocks.remove(block.getLocation()));
			event.blockList().removeIf(block -> block.getType() == Material.GLASS);
			return;
		}
		event.setCancelled(true);
    }

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerItemDamage(PlayerItemDamageEvent event) {
		event.setDamage(0);
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (Main.getParty().isStart()) {
			if (event.getBlock().getType() == Material.BED_BLOCK) return;
			if (!EventCancel.blocks.remove(event.getBlock().getLocation())) {
				event.setCancelled(true);
				if (event.getPlayer() != null) {
					event.getPlayer().sendMessage("§cYou can only break blocks placed by players!");
				}
			}
			return;
		}

		event.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onHitPlayer(EntityDamageByEntityEvent event) {
    	if (Main.getParty().isStart()) return;
		event.setCancelled(true);
	}
	
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		if (Main.getParty().isStart()) {
			if (event.getCurrentItem() != null) {
				boolean armor = event.getCurrentItem().getType().name().toLowerCase(Locale.ROOT).contains("_boots") ||
						event.getCurrentItem().getType().name().toLowerCase(Locale.ROOT).contains("_chestplate") ||
						event.getCurrentItem().getType().name().toLowerCase(Locale.ROOT).contains("_leggings") ||
						event.getCurrentItem().getType().name().toLowerCase(Locale.ROOT).contains("_helmet");

				if (armor) event.setCancelled(true);
			}
			return;
		}
    	event.setCancelled(event.getWhoClicked().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onPlayerArmorStandManipulation(PlayerArmorStandManipulateEvent event) {
    	event.setCancelled(true);
    }

}
