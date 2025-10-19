package com.brac.injector;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BedwarsItemAddonInjector extends ConfigInjector {
    
    
    private final Map<String, File> itemsBackups = new HashMap<>();
    
  
    public boolean injectItemsConfig(String gameName, FileConfiguration config) {
        try {
            Plugin bwia = Bukkit.getPluginManager().getPlugin("BedwarsItemAddon");
            if (bwia == null || !bwia.isEnabled()) {
                System.err.println("[BRAC] BedwarsItemAddon plugin is not loaded or enabled!");
                return false;
            }
            
           
            File dataFolder = bwia.getDataFolder();
            File itemsFile = new File(dataFolder, "items.yml");
            
           
            if (!itemsBackups.containsKey(gameName)) {
                File backupFile = new File(dataFolder, "items_backup_" + gameName + ".yml");
                if (itemsFile.exists()) {
                   
                    try {
                        java.nio.file.Files.copy(
                            itemsFile.toPath(), 
                            backupFile.toPath(), 
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                        );
                        itemsBackups.put(gameName, backupFile);
                        System.out.println("[BRAC] Backed up original items.yml for game: " + gameName);
                    } catch (Exception e) {
                        System.err.println("[BRAC] Failed to backup items.yml: " + e.getMessage());
                    }
                }
            }
            
           
            try {
                config.save(itemsFile);
                System.out.println("[BRAC] Saved custom items.yml for game: " + gameName);
            } catch (Exception e) {
                System.err.println("[BRAC] Failed to save items.yml: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            
           
            try {
                Class<?> configClass = Class.forName("me.ram.bedwarsitemaddon.config.Config");
                Method loadConfigMethod = configClass.getDeclaredMethod("loadConfig");
                loadConfigMethod.setAccessible(true);
                loadConfigMethod.invoke(null);
                System.out.println("[BRAC] Successfully reloaded BedwarsItemAddon config for game: " + gameName);
                return true;
            } catch (Exception e) {
                System.err.println("[BRAC] Error calling Config.loadConfig(): " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("[BRAC] Error injecting BedwarsItemAddon items config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }
    
   
    public boolean restoreItemsConfig(String gameName) {
        try {
            Plugin bwia = Bukkit.getPluginManager().getPlugin("BedwarsItemAddon");
            if (bwia == null || !bwia.isEnabled()) {
                System.err.println("[BRAC] BedwarsItemAddon plugin is not loaded or enabled!");
                return false;
            }
            
            File backupFile = itemsBackups.get(gameName);
            if (backupFile == null || !backupFile.exists()) {
                System.out.println("[BRAC] No backup found for game: " + gameName);
                return true; 
            }
            
           
            File dataFolder = bwia.getDataFolder();
            File itemsFile = new File(dataFolder, "items.yml");
            
           
            try {
                java.nio.file.Files.copy(
                    backupFile.toPath(), 
                    itemsFile.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                System.out.println("[BRAC] Restored original items.yml for game: " + gameName);
            } catch (Exception e) {
                System.err.println("[BRAC] Failed to restore items.yml: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            
         
            try {
                Class<?> configClass = Class.forName("me.ram.bedwarsitemaddon.config.Config");
                Method loadConfigMethod = configClass.getDeclaredMethod("loadConfig");
                loadConfigMethod.setAccessible(true);
                loadConfigMethod.invoke(null);
                System.out.println("[BRAC] Successfully restored BedwarsItemAddon config for game: " + gameName);
                
               
                itemsBackups.remove(gameName);
                backupFile.delete();
                
                return true;
            } catch (Exception e) {
                System.err.println("[BRAC] Error calling Config.loadConfig(): " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("[BRAC] Error restoring BedwarsItemAddon items config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }
    

    public boolean isAvailable() {
        Plugin bwia = Bukkit.getPluginManager().getPlugin("BedwarsItemAddon");
        return bwia != null && bwia.isEnabled();
    }
}

