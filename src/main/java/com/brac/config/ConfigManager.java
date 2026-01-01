package com.brac.config;

import com.brac.BedwarsRelAutoConfig;
import com.brac.util.FileUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ConfigManager {
    private final BedwarsRelAutoConfig plugin;
    private final File configsDir;
    private final Map<String, Map<ConfigType, File>> configTemplates;

    public ConfigManager(BedwarsRelAutoConfig plugin) {
        this.plugin = plugin;
        this.configsDir = new File(plugin.getDataFolder(), "configs");
        this.configTemplates = new HashMap<>();

        if (!configsDir.exists()) {
            configsDir.mkdirs();
        }

        // Load existing templates
        loadTemplates();
    }

    private void loadTemplates() {
        configTemplates.clear();

        if (!configsDir.exists() || !configsDir.isDirectory()) {
            return;
        }

        File[] templateDirs = configsDir.listFiles(File::isDirectory);
        if (templateDirs == null) {
            return;
        }

        for (File templateDir : templateDirs) {
            String templateName = templateDir.getName();
            Map<ConfigType, File> configs = new HashMap<>();

            for (ConfigType type : ConfigType.values()) {
                // Try new structure first: configs/AAA/config/config.yml
                File typeSubDir = new File(templateDir, type.getName());
                File configFile = new File(typeSubDir, type.getFileName());

                if (configFile.exists()) {
                    configs.put(type, configFile);
                    plugin.getLogger().info(
                            "[BRAC] Found template: " + templateName + "/" + type.getName() + "/" + type.getFileName());
                } else {
                    // Try legacy structure: configs/AAA/config.yml
                    File legacyFile = new File(templateDir, type.getFileName());
                    if (legacyFile.exists()) {
                        configs.put(type, legacyFile);
                        plugin.getLogger()
                                .info("[BRAC] Found legacy template: " + templateName + "/" + type.getFileName());
                    }
                }
            }

            if (!configs.isEmpty()) {
                configTemplates.put(templateName, configs);
            }
        }

        plugin.getLogger().info("Loaded " + configTemplates.size() + " configuration templates");
    }

    public boolean createTemplate(String name, ConfigType type) {
        // Check if template already exists
        if (templateExists(name, type)) {
            return false;
        }

        // Get source file path from plugin config
        String sourcePath = getSourcePath(type);
        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            plugin.getLogger().warning("Source file not found: " + sourcePath);
            return false;
        }

        File templateDir = new File(configsDir, name);
        File typeSubDir = new File(templateDir, type.getName());
        if (!typeSubDir.exists()) {
            typeSubDir.mkdirs();
        }

        File destFile = new File(typeSubDir, type.getFileName());
        if (!FileUtil.copyFile(sourceFile, destFile)) {
            plugin.getLogger().warning("Failed to copy file: " + sourceFile.getPath());
            return false;
        }

        if (!configTemplates.containsKey(name)) {
            configTemplates.put(name, new HashMap<>());
        }
        configTemplates.get(name).put(type, destFile);

        plugin.getLogger().info("Created template: " + name + " (" + type + ")");
        return true;
    }

    public boolean deleteTemplate(String name, ConfigType type) {
        if (!configTemplates.containsKey(name)) {
            return false;
        }

        File templateDir = new File(configsDir, name);

        if (type == null) {

            if (FileUtil.deleteRecursively(templateDir)) {
                configTemplates.remove(name);
                plugin.getLogger().info("Deleted template: " + name);
                return true;
            }
        } else {

            Map<ConfigType, File> configs = configTemplates.get(name);
            if (configs.containsKey(type)) {

                File typeSubDir = new File(templateDir, type.getName());
                if (FileUtil.deleteRecursively(typeSubDir)) {
                    configs.remove(type);

                    // If no configs left, remove template directory
                    if (configs.isEmpty()) {
                        FileUtil.deleteRecursively(templateDir);
                        configTemplates.remove(name);
                    }

                    plugin.getLogger().info("Deleted template config: " + name + " (" + type + ")");
                    return true;
                }
            }
        }

        return false;
    }

    public boolean templateExists(String name, ConfigType type) {
        return configTemplates.containsKey(name) &&
                configTemplates.get(name).containsKey(type);
    }

    public File getTemplateFile(String name, ConfigType type) {
        if (!templateExists(name, type)) {
            return null;
        }
        return configTemplates.get(name).get(type);
    }

    public YamlConfiguration loadTemplate(String name, ConfigType type) {
        File file = getTemplateFile(name, type);
        if (file == null) {
            return null;
        }

        try {
            String content = readFileWithEncoding(file);
            // Try to load the configuration
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(content);
            return config;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load config file: " + file.getAbsolutePath());
            plugin.getLogger().warning("Error: " + e.getMessage());
            plugin.getLogger().warning("Please check the YAML syntax of this file manually.");
            plugin.getLogger()
                    .warning("Common issues: incorrect indentation, missing colons, or invalid ItemMeta format.");
            return null;
        }
    }

    private String readFileWithEncoding(File file) throws Exception {
        byte[] bytes = Files.readAllBytes(file.toPath());

        // Try UTF-8 first
        try {
            String content = new String(bytes, StandardCharsets.UTF_8);

            if (content.startsWith("\uFEFF")) {
                content = content.substring(1);
            }

            if (!content.contains("\uFFFD")) {
                return content;
            }
        } catch (Exception e) {
        }

        // Try GB2312 encoding
        try {
            String content = new String(bytes, "GB2312");
            plugin.getLogger().info("Detected GB2312 encoding for file: " + file.getName());
            plugin.getLogger().info("Converting to UTF-8...");
            return content;
        } catch (Exception e) {

            plugin.getLogger().warning("Failed to detect encoding, using UTF-8");
            String content = new String(bytes, StandardCharsets.UTF_8);
            if (content.startsWith("\uFEFF")) {
                content = content.substring(1);
            }
            return content;
        }
    }

    public Set<String> getTemplateNames() {
        return new HashSet<>(configTemplates.keySet());
    }

    public Map<String, Set<ConfigType>> getAllTemplates() {
        Map<String, Set<ConfigType>> result = new HashMap<>();
        for (Map.Entry<String, Map<ConfigType, File>> entry : configTemplates.entrySet()) {
            result.put(entry.getKey(), entry.getValue().keySet());
        }
        return result;
    }

    private String getSourcePath(ConfigType type) {
        switch (type) {
            case CONFIG:
                return plugin.getConfig().getString("defaults.bedwarsrel-config",
                        "plugins/BedwarsRel/config.yml");
            case SHOP:
                return plugin.getConfig().getString("defaults.bedwarsrel-shop",
                        "plugins/BedwarsRel/shop.yml");
            case TEAMSHOP:
                return plugin.getConfig().getString("defaults.sba-teamshop",
                        "plugins/BedwarsScoreBoardAddon/team_shop.yml");
            case SBACONFIG:
                return plugin.getConfig().getString("defaults.sba-config",
                        "plugins/BedwarsScoreBoardAddon/config.yml");
            case ITEMS:
                return plugin.getConfig().getString("defaults.bwia-items",
                        "plugins/BedwarsItemAddon/items.yml");
            default:
                return null;
        }
    }

    public void reload() {
        loadTemplates();
    }
}
