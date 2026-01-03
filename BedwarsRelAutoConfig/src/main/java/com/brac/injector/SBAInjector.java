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

    private boolean useAdaptMode = false;
    private Class<?> configClass = null;

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
            // Check for the Config class with per-game support methods
            configClass = Class.forName("me.ram.bedwarsscoreboardaddon.config.Config");
            try {
                configClass.getMethod("setGameConfig", String.class, FileConfiguration.class, FileConfiguration.class);
                useAdaptMode = true;
                System.out.println("[BRAC] Detected BedwarsScoreBoardAddon-Adapt, using per-game config mode");
            } catch (NoSuchMethodException e) {
                useAdaptMode = false;
                System.out.println("[BRAC] Detected original BedwarsScoreBoardAddon, using legacy reflection mode");
            }
        } catch (ClassNotFoundException e) {
            useAdaptMode = false;
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

    // Legacy injection (for non-adapt version)
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
        if (useAdaptMode) {
            return restoreConfigAdapt(gameName);
        }
        return restoreMainConfigLegacy(gameName);
    }

    private boolean restoreConfigAdapt(String gameName) {
        try {
            Method removeMethod = configClass.getDeclaredMethod("removeGameConfig", String.class);
            removeMethod.invoke(null, gameName);
            System.out.println("[BRAC] Successfully restored SBA config using Adapt mode for game: " + gameName);
            return true;
        } catch (Exception e) {
            System.err.println("[BRAC] Error restoring SBA config with Adapt mode for game: " + gameName);
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
            return true;
        }

        if (useAdaptMode) {
            return applyPendingConfigsAdapt(gameName, pending);
        }

        return applyPendingConfigsLegacy(gameName, pending);
    }

    private boolean applyPendingConfigsAdapt(String gameName, PendingGameConfig pending) {
        try {
            Method setMethod = configClass.getDeclaredMethod("setGameConfig", String.class, FileConfiguration.class,
                    FileConfiguration.class);
            setMethod.invoke(null, gameName, pending.mainConfig, pending.teamShopConfig);

            System.out.println("[BRAC] Successfully registered per-game config for game: " + gameName);
            pendingConfigs.remove(gameName);
            return true;
        } catch (Exception e) {
            System.err.println("[BRAC] Error applying SBA configs with Adapt mode for game: " + gameName);
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
        if (useAdaptMode) {
            // restoreConfigAdapt handles both configs
            return true;
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
