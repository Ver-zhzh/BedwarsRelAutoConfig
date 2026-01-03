package me.ram.bedwarsscoreboardaddon.addon;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class Title implements Listener {

	private Map<String, Integer> Times = new HashMap<String, Integer>();

	@EventHandler
	public void onStarted(BedwarsGameStartedEvent e) {
		Game game = e.getGame();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		me.ram.bedwarsscoreboardaddon.config.ArenaConfig arenaConfig = arena.getArenaConfig();
		Times.put(e.getGame().getName(), e.getGame().getTimeLeft());
		if (arenaConfig.getBoolean("start_title.enabled", Config.start_title_enabled)) {
			for (Player player : e.getGame().getPlayers()) {
				Utils.clearTitle(player);
			}
			int delay = game.getRegion().getWorld().getName().equals(game.getLobby().getWorld().getName()) ? 5 : 30;
			arena.addGameTask(new BukkitRunnable() {
				int rn = 0;
				java.util.List<String> titles = arenaConfig.getStringList("start_title.title", Config.start_title_title);

				@Override
				public void run() {
					if (rn < titles.size()) {
						for (Player player : e.getGame().getPlayers()) {
							Utils.sendTitle(player, 0, 80, 5, titles.get(rn), arenaConfig.getString("start_title.subtitle", Config.start_title_subtitle));
						}
						rn++;
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), delay, 0L));
		}
		if (game.getLobby().getWorld().equals(game.getRegion().getWorld())) {
			PlaySound.playSound(e.getGame(), arenaConfig.getStringList("play_sound.sound.start", Config.play_sound_sound_start));
		} else {
			arena.addGameTask(new BukkitRunnable() {
				@Override
				public void run() {
					PlaySound.playSound(e.getGame(), arenaConfig.getStringList("play_sound.sound.start", Config.play_sound_sound_start));
				}
			}.runTaskLater(Main.getInstance(), 30L));
		}
	}

	@EventHandler
	public void onDestroyed(BedwarsTargetBlockDestroyedEvent e) {
		Game game = e.getGame();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		me.ram.bedwarsscoreboardaddon.config.ArenaConfig arenaConfig = arena.getArenaConfig();
		if (arenaConfig.getBoolean("destroyed_title.enabled", Config.destroyed_title_enabled)) {
			for (Player player : e.getTeam().getPlayers()) {
				Utils.sendTitle(player, 1, 30, 1, arenaConfig.getString("destroyed_title.title", Config.destroyed_title_title), arenaConfig.getString("destroyed_title.subtitle", Config.destroyed_title_subtitle));
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		me.ram.bedwarsscoreboardaddon.config.ArenaConfig arenaConfig = arena.getArenaConfig();
		if (arenaConfig.getBoolean("die_out_title.enabled", Config.die_out_title_enabled)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (game.getState() == GameState.RUNNING && game.isSpectator(player)) {
						Utils.sendTitle(player, 1, 80, 5, arenaConfig.getString("die_out_title.title", Config.die_out_title_title), arenaConfig.getString("die_out_title.subtitle", Config.die_out_title_subtitle));
					}
				}
			}.runTaskLater(Main.getInstance(), 5L);
		}
	}

	@EventHandler
	public void onOver(BedwarsGameOverEvent e) {
		Game game = e.getGame();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		me.ram.bedwarsscoreboardaddon.config.ArenaConfig arenaConfig = arena.getArenaConfig();
		if (arenaConfig.getBoolean("victory_title.enabled", Config.victory_title_enabled)) {
			Team team = e.getWinner();
			int time = Times.getOrDefault(e.getGame().getName(), 3600) - e.getGame().getTimeLeft();
			String formattime = time / 60 + ":" + ((time % 60 < 10) ? ("0" + time % 60) : (time % 60));
			new BukkitRunnable() {
				@Override
				public void run() {
					if (team != null && team.getPlayers() != null) {
						for (Player player : team.getPlayers()) {
							if (player.isOnline()) {
								Utils.clearTitle(player);
							}
						}
					}
				}
			}.runTaskLater(Main.getInstance(), 1L);
			arena.addGameTask(new BukkitRunnable() {
				int rn = 0;
				java.util.List<String> titles = arenaConfig.getStringList("victory_title.title", Config.victory_title_title);

				@Override
				public void run() {
					if (rn < titles.size()) {
						if (team != null && team.getPlayers() != null) {
							for (Player player : team.getPlayers()) {
								if (player.isOnline()) {
									Utils.sendTitle(player, 0, 80, 5, titles.get(rn).replace("{time}", formattime).replace("{color}", team.getChatColor() + "").replace("{team}", team.getName()), arenaConfig.getString("victory_title.subtitle", Config.victory_title_subtitle).replace("{time}", formattime).replace("{color}", team.getChatColor() + "").replace("{team}", team.getName()));
								}
							}
							rn++;
						} else {
							this.cancel();
						}
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 40L, 0L));
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				PlaySound.playSound(e.getGame(), arenaConfig.getStringList("play_sound.sound.over", Config.play_sound_sound_over));
			}
		}.runTaskLater(Main.getInstance(), 40L);
	}

	@EventHandler
	public void onJoined(BedwarsPlayerJoinedEvent e) {
		Game game = e.getGame();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		me.ram.bedwarsscoreboardaddon.config.ArenaConfig arenaConfig = (arena != null) ? arena.getArenaConfig() : new me.ram.bedwarsscoreboardaddon.config.ArenaConfig(game);
		for (Player player : e.getGame().getPlayers()) {
			if (player.getName().contains(",") || player.getName().contains("[") || player.getName().contains("]")) {
				player.kickPlayer("");
			}
			if (!(e.getGame().getState() != GameState.WAITING && e.getGame().getState() == GameState.RUNNING)) {
				if (arenaConfig.getBoolean("jointitle.enabled", Config.jointitle_enabled)) {
					Utils.sendTitle(player, e.getPlayer(), 5, 50, 5, arenaConfig.getString("jointitle.title", Config.jointitle_title).replace("{player}", e.getPlayer().getName()), arenaConfig.getString("jointitle.subtitle", Config.jointitle_subtitle).replace("{player}", e.getPlayer().getName()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamageTitle(EntityDamageByEntityEvent e) {
		if (e.isCancelled() || !(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
			return;
		}
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer((Player) e.getDamager());
		if (game == null || game.getState() != GameState.RUNNING) {
			return;
		}
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		me.ram.bedwarsscoreboardaddon.config.ArenaConfig arenaConfig = (arena != null) ? arena.getArenaConfig() : new me.ram.bedwarsscoreboardaddon.config.ArenaConfig(game);
		if (!arenaConfig.getBoolean("damagetitle.enabled", Config.damagetitle_enabled)) {
			return;
		}
		if (!(game.getPlayers().contains((Player) e.getDamager()) && game.getPlayers().contains((Player) e.getEntity()))) {
			return;
		}
		Player player = (Player) e.getEntity();
		Player damager = (Player) e.getDamager();
		if (BedwarsUtil.isSpectator(game, damager) || BedwarsUtil.isSpectator(game, player)) {
			return;
		}
		String title = arenaConfig.getString("damagetitle.title", Config.damagetitle_title);
		String subtitle = arenaConfig.getString("damagetitle.subtitle", Config.damagetitle_subtitle);
		if (!title.equals("") || !subtitle.equals("")) {
			DecimalFormat df = new DecimalFormat("0.00");
			DecimalFormat df2 = new DecimalFormat("#");
			double health = player.getHealth() - e.getFinalDamage();
			health = health < 0 ? 0 : health;
			Utils.sendTitle((Player) e.getDamager(), player, 0, 20, 0, title.replace("{player}", player.getName()).replace("{damage}", df.format(e.getDamage())).replace("{health}", df2.format(health)).replace("{maxhealth}", df2.format(player.getMaxHealth())), subtitle.replace("{player}", player.getName()).replace("{damage}", df.format(e.getDamage())).replace("{health}", df2.format(health)).replace("{maxhealth}", df2.format(player.getMaxHealth())));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBowDamage(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (!(e.getDamager() instanceof Arrow) || !(e.getEntity() instanceof Player)) {
			return;
		}
		Arrow arrow = (Arrow) e.getDamager();
		if (!(arrow.getShooter() instanceof Player)) {
			return;
		}
		Player shooter = (Player) arrow.getShooter();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(shooter);
		if (game == null) {
			return;
		}
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		me.ram.bedwarsscoreboardaddon.config.ArenaConfig arenaConfig = (arena != null) ? arena.getArenaConfig() : new me.ram.bedwarsscoreboardaddon.config.ArenaConfig(game);
		if (!arenaConfig.getBoolean("bowdamage.enabled", Config.bowdamage_enabled)) {
			return;
		}
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		Player player = (Player) e.getEntity();
		Integer damage = (int) e.getFinalDamage();
		if (game.getPlayerTeam(shooter) == game.getPlayerTeam(player)) {
			e.setCancelled(true);
		}
		if (player.isDead()) {
			return;
		}
		double health = player.getHealth() - e.getFinalDamage();
		health = health < 0 ? 0 : health;
		DecimalFormat df = new DecimalFormat("#");
		String title = arenaConfig.getString("bowdamage.title", Config.bowdamage_title);
		String subtitle = arenaConfig.getString("bowdamage.subtitle", Config.bowdamage_subtitle);
		if (!title.equals("") || !subtitle.equals("")) {
			Utils.sendTitle(shooter, player, 0, 20, 0, title.replace("{player}", player.getName()).replace("{damage}", damage + "").replace("{health}", df.format(health)).replace("{maxhealth}", df.format(player.getMaxHealth())), subtitle.replace("{player}", player.getName()).replace("{damage}", damage + "").replace("{health}", df.format(health)).replace("{maxhealth}", df.format(player.getMaxHealth())));
		}
		String message = arenaConfig.getString("bowdamage.message", Config.bowdamage_message);
		if (!message.equals("")) {
			Utils.sendMessage(shooter, player, message.replace("{player}", player.getName()).replace("{damage}", damage + "").replace("{health}", df.format(health)).replace("{maxhealth}", df.format(player.getMaxHealth())));
		}
	}
}

