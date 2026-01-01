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
import me.ram.bedwarsscoreboardaddon.api.ConfigAPI;
import me.ram.bedwarsscoreboardaddon.api.GameConfig;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class AlarmTrap implements Upgrade {

	@Getter
	private Game game;
	@Getter
	private Team team;
	@Getter
	@Setter
	private int level;
	@Getter
	@Setter
	private String buyer;

	public AlarmTrap(Game game, Team team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
	}

	public UpgradeType getType() {
		return UpgradeType.ALARM_TRAP;
	}

	public String getName() {
		GameConfig config = ConfigAPI.getGameConfig(game.getName());
		return config.teamshop_upgrade_name.get("ALARM_TRAP");
	}

	public void runUpgrade() {
		if (level < 1) {
			return;
		}
		GameConfig config = ConfigAPI.getGameConfig(game.getName());
		TeamShop teamShop = Main.getInstance().getArenaManager().getArena(game.getName()).getTeamShop();
		for (Player player : game.getPlayers()) {
			if (BedwarsUtil.isSpectator(game, player) || player.getGameMode() == GameMode.SPECTATOR) {
				continue;
			}
			if (team != game.getPlayerTeam(player) && team.getTargetFeetBlock().distanceSquared(player.getLocation()) <= Math.pow(config.teamshop_upgrade_alarm_trap_trigger_range, 2) && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				if (teamShop.isCoolingPlayer(team, player) || teamShop.isImmunePlayer(player)) {
					continue;
				}
				level = 0;
				teamShop.removeTrap(this);
				teamShop.addCoolingPlayer(team, player);
				if (Config.invisibility_player_damage_show_player) {
					Main.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer().removePlayer(player);
				} else {
					Main.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer().showPlayerArmor(player);
				}
				if (!config.teamshop_upgrade_alarm_trap_trigger_title.equals("") || !config.teamshop_upgrade_alarm_trap_trigger_subtitle.equals("")) {
					Team t = game.getPlayerTeam(player);
					for (Player teamplayers : team.getPlayers()) {
						Utils.sendTitle(teamplayers, 5, 80, 5, config.teamshop_upgrade_alarm_trap_trigger_title.replace("{player}", player.getName()).replace("{team}", t.getName()).replace("{team_color}", team.getChatColor().toString()), config.teamshop_upgrade_alarm_trap_trigger_subtitle.replace("{player}", player.getName()).replace("{team}", t.getName()).replace("{team_color}", team.getChatColor().toString()));
					}
				}
				if (team.getPlayers().size() > 0) {
					Main.getInstance().getArenaManager().getArena(game.getName()).getTeamShop().updateTeamShop(team.getPlayers().get(0));
				}
				break;
			}
		}
	}
}
