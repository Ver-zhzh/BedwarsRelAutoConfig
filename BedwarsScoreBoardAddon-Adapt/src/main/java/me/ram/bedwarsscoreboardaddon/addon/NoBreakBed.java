package me.ram.bedwarsscoreboardaddon.addon;

import com.comphenix.protocol.*;
import com.comphenix.protocol.wrappers.*;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.config.ArenaConfig;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.comphenix.protocol.events.*;

public class NoBreakBed {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	private boolean bre;
	private String formattime = "00:00";
	private PacketListener packetListener;

	public NoBreakBed(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		this.game = arena.getGame();
		bre = false;
		ArenaConfig arenaConfig = arena.getArenaConfig();
		if (!arenaConfig.getBoolean("nobreakbed.enabled", Config.nobreakbed_enabled)) {
			return;
		}
		registerPacketListener();
		arena.addGameTask(new BukkitRunnable() {
			@Override
			public void run() {

				ArenaConfig arenaConfig = arena.getArenaConfig();
				int time = game.getTimeLeft() - arenaConfig.getInt("nobreakbed.gametime", Config.nobreakbed_gametime);
				String ftime = time / 60 + ":" + ((time % 60 < 10) ? ("0" + time % 60) : (time % 60));
				formattime = ftime;
				if (game.getTimeLeft() <= arenaConfig.getInt("nobreakbed.gametime", Config.nobreakbed_gametime)) {
					bre = true;
					if (arenaConfig.getBoolean("nobreakbed.enabled", Config.nobreakbed_enabled)) {
						for (Player player : game.getPlayers()) {
							String title = arenaConfig.getString("nobreakbed.title", Config.nobreakbed_title);
							String subtitle = arenaConfig.getString("nobreakbed.subtitle", Config.nobreakbed_subtitle);
							if (!title.equals("") || !subtitle.equals("")) {
								Utils.sendTitle(player, 10, 50, 10, title, subtitle);
							}
							String msg = arenaConfig.getString("nobreakbed.message", Config.nobreakbed_message);
							if (!msg.equals("")) {
								player.sendMessage(msg);
							}
						}
					}
					cancel();
					return;
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 21L));
	}

	public String getTime() {
		return formattime;
	}

	public void onEnd() {
		if (packetListener != null) {
			ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
		}
	}

	private void registerPacketListener() {
		packetListener = new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST,
				new PacketType[] { PacketType.Play.Client.BLOCK_DIG }) {
			public void onPacketReceiving(PacketEvent e) {
				ArenaConfig arenaConfig = arena.getArenaConfig();
				if (!arenaConfig.getBoolean("nobreakbed.enabled", Config.nobreakbed_enabled)) {
					return;
				}
				Player player = e.getPlayer();
				if (!game.getPlayers().contains(player)) {
					return;
				}
				if (BedwarsUtil.isSpectator(game, player) || game.getState() != GameState.RUNNING) {
					return;
				}
				if (!bre && e.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
					PacketContainer packet = e.getPacket();
					BlockPosition position = packet.getBlockPositionModifier().read(0);
					Block block = new Location(player.getWorld(), position.getX(), position.getY(), position.getZ())
							.getBlock();
					if (!block.getType().equals(game.getTargetMaterial())) {
						return;
					}
					if (!packet.getPlayerDigTypes().read(0).equals(EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK)) {
						return;
					}
					player.sendMessage(
							arenaConfig.getString("nobreakbed.nobreakmessage", Config.nobreakbed_nobreakmessage));
					e.setCancelled(true);
					block.getState().update();
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
	}
}
