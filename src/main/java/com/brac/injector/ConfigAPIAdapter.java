package com.brac.injector;

import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for BedwarsRel-Adapt's ConfigAPI
 * Uses the new ConfigAPI instead of reflection injection
 * 
 * @author Ver_zhzh
 */
public class ConfigAPIAdapter {
    
    private static final String CONFIG_API_CLASS = "io.github.bedwarsrel.api.ConfigAPI";
    private static boolean configAPIAvailable = false;
    private static Class<?> configAPIClass = null;
    private static Method registerMethod = null;
    private static Method unregisterMethod = null;
    
    // Backup storage for configurations (for fallback to reflection mode)
    private final Map<String, FileConfiguration> mainConfigBackups = new HashMap<>();
    private final Map<String, FileConfiguration> shopConfigBackups = new HashMap<>();
    
    static {
        try {
            configAPIClass = Class.forName(CONFIG_API_CLASS);
            registerMethod = configAPIClass.getDeclaredMethod("registerGameConfig",
                String.class, FileConfiguration.class, FileConfiguration.class);
            unregisterMethod = configAPIClass.getDeclaredMethod("unregisterGameConfig", String.class);
            configAPIAvailable = true;
            System.out.println("[BRAC] ========================================");
            System.out.println("[BRAC] BedwarsRel-Adapt detected!");
            System.out.println("[BRAC] Using ConfigAPI mode for better performance");
            System.out.println("[BRAC] ========================================");
        } catch (ClassNotFoundException e) {
            System.out.println("[BRAC] ========================================");
            System.out.println("[BRAC] Original BedwarsRel detected");
            System.out.println("[BRAC] Using reflection injection mode");
            System.out.println("[BRAC] Tip: Consider upgrading to BedwarsRel-Adapt for better compatibility");
            System.out.println("[BRAC] ========================================");
            configAPIAvailable = false;
        } catch (NoSuchMethodException e) {
            System.err.println("[BRAC] ConfigAPI found but methods are incompatible!");
            e.printStackTrace();
            configAPIAvailable = false;
        }
    }
    
    /**
     * Check if ConfigAPI is available
     * 
     * @return true if ConfigAPI is available
     */
    public static boolean isConfigAPIAvailable() {
        return configAPIAvailable;
    }
    
    /**
     * Register game configuration using ConfigAPI
     * 
     * @param gameName Game name
     * @param mainConfig Main configuration
     * @param shopConfig Shop configuration
     * @return true if successful
     */
    public boolean registerGameConfig(String gameName, FileConfiguration mainConfig, FileConfiguration shopConfig) {
        if (!configAPIAvailable) {
            System.err.println("[BRAC] ConfigAPI not available!");
            return false;
        }
        
        try {
            // Debug: Print config details
            System.out.println("[BRAC-DEBUG] Registering config for game: " + gameName);
            System.out.println("[BRAC-DEBUG]   mainConfig: " + (mainConfig != null ? "present (" + mainConfig.getKeys(false).size() + " keys)" : "null"));
            System.out.println("[BRAC-DEBUG]   shopConfig: " + (shopConfig != null ? "present (" + shopConfig.getKeys(false).size() + " keys)" : "null"));

            // Test: Read a specific value from mainConfig
            if (mainConfig != null) {
                String chatPrefix = mainConfig.getString("chat-prefix", "NOT_FOUND");
                System.out.println("[BRAC-DEBUG]   chat-prefix value: " + chatPrefix);
            }

            // Backup configs for potential restore
            if (mainConfig != null) {
                mainConfigBackups.put(gameName, mainConfig);
            }
            if (shopConfig != null) {
                shopConfigBackups.put(gameName, shopConfig);
            }

            // Call ConfigAPI.registerGameConfig(gameName, mainConfig, shopConfig)
            Boolean success = (Boolean) registerMethod.invoke(null, gameName, mainConfig, shopConfig);

            if (success != null && success) {
                System.out.println("[BRAC] Successfully registered config via ConfigAPI for game: " + gameName);
                return true;
            } else {
                System.err.println("[BRAC] ConfigAPI.registerGameConfig returned false for game: " + gameName);
                return false;
            }
        } catch (Exception e) {
            System.err.println("[BRAC] Error registering game config via ConfigAPI for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Unregister game configuration using ConfigAPI
     * 
     * @param gameName Game name
     * @return true if successful
     */
    public boolean unregisterGameConfig(String gameName) {
        if (!configAPIAvailable) {
            System.err.println("[BRAC] ConfigAPI not available!");
            return false;
        }
        
        try {
            // Call ConfigAPI.unregisterGameConfig(gameName)
            Boolean success = (Boolean) unregisterMethod.invoke(null, gameName);
            
            // Clean up backups
            mainConfigBackups.remove(gameName);
            shopConfigBackups.remove(gameName);
            
            if (success != null && success) {
                System.out.println("[BRAC] Successfully unregistered config via ConfigAPI for game: " + gameName);
                return true;
            } else {
                System.out.println("[BRAC] ConfigAPI.unregisterGameConfig returned false for game: " + gameName);
                return false;
            }
        } catch (Exception e) {
            System.err.println("[BRAC] Error unregistering game config via ConfigAPI for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get backed up main config
     * 
     * @param gameName Game name
     * @return Main config or null
     */
    public FileConfiguration getMainConfigBackup(String gameName) {
        return mainConfigBackups.get(gameName);
    }
    
    /**
     * Get backed up shop config
     * 
     * @param gameName Game name
     * @return Shop config or null
     */
    public FileConfiguration getShopConfigBackup(String gameName) {
        return shopConfigBackups.get(gameName);
    }
}

