package me.ram.bedwarsscoreboardaddon.addon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.api.HolographicAPI;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.config.ArenaConfig;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class Holographic {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	private List<HolographicAPI> ablocks;
	private List<HolographicAPI> atitles;
	private List<HolographicAPI> btitles;
	private Map<String, HolographicAPI> pbtitles;
	private ResourceUpgrade resourceupgrade;
	private HashMap<HolographicAPI, Location> armor_locations;
	private HashMap<HolographicAPI, Boolean> armor_upward;
	private HashMap<HolographicAPI, Integer> armor_algebra;

	public Holographic(Arena arena, ResourceUpgrade resourceupgrade) {
		this.arena = arena;
		this.game = arena.getGame();
		ablocks = new ArrayList<HolographicAPI>();
		atitles = new ArrayList<HolographicAPI>();
		btitles = new ArrayList<HolographicAPI>();
		pbtitles = new HashMap<String, HolographicAPI>();
		armor_locations = new HashMap<HolographicAPI, Location>();
		armor_upward = new HashMap<HolographicAPI, Boolean>();
		armor_algebra = new HashMap<HolographicAPI, Integer>();
		this.resourceupgrade = resourceupgrade;
		armor_algebra = new HashMap<HolographicAPI, Integer>();
		this.resourceupgrade = resourceupgrade;
		ArenaConfig arenaConfig = arena.getArenaConfig();
		if (arenaConfig.getBoolean("holographic.resource.enabled", Config.holographic_resource_enabled)) {
			for (String r : arenaConfig.getStringList("holographic.resource.list")) {
				if (r == null && Config.holographic_resource != null) {
					// Fallback to global if list is missing in per-game config but present in
					// global
					// However, getStringList returns empty list if not found, not null.
					// So we might need to check if empty and fallback.
				}
				if (arenaConfig.getStringList("holographic.resource.list").isEmpty()) {
					for (String gr : Config.holographic_resource) {
						this.setArmorStand(game, gr);
					}
				} else {
					this.setArmorStand(game, r);
				}
			}
		}
		if (arenaConfig.getBoolean("holographic.bed_title.bed_alive.enabled",
				Config.holographic_bed_title_bed_alive_enabled)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Team team : game.getTeams().values()) {
						Location location = team.getTargetHeadBlock().clone().add(0.5, 0, 0.5);
						HolographicAPI holo = new HolographicAPI(location.clone().add(0, -1.25, 0),
								arenaConfig.getString("holographic.bed_title.bed_alive.title",
										Config.holographic_bedtitle_bed_alive_title));
						for (Player player : team.getPlayers()) {
							holo.display(player);
						}
						pbtitles.put(team.getName(), holo);
						arena.addGameTask(new BukkitRunnable() {
							@Override
							public void run() {
								if (team.isDead(game)) {
									cancel();
									holo.remove();
								}
							}
						}.runTaskTimer(Main.getInstance(), 1L, 1L));
					}
				}
			}.runTaskLater(Main.getInstance(), 20L);
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				if (game.getState() != GameState.RUNNING || game.getPlayers().size() < 1) {
					cancel();
					for (HolographicAPI holo : ablocks) {
						holo.remove();
					}
					for (HolographicAPI holo : atitles) {
						holo.remove();
					}
					for (HolographicAPI holo : btitles) {
						holo.remove();
					}
					for (HolographicAPI holo : pbtitles.values()) {
						holo.remove();
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 1L, 1L);
	}

	public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {
		Game game = e.getGame();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		ArenaConfig arenaConfig = arena.getArenaConfig();
		if (arenaConfig.getBoolean("holographic.bed_title.bed_destroyed.enabled",
				Config.holographic_bed_title_bed_destroyed_enabled)) {
			Team team = e.getTeam();
			if (pbtitles.containsKey(team.getName())) {
				pbtitles.get(team.getName()).remove();
			}
			Location loc = team.getTargetHeadBlock().clone().add(0, -1, 0);
			if (loc.getX() == loc.getBlock().getLocation().getX()) {
				loc.add(0.5, 0, 0);
			}
			if (loc.getZ() == loc.getBlock().getLocation().getZ()) {
				loc.add(0, 0, 0.5);
			}
			if (!loc.getBlock().getChunk().isLoaded()) {
				loc.getBlock().getChunk().load(true);
			}
			HolographicAPI holo = new HolographicAPI(loc, arenaConfig
					.getString("holographic.bed_title.bed_destroyed.title",
							Config.holographic_bedtitle_bed_destroyed_title)
					.replace("{player}", game.getPlayerTeam(e.getPlayer()).getChatColor() + e.getPlayer().getName()));
			btitles.add(holo);
			for (Player player : game.getPlayers()) {
				holo.display(player);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					if (game.getState() == GameState.RUNNING) {
						if (!holo.getLocation().getBlock().getChunk().isLoaded()) {
							holo.getLocation().getBlock().getChunk().load(true);
						}
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 0L, 0L);
		}
	}

	private void setArmorStand(Game game, String res) {
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		ArenaConfig arenaConfig = arena.getArenaConfig();
		for (ResourceSpawner spawner : game.getResourceSpawners()) {
			for (ItemStack itemStack : spawner.getResources()) {
				if (itemStack.getType() == Material.getMaterial(
						arenaConfig.getConfig().getInt("holographic.resource.resources." + res + ".item"))) {
					if (!spawner.getLocation().getBlock().getChunk().isLoaded()) {
						spawner.getLocation().getBlock().getChunk().load(true);
					}
					HolographicAPI holo = new HolographicAPI(
							spawner.getLocation().clone().add(0,
									arenaConfig.getConfig()
											.getDouble("holographic.resource.resources." + res + ".height") - 0.35,
									0),
							null);
					holo.setEquipment(Arrays.asList(new ItemStack(Material
							.getMaterial(arenaConfig.getConfig()
									.getInt("holographic.resource.resources." + res + ".block")))));
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
						for (Player player : game.getPlayers()) {
							holo.display(player);
						}
					}, 20L);
					ArrayList<String> titles = new ArrayList<String>();
					for (String title : arenaConfig.getStringList("holographic.resource.resources." + res + ".title")) {
						titles.add(ColorUtil.color(title));
					}
					this.setArmorStandRun(game, holo, titles, itemStack);
				}
			}
		}
	}

	private void setArmorStandRun(Game game, HolographicAPI holo, ArrayList<String> titles, ItemStack itemStack) {
		Collections.reverse(titles);
		Location aslocation = holo.getLocation().clone().add(0, 0.5, 0);
		for (String title : titles) {
			this.setTitle(game, aslocation, title, itemStack);
			aslocation.add(0, 0.375, 0);
		}
		ablocks.add(holo);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() == GameState.RUNNING) {
					if (!holo.getLocation().getBlock().getChunk().isLoaded()) {
						holo.getLocation().getBlock().getChunk().load(true);
					}
					moveArmorStand(holo, game);
				} else {
					armor_locations.remove(holo);
					armor_upward.remove(holo);
					armor_algebra.remove(holo);
					holo.remove();
					ablocks.remove(holo);
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 1L, 1L);
	}

	private void setTitle(Game game, Location location, String title, ItemStack itemStack) {
		if (!location.getBlock().getChunk().isLoaded()) {
			location.getBlock().getChunk().load(true);
		}
		HolographicAPI holo = new HolographicAPI(location, " ");
		atitles.add(holo);
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			for (Player player : game.getPlayers()) {
				holo.display(player);
			}
		}, 20L);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() == GameState.RUNNING) {
					if (!holo.getLocation().getBlock().getChunk().isLoaded()) {
						holo.getLocation().getBlock().getChunk().load(true);
					}
					String customName = title;
					customName = customName.replace("{level}", resourceupgrade.getLevel().get(itemStack.getType()));
					for (Material sitem : resourceupgrade.getSpawnTime().keySet()) {
						if (itemStack.getType() == sitem) {
							customName = customName.replace("{generate_time}",
									resourceupgrade.getSpawnTime().get(sitem) + "");
						}
					}
					holo.setTitle(customName);
				} else {
					holo.remove();
					atitles.remove(holo);
					cancel();
					return;
				}
			}
		}.runTaskTimer(Main.getInstance(), 5L, 5L);
	}

	public void onPlayerLeave(Player player) {
		if (player.isOnline()) {
			for (HolographicAPI holo : pbtitles.values()) {
				holo.destroy(player);
			}
		}
	}

	public void onPlayerJoin(Player player) {
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() == GameState.RUNNING && player.isOnline() && game.getPlayers().contains(player)) {
					for (HolographicAPI holo : ablocks) {
						holo.display(player);
					}
					for (HolographicAPI holo : atitles) {
						holo.display(player);
					}
					for (HolographicAPI holo : btitles) {
						holo.display(player);
					}
					Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
					if (arena.getRejoin().getPlayers().containsKey(player.getName())) {
						Team team = game.getPlayerTeam(player);
						if (team != null && !team.isDead(game)) {
							pbtitles.get(team.getName()).display(player);
						}
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 10L);
	}

	public void remove() {
		for (HolographicAPI holo : ablocks) {
			holo.remove();
		}
		for (HolographicAPI holo : atitles) {
			holo.remove();
		}
		for (HolographicAPI holo : btitles) {
			holo.remove();
		}
		for (HolographicAPI holo : pbtitles.values()) {
			holo.remove();
		}
	}

	public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
		Player player = e.getPlayer();
		Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (getGame == null) {
			return;
		}
		if (!getGame.getPlayers().contains(player)) {
			return;
		}
		if (getGame.getState() != GameState.WAITING && getGame.getState() == GameState.RUNNING) {
			e.setCancelled(true);
		}
	}

	private void moveArmorStand(HolographicAPI holo, Game game) {
		if (!armor_locations.containsKey(holo)) {
			armor_locations.put(holo, holo.getLocation().clone());
		}
		if (!armor_upward.containsKey(holo)) {
			armor_upward.put(holo, true);
		}
		if (!armor_algebra.containsKey(holo)) {
			armor_algebra.put(holo, 0);
		}
		Location location = armor_locations.get(holo);
		Integer algebra = armor_algebra.get(holo);
		boolean upward = armor_upward.get(holo);
		double turn = 1;
		if (!armor_upward.get(holo)) {
			turn = -turn;
		}
		double move_yaw = 0;
		double move_y = 0;
		if (algebra <= 30) {
			move_yaw += algebra * 0.62 * turn;
		} else {
			move_yaw += (59 - algebra) * 0.62 * turn;
		}
		if (algebra >= 9 && algebra <= 50) {
			move_y += 0.01125 * turn;
		}
		location.setY(location.getY() + move_y);
		if (algebra >= 59) {
			armor_algebra.put(holo, 0);
			armor_upward.put(holo, !upward);
		}
		armor_algebra.put(holo, armor_algebra.get(holo) + 1);
		double yaw = location.getYaw();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		ArenaConfig arenaConfig = arena.getArenaConfig();
		yaw += (move_yaw * arenaConfig.getDouble("holographic.resource.speed", Config.holographic_resource_speed));
		yaw = yaw > 360 ? (yaw - 360) : yaw;
		yaw = yaw < -360 ? (yaw + 360) : yaw;
		location.setYaw((float) yaw);
		holo.teleport(location);
	}
}
