package com.brac.config;

import com.brac.BedwarsRelAutoConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MappingManager {
    private final BedwarsRelAutoConfig plugin;
    private final File mappingsFile;
    private YamlConfiguration mappings;

    private final Map<String, Map<ConfigType, String>> gameConfigMappings;

    private final Map<String, Set<ConfigType>> randomEnabledGames;

    public MappingManager(BedwarsRelAutoConfig plugin) {
        this.plugin = plugin;
        this.mappingsFile = new File(plugin.getDataFolder(), "mappings.yml");
        this.gameConfigMappings = new HashMap<>();
        this.randomEnabledGames = new HashMap<>();

        loadMappings();
    }

    private void loadMappings() {
        gameConfigMappings.clear();
        randomEnabledGames.clear();

        if (!mappingsFile.exists()) {
            // Create default mappings file
            mappings = new YamlConfiguration();
            mappings.set("games", new HashMap<>());
            saveMappings();
        } else {
            mappings = YamlConfiguration.loadConfiguration(mappingsFile);
        }

        ConfigurationSection gamesSection = mappings.getConfigurationSection("games");
        if (gamesSection != null) {
            for (String gameName : gamesSection.getKeys(false)) {
                ConfigurationSection gameSection = gamesSection.getConfigurationSection(gameName);
                if (gameSection != null) {
                    Map<ConfigType, String> configs = new HashMap<>();
                    Set<ConfigType> randomTypes = new HashSet<>();

                    for (ConfigType type : ConfigType.values()) {
                        String templateName = gameSection.getString(type.getName());
                        if (templateName != null) {
                            configs.put(type, templateName);
                        }

                        // Load random enable status
                        boolean randomEnabled = gameSection.getBoolean(type.getName() + "_random", false);
                        if (randomEnabled) {
                            randomTypes.add(type);
                        }
                    }

                    if (!configs.isEmpty()) {
                        gameConfigMappings.put(gameName, configs);
                    }

                    if (!randomTypes.isEmpty()) {
                        randomEnabledGames.put(gameName, randomTypes);
                    }
                }
            }
        }

        plugin.getLogger().info("Loaded mappings for " + gameConfigMappings.size() + " games");
    }

    /**
     * Save mappings to file
     */
    private void saveMappings() {
        try {
            mappings.save(mappingsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save mappings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean enableConfig(String gameName, ConfigType type, String templateName) {
        if (!gameConfigMappings.containsKey(gameName)) {
            gameConfigMappings.put(gameName, new HashMap<>());
        }

        gameConfigMappings.get(gameName).put(type, templateName);

        // Update file
        mappings.set("games." + gameName + "." + type.getName(), templateName);
        saveMappings();

        plugin.getLogger().info("Enabled " + type + " config '" + templateName + "' for game: " + gameName);
        return true;
    }

    public boolean disableConfig(String gameName, ConfigType type) {
        if (!gameConfigMappings.containsKey(gameName)) {
            return false;
        }

        Map<ConfigType, String> configs = gameConfigMappings.get(gameName);
        if (!configs.containsKey(type)) {
            return false;
        }

        configs.remove(type);

        // If no configs left for this game, remove the game entry
        if (configs.isEmpty()) {
            gameConfigMappings.remove(gameName);
            mappings.set("games." + gameName, null);
        } else {
            mappings.set("games." + gameName + "." + type.getName(), null);
        }

        saveMappings();

        plugin.getLogger().info("Disabled " + type + " config for game: " + gameName);
        return true;
    }

    public boolean hasCustomConfig(String gameName, ConfigType type) {
        return gameConfigMappings.containsKey(gameName) &&
                gameConfigMappings.get(gameName).containsKey(type);
    }

    public String getTemplateName(String gameName, ConfigType type) {
        if (!hasCustomConfig(gameName, type)) {
            return null;
        }
        return gameConfigMappings.get(gameName).get(type);
    }

    public Map<ConfigType, String> getGameConfigs(String gameName) {
        if (!gameConfigMappings.containsKey(gameName)) {
            return new HashMap<>();
        }
        return new HashMap<>(gameConfigMappings.get(gameName));
    }

    public List<String> getGamesUsingTemplate(String templateName, ConfigType type) {
        List<String> games = new ArrayList<>();

        for (Map.Entry<String, Map<ConfigType, String>> entry : gameConfigMappings.entrySet()) {
            String gameName = entry.getKey();
            Map<ConfigType, String> configs = entry.getValue();

            if (configs.containsKey(type) && configs.get(type).equals(templateName)) {
                games.add(gameName);
            }
        }

        return games;
    }

    public Set<String> getGameNames() {
        return new HashSet<>(gameConfigMappings.keySet());
    }

    public Map<String, Map<ConfigType, String>> getAllMappings() {
        Map<String, Map<ConfigType, String>> result = new HashMap<>();
        for (Map.Entry<String, Map<ConfigType, String>> entry : gameConfigMappings.entrySet()) {
            result.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return result;
    }

    public boolean isTemplateInUse(String templateName) {
        for (Map<ConfigType, String> configs : gameConfigMappings.values()) {
            if (configs.containsValue(templateName)) {
                return true;
            }
        }
        return false;
    }

    public boolean enableRandomConfig(String gameName, ConfigType type) {
        if (!randomEnabledGames.containsKey(gameName)) {
            randomEnabledGames.put(gameName, new HashSet<>());
        }

        if (type == null) {
            // Enable random for all types
            randomEnabledGames.get(gameName).addAll(Arrays.asList(ConfigType.values()));
            for (ConfigType t : ConfigType.values()) {
                mappings.set("games." + gameName + "." + t.getName() + "_random", true);
            }
        } else {
            randomEnabledGames.get(gameName).add(type);
            mappings.set("games." + gameName + "." + type.getName() + "_random", true);
        }

        saveMappings();
        return true;
    }

    public boolean disableRandomConfig(String gameName, ConfigType type) {
        if (!randomEnabledGames.containsKey(gameName)) {
            return false;
        }

        if (type == null) {
            // Disable random for all types
            randomEnabledGames.remove(gameName);
            for (ConfigType t : ConfigType.values()) {
                mappings.set("games." + gameName + "." + t.getName() + "_random", null);
            }
        } else {
            randomEnabledGames.get(gameName).remove(type);
            if (randomEnabledGames.get(gameName).isEmpty()) {
                randomEnabledGames.remove(gameName);
            }
            mappings.set("games." + gameName + "." + type.getName() + "_random", null);
        }

        saveMappings();
        return true;
    }

    public boolean isRandomEnabled(String gameName, ConfigType type) {
        return randomEnabledGames.containsKey(gameName) &&
                randomEnabledGames.get(gameName).contains(type);
    }

    public Set<ConfigType> getRandomEnabledTypes(String gameName) {
        if (!randomEnabledGames.containsKey(gameName)) {
            return new HashSet<>();
        }
        return new HashSet<>(randomEnabledGames.get(gameName));
    }

    public void reload() {
        loadMappings();
    }

    /**
     * 设置游戏的配置模板（用于 RandomManager）
     */
    public void setTemplate(String gameName, String templateName) {
        // 默认设置为 CONFIG 类型，因为 RandomManager 目前只处理 CONFIG
        // 如果需要支持其他类型，可以重载此方法
        enableConfig(gameName, ConfigType.CONFIG, templateName);
    }
}
