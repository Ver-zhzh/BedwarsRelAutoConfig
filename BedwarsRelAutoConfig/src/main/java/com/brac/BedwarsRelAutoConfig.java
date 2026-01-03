package com.brac;

import com.brac.command.BRACCommand;
import com.brac.command.BRACTabCompleter;
import com.brac.config.ConfigManager;
import com.brac.config.MappingManager;
import com.brac.config.RandomManager;
import com.brac.listener.GameConfigListener;
import com.brac.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ver_zhzh
 * @version 1.1.0
 */
public class BedwarsRelAutoConfig extends JavaPlugin {

    private ConfigManager configManager;
    private MappingManager mappingManager;
    private RandomManager randomManager;
    private GameConfigListener gameConfigListener;
    private File messagesFile;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Create messages file
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        // Load messages
        MessageUtil.loadMessages(messagesFile);

        Map<String, String> versionPlaceholder = new HashMap<>();
        versionPlaceholder.put("version", getDescription().getVersion());

        // Check dependencies
        if (!checkDependencies()) {
            Map<String, String> depPlaceholder = new HashMap<>();
            depPlaceholder.put("plugin", "BedwarsRel");
            getLogger().severe(MessageUtil.getMessage("plugin.dependency-missing", depPlaceholder));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize managers
        configManager = new ConfigManager(this);
        mappingManager = new MappingManager(this);
        randomManager = new RandomManager(this);

        // Register event listener
        gameConfigListener = new GameConfigListener(this);
        Bukkit.getPluginManager().registerEvents(gameConfigListener, this);

        // Register command
        getCommand("brac").setExecutor(new BRACCommand(this));
        getCommand("brac").setTabCompleter(new BRACTabCompleter(this));

        // Flashy Banner
        getServer().getConsoleSender().sendMessage("§b====================================================");
        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage("              §eBedwarsRelAutoConfig");
        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage("   §a插件版本: " + getDescription().getVersion());
        getServer().getConsoleSender().sendMessage("   §a作者: Ver_zhzh");
        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage("   §6BedwarsRel游戏 多配置管理器");
        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage("§b====================================================");

        getLogger().info(MessageUtil.getMessage("plugin.loading", versionPlaceholder));

        // Print enabled message
        getLogger().info(MessageUtil.getMessage("plugin.enabled", versionPlaceholder));

        int templateCount = configManager.getTemplateNames().size();
        int mappingCount = mappingManager.getGameNames().size();
        getLogger().info("已加载 " + templateCount + " 个配置模板");
        getLogger().info("已加载 " + mappingCount + " 个游戏配置");
    }

    @Override
    public void onDisable() {
        // Restore all configs and clear backups
        if (gameConfigListener != null) {
            gameConfigListener.restoreAllConfigs();
            gameConfigListener.getBedwarsRelInjector().clearBackups();
            gameConfigListener.getSbaInjector().clearBackups();
        }

        getLogger().info("BedwarsRelAutoConfig 已禁用.");
    }

    private boolean checkDependencies() {
        // Check BedwarsRel
        if (!Bukkit.getPluginManager().isPluginEnabled("BedwarsRel")) {
            getLogger().severe("§c未找到 BedwarsRel！本插件需要 BedwarsRel 1.3.6");
            return false;
        }

        // Check BedwarsRel version
        String version = Bukkit.getPluginManager().getPlugin("BedwarsRel").getDescription().getVersion();
        if (!version.equals("1.3.6")) {
            getServer().getConsoleSender().sendMessage("§e检测到 BedwarsRel 版本 " + version + "。本插件专为 1.3.6 设计");
            getServer().getConsoleSender().sendMessage("§e部分功能可能无法正常工作！");
        }

        // Check BedwarsScoreBoardAddon (optional)
        if (Bukkit.getPluginManager().isPluginEnabled("BedwarsScoreBoardAddon")) {
            getServer().getConsoleSender().sendMessage("§a检测到 BedwarsScoreBoardAddon！SBA 配置管理已启用。");
        } else {
            getLogger().info("未找到 BedwarsScoreBoardAddon。SBA 配置管理已禁用。");
        }

        // Check for Adapt versions (Per-Game Config Support)
        checkAdaptVersions();

        return true;
    }

    private boolean isBedwarsRelAdapt = false;
    private boolean isBedwarsItemAddonAdapt = false;

    private void checkAdaptVersions() {
        // Check BedwarsRel Adapt
        try {
            Class<?> gameClass = Class.forName("io.github.bedwarsrel.game.Game");
            gameClass.getMethod("setCustomMainConfig", org.bukkit.configuration.file.FileConfiguration.class);
            isBedwarsRelAdapt = true;
            getServer().getConsoleSender().sendMessage("§a检测到 BedwarsRel-Adapt！Per-game 配置功能已启用。");
        } catch (Exception e) {
            isBedwarsRelAdapt = false;
            getServer().getConsoleSender().sendMessage("§e检测到 Legacy BedwarsRel。Per-game 配置功能已禁用。");
            getServer().getConsoleSender().sendMessage("§e请更新至 BedwarsRel-Adapt 以使用 Per-game 配置功能。");
        }

        // Check BedwarsItemAddon Adapt
        if (Bukkit.getPluginManager().isPluginEnabled("BedwarsItemAddon")) {
            try {
                Class<?> configClass = Class.forName("me.ram.bedwarsitemaddon.config.Config");
                configClass.getMethod("setGameItemsConfig", String.class,
                        org.bukkit.configuration.file.FileConfiguration.class);
                isBedwarsItemAddonAdapt = true;
                getServer().getConsoleSender().sendMessage("§a检测到 BedwarsItemAddon-Adapt！Per-game 物品功能已启用。");
            } catch (Exception e) {
                isBedwarsItemAddonAdapt = false;
                getServer().getConsoleSender().sendMessage("§e检测到 Legacy BedwarsItemAddon。Per-game 物品功能已禁用。");
            }
        }
    }

    public boolean isBedwarsRelAdapt() {
        return isBedwarsRelAdapt;
    }

    public boolean isBedwarsItemAddonAdapt() {
        return isBedwarsItemAddonAdapt;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MappingManager getMappingManager() {
        return mappingManager;
    }

    public RandomManager getRandomManager() {
        return randomManager;
    }

    public GameConfigListener getGameConfigListener() {
        return gameConfigListener;
    }

    public File getMessagesFile() {
        return messagesFile;
    }
}
