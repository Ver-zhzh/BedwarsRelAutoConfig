package me.ram.bedwarsscoreboardaddon.utils;

import org.bukkit.entity.Player;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.api.GameConfig;
import me.ram.bedwarsscoreboardaddon.config.Config;

/**
 * Helper class to simplify per-game configuration access
 * 
 * This class provides convenient methods to get GameConfig for players and games,
 * reducing boilerplate code in addon classes.
 * 
 * @author BRAC Team
 * @version 1.0
 */
public class ConfigHelper {
    
    /**
     * Get per-game configuration for a player's current game
     * 
     * @param player The player to get config for
     * @return GameConfig for the player's game, or default config if player is not in a game
     */
    public static GameConfig getConfig(Player player) {
        if (player == null) {
            return Config.getConfig((Game) null);
        }
        
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        return Config.getConfig(game);
    }
    
    /**
     * Get per-game configuration for a specific game
     * 
     * @param game The game to get config for
     * @return GameConfig for the specified game, or default config if game is null
     */
    public static GameConfig getConfig(Game game) {
        return Config.getConfig(game);
    }
    
    /**
     * Get per-game configuration by game name
     * 
     * @param gameName The name of the game
     * @return GameConfig for the specified game
     */
    public static GameConfig getConfig(String gameName) {
        return Config.getConfig(gameName);
    }
}

