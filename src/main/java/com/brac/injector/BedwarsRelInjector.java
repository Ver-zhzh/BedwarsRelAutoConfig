package com.brac.injector;

import io.github.bedwarsrel.BedwarsRel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class BedwarsRelInjector extends ConfigInjector {
    
  
    private final Map<String, FileConfiguration> shopBackups = new HashMap<>();
    private final Map<String, FileConfiguration> configBackups = new HashMap<>();
    
   
    public boolean injectShopConfig(String gameName, YamlConfiguration shopConfig) {
        try {
          
            BedwarsRel bedwarsRel = BedwarsRel.getInstance();
            if (bedwarsRel == null) {
                System.err.println("[BRAC] BedwarsRel instance is null!");
                return false;
            }
            
          
            if (!shopBackups.containsKey(gameName)) {
                FileConfiguration backup = backupInstanceConfig(bedwarsRel, "shopConfig");
                if (backup != null) {
                    shopBackups.put(gameName, backup);
                }
            }
            
            
            boolean success = injectInstanceField(bedwarsRel, "shopConfig", shopConfig);
            
            if (success) {
                System.out.println("[BRAC] Injected shop config for game: " + gameName);
            }
            
            return success;
        } catch (Exception e) {
            System.err.println("[BRAC] Error injecting shop config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }
    
   
    public boolean restoreShopConfig(String gameName) {
        try {
            FileConfiguration backup = shopBackups.get(gameName);
            if (backup == null) {
                System.err.println("[BRAC] No backup found for game: " + gameName);
                return false;
            }
            
            BedwarsRel bedwarsRel = BedwarsRel.getInstance();
            if (bedwarsRel == null) {
                System.err.println("[BRAC] BedwarsRel instance is null!");
                return false;
            }
            
            boolean success = injectInstanceField(bedwarsRel, "shopConfig", backup);
            
            if (success) {
                shopBackups.remove(gameName);
                System.out.println("[BRAC] Restored shop config for game: " + gameName);
            }
            
            return success;
        } catch (Exception e) {
            System.err.println("[BRAC] Error restoring shop config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }
    
   
    public boolean injectMainConfig(String gameName, FileConfiguration config) {
        try {
            BedwarsRel bedwarsRel = BedwarsRel.getInstance();
            if (bedwarsRel == null) {
                System.err.println("[BRAC] BedwarsRel instance is null!");
                return false;
            }
            
           
            if (!configBackups.containsKey(gameName)) {
                FileConfiguration backup = bedwarsRel.getConfig();
                if (backup != null) {
                    // Create a copy
                    YamlConfiguration backupCopy = new YamlConfiguration();
                    for (String key : backup.getKeys(true)) {
                        backupCopy.set(key, backup.get(key));
                    }
                    configBackups.put(gameName, backupCopy);
                }
            }
            
           
            boolean success = injectInstanceField(bedwarsRel, "newConfig", config);
            
            if (success) {
                System.out.println("[BRAC] Injected main config for game: " + gameName);
            }
            
            return success;
        } catch (Exception e) {
            System.err.println("[BRAC] Error injecting main config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }
    
   
    public boolean restoreMainConfig(String gameName) {
        try {
            FileConfiguration backup = configBackups.get(gameName);
            if (backup == null) {
                System.err.println("[BRAC] No backup found for game: " + gameName);
                return false;
            }
            
            BedwarsRel bedwarsRel = BedwarsRel.getInstance();
            if (bedwarsRel == null) {
                System.err.println("[BRAC] BedwarsRel instance is null!");
                return false;
            }
            
            boolean success = injectInstanceField(bedwarsRel, "config", backup);
            
            if (success) {
                configBackups.remove(gameName);
                System.out.println("[BRAC] Restored main config for game: " + gameName);
            }
            
            return success;
        } catch (Exception e) {
            System.err.println("[BRAC] Error restoring main config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }
    
   
    public void clearBackups() {
        shopBackups.clear();
        configBackups.clear();
    }
    
   
    public boolean hasBackups(String gameName) {
        return shopBackups.containsKey(gameName) || configBackups.containsKey(gameName);
    }
}

