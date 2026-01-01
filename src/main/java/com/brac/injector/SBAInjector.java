package com.brac.injector;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class SBAInjector extends ConfigInjector {

    private final Map<String, FileConfiguration> configBackups = new HashMap<>();
    private final Map<String, FileConfiguration> teamShopBackups = new HashMap<>();

    private final Map<String, PendingGameConfig> pendingConfigs = new HashMap<>();

    private boolean useConfigAPI = false;
    private Class<?> configAPIClass = null;

    private static class PendingGameConfig {
        FileConfiguration mainConfig;
        FileConfiguration teamShopConfig;

        boolean hasAnyConfig() {
            return mainConfig != null || teamShopConfig != null;
        }
    }

    public SBAInjector() {
        detectSBAVersion();
    }

    private void detectSBAVersion() {
        try {

            configAPIClass = Class.forName("me.ram.bedwarsscoreboardaddon.api.ConfigAPI");
            useConfigAPI = true;
            System.out.println("[BRAC] Detected BedwarsScoreBoardAddon-Adapt, using ConfigAPI mode");
        } catch (ClassNotFoundException e) {

            useConfigAPI = false;
            System.out.println("[BRAC] Detected original BedwarsScoreBoardAddon, using legacy reflection mode");
        }
    }

    public boolean injectMainConfig(String gameName, FileConfiguration config) {

        PendingGameConfig pending = pendingConfigs.computeIfAbsent(gameName, k -> new PendingGameConfig());
        pending.mainConfig = config;

        System.out.println("[BRAC] Cached SBA main config for game: " + gameName);
        return true;
    }

    public boolean injectTeamShopConfig(String gameName, FileConfiguration config) {
        PendingGameConfig pending = pendingConfigs.computeIfAbsent(gameName, k -> new PendingGameConfig());
        pending.teamShopConfig = config;
        System.out.println("[BRAC] Cached SBA team shop config for game: " + gameName);
        return true;
    }

    private boolean injectMainConfigWithAPI(String gameName, FileConfiguration config) {
        try {

            Method createTempMethod = configAPIClass.getDeclaredMethod("createTempConfigFile",
                    String.class, FileConfiguration.class, String.class);
            File tempConfigFile = (File) createTempMethod.invoke(null, gameName, config, "config.yml");

            if (tempConfigFile == null) {
                System.err.println("[BRAC] Failed to create temp config file for game: " + gameName);
                return false;
            }

            Method loadGameConfigMethod = configAPIClass.getDeclaredMethod("loadGameConfig",
                    String.class, File.class, File.class, File.class);
            Boolean success = (Boolean) loadGameConfigMethod.invoke(null, gameName, tempConfigFile, null, null);

            if (success != null && success) {
                System.out.println("[BRAC] Successfully injected SBA config using ConfigAPI for game: " + gameName);
                return true;
            } else {
                System.err.println("[BRAC] ConfigAPI.loadGameConfig() returned false for game: " + gameName);
                return false;
            }

        } catch (Exception e) {
            System.err.println("[BRAC] Error injecting SBA config with ConfigAPI for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }

    private boolean injectMainConfigLegacy(String gameName, FileConfiguration config) {
        try {

            Plugin sbaPlugin = Bukkit.getPluginManager().getPlugin("BedwarsScoreBoardAddon");
            if (sbaPlugin == null) {
                System.err.println("[BRAC] BedwarsScoreBoardAddon plugin not found!");
                return false;
            }

            if (!configBackups.containsKey(gameName)) {
                FileConfiguration backup = sbaPlugin.getConfig();
                if (backup != null) {
                    // Create a deep copy
                    YamlConfiguration backupCopy = new YamlConfiguration();
                    for (String key : backup.getKeys(true)) {
                        backupCopy.set(key, backup.get(key));
                    }
                    configBackups.put(gameName, backupCopy);
                    System.out.println("[BRAC] Backed up original SBA config for game: " + gameName);
                }
            }

            boolean success = injectInstanceField(sbaPlugin, "config", config);

            if (success) {

                try {
                    Class<?> configClass = Class.forName("me.ram.bedwarsscoreboardaddon.config.Config");
                    Method loadConfigMethod = configClass.getDeclaredMethod("loadConfig");
                    loadConfigMethod.setAccessible(true);
                    loadConfigMethod.invoke(null);
                    System.out.println("[BRAC] Successfully injected and reloaded SBA config for game: " + gameName);
                } catch (Exception e) {
                    System.err.println("[BRAC] Error calling Config.loadConfig(): " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            } else {
                System.err.println("[BRAC] Failed to inject SBA config field for game: " + gameName);
            }

            return success;
        } catch (Exception e) {
            System.err.println("[BRAC] Error injecting SBA main config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreMainConfig(String gameName) {

        if (useConfigAPI && configAPIClass != null) {
            return restoreMainConfigWithAPI(gameName);
        }

        return restoreMainConfigLegacy(gameName);
    }

    private boolean restoreMainConfigWithAPI(String gameName) {
        try {

            Method restoreMethod = configAPIClass.getDeclaredMethod("restoreDefaultConfig");
            Boolean success = (Boolean) restoreMethod.invoke(null);

            if (success != null && success) {

                Method cleanupMethod = configAPIClass.getDeclaredMethod("cleanupTempConfigs", String.class);
                cleanupMethod.invoke(null, gameName);

                System.out.println("[BRAC] Successfully restored SBA config using ConfigAPI for game: " + gameName);
                return true;
            } else {
                System.err.println("[BRAC] ConfigAPI.restoreDefaultConfig() returned false for game: " + gameName);
                return false;
            }

        } catch (Exception e) {
            System.err.println("[BRAC] Error restoring SBA config with ConfigAPI for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }

    private boolean restoreMainConfigLegacy(String gameName) {
        try {
            FileConfiguration backup = configBackups.get(gameName);
            if (backup == null) {
                System.out.println("[BRAC] No backup found for game: " + gameName + ", skipping restore");
                return true;
            }

            Plugin sbaPlugin = Bukkit.getPluginManager().getPlugin("BedwarsScoreBoardAddon");
            if (sbaPlugin == null) {
                System.err.println("[BRAC] BedwarsScoreBoardAddon plugin not found!");
                return false;
            }

            boolean success = injectInstanceField(sbaPlugin, "config", backup);

            if (success) {

                try {
                    Class<?> configClass = Class.forName("me.ram.bedwarsscoreboardaddon.config.Config");
                    Method loadConfigMethod = configClass.getDeclaredMethod("loadConfig");
                    loadConfigMethod.setAccessible(true);
                    loadConfigMethod.invoke(null);
                    System.out.println("[BRAC] Successfully restored SBA config for game: " + gameName);
                } catch (Exception e) {
                    System.err.println("[BRAC] Error calling Config.loadConfig() during restore: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }

                configBackups.remove(gameName);
            } else {
                System.err.println("[BRAC] Failed to restore SBA config field for game: " + gameName);
            }

            return success;
        } catch (Exception e) {
            System.err.println("[BRAC] Error restoring SBA main config for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }

    public boolean injectTeamShopConfig(String gameName, YamlConfiguration teamShopConfig) {

        PendingGameConfig pending = pendingConfigs.computeIfAbsent(gameName, k -> new PendingGameConfig());
        pending.teamShopConfig = teamShopConfig;

        System.out.println("[BRAC] Cached SBA team shop config for game: " + gameName);
        return true;
    }

    public boolean applyPendingConfigs(String gameName) {
        PendingGameConfig pending = pendingConfigs.get(gameName);

        if (pending == null || !pending.hasAnyConfig()) {
            System.out.println("[BRAC] No pending configs to apply for game: " + gameName);
            return true; // Not an error, just nothing to do
        }

        if (useConfigAPI && configAPIClass != null) {
            return applyPendingConfigsWithAPI(gameName, pending);
        }

        return applyPendingConfigsLegacy(gameName, pending);
    }

    private boolean applyPendingConfigsWithAPI(String gameName, PendingGameConfig pending) {
        try {
            boolean success = true;
            File tempMainConfigFile = null;
            File tempTeamShopFile = null;

            if (pending.mainConfig != null) {
                tempMainConfigFile = new File(
                        Bukkit.getPluginManager().getPlugin("BedwarsRelAutoConfig").getDataFolder(),
                        "temp_" + gameName + "_config.yml");
                tempMainConfigFile.getParentFile().mkdirs();
                pending.mainConfig.save(tempMainConfigFile);
                System.out.println("[BRAC] Saved temp main config for game: " + gameName);
            }

            if (pending.teamShopConfig != null) {
                tempTeamShopFile = new File(Bukkit.getPluginManager().getPlugin("BedwarsRelAutoConfig").getDataFolder(),
                        "temp_" + gameName + "_team_shop.yml");
                tempTeamShopFile.getParentFile().mkdirs();
                pending.teamShopConfig.save(tempTeamShopFile);
                System.out.println("[BRAC] Saved temp team_shop config for game: " + gameName);
            }

            Method registerMethod = configAPIClass.getDeclaredMethod("registerGameConfig",
                    String.class, File.class, File.class);
            Boolean registerSuccess = (Boolean) registerMethod.invoke(null, gameName, tempMainConfigFile,
                    tempTeamShopFile);

            if (registerSuccess != null && registerSuccess) {
                System.out.println("[BRAC] Successfully registered per-game config for game: " + gameName);
            } else {
                System.err.println("[BRAC] Failed to register per-game config for game: " + gameName);
                success = false;
            }

            if (tempMainConfigFile != null && tempMainConfigFile.exists()) {
                tempMainConfigFile.delete();
            }
            if (tempTeamShopFile != null && tempTeamShopFile.exists()) {
                tempTeamShopFile.delete();
            }

            if (success) {

                pendingConfigs.remove(gameName);
            }

            return success;

        } catch (Exception e) {
            System.err.println("[BRAC] Error applying SBA configs with ConfigAPI for game: " + gameName);
            e.printStackTrace();
            return false;
        }
    }

    private boolean applyPendingConfigsLegacy(String gameName, PendingGameConfig pending) {
        boolean success = true;

        if (pending.mainConfig != null) {
            success = injectMainConfigLegacy(gameName, pending.mainConfig);
        }

        if (pending.teamShopConfig != null) {
            System.out.println("[BRAC] Team shop config injection is not supported in original SBA (use SBA-Adapt)");
        }

        if (success) {

            pendingConfigs.remove(gameName);
        }

        return success;
    }

    public boolean restoreTeamShopConfig(String gameName) {

        if (useConfigAPI && configAPIClass != null) {
            try {
                Method unregisterMethod = configAPIClass.getDeclaredMethod("unregisterGameConfig", String.class);
                unregisterMethod.invoke(null, gameName);
                System.out.println("[BRAC] Successfully unregistered team_shop config for game: " + gameName);
                return true;
            } catch (Exception e) {
                System.err.println("[BRAC] Error unregistering team_shop config for game: " + gameName);
                e.printStackTrace();
                return false;
            }
        }

        System.out.println("[BRAC] Team shop config restoration is not supported in original SBA (use SBA-Adapt)");
        return false;
    }

    public void clearBackups() {
        configBackups.clear();
        teamShopBackups.clear();
    }

    public boolean hasBackups(String gameName) {
        return configBackups.containsKey(gameName) || teamShopBackups.containsKey(gameName);
    }
}
