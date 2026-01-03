package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

public class Heal implements Upgrade {

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

	private int i;

	public Heal(Game game, Team team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
		i = 0;
	}

	public UpgradeType getType() {
		return UpgradeType.HEAL;
	}

	public String getName() {
		return Config.teamshop_upgrade_name.get(getType());
	}

	public void runUpgrade() {
		if (level < 1) {
			return;
		}
		for (Player player : team.getPlayers()) {
			if (!BedwarsUtil.isSpectator(game, player) && player.getGameMode() != GameMode.SPECTATOR) {
				if (team.getTargetFeetBlock()
						.distance(player.getLocation()) <= Config.teamshop_upgrade_heal_trigger_range) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, 1), true);
				}
			}
		}
		if (i >= 6) {
			i = 0;
			Location location = team.getTargetHeadBlock().clone().add(0.5, 1, 0.5);
			for (Player player : team.getPlayers()) {
				for (int i = 0; i < 10; i++) {
					player.playEffect(
							location.clone().add(
									(Math.random() - Math.random()) * Config.teamshop_upgrade_heal_trigger_range,
									(Math.random() - Math.random()) * Config.teamshop_upgrade_heal_trigger_range,
									(Math.random() - Math.random()) * Config.teamshop_upgrade_heal_trigger_range),
							Effect.HAPPY_VILLAGER, 0);
				}
			}
		}
		i++;
	}
}
