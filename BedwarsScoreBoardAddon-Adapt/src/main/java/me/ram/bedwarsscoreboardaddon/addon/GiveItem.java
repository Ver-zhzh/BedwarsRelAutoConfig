package me.ram.bedwarsscoreboardaddon.addon;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.config.ArenaConfig;
import org.bukkit.configuration.ConfigurationSection;

public class GiveItem implements Listener {

	@EventHandler
	public void onStarted(BedwarsGameStartedEvent e) {
		Arena arena = Main.getInstance().getArenaManager().getArena(e.getGame().getName());
		arena.addGameTask(Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			for (Player player : e.getGame().getPlayers()) {
				Team team = e.getGame().getPlayerTeam(player);
				GiveItem.giveItem(player, team, false, arena.getArenaConfig());
			}
		}, 5L));
	}

	public static void giveItem(Player player, Team team, boolean respawn, ArenaConfig arenaConfig) {
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Object> map3 = new HashMap<String, Object>();
		Map<String, Object> map4 = new HashMap<String, Object>();

		ConfigurationSection helmetSection = arenaConfig.getConfig()
				.getConfigurationSection("giveitem.armor.helmet.item");
		Map<String, Object> helmetConfig = helmetSection != null ? helmetSection.getValues(false)
				: Config.giveitem_armor_helmet_item;

		ConfigurationSection chestplateSection = arenaConfig.getConfig()
				.getConfigurationSection("giveitem.armor.chestplate.item");
		Map<String, Object> chestplateConfig = chestplateSection != null ? chestplateSection.getValues(false)
				: Config.giveitem_armor_chestplate_item;

		ConfigurationSection leggingsSection = arenaConfig.getConfig()
				.getConfigurationSection("giveitem.armor.leggings.item");
		Map<String, Object> leggingsConfig = leggingsSection != null ? leggingsSection.getValues(false)
				: Config.giveitem_armor_leggings_item;

		ConfigurationSection bootsSection = arenaConfig.getConfig()
				.getConfigurationSection("giveitem.armor.boots.item");
		Map<String, Object> bootsConfig = bootsSection != null ? bootsSection.getValues(false)
				: Config.giveitem_armor_boots_item;

		for (String str : helmetConfig.keySet()) {
			if (str.equals("type")) {
				if (helmetConfig.get(str).equals("TEAM_ARMOR")) {
					map1.put(str, "LEATHER_HELMET");
				} else {
					map1.put(str, helmetConfig.get(str));
				}
			} else {
				map1.put(str, helmetConfig.get(str));
			}
		}
		for (String str : chestplateConfig.keySet()) {
			if (str.equals("type")) {
				if (chestplateConfig.get(str).equals("TEAM_ARMOR")) {
					map2.put(str, "LEATHER_CHESTPLATE");
				} else {
					map2.put(str, chestplateConfig.get(str));
				}
			} else {
				map2.put(str, chestplateConfig.get(str));
			}
		}
		for (String str : leggingsConfig.keySet()) {
			if (str.equals("type")) {
				if (leggingsConfig.get(str).equals("TEAM_ARMOR")) {
					map3.put(str, "LEATHER_LEGGINGS");
				} else {
					map3.put(str, leggingsConfig.get(str));
				}
			} else {
				map3.put(str, leggingsConfig.get(str));
			}
		}
		for (String str : bootsConfig.keySet()) {
			if (str.equals("type")) {
				if (bootsConfig.get(str).equals("TEAM_ARMOR")) {
					map4.put(str, "LEATHER_BOOTS");
				} else {
					map4.put(str, bootsConfig.get(str));
				}
			} else {
				map4.put(str, bootsConfig.get(str));
			}
		}
		ItemStack helmet = null;
		ItemStack chestplate = null;
		ItemStack leggings = null;
		ItemStack boots = null;
		try {
			helmet = ItemStack.deserialize(map1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			chestplate = ItemStack.deserialize(map2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			leggings = ItemStack.deserialize(map3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			boots = ItemStack.deserialize(map4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (helmet != null && helmetConfig.get("type").equals("TEAM_ARMOR")) {
			LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
			meta.setColor(team.getColor().getColor());
			helmet.setItemMeta((ItemMeta) meta);
		}
		if (chestplate != null && chestplateConfig.get("type").equals("TEAM_ARMOR")) {
			LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
			meta.setColor(team.getColor().getColor());
			chestplate.setItemMeta((ItemMeta) meta);
		}
		if (leggings != null && leggingsConfig.get("type").equals("TEAM_ARMOR")) {
			LeatherArmorMeta meta = (LeatherArmorMeta) leggings.getItemMeta();
			meta.setColor(team.getColor().getColor());
			leggings.setItemMeta((ItemMeta) meta);
		}
		if (boots != null && bootsConfig.get("type").equals("TEAM_ARMOR")) {
			LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
			meta.setColor(team.getColor().getColor());
			boots.setItemMeta((ItemMeta) meta);
		}

		String helmetGive = arenaConfig.getString("giveitem.armor.helmet.give", Config.giveitem_armor_helmet_give);
		if (helmetGive.equalsIgnoreCase("true")
				|| (helmetGive.equalsIgnoreCase("start") && !respawn)
				|| (helmetGive.equalsIgnoreCase("respawn") && respawn)) {
			player.getInventory().setHelmet(helmet);
		}

		String chestplateGive = arenaConfig.getString("giveitem.armor.chestplate.give",
				Config.giveitem_armor_chestplate_give);
		if (chestplateGive.equalsIgnoreCase("true")
				|| (chestplateGive.equalsIgnoreCase("start") && !respawn)
				|| (chestplateGive.equalsIgnoreCase("respawn") && respawn)) {
			player.getInventory().setChestplate(chestplate);
		}

		String leggingsGive = arenaConfig.getString("giveitem.armor.leggings.give",
				Config.giveitem_armor_leggings_give);
		if (leggingsGive.equalsIgnoreCase("true")
				|| (leggingsGive.equalsIgnoreCase("start") && !respawn)
				|| (leggingsGive.equalsIgnoreCase("respawn") && respawn)) {
			player.getInventory().setLeggings(leggings);
		}

		String bootsGive = arenaConfig.getString("giveitem.armor.boots.give", Config.giveitem_armor_boots_give);
		if (bootsGive.equalsIgnoreCase("true")
				|| (bootsGive.equalsIgnoreCase("start") && !respawn)
				|| (bootsGive.equalsIgnoreCase("respawn") && respawn)) {
			player.getInventory().setBoots(boots);
		}

		ConfigurationSection giveItemsSection = arenaConfig.getConfig().getConfigurationSection("giveitem.item");
		if (giveItemsSection != null) {
			for (String items : giveItemsSection.getKeys(false)) {
				String give_option = arenaConfig.getString("giveitem.item." + items + ".give", "true");
				int slot = arenaConfig.getInt("giveitem.item." + items + ".slot", 0);
				if (give_option.equalsIgnoreCase("true") || (give_option.equalsIgnoreCase("start") && !respawn)
						|| (give_option.equalsIgnoreCase("respawn") && respawn)) {
					try {
						ItemStack itemStack = ItemStack.deserialize((Map<String, Object>) arenaConfig.getConfig()
								.getList("giveitem.item." + items + ".item").get(0));
						player.getInventory().setItem(slot, itemStack);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer((Player) e.getWhoClicked());
		if (game == null || game.getState() != GameState.RUNNING) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		if (game.getPlayerTeam(player) == null) {
			return;
		}
		Inventory inventory = e.getInventory();
		if (inventory.getHolder() == null) {
			return;
		}
		if (!(inventory.getHolder().equals(player.getInventory().getHolder())
				&& (inventory.getTitle().equals("container.crafting")
						|| inventory.getTitle().equals("container.inventory")))) {
			return;
		}
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		ArenaConfig arenaConfig = arena.getArenaConfig();
		if (e.getRawSlot() == 5
				&& !arenaConfig.getBoolean("giveitem.armor.helmet.move", Config.giveitem_armor_helmet_move)) {
			e.setCancelled(true);
			return;
		}
		if (e.getRawSlot() == 6
				&& !arenaConfig.getBoolean("giveitem.armor.chestplate.move", Config.giveitem_armor_chestplate_move)) {
			e.setCancelled(true);
			return;
		}
		if (e.getRawSlot() == 7
				&& !arenaConfig.getBoolean("giveitem.armor.leggings.move", Config.giveitem_armor_leggings_move)) {
			e.setCancelled(true);
			return;
		}
		if (e.getRawSlot() == 8
				&& !arenaConfig.getBoolean("giveitem.armor.boots.move", Config.giveitem_armor_boots_move)) {
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer((Player) e.getEntity());
			if (game == null) {
				return;
			}
			Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
			if (arena == null) {
				return;
			}
			Player p = e.getEntity();
			if (game.getPlayerTeam(p) == null) {
				return;
			}
			if (game.getPlayerTeam(p).isDead(game)) {
				return;
			}
			arena.addGameTask(new BukkitRunnable() {
				Player player = e.getEntity();
				ItemStack stack1 = player.getInventory().getHelmet();
				ItemStack stack2 = player.getInventory().getChestplate();
				ItemStack stack3 = player.getInventory().getLeggings();
				ItemStack stack4 = player.getInventory().getBoots();

				@Override
				public void run() {
					Team team = game.getPlayerTeam(player);
					ArenaConfig arenaConfig = arena.getArenaConfig();
					GiveItem.giveItem(player, team, true, arenaConfig);
					if (arenaConfig.getBoolean("giveitem.keeparmor", Config.giveitem_keeparmor)) {
						if (stack1 != null) {
							player.getInventory().setHelmet(stack1);
						}
						if (stack2 != null) {
							player.getInventory().setChestplate(stack2);
						}
						if (stack3 != null) {
							player.getInventory().setLeggings(stack3);
						}
						if (stack4 != null) {
							player.getInventory().setBoots(stack4);
						}
					}
				}
			}.runTaskLater(Main.getInstance(), 1L));
		}
	}
}
