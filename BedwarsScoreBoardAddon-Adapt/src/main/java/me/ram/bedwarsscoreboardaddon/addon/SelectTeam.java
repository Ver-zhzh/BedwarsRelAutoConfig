package me.ram.bedwarsscoreboardaddon.addon;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.config.ArenaConfig;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;

public class SelectTeam {

	public static void openSelectTeam(Game game, Player player) {
		int size = 27 + (9 * (game.getTeams().values().size() / 7));
		Inventory inventory = Bukkit.createInventory(null, size, BedwarsRel._l(player, "lobby.chooseteam"));
		int slot = 10;
		for (Team team : game.getTeams().values()) {
			switch (slot) {
				case 17:
					slot = 19;
					break;
				case 26:
					slot = 28;
					break;
				default:
					break;
			}
			Wool wool = new Wool(team.getColor().getDyeColor());
			ItemStack itemStack = wool.toItemStack(1);
			ItemMeta itemMeta = itemStack.getItemMeta();
			String color = team.getChatColor().toString();
			Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
			ArenaConfig arenaConfig = arena.getArenaConfig();
			String status = arenaConfig.getString("select-team.status.select", Config.select_team_status_select);
			if (team.getPlayers().contains(player)) {
				status = arenaConfig.getString("select-team.status.inteam", Config.select_team_status_inteam);
			} else if (team.getPlayers().size() >= team.getMaxPlayers()) {
				status = arenaConfig.getString("select-team.status.team-full", Config.select_team_status_team_full);
			}
			itemMeta.setDisplayName(arenaConfig.getString("select-team.item.name", Config.select_team_item_name)
					.replace("{status}", status).replace("{team}", team.getName()).replace("{color}", color)
					.replace("{players}", team.getPlayers().size() + "")
					.replace("{maxplayers}", team.getMaxPlayers() + ""));
			List<String> lore = new ArrayList<String>();
			for (String l : arenaConfig.getStringList("select-team.item.lore")) {
				if (l.contains("{players_list}")) {
					if (team.getPlayers().size() > 0) {
						for (Player p : team.getPlayers()) {
							lore.add(l.replace("{status}", status).replace("{team}", team.getName())
									.replace("{color}", color).replace("{players}", team.getPlayers().size() + "")
									.replace("{maxplayers}", team.getMaxPlayers() + "")
									.replace("{players_list}", p.getDisplayName()));
						}
					} else {
						lore.add(l.replace("{status}", status).replace("{team}", team.getName())
								.replace("{color}", color).replace("{players}", team.getPlayers().size() + "")
								.replace("{maxplayers}", team.getMaxPlayers() + "")
								.replace("{players_list}", arenaConfig.getString("select-team.no-players",
										Config.select_team_no_players)));
					}
				} else {
					lore.add(l.replace("{status}", status).replace("{team}", team.getName()).replace("{color}", color)
							.replace("{players}", team.getPlayers().size() + "")
							.replace("{maxplayers}", team.getMaxPlayers() + ""));
				}
			}
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
			inventory.setItem(slot, itemStack);
			slot++;
		}
		player.closeInventory();
		player.openInventory(inventory);
	}
}
