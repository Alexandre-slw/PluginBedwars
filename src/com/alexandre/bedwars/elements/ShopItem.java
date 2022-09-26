package com.alexandre.bedwars.elements;

import com.alexandre.core.api.inventory.ItemBuilder;
import org.bukkit.Material;

public class ShopItem {

    public enum Type {IRON, GOLD, EMERALD, DIAMOND}

    private final ItemBuilder item;
    private final int price;
    private final Type type;

    public ShopItem(ItemBuilder item, int price, Type type) {
        this.item = item;
        this.price = price;
        this.type = type;
    }

    public ItemBuilder getItem() {
        return this.item;
    }

    public int getPrice() {
        return this.price;
    }

    public Type getType() {
        return this.type;
    }

    public Material getMaterial() {
        if (this.getType() == ShopItem.Type.GOLD) return Material.GOLD_INGOT;
        if (this.getType() == ShopItem.Type.EMERALD) return Material.EMERALD;
        if (this.getType() == ShopItem.Type.DIAMOND) return Material.DIAMOND;
        return Material.IRON_INGOT;
    }
}
