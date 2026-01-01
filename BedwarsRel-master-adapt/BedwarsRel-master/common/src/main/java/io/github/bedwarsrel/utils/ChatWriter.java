package io.github.bedwarsrel.utils;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ChatWriter {

  /**
   * Generate plugin message with global config (backward compatible)
   */
  public static String pluginMessage(String str) {
    return ChatColor.translateAlternateColorCodes('&',
        BedwarsRel.getInstance().getConfig().getString("chat-prefix",
            ChatColor.GRAY + "[" + ChatColor.AQUA + "BedWars" + ChatColor.GRAY + "]"))
        + " " + ChatColor.WHITE + str;
  }

  /**
   * Generate plugin message with per-game config
   * 
   * @param game The game context for per-game config
   * @param str  The message string
   * @return Formatted plugin message
   */
  public static String pluginMessage(Game game, String str) {
    FileConfiguration config;
    String chatPrefix;

    if (game != null) {
      config = game.getGameConfig();
      chatPrefix = config.getString("chat-prefix", null);
    } else {
      config = BedwarsRel.getInstance().getConfig();
      chatPrefix = config.getString("chat-prefix", null);
    }

    if (chatPrefix == null) {
      chatPrefix = ChatColor.GRAY + "[" + ChatColor.AQUA + "BedWars" + ChatColor.GRAY + "]";
    }

    return ChatColor.translateAlternateColorCodes('&', chatPrefix) + " " + ChatColor.WHITE + str;
  }

}
