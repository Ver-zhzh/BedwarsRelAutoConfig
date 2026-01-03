package me.ram.bedwarsscoreboardaddon.config;

import org.bukkit.configuration.file.FileConfiguration;
import me.ram.bedwarsscoreboardaddon.arena.Arena;

public class ArenaConfig {
    private final Arena arena;
    private final String gameName;
    private final FileConfiguration config;

    public ArenaConfig(Arena arena) {
        this.arena = arena;
        this.gameName = arena.getGame().getName();
        this.config = Config.getGameConfig(gameName);
    }

    public ArenaConfig(io.github.bedwarsrel.game.Game game) {
        this.arena = null;
        this.gameName = game.getName();
        this.config = Config.getGameConfig(gameName);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Get team shop config dynamically (NOT cached) to support BRAC per-game
     * injection
     * This ensures we always get the latest config even if it was injected after
     * Arena creation
     */
    public FileConfiguration getTeamShopConfig() {
        return Config.getGameTeamShopConfig(gameName);
    }

    // Add helper methods to access specific config values
    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public String getString(String path, String def) {
        String value = config.getString(path, def);
        return value != null ? me.ram.bedwarsscoreboardaddon.utils.ColorUtil.color(value) : def;
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public java.util.List<String> getStringList(String path) {
        java.util.List<String> list = config.getStringList(path);
        java.util.List<String> coloredList = new java.util.ArrayList<>();
        for (String s : list) {
            coloredList.add(me.ram.bedwarsscoreboardaddon.utils.ColorUtil.color(s));
        }
        return coloredList;
    }

    public java.util.List<String> getStringList(String path, java.util.List<String> def) {
        if (config.contains(path)) {
            java.util.List<String> list = config.getStringList(path);
            java.util.List<String> coloredList = new java.util.ArrayList<>();
            for (String s : list) {
                coloredList.add(me.ram.bedwarsscoreboardaddon.utils.ColorUtil.color(s));
            }
            return coloredList;
        }
        return def;
    }

    public org.bukkit.configuration.ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    // We will add more specific getters as needed during refactoring
}
