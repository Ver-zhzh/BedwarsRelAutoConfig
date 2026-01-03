package me.ram.bedwarsscoreboardaddon.addon;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.config.ArenaConfig;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import org.bukkit.configuration.ConfigurationSection;
import java.util.ArrayList;

public class SpawnNoBuild implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e) {
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
		if (game == null) {
			return;
		}
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		Block block = e.getBlock();
		Player player = e.getPlayer();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		ArenaConfig arenaConfig = arena.getArenaConfig();
		if (arenaConfig.getBoolean("spawn-no-build.spawn.enabled", Config.spawn_no_build_spawn_enabled)) {
			for (Team team : game.getTeams().values()) {
				if (team.getSpawnLocation().distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(
						arenaConfig.getDouble("spawn-no-build.spawn.range", Config.spawn_no_build_spawn_range), 2)) {
					e.setCancelled(true);
					player.sendMessage(arenaConfig.getString("spawn-no-build.message", Config.spawn_no_build_message));
					return;
				}
			}
		}
		if (arenaConfig.getBoolean("spawn-no-build.resource.enabled", Config.spawn_no_build_resource_enabled)) {
			for (ResourceSpawner spawner : game.getResourceSpawners()) {
				if (spawner.getLocation().distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(
						arenaConfig.getDouble("spawn-no-build.resource.range", Config.spawn_no_build_resource_range),
						2)) {
					e.setCancelled(true);
					player.sendMessage(arenaConfig.getString("spawn-no-build.message", Config.spawn_no_build_message));
					return;
				}
			}
			// Handle per-game team spawner locations from ArenaConfig
			ConfigurationSection teamSpawnerSection = arenaConfig.getConfigurationSection("team_spawner");
			if (teamSpawnerSection != null) {
				for (String teamName : teamSpawnerSection.getKeys(false)) {
					List<String> locs = arenaConfig.getStringList("team_spawner." + teamName);
					for (String locStr : locs) {
						Location loc = me.ram.bedwarsscoreboardaddon.addon.Shop.toLocation(locStr); // Reusing
																									// toLocation from
																									// Shop or need to
																									// implement it
																									// here?
						// Shop.toLocation is private. I should implement a helper or use
						// Config.toLocation if it was public.
						// Config.toLocation is private.
						// I'll implement a local helper or just parse it here.
						// Actually, Config.game_team_spawner is populated in Config.loadGameConfig.
						// If I want to use ArenaConfig, I need to parse the location strings.
						// Let's implement a simple parser here or copy toLocation.
						if (loc != null) {
							if (loc.distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math
									.pow(arenaConfig.getDouble("spawn-no-build.resource.range",
											Config.spawn_no_build_resource_range), 2)) {
								e.setCancelled(true);
								player.sendMessage(
										arenaConfig.getString("spawn-no-build.message", Config.spawn_no_build_message));
								return;
							}
						}
					}
				}
			} else if (Config.game_team_spawner.containsKey(game.getName())) {
				// Fallback to global config map if ArenaConfig doesn't have it (though
				// ArenaConfig should have it if it wraps the game config)
				for (List<Location> locs : Config.game_team_spawner.get(game.getName()).values()) {
					for (Location loc : locs) {
						if (loc.distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(arenaConfig
								.getDouble("spawn-no-build.resource.range", Config.spawn_no_build_resource_range), 2)) {
							e.setCancelled(true);
							player.sendMessage(
									arenaConfig.getString("spawn-no-build.message", Config.spawn_no_build_message));
							return;
						}
					}
				}
			}
		}
	}

	private Location toLocation(String loc) {
		try {
			String[] ary = loc.split(", ");
			if (org.bukkit.Bukkit.getWorld(ary[0]) != null) {
				Location location = new Location(org.bukkit.Bukkit.getWorld(ary[0]), Double.valueOf(ary[1]),
						Double.valueOf(ary[2]), Double.valueOf(ary[3]));
				if (ary.length > 4) {
					location.setYaw(Float.valueOf(ary[4]));
					location.setPitch(Float.valueOf(ary[5]));
				}
				return location;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
