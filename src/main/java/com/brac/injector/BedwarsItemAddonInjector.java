package com.brac.injector;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Method;

/**
 * BedwarsItemAddon 配置注入器
 * 
 * 使用 BedwarsItemAddon-adapt 版本提供的 per-game 配置 API，
 * 直接通过反射调用 Config.setGameItemsConfig() 和 Config.removeGameItemsConfig()
 * 来实现配置的动态注入，无需备份和恢复文件。
 */
public class BedwarsItemAddonInjector extends ConfigInjector {

    private static final String CONFIG_CLASS = "me.ram.bedwarsitemaddon.config.Config";

    /**
     * 为游戏注入自定义 items 配置
     * 
     * @param gameName 游戏名称
     * @param config   自定义配置
     * @return 是否成功
     */
    public boolean injectItemsConfig(String gameName, FileConfiguration config) {
        try {
            if (!isAvailable()) {
                System.err.println("[BRAC] BedwarsItemAddon plugin is not loaded or enabled!");
                return false;
            }

            // 使用反射调用 Config.setGameItemsConfig(gameName, config)
            Class<?> configClass = Class.forName(CONFIG_CLASS);
            Method setMethod = configClass.getDeclaredMethod("setGameItemsConfig", String.class,
                    FileConfiguration.class);
            setMethod.setAccessible(true);
            setMethod.invoke(null, gameName, config);

            System.out.println("[BRAC] Injected items config for game: " + gameName + " via per-game API");
            return true;

        } catch (ClassNotFoundException e) {
            System.err.println(
                    "[BRAC] BedwarsItemAddon Config class not found. Make sure you are using BedwarsItemAddon-adapt version!");
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e) {
            System.err.println(
                    "[BRAC] setGameItemsConfig method not found. Make sure you are using BedwarsItemAddon-adapt version with per-game API!");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("[BRAC] Error injecting BedwarsItemAddon items config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 为游戏注入自定义 items 配置（从文件加载）
     * 
     * @param gameName     游戏名称
     * @param templateFile 模板配置文件
     * @return 是否成功
     */
    public boolean injectItemsConfigFile(String gameName, File templateFile) {
        try {
            if (templateFile == null || !templateFile.exists()) {
                System.err.println("[BRAC] Template items.yml file does not exist: " +
                        (templateFile != null ? templateFile.getAbsolutePath() : "null"));
                return false;
            }

            // 从文件加载配置
            FileConfiguration config = YamlConfiguration.loadConfiguration(templateFile);

            // 使用 per-game API 注入
            return injectItemsConfig(gameName, config);

        } catch (Exception e) {
            System.err.println("[BRAC] Error loading items config file for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除游戏的自定义 items 配置
     * 
     * @param gameName 游戏名称
     * @return 是否成功
     */
    public boolean restoreItemsConfig(String gameName) {
        try {
            if (!isAvailable()) {
                System.err.println("[BRAC] BedwarsItemAddon plugin is not loaded or enabled!");
                return false;
            }

            // 使用反射调用 Config.removeGameItemsConfig(gameName)
            Class<?> configClass = Class.forName(CONFIG_CLASS);
            Method removeMethod = configClass.getDeclaredMethod("removeGameItemsConfig", String.class);
            removeMethod.setAccessible(true);
            removeMethod.invoke(null, gameName);

            System.out.println("[BRAC] Removed items config for game: " + gameName + " via per-game API");
            return true;

        } catch (ClassNotFoundException e) {
            System.err.println(
                    "[BRAC] BedwarsItemAddon Config class not found. Make sure you are using BedwarsItemAddon-adapt version!");
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e) {
            System.err.println(
                    "[BRAC] removeGameItemsConfig method not found. Make sure you are using BedwarsItemAddon-adapt version with per-game API!");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("[BRAC] Error removing BedwarsItemAddon items config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查游戏是否有自定义配置
     * 
     * @param gameName 游戏名称
     * @return 是否有自定义配置
     */
    public boolean hasGameConfig(String gameName) {
        try {
            if (!isAvailable()) {
                return false;
            }

            Class<?> configClass = Class.forName(CONFIG_CLASS);
            Method hasMethod = configClass.getDeclaredMethod("hasGameItemsConfig", String.class);
            hasMethod.setAccessible(true);
            return (Boolean) hasMethod.invoke(null, gameName);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查 BedwarsItemAddon 插件是否可用
     */
    public boolean isAvailable() {
        Plugin bwia = Bukkit.getPluginManager().getPlugin("BedwarsItemAddon");
        return bwia != null && bwia.isEnabled();
    }

    /**
     * 检查是否是支持 per-game API 的 adapt 版本
     */
    public boolean isAdaptVersion() {
        try {
            Class<?> configClass = Class.forName(CONFIG_CLASS);
            configClass.getDeclaredMethod("setGameItemsConfig", String.class, FileConfiguration.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==========================================
    // Legacy Support (For BungeeCord Mode)
    // ==========================================

    /**
     * Legacy模式：注入 items 配置（直接修改 items.yml 文件并重载）
     */
    public boolean injectItemsConfigLegacy(String gameName, File templateFile) {
        try {
            if (!isAvailable()) {
                return false;
            }

            Plugin bwia = Bukkit.getPluginManager().getPlugin("BedwarsItemAddon");
            File itemsFile = new File(bwia.getDataFolder(), "items.yml");
            File backupFile = new File(bwia.getDataFolder(), "items.yml.bak");

            // 1. 备份原始 items.yml (如果还没有备份)
            if (itemsFile.exists() && !backupFile.exists()) {
                java.nio.file.Files.copy(itemsFile.toPath(), backupFile.toPath());
            }

            // 2. 覆盖 items.yml
            if (templateFile.exists()) {
                // 先删除旧的 items.yml (确保完全覆盖)
                if (itemsFile.exists()) {
                    itemsFile.delete();
                }
                java.nio.file.Files.copy(templateFile.toPath(), itemsFile.toPath());
            }

            // 3. 重载配置
            reloadItemsConfig();

            System.out.println("[BRAC] Legacy: Overwrote items.yml for game: " + gameName);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Legacy模式：恢复 items 配置
     */
    public boolean restoreItemsConfigLegacy(String gameName) {
        try {
            if (!isAvailable()) {
                return false;
            }

            Plugin bwia = Bukkit.getPluginManager().getPlugin("BedwarsItemAddon");
            File itemsFile = new File(bwia.getDataFolder(), "items.yml");
            File backupFile = new File(bwia.getDataFolder(), "items.yml.bak");

            // 1. 恢复备份
            if (backupFile.exists()) {
                if (itemsFile.exists()) {
                    itemsFile.delete();
                }
                java.nio.file.Files.copy(backupFile.toPath(), itemsFile.toPath());

                // 删除备份? 或者保留?
                // 为了安全，保留备份，直到插件禁用?
                // 这里我们选择保留，下次注入时如果存在就不覆盖备份
                // 但如果用户修改了配置，可能需要更新备份...
                // 简单起见，我们假设 BungeeCord 模式下，原始配置是不变的
            }

            // 2. 重载配置
            reloadItemsConfig();

            System.out.println("[BRAC] Legacy: Restored items.yml");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通过反射调用 Config.loadItems()
     */
    private void reloadItemsConfig() {
        try {
            Class<?> configClass = Class.forName(CONFIG_CLASS);
            // 尝试查找 loadItems 方法 (可能是 public static void loadItems())
            // 注意：方法名可能不同，需要根据实际源码确认
            // 根据之前的分析，应该是 loadItems
            try {
                Method loadMethod = configClass.getDeclaredMethod("loadItems");
                loadMethod.setAccessible(true);
                loadMethod.invoke(null);
            } catch (NoSuchMethodException e) {
                // 如果找不到 loadItems，尝试 reload
                try {
                    Method reloadMethod = configClass.getDeclaredMethod("reload");
                    reloadMethod.setAccessible(true);
                    reloadMethod.invoke(null);
                } catch (NoSuchMethodException e2) {
                    System.err.println(
                            "[BRAC] Could not find loadItems or reload method in BedwarsItemAddon Config class!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
