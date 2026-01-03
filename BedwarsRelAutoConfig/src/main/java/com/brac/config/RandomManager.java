package com.brac.config;

import com.brac.BedwarsRelAutoConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RandomManager {

    private final BedwarsRelAutoConfig plugin;
    private final File randomFile;
    private FileConfiguration randomConfig;

    // 存储格式: 游戏名 -> 类型 -> 排除的配置名列表
    private final Map<String, Map<ConfigType, Set<String>>> exclusions;

    public RandomManager(BedwarsRelAutoConfig plugin) {
        this.plugin = plugin;
        this.randomFile = new File(plugin.getDataFolder(), "random.yml");
        this.exclusions = new HashMap<>();

        loadExclusions();
    }

    private void loadExclusions() {
        if (!randomFile.exists()) {
            try {
                randomFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建 random.yml 文件: " + e.getMessage());
                return;
            }
        }

        randomConfig = YamlConfiguration.loadConfiguration(randomFile);
        exclusions.clear();

        // 加载排除规则
        if (randomConfig.contains("exclusions")) {
            for (String gameName : randomConfig.getConfigurationSection("exclusions").getKeys(false)) {
                Map<ConfigType, Set<String>> gameExclusions = new HashMap<>();

                for (String typeName : randomConfig.getConfigurationSection("exclusions." + gameName).getKeys(false)) {
                    ConfigType type = ConfigType.fromString(typeName);
                    if (type != null) {
                        List<String> excludedNames = randomConfig
                                .getStringList("exclusions." + gameName + "." + typeName);
                        gameExclusions.put(type, new HashSet<>(excludedNames));
                    }
                }

                exclusions.put(gameName, gameExclusions);
            }
        }

        plugin.getLogger().info("已加载 " + exclusions.size() + " 个游戏的随机排除规则");
    }

    /**
     * 保存随机排除规则
     */
    private void saveExclusions() {
        randomConfig.set("exclusions", null); // 清空现有数据

        for (Map.Entry<String, Map<ConfigType, Set<String>>> gameEntry : exclusions.entrySet()) {
            String gameName = gameEntry.getKey();

            for (Map.Entry<ConfigType, Set<String>> typeEntry : gameEntry.getValue().entrySet()) {
                ConfigType type = typeEntry.getKey();
                Set<String> excludedNames = typeEntry.getValue();

                if (!excludedNames.isEmpty()) {
                    randomConfig.set("exclusions." + gameName + "." + type.getName(),
                            new ArrayList<>(excludedNames));
                }
            }
        }

        try {
            randomConfig.save(randomFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存 random.yml 文件: " + e.getMessage());
        }
    }

    public void addExclusion(String gameName, ConfigType type, String templateName) {
        exclusions.computeIfAbsent(gameName, k -> new HashMap<>())
                .computeIfAbsent(type, k -> new HashSet<>())
                .add(templateName);
        saveExclusions();
    }

    public boolean removeExclusion(String gameName, ConfigType type, String templateName) {
        if (!exclusions.containsKey(gameName)) {
            return false;
        }

        Map<ConfigType, Set<String>> gameExclusions = exclusions.get(gameName);
        if (!gameExclusions.containsKey(type)) {
            return false;
        }

        boolean removed = gameExclusions.get(type).remove(templateName);

        // 清理空集合
        if (gameExclusions.get(type).isEmpty()) {
            gameExclusions.remove(type);
        }
        if (gameExclusions.isEmpty()) {
            exclusions.remove(gameName);
        }

        if (removed) {
            saveExclusions();
        }

        return removed;
    }

    public boolean isExcluded(String gameName, ConfigType type, String templateName) {
        return exclusions.containsKey(gameName) &&
                exclusions.get(gameName).containsKey(type) &&
                exclusions.get(gameName).get(type).contains(templateName);
    }

    public Map<ConfigType, Set<String>> getExclusions(String gameName) {
        return exclusions.getOrDefault(gameName, new HashMap<>());
    }

    public String randomSelect(String gameName, ConfigType type) {
        // 获取所有可用的配置模板
        Map<String, Set<ConfigType>> allTemplates = plugin.getConfigManager().getAllTemplates();
        List<String> availableTemplates = new ArrayList<>();

        // 过滤出符合条件的配置
        for (Map.Entry<String, Set<ConfigType>> entry : allTemplates.entrySet()) {
            String templateName = entry.getKey();
            Set<ConfigType> types = entry.getValue();

            // 如果指定了类型，检查模板是否包含该类型
            if (type != null) {
                if (types.contains(type) && !isExcluded(gameName, type, templateName)) {
                    availableTemplates.add(templateName);
                }
            } else {
                // 如果没有指定类型，检查模板的任意类型是否未被排除
                for (ConfigType t : types) {
                    if (!isExcluded(gameName, t, templateName)) {
                        availableTemplates.add(templateName);
                        break;
                    }
                }
            }
        }

        // 如果没有可用配置，返回 null
        if (availableTemplates.isEmpty()) {
            return null;
        }

        // 随机选择一个
        Random random = new Random();
        return availableTemplates.get(random.nextInt(availableTemplates.size()));
    }

    public String randomSelectForMultipleTypes(String gameName, List<ConfigType> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }

        Map<String, Set<ConfigType>> allTemplates = plugin.getConfigManager().getAllTemplates();
        List<String> availableTemplates = new ArrayList<>();

        for (Map.Entry<String, Set<ConfigType>> entry : allTemplates.entrySet()) {
            String templateName = entry.getKey();
            Set<ConfigType> templateTypes = entry.getValue();

            // 检查模板是否包含至少一个请求的类型，且未被排除
            boolean hasValidType = false;
            for (ConfigType type : types) {
                if (templateTypes.contains(type) && !isExcluded(gameName, type, templateName)) {
                    hasValidType = true;
                    break;
                }
            }

            if (hasValidType) {
                availableTemplates.add(templateName);
            }
        }

        // 如果没有可用配置，返回 null
        if (availableTemplates.isEmpty()) {
            return null;
        }

        // 随机选择一个
        Random random = new Random();
        return availableTemplates.get(random.nextInt(availableTemplates.size()));
    }

    // reload
    public void reload() {
        loadExclusions();
    }

    public Map<String, Map<ConfigType, Set<String>>> getAllExclusions() {
        return new HashMap<>(exclusions);
    }

    /**
     * 处理游戏的随机配置选择
     * 如果游戏配置为使用随机模板，则选择一个并设置到 MappingManager
     */
    public void handleRandomConfig(String gameName) {
        // 检查是否启用了随机配置（这通常在 config.yml 中定义，或者通过某种规则）
        // 目前简单的逻辑是：如果 MappingManager 中没有为该游戏指定明确的配置，
        // 尝试随机选择一个。

        // 注意：这里的逻辑取决于 MappingManager 是如何工作的。
        // 如果 MappingManager 已经有配置，我们可能不需要做任何事。
        // 但如果我们需要强制随机，或者这是第一次加载...

        // 假设我们在 plugin config 中有一个设置 "random-games" 列表，或者默认所有未配置的游戏都随机
        boolean randomEnabled = plugin.getConfig().getBoolean("settings.random-enabled", false);
        if (!randomEnabled) {
            return;
        }

        // 检查 MappingManager 是否已有配置
        if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.CONFIG)) {
            return; // 已有配置，跳过
        }

        // 尝试随机选择一个包含 CONFIG 类型的模板
        String selectedTemplate = randomSelect(gameName, ConfigType.CONFIG);

        if (selectedTemplate != null) {
            plugin.getLogger().info("Randomly selected template '" + selectedTemplate + "' for game: " + gameName);

            // 将选择的模板应用到 MappingManager
            // 这需要 MappingManager 提供一个 setTemplate 方法
            plugin.getMappingManager().setTemplate(gameName, selectedTemplate);
        }
    }
}
