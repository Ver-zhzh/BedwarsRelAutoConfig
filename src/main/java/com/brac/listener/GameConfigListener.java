package com.brac.listener;

import com.brac.BedwarsRelAutoConfig;
import com.brac.config.ConfigType;
import com.brac.injector.BedwarsItemAddonInjector;
import com.brac.injector.BedwarsRelInjector;
import com.brac.injector.SBAInjector;
import io.github.bedwarsrel.events.BedwarsGameEndEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class GameConfigListener implements Listener {
    
    private final BedwarsRelAutoConfig plugin;
    private final BedwarsRelInjector bedwarsRelInjector;
    private final SBAInjector sbaInjector;
    private final BedwarsItemAddonInjector bwiaInjector;

    public GameConfigListener(BedwarsRelAutoConfig plugin) {
        this.plugin = plugin;
        this.bedwarsRelInjector = new BedwarsRelInjector();
        this.sbaInjector = new SBAInjector();
        this.bwiaInjector = new BedwarsItemAddonInjector();
    }
    

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameStart(BedwarsGameStartEvent event) {
        Game game = event.getGame();
        String gameName = game.getName();

        if (plugin.getConfig().getBoolean("settings.debug", false)) {
            plugin.getLogger().info("Game starting: " + gameName);
        }

       
        java.util.List<ConfigType> randomEnabledTypes = new java.util.ArrayList<>();
        for (ConfigType type : ConfigType.values()) {
            if (plugin.getMappingManager().isRandomEnabled(gameName, type)) {
                randomEnabledTypes.add(type);
            }
        }

        
        if (!randomEnabledTypes.isEmpty()) {
            
            String selectedTemplate = plugin.getRandomManager().randomSelectForMultipleTypes(gameName, randomEnabledTypes);

            if (selectedTemplate != null) {
               
                java.util.Map<String, java.util.Set<ConfigType>> allTemplates = plugin.getConfigManager().getAllTemplates();
                java.util.Set<ConfigType> templateTypes = allTemplates.get(selectedTemplate);

                if (templateTypes != null) {
                    for (ConfigType type : randomEnabledTypes) {
                        if (templateTypes.contains(type)) {
                            plugin.getMappingManager().enableConfig(gameName, type, selectedTemplate);
                            plugin.getLogger().info("Randomly selected " + type + " config '" + selectedTemplate + "' for game: " + gameName);
                        } else {
                            plugin.getLogger().warning("Template '" + selectedTemplate + "' does not contain " + type + " config for game: " + gameName);
                        }
                    }
                } else {
                    plugin.getLogger().warning("Failed to get template types for: " + selectedTemplate);
                }
            } else {
                plugin.getLogger().warning("Failed to randomly select config template for game: " + gameName);
            }
        }


        boolean hasCustomConfigs = false;

        // Inject BedwarsRel config
        if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.CONFIG)) {
            String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.CONFIG);
            YamlConfiguration config = plugin.getConfigManager().loadTemplate(templateName, ConfigType.CONFIG);
            
            if (config != null) {
                if (bedwarsRelInjector.injectMainConfig(gameName, config)) {
                    hasCustomConfigs = true;
                    plugin.getLogger().info("Injected custom config for game: " + gameName);
                } else {
                    plugin.getLogger().warning("Failed to inject config for game: " + gameName);
                }
            } else {
                plugin.getLogger().warning("Failed to load config template: " + templateName);
            }
        }
        
        // Inject BedwarsRel shop config
        if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.SHOP)) {
            String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.SHOP);
            YamlConfiguration shopConfig = plugin.getConfigManager().loadTemplate(templateName, ConfigType.SHOP);
            
            if (shopConfig != null) {
                if (bedwarsRelInjector.injectShopConfig(gameName, shopConfig)) {
                    hasCustomConfigs = true;
                    plugin.getLogger().info("Injected custom shop config for game: " + gameName);
                } else {
                    plugin.getLogger().warning("Failed to inject shop config for game: " + gameName);
                }
            } else {
                plugin.getLogger().warning("Failed to load shop config template: " + templateName);
            }
        }
        
        // Inject SBA config (if have)
        if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsScoreBoardAddon")) {
            boolean hasSBAConfigs = false;

            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.SBACONFIG)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.SBACONFIG);
                YamlConfiguration sbaConfig = plugin.getConfigManager().loadTemplate(templateName, ConfigType.SBACONFIG);

                if (sbaConfig != null) {
                    if (sbaInjector.injectMainConfig(gameName, sbaConfig)) {
                        hasSBAConfigs = true;
                        plugin.getLogger().info("Cached custom SBA config for game: " + gameName);
                    } else {
                        plugin.getLogger().warning("Failed to cache SBA config for game: " + gameName);
                    }
                } else {
                    plugin.getLogger().warning("Failed to load SBA config template: " + templateName);
                }
            }

            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.TEAMSHOP)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.TEAMSHOP);
                YamlConfiguration teamShopConfig = plugin.getConfigManager().loadTemplate(templateName, ConfigType.TEAMSHOP);

                if (teamShopConfig != null) {
                    if (sbaInjector.injectTeamShopConfig(gameName, teamShopConfig)) {
                        hasSBAConfigs = true;
                        plugin.getLogger().info("Cached custom team shop config for game: " + gameName);
                    } else {
                        plugin.getLogger().warning("Failed to cache team shop config for game: " + gameName);
                    }
                } else {
                    plugin.getLogger().warning("Failed to load team shop config template: " + templateName);
                }
            }

            if (hasSBAConfigs) {
                if (sbaInjector.applyPendingConfigs(gameName)) {
                    hasCustomConfigs = true;
                    plugin.getLogger().info("Successfully applied SBA configs for game: " + gameName);
                } else {
                    plugin.getLogger().warning("Failed to apply SBA configs for game: " + gameName);
                }
            }
        }

  
        if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsItemAddon")) {
            if (plugin.getMappingManager().hasCustomConfig(gameName, ConfigType.ITEMS)) {
                String templateName = plugin.getMappingManager().getTemplateName(gameName, ConfigType.ITEMS);
                YamlConfiguration itemsConfig = plugin.getConfigManager().loadTemplate(templateName, ConfigType.ITEMS);

                if (itemsConfig != null) {
                    if (bwiaInjector.injectItemsConfig(gameName, itemsConfig)) {
                        hasCustomConfigs = true;
                        plugin.getLogger().info("Injected custom items config for game: " + gameName);
                    } else {
                        plugin.getLogger().warning("Failed to inject items config for game: " + gameName);
                    }
                } else {
                    plugin.getLogger().warning("Failed to load items config template: " + templateName);
                }
            }
        }

        if (hasCustomConfigs && plugin.getConfig().getBoolean("settings.debug", false)) {
            plugin.getLogger().info("Successfully injected custom configurations for game: " + gameName);
        }
    }
    
  
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameEnd(BedwarsGameEndEvent event) {
        Game game = event.getGame();
        String gameName = game.getName();
        
        if (plugin.getConfig().getBoolean("settings.debug", false)) {
            plugin.getLogger().info("Game ending: " + gameName);
        }
        
       
        if (bedwarsRelInjector.hasBackups(gameName)) {
            bedwarsRelInjector.restoreMainConfig(gameName);
            bedwarsRelInjector.restoreShopConfig(gameName);
            plugin.getLogger().info("Restored BedwarsRel configs for game: " + gameName);
        }
        
      
        if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsScoreBoardAddon")) {
            if (sbaInjector.hasBackups(gameName)) {
                sbaInjector.restoreMainConfig(gameName);
                sbaInjector.restoreTeamShopConfig(gameName);
                plugin.getLogger().info("Restored SBA configs for game: " + gameName);
            }
        }

        
        if (plugin.getServer().getPluginManager().isPluginEnabled("BedwarsItemAddon")) {
            if (bwiaInjector.isAvailable()) {
                bwiaInjector.restoreItemsConfig(gameName);
                plugin.getLogger().info("Restored BedwarsItemAddon configs for game: " + gameName);
            }
        }
    }
    

    public BedwarsRelInjector getBedwarsRelInjector() {
        return bedwarsRelInjector;
    }
    
  
    public SBAInjector getSbaInjector() {
        return sbaInjector;
    }
}

