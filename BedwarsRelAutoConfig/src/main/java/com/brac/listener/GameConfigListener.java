package com.brac.listener;

import com.brac.BedwarsRelAutoConfig;
import com.brac.config.ConfigType;
import com.brac.injector.BedwarsItemAddonInjector;
import com.brac.injector.BedwarsRelInjector;
import com.brac.injector.SBAInjector;
import io.github.bedwarsrel.events.BedwarsGameEndEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinEvent;
import io.github.bedwarsrel.game.Game;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class GameConfigListener implements Listener {

    private final BedwarsRelAutoConfig plugin;
    private final BedwarsRelInjector bedwarsRelInjector;
    private final SBAInjector sbaInjector;
    private final BedwarsItemAddonInjector bwiaInjector;

    // Track which games have been configured to avoid re-injection
    private final Set<String> configuredGames = new HashSet<>();

    public GameConfigListener(BedwarsRelAutoConfig plugin) {
        this.plugin = plugin;
        this.bedwarsRelInjector = new BedwarsRelInjector();
        this.sbaInjector = new SBAInjector();
        this.bwiaInjector = new BedwarsItemAddonInjector();
    }

    /**
     * Inject config when FIRST player joins the game (before game starts)
     * This ensures config is applied before lobby countdown starts
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(BedwarsPlayerJoinEvent event) {
        Game game = event.getGame();
        String gameName = game.getName();

        // Only inject on first player join for this game session
        if (configuredGames.contains(gameName)) {
            return;
        }

        if (plugin.getConfig().getBoolean("settings.debug", false)) {
            plugin.getLogger().info("[BRAC] First player joining game: " + gameName + ", injecting configs...");
        }

        // Handle random config selection
        plugin.getRandomManager().handleRandomConfig(gameName);

        boolean hasCustomConfigs = false;

        // Inject BedwarsRel configs (Main & Shop)
        if (plugin.isBedwarsRelAdapt()) {
            boolean hasMainConfig = false;
            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.CONFIG)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.CONFIG);
                org.bukkit.configuration.file.FileConfiguration config = plugin.getConfigManager()
                        .loadTemplate(templateName, ConfigType.CONFIG);

                if (config != null) {
                    if (bedwarsRelInjector.injectMainConfig(gameName, config)) {
                        hasCustomConfigs = true;
                        hasMainConfig = true;
                        if (plugin.getConfig().getBoolean("settings.debug", false)) {
                            plugin.getLogger().info("Injected custom main config for game: " + gameName);
                        }
                    }
                }
            }

            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.SHOP)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.SHOP);
                YamlConfiguration shopConfig = plugin.getConfigManager().loadTemplate(templateName, ConfigType.SHOP);

                if (shopConfig != null) {
                    if (bedwarsRelInjector.injectShopConfig(gameName, shopConfig)) {
                        hasCustomConfigs = true;
                        if (plugin.getConfig().getBoolean("settings.debug", false)) {
                            plugin.getLogger().info("Injected custom shop config for game: " + gameName);
                        }
                    }
                }
            }
        } else {
            // Legacy Mode: Global Config Overwrite (BungeeCord Mode)
            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.CONFIG)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.CONFIG);
                java.io.File templateFile = plugin.getConfigManager().getTemplateFile(templateName, ConfigType.CONFIG);

                if (templateFile != null && templateFile.exists()) {
                    if (bedwarsRelInjector.injectMainConfigLegacy(gameName, templateFile)) {
                        hasCustomConfigs = true;
                        if (plugin.getConfig().getBoolean("settings.debug", false)) {
                            plugin.getLogger().info("Legacy: Overwrote global config for game: " + gameName);
                        }
                    }
                }
            }

            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.SHOP)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.SHOP);
                java.io.File templateFile = plugin.getConfigManager().getTemplateFile(templateName, ConfigType.SHOP);

                if (templateFile != null && templateFile.exists()) {
                    if (bedwarsRelInjector.injectShopConfigLegacy(gameName, templateFile)) {
                        hasCustomConfigs = true;
                        if (plugin.getConfig().getBoolean("settings.debug", false)) {
                            plugin.getLogger().info("Legacy: Overwrote global shop config for game: " + gameName);
                        }
                    }
                }
            }
        }

        // Inject SBA configs (if enabled)
        if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsScoreBoardAddon")) {
            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.SBACONFIG)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.SBACONFIG);
                YamlConfiguration sbaConfig = plugin.getConfigManager().loadTemplate(templateName,
                        ConfigType.SBACONFIG);

                if (sbaConfig != null) {
                    if (sbaInjector.injectMainConfig(gameName, sbaConfig)) {
                        hasCustomConfigs = true;
                        if (plugin.getConfig().getBoolean("settings.debug", false)) {
                            plugin.getLogger().info("Injected custom SBA config for game: " + gameName);
                        }
                    }
                }
            }

            // Inject SBA Team Shop Config
            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.TEAMSHOP)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.TEAMSHOP);
                YamlConfiguration teamShopConfig = plugin.getConfigManager().loadTemplate(templateName,
                        ConfigType.TEAMSHOP);

                if (teamShopConfig != null) {
                    if (sbaInjector.injectTeamShopConfig(gameName, teamShopConfig)) {
                        hasCustomConfigs = true;
                        if (plugin.getConfig().getBoolean("settings.debug", false)) {
                            plugin.getLogger().info("Injected custom SBA team shop config for game: " + gameName);
                        }
                    }
                }
            }

            // Apply the pending SBA configs (CRITICAL: This actually registers the configs
            // with SBA)
            sbaInjector.applyPendingConfigs(gameName);
        }

        // Inject BedwarsItemAddon configs (if enabled)
        if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsItemAddon")) {
            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.ITEMS)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.ITEMS);
                // Get template file directly to preserve comments
                java.io.File templateFile = plugin.getConfigManager().getTemplateFile(templateName,
                        ConfigType.ITEMS);

                if (templateFile != null && templateFile.exists()) {
                    if (plugin.isBedwarsItemAddonAdapt()) {
                        // Adapt Mode
                        if (bwiaInjector.injectItemsConfigFile(gameName, templateFile)) {
                            hasCustomConfigs = true;
                            plugin.getLogger()
                                    .info("Injected custom items config (with comments) for game: " + gameName);
                        } else {
                            plugin.getLogger().warning("Failed to inject items config for game: " + gameName);
                        }
                    } else {
                        // Legacy Mode
                        if (bwiaInjector.injectItemsConfigLegacy(gameName, templateFile)) {
                            hasCustomConfigs = true;
                            plugin.getLogger().info("Legacy: Overwrote items.yml for game: " + gameName);
                        }
                    }
                } else {
                    plugin.getLogger().warning("Failed to find items config template file: " + templateName);
                }
            }
        }

        if (hasCustomConfigs) {
            configuredGames.add(gameName);
            if (plugin.getConfig().getBoolean("settings.debug", false)) {
                plugin.getLogger().info("Successfully injected custom configurations for game: " + gameName);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameEnd(BedwarsGameEndEvent event) {
        Game game = event.getGame();
        String gameName = game.getName();

        if (plugin.getConfig().getBoolean("settings.debug", false)) {
            plugin.getServer().getConsoleSender().sendMessage("§e[BRAC] 游戏结束: " + gameName);
        }

        // Remove from configured games so next session can be configured again
        configuredGames.remove(gameName);

        if (plugin.isBedwarsRelAdapt()) {
            if (bedwarsRelInjector.hasBackups(gameName)) {
                bedwarsRelInjector.restoreMainConfig(gameName);
                bedwarsRelInjector.restoreShopConfig(gameName);
                plugin.getServer().getConsoleSender().sendMessage("§a[BRAC] 已恢复 BedwarsRel 配置: " + gameName);
            }
        } else {
            // Legacy Mode: Restore global config
            boolean restoredMain = bedwarsRelInjector.restoreMainConfigLegacy(gameName);
            boolean restoredShop = bedwarsRelInjector.restoreShopConfigLegacy(gameName);

            if (restoredMain || restoredShop) {
                plugin.getServer().getConsoleSender().sendMessage("§e[BRAC] Legacy: 已从备份恢复 BedwarsRel 全局配置。");
            }
        }

        if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsScoreBoardAddon")) {
            if (sbaInjector.hasBackups(gameName)) {
                sbaInjector.restoreMainConfig(gameName);
                sbaInjector.restoreTeamShopConfig(gameName);
                plugin.getServer().getConsoleSender().sendMessage("§a[BRAC] 已恢复 SBA 配置: " + gameName);
            }
        }

        if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsItemAddon")) {
            if (plugin.isBedwarsItemAddonAdapt()) {
                if (bwiaInjector.isAvailable()) {
                    bwiaInjector.restoreItemsConfig(gameName);
                    plugin.getServer().getConsoleSender().sendMessage("§a[BRAC] 已恢复 BedwarsItemAddon 配置: " + gameName);
                }
            } else {
                // Legacy Mode: Restore items.yml from backup
                if (bwiaInjector.restoreItemsConfigLegacy(gameName)) {
                    plugin.getServer().getConsoleSender().sendMessage("§e[BRAC] Legacy: 已从备份恢复 items.yml。");
                }
            }
        }
    }

    /**
     * Restore configs for all configured games
     * Called on plugin disable
     */
    public void restoreAllConfigs() {
        if (configuredGames.isEmpty()) {
            return;
        }

        plugin.getServer().getConsoleSender().sendMessage("§e[BRAC] 正在恢复所有已注入的配置...");

        // Create a copy to avoid ConcurrentModificationException if we were removing
        // from the set
        // (though here we just clear it at the end)
        for (String gameName : new HashSet<>(configuredGames)) {
            try {
                if (plugin.isBedwarsRelAdapt()) {
                    if (bedwarsRelInjector.hasBackups(gameName)) {
                        bedwarsRelInjector.restoreMainConfig(gameName);
                        bedwarsRelInjector.restoreShopConfig(gameName);
                    }
                } else {
                    // Legacy Mode
                    bedwarsRelInjector.restoreMainConfigLegacy(gameName);
                    bedwarsRelInjector.restoreShopConfigLegacy(gameName);
                }

                if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsScoreBoardAddon")) {
                    if (sbaInjector.hasBackups(gameName)) {
                        sbaInjector.restoreMainConfig(gameName);
                        sbaInjector.restoreTeamShopConfig(gameName);
                    }
                }

                if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsItemAddon")) {
                    if (plugin.isBedwarsItemAddonAdapt()) {
                        if (bwiaInjector.isAvailable()) {
                            bwiaInjector.restoreItemsConfig(gameName);
                        }
                    } else {
                        // Legacy Mode
                        bwiaInjector.restoreItemsConfigLegacy(gameName);
                    }
                }

                plugin.getServer().getConsoleSender().sendMessage("§a[BRAC] 已恢复配置: " + gameName);

            } catch (Exception e) {
                plugin.getServer().getConsoleSender()
                        .sendMessage("§c[BRAC] 恢复配置时出错 (" + gameName + "): " + e.getMessage());
                e.printStackTrace();
            }
        }

        configuredGames.clear();
        plugin.getServer().getConsoleSender().sendMessage("§a[BRAC] 所有配置已恢复。");
    }

    public BedwarsRelInjector getBedwarsRelInjector() {
        return bedwarsRelInjector;
    }

    public SBAInjector getSbaInjector() {
        return sbaInjector;
    }

    public BedwarsItemAddonInjector getBwiaInjector() {
        return bwiaInjector;
    }
}
