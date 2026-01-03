package me.ram.bedwarsscoreboardaddon.addon;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.game.Game;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.config.ArenaConfig;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonSetHealthEvent;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class HealthLevel {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	private Map<String, String> levelTime;

	public Map<String, String> getLevelTime() {
		return levelTime;
	}

	private Integer nowHealth;

	public Integer getNowHealth() {
		return nowHealth;
	}

	public HealthLevel(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		levelTime = new HashMap<String, String>();
		nowHealth = 20;
		ArenaConfig arenaConfig = arena.getArenaConfig();
		if (arenaConfig.getBoolean("sethealth.start.enabled", Config.sethealth_start_enabled)) {
			nowHealth = arenaConfig.getInt("sethealth.start.health", Config.sethealth_start_health);
			arena.addGameTask(new BukkitRunnable() {
				@Override
				public void run() {
					for (Player player : game.getPlayers()) {
						player.setMaxHealth(
								arenaConfig.getInt("sethealth.start.health", Config.sethealth_start_health));
						player.setHealth(player.getMaxHealth());
					}
				}
			}.runTaskLater(Main.getInstance(), 0L));
		}
		for (String sh : arenaConfig.getConfig().getConfigurationSection("sethealth").getKeys(false)) {
			if (!sh.equals("start")) {
				arena.addGameTask(new BukkitRunnable() {
					int gametime = arenaConfig.getConfig().getInt("sethealth." + sh + ".gametime");
					int maxhealth = arenaConfig.getConfig().getInt("sethealth." + sh + ".health");
					String title = arenaConfig.getConfig().getString("sethealth." + sh + ".title");
					String subtitle = arenaConfig.getConfig().getString("sethealth." + sh + ".subtitle");
					String message = arenaConfig.getConfig().getString("sethealth." + sh + ".message");
					Boolean isExecuted = false;

					@Override
					public void run() {
						if (isExecuted) {
							cancel();
							return;
						}
						int remtime = game.getTimeLeft() - gametime;
						String formatremtime = remtime / 60 + ":"
								+ ((remtime % 60 < 10) ? ("0" + remtime % 60) : (remtime % 60));
						levelTime.put(sh, formatremtime);
						if (game.getTimeLeft() <= gametime) {
							isExecuted = true;
							BoardAddonSetHealthEvent setHealthEvent = new BoardAddonSetHealthEvent(game);
							Bukkit.getPluginManager().callEvent(setHealthEvent);
							if (setHealthEvent.isCancelled()) {
								cancel();
								return;
							}
							nowHealth = maxhealth;
							for (Player player : game.getPlayers()) {
								double dhealth = maxhealth - player.getMaxHealth();
								player.setMaxHealth(maxhealth);
								if (dhealth > 0) {
									double nhealth = player.getHealth() + dhealth;
									nhealth = nhealth > maxhealth ? maxhealth : nhealth;
									player.setHealth(nhealth);
								}
								if (!title.equals("") || !subtitle.equals("")) {
									Utils.sendTitle(player, 10, 50, 10, ColorUtil.color(title),
											ColorUtil.color(subtitle));
								}
								if (!message.equals("")) {
									player.sendMessage(ColorUtil.color(message));
								}
							}
							PlaySound.playSound(game, arenaConfig.getStringList("play_sound.sound.sethealth",
									Config.play_sound_sound_sethealth));
							cancel();
						}
					}
				}.runTaskTimer(Main.getInstance(), 0L, 21L));
			}
		}
	}
}
