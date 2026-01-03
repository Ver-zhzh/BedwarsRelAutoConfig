package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import lombok.Setter;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

public class FastDig implements Upgrade {

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

	public FastDig(Game game, Team team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
	}

	public UpgradeType getType() {
		return UpgradeType.FAST_DIG;
	}

	public String getName() {
		return Config.teamshop_upgrade_name.get(getType());
	}

	public void runUpgrade() {
		if (level < 1) {
			return;
		}
		for (Player p : team.getPlayers()) {
			if (!BedwarsUtil.isSpectator(game, p)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 30, getLevel() - 1), true);
			}
		}
	}
}
