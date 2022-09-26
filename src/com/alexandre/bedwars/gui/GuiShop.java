package com.alexandre.bedwars.gui;

import com.alexandre.bedwars.players.BedwarsPlayer;
import com.alexandre.core.api.inventory.FastInv;
import com.alexandre.core.api.inventory.ItemBuilder;
import com.alexandre.bedwars.elements.ShopItem;
import com.alexandre.bedwars.utils.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GuiShop extends FastInv {

    private BedwarsPlayer player;

    private static final int PANEL_SLOT = 19;
    private static final ArrayList<ShopItem> HOME_PANEL = new ArrayList<>();

    private static final ArrayList<ShopItem> BLOCKS_PANEL = new ArrayList<>();
    private static final ArrayList<ShopItem> COMBATS_PANEL = new ArrayList<>();
    private static final ArrayList<ShopItem> ARMORS_PANEL = new ArrayList<>();
    private static final ArrayList<ShopItem> TOOLS_PANEL = new ArrayList<>();
    private static final ArrayList<ShopItem> DISTANCE_PANEL = new ArrayList<>();
    private static final ArrayList<ShopItem> POTIONS_PANEL = new ArrayList<>();
    private static final ArrayList<ShopItem> UTILS_PANEL = new ArrayList<>();

    private static final ArrayList<ShopItem> PICKAXES = new ArrayList<>();
    private static final ArrayList<ShopItem> AXES = new ArrayList<>();

    static {
        ItemBuilder potionSpeed = new ItemBuilder(Material.POTION).name("Potion of Swiftness II (45 seconds)").data(2);
        potionSpeed.meta(PotionMeta.class, potionMeta -> {
            potionMeta.setMainEffect(PotionEffectType.SPEED);
            potionMeta.addCustomEffect(PotionEffectType.SPEED.createEffect(45 * 20, 1), false);
        });

        ItemBuilder potionJump = new ItemBuilder(Material.POTION).name("Potion of Leaping V (45 seconds)").data(8);
        potionJump.meta(PotionMeta.class, potionMeta -> {
            potionMeta.setMainEffect(PotionEffectType.JUMP);
            potionMeta.addCustomEffect(PotionEffectType.JUMP.createEffect(45 * 20, 4), false);
        });

        ItemBuilder potionInvisibility = new ItemBuilder(Material.POTION).name("Potion of Invisibility (30 seconds)").data(14);
        potionInvisibility.meta(PotionMeta.class, potionMeta -> {
            potionMeta.setMainEffect(PotionEffectType.INVISIBILITY);
            potionMeta.addCustomEffect(PotionEffectType.INVISIBILITY.createEffect(30 * 20, 2), false);
        });

        ShopItem wool = new ShopItem(new ItemBuilder(Material.WOOL).name("Wool").lore("§7Perfect to build bridges between islands.", "").amount(16), 4, ShopItem.Type.IRON);
        ShopItem clay = new ShopItem(new ItemBuilder(Material.HARD_CLAY).name("Hardened clay").lore("§7Basic bloc to protect your bed.", "").amount(16), 12, ShopItem.Type.IRON);
        ShopItem glass = new ShopItem(new ItemBuilder(Material.GLASS).name("Glass explosion-proof").lore("§7Resists to explosion.", "").amount(4), 12, ShopItem.Type.IRON);
        ShopItem end_stone = new ShopItem(new ItemBuilder(Material.ENDER_STONE).name("End stone").lore("§7Solid bloc to protect your bed.", "").amount(12), 24, ShopItem.Type.IRON);
        ShopItem ladder = new ShopItem(new ItemBuilder(Material.LADDER).name("Ladders").lore("§7Useful to climb towers.", "").amount(16), 4, ShopItem.Type.IRON);
        ShopItem wood = new ShopItem(new ItemBuilder(Material.WOOD).name("Wood planks").lore("§7A good block to protect your bed.", "").amount(16), 4, ShopItem.Type.GOLD);
        ShopItem obsidian = new ShopItem(new ItemBuilder(Material.OBSIDIAN).name("Obsidian").lore("§7Ultimate protection for your bed.", "").amount(4), 4, ShopItem.Type.EMERALD);

        ShopItem stone_sword = new ShopItem(new ItemBuilder(Material.STONE_SWORD).name("Stone sword"), 10, ShopItem.Type.IRON);
        ShopItem iron_sword = new ShopItem(new ItemBuilder(Material.IRON_SWORD).name("Iron sword"), 7, ShopItem.Type.GOLD);
        ShopItem diamond_sword = new ShopItem(new ItemBuilder(Material.DIAMOND_SWORD).name("Diamond sword"), 4, ShopItem.Type.EMERALD);
        ShopItem kb_stick = new ShopItem(new ItemBuilder(Material.STICK).name("Stick (KnockBack 1)").enchant(Enchantment.KNOCKBACK), 5, ShopItem.Type.GOLD);

        ShopItem chain_armor = new ShopItem(new ItemBuilder(Material.CHAINMAIL_BOOTS).name("Permanent Chain armor").lore("§7Legging and boots in chain mails, you will always spawn with it.", ""), 40, ShopItem.Type.IRON);
        ShopItem iron_armor = new ShopItem(new ItemBuilder(Material.IRON_BOOTS).name("Permanent Iron armor").lore("§7Legging and boots in iron, you will always spawn with it.", ""), 12, ShopItem.Type.GOLD);
        ShopItem diamond_armor = new ShopItem(new ItemBuilder(Material.DIAMOND_BOOTS).name("Permanent Diamond armor").lore("§7Legging and boots in diamond, you will always spawn with it.", ""), 6, ShopItem.Type.EMERALD);

        ShopItem shears = new ShopItem(new ItemBuilder(Material.SHEARS).name("Shears"), 20, ShopItem.Type.IRON);
        ShopItem pickaxe_1 = new ShopItem(new ItemBuilder(Material.WOOD_PICKAXE).name("Wood pickaxe (Efficiency I)").enchant(Enchantment.DIG_SPEED, 1).lore("§7This tool can be upgrade.", "§7It will downgrade of a level when you will die.", "", "§7You will always spawn with at least the level 1.", ""), 10, ShopItem.Type.IRON);
        ShopItem pickaxe_2 = new ShopItem(new ItemBuilder(Material.IRON_PICKAXE).name("Iron pickaxe (Efficiency II)").enchant(Enchantment.DIG_SPEED, 2).lore("§7This tool can be upgrade.", "§7It will downgrade of a level when you will die.", "", "§7You will always spawn with at least the level 1.", ""), 10, ShopItem.Type.IRON);
        ShopItem pickaxe_3 = new ShopItem(new ItemBuilder(Material.GOLD_PICKAXE).name("Gold pickaxe (Efficiency III, Sharpness II)").enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.DAMAGE_ALL, 2).lore("§7This tool can be upgrade.", "§7It will downgrade of a level when you will die.", "", "§7You will always spawn with at least the level 1.", ""), 3, ShopItem.Type.GOLD);
        ShopItem pickaxe_4 = new ShopItem(new ItemBuilder(Material.DIAMOND_PICKAXE).name("Diamond pickaxe (Efficiency III)").enchant(Enchantment.DIG_SPEED, 3).lore("§7This tool can be upgrade.", "§7It will downgrade of a level when you will die.", "", "§7You will always spawn with at least the level 1.", ""), 6, ShopItem.Type.GOLD);
        ShopItem axe_1 = new ShopItem(new ItemBuilder(Material.WOOD_AXE).name("Wood axe (Efficiency I)").enchant(Enchantment.DIG_SPEED, 1).lore("§7This tool can be upgrade.", "§7It will downgrade of a level when you will die.", "", "§7You will always spawn with at least the level 1.", ""), 10, ShopItem.Type.IRON);
        ShopItem axe_2 = new ShopItem(new ItemBuilder(Material.STONE_AXE).name("Stone axe (Efficiency I)").enchant(Enchantment.DIG_SPEED, 1).lore("§7This tool can be upgrade.", "§7It will downgrade of a level when you will die.", "", "§7You will always spawn with at least the level 1.", ""), 10, ShopItem.Type.IRON);
        ShopItem axe_3 = new ShopItem(new ItemBuilder(Material.IRON_AXE).name("Iron axe (Efficiency II)").enchant(Enchantment.DIG_SPEED, 2).lore("§7This tool can be upgrade.", "§7It will downgrade of a level when you will die.", "", "§7You will always spawn with at least the level 1.", ""), 3, ShopItem.Type.GOLD);
        ShopItem axe_4 = new ShopItem(new ItemBuilder(Material.DIAMOND_AXE).name("Diamond axe (Efficiency III)").enchant(Enchantment.DIG_SPEED, 3).lore("§7This tool can be upgrade.", "§7It will downgrade of a level when you will die.", "", "§7You will always spawn with at least the level 1.", ""), 6, ShopItem.Type.GOLD);

        ShopItem arrows = new ShopItem(new ItemBuilder(Material.ARROW).name("Arrows").amount(8), 2, ShopItem.Type.GOLD);
        ShopItem bow = new ShopItem(new ItemBuilder(Material.BOW).name("Bow"), 12, ShopItem.Type.GOLD);
        ShopItem bow_power = new ShopItem(new ItemBuilder(Material.BOW).name("Bow (Power I)").enchant(Enchantment.ARROW_DAMAGE, 1), 24, ShopItem.Type.GOLD);
        ShopItem bow_power_punch = new ShopItem(new ItemBuilder(Material.BOW).name("Bow (Power I, Punch I)").enchant(Enchantment.ARROW_DAMAGE, 1).enchant(Enchantment.ARROW_KNOCKBACK, 1), 6, ShopItem.Type.EMERALD);

        ShopItem potion_speed = new ShopItem(potionSpeed.lore("§9Speed II (0:45)", ""), 1, ShopItem.Type.EMERALD);
        ShopItem potion_jump = new ShopItem(potionJump.lore("§9Jump Boost V (0:45)", ""), 1, ShopItem.Type.EMERALD);
        ShopItem potion_invisibility = new ShopItem(potionInvisibility.lore("§9Full Invisibility (0:30)", ""), 2, ShopItem.Type.EMERALD);

        ShopItem golden_apple = new ShopItem(new ItemBuilder(Material.GOLDEN_APPLE).name("Golden Apple").lore("§7Useful to heal yourself.", ""), 3, ShopItem.Type.GOLD);
        ShopItem tnt = new ShopItem(new ItemBuilder(Material.TNT).name("Tnt").lore("§7Explode instantly.", ""), 4, ShopItem.Type.GOLD);
        ShopItem ender_pearl = new ShopItem(new ItemBuilder(Material.ENDER_PEARL).name("Ender pearl").lore("§7Launch it to teleport yourself.", ""), 4, ShopItem.Type.EMERALD);
        ShopItem water_bucket = new ShopItem(new ItemBuilder(Material.WATER_BUCKET).name("Water bucket").lore("§7Can protect the bed against explosion.", ""), 3, ShopItem.Type.GOLD);
        ShopItem sponge = new ShopItem(new ItemBuilder(Material.SPONGE).name("Sponge").amount(4), 3, ShopItem.Type.GOLD);

        HOME_PANEL.add(wool);
        HOME_PANEL.add(stone_sword);
        HOME_PANEL.add(chain_armor);
        HOME_PANEL.add(null);
        HOME_PANEL.add(bow);
        HOME_PANEL.add(potion_speed);
        HOME_PANEL.add(tnt);

        HOME_PANEL.add(wood);
        HOME_PANEL.add(iron_sword);
        HOME_PANEL.add(iron_armor);
        HOME_PANEL.add(shears);
        HOME_PANEL.add(arrows);
        HOME_PANEL.add(potion_invisibility);
        HOME_PANEL.add(water_bucket);

        HOME_PANEL.add(null);
        HOME_PANEL.add(null);
        HOME_PANEL.add(null);
        HOME_PANEL.add(null);
        HOME_PANEL.add(null);
        HOME_PANEL.add(null);
        HOME_PANEL.add(null);

        BLOCKS_PANEL.add(wool);
        BLOCKS_PANEL.add(clay);
        BLOCKS_PANEL.add(glass);
        BLOCKS_PANEL.add(end_stone);
        BLOCKS_PANEL.add(ladder);
        BLOCKS_PANEL.add(wood);
        BLOCKS_PANEL.add(obsidian);

        COMBATS_PANEL.add(stone_sword);
        COMBATS_PANEL.add(iron_sword);
        COMBATS_PANEL.add(diamond_sword);
        COMBATS_PANEL.add(kb_stick);

        ARMORS_PANEL.add(chain_armor);
        ARMORS_PANEL.add(iron_armor);
        ARMORS_PANEL.add(diamond_armor);

        TOOLS_PANEL.add(shears);
        TOOLS_PANEL.add(pickaxe_1);
        TOOLS_PANEL.add(axe_1);

        DISTANCE_PANEL.add(arrows);
        DISTANCE_PANEL.add(bow);
        DISTANCE_PANEL.add(bow_power);
        DISTANCE_PANEL.add(bow_power_punch);

        POTIONS_PANEL.add(potion_speed);
        POTIONS_PANEL.add(potion_jump);
        POTIONS_PANEL.add(potion_invisibility);

        UTILS_PANEL.add(golden_apple);
        UTILS_PANEL.add(tnt);
        UTILS_PANEL.add(ender_pearl);
        UTILS_PANEL.add(water_bucket);
        UTILS_PANEL.add(sponge);

        PICKAXES.add(pickaxe_1);
        PICKAXES.add(pickaxe_2);
        PICKAXES.add(pickaxe_3);
        PICKAXES.add(pickaxe_4);

        AXES.add(axe_1);
        AXES.add(axe_2);
        AXES.add(axe_3);
        AXES.add(axe_4);
    }

    private ArrayList<ShopItem> currentPanel;

    public GuiShop(BedwarsPlayer player) {
        super(9 * 6, "Game selector");
        this.player = player;

        this.currentPanel = HOME_PANEL;

        this.displayCurrentPanel();

        this.init();
    }

    public void addCategory(int slot, ItemBuilder item) {
        boolean selected = false;
        switch (slot) {
            case 0:
                selected = this.currentPanel == HOME_PANEL;
                break;
            case 1:
                selected = this.currentPanel == BLOCKS_PANEL;
                break;
            case 2:
                selected = this.currentPanel == COMBATS_PANEL;
                break;
            case 3:
                selected = this.currentPanel == ARMORS_PANEL;
                break;
            case 4:
                selected = this.currentPanel == TOOLS_PANEL;
                break;
            case 5:
                selected = this.currentPanel == DISTANCE_PANEL;
                break;
            case 6:
                selected = this.currentPanel == POTIONS_PANEL;
                break;
            case 7:
                selected = this.currentPanel == UTILS_PANEL;
                break;
        }

        if (selected) {
            item.enchant(Enchantment.DURABILITY, 1).flags(ItemFlag.HIDE_ENCHANTS);
            item.lore("§aSelected");
            item.name("§b" + item.build().getItemMeta().getDisplayName());
            this.setItem(slot + 9, new ItemBuilder(Material.STAINED_GLASS_PANE).name("§7⬆ Categories").data(DyeColor.GREEN.getWoolData()).lore("§7⬇ Items").build());
        } else {
            item.name("§a" + item.build().getItemMeta().getDisplayName());
        }

        this.setItem(slot, item.build());
    }

    public void displayCurrentPanel() {
        this.setItems(9, 17, new ItemBuilder(Material.STAINED_GLASS_PANE).name("§7⬆ Categories").data(DyeColor.GRAY.getWoolData()).lore("§7⬇ Items").build());

        this.addCategory(0, new ItemBuilder(Material.NETHER_STAR).name("Home").lore("§eClick to show!").flags());
        this.addCategory(1, new ItemBuilder(Material.HARD_CLAY).name("Blocks").lore("§eClick to show!").flags());
        this.addCategory(2, new ItemBuilder(Material.GOLD_SWORD).name("Combats").lore("§eClick to show!").flags());
        this.addCategory(3, new ItemBuilder(Material.CHAINMAIL_BOOTS).name("Armors").lore("§eClick to show!").flags());
        this.addCategory(4, new ItemBuilder(Material.STONE_PICKAXE).name("Tools").lore("§eClick to show!").flags());
        this.addCategory(5, new ItemBuilder(Material.BOW).name("Distance").lore("§eClick to show!").flags());
        this.addCategory(6, new ItemBuilder(Material.BREWING_STAND_ITEM).name("Potions").lore("§eClick to show!").flags());
        this.addCategory(7, new ItemBuilder(Material.TNT).name("Utils").lore("§eClick to show!").flags());

        int fix = 0;
        for (int slot = 0; slot < 7 * 3; slot++) {
            if (slot > 0 && slot % 7 == 0) {
                fix += 2;
            }

            if (slot >= this.currentPanel.size()) {
                this.removeItem(PANEL_SLOT + slot + fix);
                continue;
            }

            ShopItem shopItem = this.currentPanel.get(slot);

            if (shopItem == null) {
                this.setItem(PANEL_SLOT + slot + fix, new ItemBuilder(Material.STAINED_GLASS_PANE).name("§7Item not available").data(DyeColor.RED.getWoolData()).build());
                continue;
            }

            int level = 0;
            if (shopItem.getItem().build().getType() == Material.WOOD_PICKAXE) {
                level = this.player.getPickaxeLevel() + 1;
                if (this.player.getPickaxeLevel() > 0) {
                    shopItem = PICKAXES.get(Math.min(this.player.getPickaxeLevel(), PICKAXES.size() - 1));
                }
            }

            if (shopItem.getItem().build().getType() == Material.WOOD_AXE) {
                level = this.player.getAxeLevel() + 1;
                if (this.player.getAxeLevel() > 0) {
                    shopItem = AXES.get(Math.min(this.player.getAxeLevel(), AXES.size() - 1));
                }
            }

            ItemBuilder item = new ItemBuilder(shopItem.getItem().build()).flags();
            if (item.build().getType() == Material.WOOL) item.data(this.player.getTeam().getColor().getWoolData());

//            if (item.build().hasItemMeta() && item.build().getItemMeta() instanceof PotionMeta) {
//                item.flags(ItemFlag.HIDE_POTION_EFFECTS);
//            }

            String color = "§f";
            if (shopItem.getType() == ShopItem.Type.GOLD) color = "§6";
            if (shopItem.getType() == ShopItem.Type.EMERALD) color = "§2";
            if (shopItem.getType() == ShopItem.Type.DIAMOND) color = "§b";
            String resource = StringUtils.capitalize(shopItem.getType().name().toLowerCase(Locale.ROOT));

            List<String> lore = item.build().getItemMeta().getLore();
            if (lore == null) lore = new ArrayList<>();

            lore.add(0, "");
            if (level > 0) lore.add(0, "§7Level: §e" + NumberUtils.toRomainNumber(level));
            if (level < 5) lore.add(0, "§7Price: " + color + shopItem.getPrice() + " " + resource);

            if (level >= 5) {
                lore.add("§aMax!");
                item.name("§c" + item.build().getItemMeta().getDisplayName());
            } else if (this.alreadyHas(shopItem)) {
                lore.add("§cYou already have this item or better!");
                item.name("§c" + item.build().getItemMeta().getDisplayName());
            } else if (this.canBuy(shopItem)) {
                lore.add("§eClick to buy!");
                item.name("§a" + item.build().getItemMeta().getDisplayName());
            } else {
                lore.add("§cYou don't have enough " + resource + "!");
                item.name("§c" + item.build().getItemMeta().getDisplayName());
            }

            item.lore(lore);
            this.setItem(PANEL_SLOT + slot + fix, item.build());
        }
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    public boolean canBuy(ShopItem shopItem) {
        return this.player.countItemInInventory(shopItem.getMaterial()) >= shopItem.getPrice();
    }

    public boolean alreadyHas(ShopItem shopItem) {
        if (shopItem.getItem().build().getType() == Material.SHEARS) {
            return this.player.getShears() != null;
        }

        if (shopItem.getItem().build().getType() == Material.DIAMOND_BOOTS) {
            return this.player.getBoots().build().getType() == Material.DIAMOND_BOOTS;
        } else if (shopItem.getItem().build().getType() == Material.IRON_BOOTS) {
            return this.player.getBoots().build().getType() == Material.IRON_BOOTS || this.player.getBoots().build().getType() == Material.DIAMOND_BOOTS;
        } else if (shopItem.getItem().build().getType() == Material.CHAINMAIL_BOOTS) {
            return this.player.getBoots().build().getType() == Material.CHAINMAIL_BOOTS || this.player.getBoots().build().getType() == Material.IRON_BOOTS || this.player.getBoots().build().getType() == Material.DIAMOND_BOOTS;
        }

        return false;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        switch (event.getSlot()) {
            case 0:
                this.currentPanel = HOME_PANEL;
                this.displayCurrentPanel();
                return;
            case 1:
                this.currentPanel = BLOCKS_PANEL;
                this.displayCurrentPanel();
                return;
            case 2:
                this.currentPanel = COMBATS_PANEL;
                this.displayCurrentPanel();
                return;
            case 3:
                this.currentPanel = ARMORS_PANEL;
                this.displayCurrentPanel();
                return;
            case 4:
                this.currentPanel = TOOLS_PANEL;
                this.displayCurrentPanel();
                return;
            case 5:
                this.currentPanel = DISTANCE_PANEL;
                this.displayCurrentPanel();
                return;
            case 6:
                this.currentPanel = POTIONS_PANEL;
                this.displayCurrentPanel();
                return;
            case 7:
                this.currentPanel = UTILS_PANEL;
                this.displayCurrentPanel();
                return;
        }

        if (event.getSlot() >= PANEL_SLOT && event.getSlot() < PANEL_SLOT + this.currentPanel.size() + 4) {
            if (event.getSlot() % 9 == 0) return;
            if ((event.getSlot() + 1) % 9 == 0) return;

            int fix = 0;
            int s = event.getSlot() - PANEL_SLOT;
            while (s / 7 > 0) {
                s -= 9;
                fix += 2;
            }

            if (event.getSlot() - PANEL_SLOT - fix >= this.currentPanel.size()) return;
            ShopItem shopItem = this.currentPanel.get(event.getSlot() - PANEL_SLOT - fix);
            if (shopItem == null) return;

            int level = 0;
            if (shopItem.getItem().build().getType() == Material.WOOD_PICKAXE) {
                level = this.player.getPickaxeLevel() + 1;
                if (this.player.getPickaxeLevel() > 0) {
                    shopItem = PICKAXES.get(Math.min(this.player.getPickaxeLevel(), PICKAXES.size() - 1));
                }
            }

            if (shopItem.getItem().build().getType() == Material.WOOD_AXE) {
                level = this.player.getAxeLevel() + 1;
                if (this.player.getAxeLevel() > 0) {
                    shopItem = AXES.get(Math.min(this.player.getAxeLevel(), AXES.size() - 1));
                }
            }

            if (level >= 5) {
                this.player.getPlayer().sendMessage("§cYou can't upgrade this tools more!");
                return;
            }

            if (this.alreadyHas(shopItem)) {
                this.player.getPlayer().sendMessage("§cYou already have this item or better!");
                return;
            }

            if (!this.canBuy(shopItem)) {
                this.player.getPlayer().sendMessage("§cYou don't have enough resources!");
                return;
            }

            this.player.removeItem(shopItem.getMaterial(), shopItem.getPrice());
            ItemStack stack = shopItem.getItem().build();
            ItemBuilder item = GuiShop.getItemBuilder(shopItem, this.player);

            if (stack.getType().name().toLowerCase(Locale.ROOT).contains("_sword")) {
                this.player.removeAllItemEndsWith("wood_sword");
            }

            if (stack.getType().name().toLowerCase(Locale.ROOT).contains("shears")) {
                this.player.setShears(new ItemBuilder(Material.SHEARS));
            }

            if (stack.getType().name().toLowerCase(Locale.ROOT).contains("_boots")) {
                switch (stack.getType()) {
                    case DIAMOND_BOOTS:
                        this.player.getLeggings().type(Material.DIAMOND_LEGGINGS);
                        break;
                    case IRON_BOOTS:
                        this.player.getLeggings().type(Material.IRON_LEGGINGS);
                        break;
                    case CHAINMAIL_BOOTS:
                        this.player.getLeggings().type(Material.CHAINMAIL_LEGGINGS);
                        break;
                }

                this.player.getBoots().type(stack.getType());
                this.player.applyArmors();
                this.displayCurrentPanel();
                return;
            }

            if (stack.getType().name().toLowerCase(Locale.ROOT).contains("_pickaxe")) {
                this.player.setPickaxeLevel(this.player.getPickaxeLevel() + 1);
                this.player.removeAllItemEndsWith("_pickaxe");
            }

            if (stack.getType().name().toLowerCase(Locale.ROOT).contains("_axe")) {
                this.player.setAxeLevel(this.player.getAxeLevel() + 1);
                this.player.removeAllItemEndsWith("_axe");
            }

            this.player.getPlayer().getInventory().addItem(item.build());

            this.displayCurrentPanel();
        }
    }

    public static ItemBuilder getItemBuilder(ShopItem shopItem, BedwarsPlayer player) {
        ItemStack stack = shopItem.getItem().build();
        ItemBuilder item = new ItemBuilder(stack.getType()).amount(stack.getAmount());
        if (item.build().getType() == Material.WOOL) item.data(player.getTeam().getColor().getWoolData());
        for (Map.Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()) {
            item.enchant(entry.getKey(), entry.getValue());
        }
        if (stack.hasItemMeta() && stack.getItemMeta() instanceof PotionMeta) {
            item.data(stack.getData().getData());
            PotionMeta meta = (PotionMeta) stack.getItemMeta();
            item.meta(PotionMeta.class, potionMeta -> {
                boolean first = true;
                for (PotionEffect potionEffect : meta.getCustomEffects()) {
                    if (potionEffect == null) continue;
                    if (first) {
                        first = false;
                        potionMeta.setMainEffect(potionEffect.getType());
                    }
                    potionMeta.addCustomEffect(potionEffect, false);
                }
            });
        }
        return item;
    }

    public static ArrayList<ShopItem> getPICKAXES() {
        return PICKAXES;
    }

    public static ArrayList<ShopItem> getAXES() {
        return AXES;
    }
}
