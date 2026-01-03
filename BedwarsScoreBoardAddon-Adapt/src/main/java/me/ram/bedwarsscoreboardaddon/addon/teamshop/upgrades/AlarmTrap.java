package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import lombok.Setter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.TeamShop;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class AlarmTrap implements Upgrade {

	private Game game;

	public Game getGame() {
		return game;
	}

	private Team team;

	public Team getTeam() {
		return team;
	}

	private int level;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	private String buyer;

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public AlarmTrap(Game game, Team team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
	}

	public UpgradeType getType() {
		return UpgradeType.ALARM_TRAP;
	}

	public String getName() {
		return Config.teamshop_upgrade_name.get(getType());
	}

	public void runUpgrade() {
		if (level < 1) {
			return;
		}
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		TeamShop teamShop = arena.getTeamShop();
		for (Player player : game.getPlayers()) {
			if (BedwarsUtil.isSpectator(game, player) || player.getGameMode() == GameMode.SPECTATOR) {
				continue;
			}
			if (team != game.getPlayerTeam(player)
					&& team.getTargetFeetBlock().distanceSquared(player.getLocation()) <= Math
							.pow(arena.getArenaConfig().getInt("teamshop.upgrade_alarm_trap_trigger_range", 5), 2)
					&& player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				if (teamShop.isCoolingPlayer(team, player) || teamShop.isImmunePlayer(player)) {
					continue;
				}
				level = 0;
				teamShop.removeTrap(this);
				teamShop.addCoolingPlayer(team, player);
				if (arena.getArenaConfig().getBoolean("invisibility_player_damage_show_player", false)) {
					arena.getInvisiblePlayer().removePlayer(player);
				} else {
					arena.getInvisiblePlayer().showPlayerArmor(player);
				}
				String title = arena.getArenaConfig().getString("teamshop.upgrade_alarm_trap_trigger_title", "");
				String subtitle = arena.getArenaConfig().getString("teamshop.upgrade_alarm_trap_trigger_subtitle", "");
				if (!title.equals("") || !subtitle.equals("")) {
					Team t = game.getPlayerTeam(player);
					for (Player teamplayers : team.getPlayers()) {
						Utils.sendTitle(teamplayers, 5, 80, 5,
								title.replace("{player}", player.getName()).replace("{team}", t.getName())
										.replace("{team_color}", team.getChatColor().toString()),
								subtitle.replace("{player}", player.getName()).replace("{team}", t.getName())
										.replace("{team_color}", team.getChatColor().toString()));
					}
				}
				if (team.getPlayers().size() > 0) {
					teamShop.updateTeamShop(team.getPlayers().get(0));
				}
				break;
			}
		}
	}
}
