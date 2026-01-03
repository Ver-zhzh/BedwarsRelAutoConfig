package me.ram.bedwarsscoreboardaddon.addon;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.manager.PlaceholderManager;
import me.ram.bedwarsscoreboardaddon.utils.PlaceholderAPIUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import me.ram.bedwarsscoreboardaddon.utils.ScoreboardUtil;

public class Actionbar {

	@Getter
	private Arena arena;
	@Getter
	private Game game;
	@Getter
	private PlaceholderManager placeholderManager;

	public Actionbar(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		placeholderManager = new PlaceholderManager(game);

		arena.addGameTask(new BukkitRunnable() {

			@Override
			public void run() {
				if (arena.getArenaConfig().getBoolean("actionbar.enabled", false)) {
					for (Player p : game.getPlayers()) {
						if (!game.isSpectator(p)) {
							String message = arena.getArenaConfig().getString("actionbar.message", "");
							Team team = game.getPlayerTeam(p);
							if (team != null) {
								message = message.replace("{color}", team.getChatColor() + "").replace("{team}",
										team.getName());
							}
							message = message.replace("{player}", p.getName()).replace("{game}", game.getName())
									.replace("{time}", getFormattedTimeLeft(game.getTimeLeft()));
							message = PlaceholderAPIUtil.setPlaceholders(p, message);
							Utils.sendPlayerActionbar(p, message);
						}
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, arena.getArenaConfig().getInt("actionbar.interval", 20)));
	}

	private String getFormattedTimeLeft(int time) {
		int min = (int) Math.floor(time / 60);
		int sec = time % 60;
		String minStr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
		String secStr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
		return minStr + ":" + secStr;
	}
}
