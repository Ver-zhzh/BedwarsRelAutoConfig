package com.brac.injector;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Files;

/**
 * BedwarsRel 配置注入器
 * 使用 BedwarsRel-Adapt 提供的 Per-Game 配置 API
 */
public class BedwarsRelInjector extends ConfigInjector {

    // ==========================================
    // Adapt Mode Support (Per-Game Config)
    // ==========================================

    /**
     * 注入商店配置到指定游戏
     */
    public boolean injectShopConfig(String gameName, FileConfiguration shopConfig) {
        try {
            Game game = getGame(gameName);
            if (game == null) {
                return false;
            }

            // 使用反射调用 setCustomShopConfig
            try {
                java.lang.reflect.Method method = game.getClass().getMethod("setCustomShopConfig",
                        FileConfiguration.class);
                method.invoke(game, shopConfig);

                // 重新加载商店分类
                game.loadItemShopCategories();
                return true;
            } catch (NoSuchMethodException e) {
                // 原版插件，不支持 per-game config，静默忽略
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 恢复商店配置（清除自定义配置，回到默认配置）
     */
    public boolean restoreShopConfig(String gameName) {
        try {
            Game game = getGame(gameName);
            if (game == null) {
                return false;
            }

            try {
                java.lang.reflect.Method method = game.getClass().getMethod("setCustomShopConfig",
                        FileConfiguration.class);
                method.invoke(game, (FileConfiguration) null);

                game.loadItemShopCategories();
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 注入主配置到指定游戏
     */
    public boolean injectMainConfig(String gameName, FileConfiguration config) {
        try {
            Game game = getGame(gameName);
            if (game == null) {
                return false;
            }

            try {
                java.lang.reflect.Method method = game.getClass().getMethod("setCustomMainConfig",
                        FileConfiguration.class);
                method.invoke(game, config);
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 恢复主配置（清除自定义配置，回到默认配置）
     */
    public boolean restoreMainConfig(String gameName) {
        try {
            Game game = getGame(gameName);
            if (game == null) {
                return false;
            }

            try {
                java.lang.reflect.Method method = game.getClass().getMethod("setCustomMainConfig",
                        FileConfiguration.class);
                method.invoke(game, (FileConfiguration) null);
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清除备份（现在使用 per-game 配置，此方法不再需要）
     */
    public void clearBackups() {
        // No-op: Per-game configs are stored in Game objects
    }

    /**
     * 检查是否有备份（现在使用 per-game 配置，检查游戏是否有自定义配置）
     */
    public boolean hasBackups(String gameName) {
        Game game = getGame(gameName);
        if (game == null) {
            return false;
        }
        // 检查游戏是否有自定义配置
        return game.getGameConfig() != BedwarsRel.getInstance().getConfig()
                || game.getGameShopConfig() != BedwarsRel.getInstance().getShopConfig();
    }

    /**
     * 获取游戏对象
     */
    private Game getGame(String gameName) {
        BedwarsRel bedwarsRel = BedwarsRel.getInstance();
        if (bedwarsRel == null) {
            System.err.println("[BRAC] BedwarsRel instance is null!");
            return null;
        }
        return bedwarsRel.getGameManager().getGame(gameName);
    }

    // ==========================================
    // Legacy Support (For BungeeCord Mode)
    // ==========================================

    /**
     * Legacy模式：注入主配置（直接修改全局配置）
     * 适用于 BungeeCord 模式（一端一图）
     */
    public boolean injectMainConfigLegacy(String gameName, File templateFile) {
        BedwarsRel plugin = BedwarsRel.getInstance();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File backupFile = new File(plugin.getDataFolder(), "config.yml.bak");

        try {
            // 1. 备份原始 config.yml (如果还没有备份)
            if (configFile.exists() && !backupFile.exists()) {
                Files.copy(configFile.toPath(), backupFile.toPath());
            }

            // 2. 覆盖 config.yml
            if (templateFile.exists()) {
                if (configFile.exists()) {
                    configFile.delete();
                }
                Files.copy(templateFile.toPath(), configFile.toPath());
            }

            // 3. 重载配置
            plugin.reloadConfig();

            // 4. 尝试重载游戏
            try {
                java.lang.reflect.Method loadGamesMethod = plugin.getClass().getDeclaredMethod("loadGames");
                loadGamesMethod.setAccessible(true);
                loadGamesMethod.invoke(plugin);
            } catch (Exception e) {
                System.out.println("[BRAC] 警告: 无法通过反射强制重载游戏 (loadGames).");
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Legacy模式：恢复主配置
     */
    public boolean restoreMainConfigLegacy(String gameName) {
        BedwarsRel plugin = BedwarsRel.getInstance();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File backupFile = new File(plugin.getDataFolder(), "config.yml.bak");

        try {
            if (backupFile.exists()) {
                if (configFile.exists()) {
                    configFile.delete();
                }
                Files.copy(backupFile.toPath(), configFile.toPath());
                backupFile.delete(); // 恢复后删除备份

                plugin.reloadConfig();

                // 尝试重载游戏
                try {
                    java.lang.reflect.Method loadGamesMethod = plugin.getClass().getDeclaredMethod("loadGames");
                    loadGamesMethod.setAccessible(true);
                    loadGamesMethod.invoke(plugin);
                } catch (Exception e) {
                    // ignore
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Legacy模式：注入商店配置（直接修改全局商店配置）
     */
    public boolean injectShopConfigLegacy(String gameName, File templateFile) {
        BedwarsRel plugin = BedwarsRel.getInstance();
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        File backupFile = new File(plugin.getDataFolder(), "shop.yml.bak");

        try {
            // 1. 备份
            if (shopFile.exists() && !backupFile.exists()) {
                Files.copy(shopFile.toPath(), backupFile.toPath());
            }

            // 2. 覆盖
            if (templateFile.exists()) {
                if (shopFile.exists()) {
                    shopFile.delete();
                }
                Files.copy(templateFile.toPath(), shopFile.toPath());
            }

            // 3. 重载商店配置
            try {
                java.lang.reflect.Method loadShopMethod = plugin.getClass().getDeclaredMethod("loadItemShopCategories");
                loadShopMethod.setAccessible(true);
                loadShopMethod.invoke(plugin);
            } catch (Exception e) {
                System.out.println("[BRAC] 警告: 无法通过反射强制重载商店 (loadItemShopCategories).");
            }

            // 尝试重载特定游戏的商店配置
            Game game = getGame(gameName);
            if (game != null) {
                try {
                    java.lang.reflect.Method loadShopMethod = game.getClass().getMethod("loadItemShopCategories");
                    loadShopMethod.invoke(game);
                } catch (Exception e) {
                    // 忽略
                }
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Legacy模式：恢复商店配置
     */
    public boolean restoreShopConfigLegacy(String gameName) {
        BedwarsRel plugin = BedwarsRel.getInstance();
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        File backupFile = new File(plugin.getDataFolder(), "shop.yml.bak");

        try {
            if (backupFile.exists()) {
                if (shopFile.exists()) {
                    shopFile.delete();
                }
                Files.copy(backupFile.toPath(), shopFile.toPath());
                backupFile.delete();

                // 重载商店配置
                try {
                    java.lang.reflect.Method loadShopMethod = plugin.getClass()
                            .getDeclaredMethod("loadItemShopCategories");
                    loadShopMethod.setAccessible(true);
                    loadShopMethod.invoke(plugin);
                } catch (Exception e) {
                    // ignore
                }

                Game game = getGame(gameName);
                if (game != null) {
                    try {
                        java.lang.reflect.Method loadShopMethod = game.getClass().getMethod("loadItemShopCategories");
                        loadShopMethod.invoke(game);
                    } catch (Exception e) {
                        // ignore
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
