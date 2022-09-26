package com.alexandre.bedwars.players.team;

import org.bukkit.Color;

public enum TeamColor {
    RED(14, 1, Color.fromRGB(10040115), Color.fromRGB(11743532), "§c"),
    BLUE(11, 4, Color.fromRGB(3361970), Color.fromRGB(2437522), "§9"),
    YELLOW(4, 11, Color.fromRGB(15066419), Color.fromRGB(14602026), "§e"),
    AQUA(9, 6, Color.fromRGB(5013401), Color.fromRGB(2651799), "§b"),
    GREEN(5, 10, Color.fromRGB(8375321), Color.fromRGB(4312372), "§2"),
    PINK(6, 9, Color.fromRGB(15892389), Color.fromRGB(14188952), "§d"),
    GRAY(7, 8, Color.fromRGB(5000268), Color.fromRGB(4408131), "§7"),
    WHITE(0, 15, Color.WHITE, Color.fromRGB(15790320), "§f");

    private final byte woolData;
    private final byte dyeData;
    private final Color color;
    private final Color firework;
    private final String textColor;

    TeamColor(int woolData, int dyeData, Color color, Color firework, String textColor) {
        this.woolData = (byte)woolData;
        this.dyeData = (byte)dyeData;
        this.color = color;
        this.firework = firework;
        this.textColor = textColor;
    }

    public byte getData() {
        return this.getWoolData();
    }

    public byte getWoolData() {
        return this.woolData;
    }

    public byte getDyeData() {
        return this.dyeData;
    }

    public Color getColor() {
        return this.color;
    }

    public Color getFireworkColor() {
        return this.firework;
    }

    public String getTextColor() {
        return this.textColor;
    }
}
