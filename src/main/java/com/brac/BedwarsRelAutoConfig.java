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
        getLogger().info(MessageUtil.getMessage("plugin.loading", versionPlaceholder));

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

        // Register commands
        BRACCommand commandExecutor = new BRACCommand(this);
        BRACTabCompleter tabCompleter = new BRACTabCompleter(this);

        getCommand("brac").setExecutor(commandExecutor);
        getCommand("brac").setTabCompleter(tabCompleter);

        // Print enabled message
        getLogger().info(MessageUtil.getMessage("plugin.enabled", versionPlaceholder));

        int templateCount = configManager.getTemplateNames().size();
        int mappingCount = mappingManager.getGameNames().size();
        getLogger().info("已加载 " + templateCount + " 个配置模板");
        getLogger().info("已加载 " + mappingCount + " 个游戏配置");
    }
    
    @Override
    public void onDisable() {
        // Clear all backups
        if (gameConfigListener != null) {
            gameConfigListener.getBedwarsRelInjector().clearBackups();
            gameConfigListener.getSbaInjector().clearBackups();
        }
        
        getLogger().info("BedwarsRelAutoConfig disabled.");
    }
    
    private boolean checkDependencies() {
        // Check BedwarsRel
        if (!Bukkit.getPluginManager().isPluginEnabled("BedwarsRel")) {
            getLogger().severe("BedwarsRel not found! This plugin requires BedwarsRel 1.3.6");
            return false;
        }
        
        // Check BedwarsRel version
        String version = Bukkit.getPluginManager().getPlugin("BedwarsRel").getDescription().getVersion();
        if (!version.equals("1.3.6")) {
            getLogger().warning("BedwarsRel version " + version + " detected. This plugin is designed for version 1.3.6");
            getLogger().warning("Some features may not work correctly!");
        }
        
        // Check BedwarsScoreBoardAddon (optional)
        if (Bukkit.getPluginManager().isPluginEnabled("BedwarsScoreBoardAddon")) {
            getLogger().info("BedwarsScoreBoardAddon detected! SBA config management enabled.");
        } else {
            getLogger().info("BedwarsScoreBoardAddon not found. SBA config management disabled.");
        }
        
        return true;
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

