package me.ram.bedwarsscoreboardaddon.addon;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.PlaceholderAPIUtil;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.ArenaConfig;
import java.util.List;

public class ChatFormat implements Listener {

	@EventHandler
	public void onCmd(PlayerCommandPreprocessEvent e) {
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
		if (game == null) {
			return;
		}
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		ArenaConfig arenaConfig = arena.getArenaConfig();
		if (!arenaConfig.getBoolean("chat_format.enabled", Config.chat_format_enabled)) {
			return;
		}
		Player player = e.getPlayer();
		if (game.getState() != GameState.RUNNING || game.isSpectator(player)) {
			return;
		}
		if (e.getMessage().length() <= 7) {
			return;
		}

		String prefix = e.getMessage().substring(0, 7);
		if (!prefix.equals("/shout ")) {
			return;
		}
		e.setCancelled(true);
		if (!arenaConfig.getBoolean("chat_format.chat.all", Config.chat_format_chat_all)) {
			return;
		}
		String msg = PlaceholderAPIUtil.setPlaceholders(player,
				arenaConfig.getString("chat_format.ingame_all", Config.chat_format_ingame_all));
		String playermsg = e.getMessage();
		playermsg = playermsg.substring(7, playermsg.length());
		for (Player p : game.getPlayers()) {
			p.sendMessage(msg.replace("{player}", player.getName()).replace("{message}", playermsg)
					.replace("{color}", game.getPlayerTeam(player).getChatColor() + "")
					.replace("{team}", game.getPlayerTeam(player).getName()));
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		ArenaConfig arenaConfig = arena.getArenaConfig();
		if (!arenaConfig.getBoolean("chat_format.enabled", Config.chat_format_enabled)) {
			return;
		}
		e.setCancelled(true);
		if (game.getState() == GameState.WAITING) {
			if (arenaConfig.getBoolean("chat_format.chat.lobby", Config.chat_format_chat_lobby)) {
				if (game.getPlayerTeam(player) == null) {
					String msg = PlaceholderAPIUtil.setPlaceholders(player,
							arenaConfig.getString("chat_format.lobby", Config.chat_format_lobby));
					msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage());
					for (Player p : game.getPlayers()) {
						p.sendMessage(msg);
					}
				} else {
					String msg = arenaConfig.getString("chat_format.lobby_team", Config.chat_format_lobby_team);
					msg = PlaceholderAPIUtil.setPlaceholders(player, msg);
					Team team = game.getPlayerTeam(player);
					msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage())
							.replace("{color}", team.getChatColor().toString()).replace("{team}", team.getName());
					for (Player p : game.getPlayers()) {
						p.sendMessage(msg);
					}
				}
			}

		} else if (game.getState() == GameState.RUNNING) {
			if (game.isSpectator(player)) {
				if (arenaConfig.getBoolean("chat_format.chat.spectator", Config.chat_format_chat_spectator)) {
					String msg = arenaConfig.getString("chat_format.spectator", Config.chat_format_spectator);
					msg = PlaceholderAPIUtil.setPlaceholders(player, msg);
					msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage());
					for (Player p : game.getPlayers()) {
						p.sendMessage(msg);
					}
				}
			} else {
				if (arenaConfig.getBoolean("chat_format.chat.all", Config.chat_format_chat_all)) {
					String prefix = "";
					boolean all = false;
					List<String> prefixes = arenaConfig.getStringList("chat_format.all_prefix");
					if (prefixes == null || prefixes.isEmpty())
						prefixes = Config.chat_format_all_prefix;
					for (String pref : prefixes) {
						if (e.getMessage().startsWith(pref)) {
							all = true;
							prefix = pref;
						}
					}
					if (all) {
						String playermsg = e.getMessage();
						playermsg = playermsg.substring(prefix.length(), playermsg.length());
						String msg = arenaConfig.getString("chat_format.ingame_all", Config.chat_format_ingame_all);
						msg = PlaceholderAPIUtil.setPlaceholders(player, msg);
						msg = msg.replace("{player}", player.getName()).replace("{message}", playermsg)
								.replace("{color}", game.getPlayerTeam(player).getChatColor() + "")
								.replace("{team}", game.getPlayerTeam(player).getName());
						for (Player p : game.getPlayers()) {
							p.sendMessage(msg);
						}
						return;
					}
				}
				String msg = arenaConfig.getString("chat_format.ingame", Config.chat_format_ingame);
				msg = PlaceholderAPIUtil.setPlaceholders(player, msg);
				msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage())
						.replace("{color}", game.getPlayerTeam(player).getChatColor() + "")
						.replace("{team}", game.getPlayerTeam(player).getName());
				for (Player p : game.getPlayerTeam(player).getPlayers()) {
					p.sendMessage(msg);
				}
			}
		}
	}
}
