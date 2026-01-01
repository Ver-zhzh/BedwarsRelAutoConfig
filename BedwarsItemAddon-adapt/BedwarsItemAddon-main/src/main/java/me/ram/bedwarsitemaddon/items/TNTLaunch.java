package me.ram.bedwarsitemaddon.items;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import me.ram.bedwarsitemaddon.Main;
import me.ram.bedwarsitemaddon.config.Config;
import me.ram.bedwarsitemaddon.event.BedwarsUseItemEvent;
import me.ram.bedwarsitemaddon.utils.LocationUtil;
import me.ram.bedwarsitemaddon.utils.TakeItemUtil;

public class TNTLaunch implements Listener {
    private final Map<Player, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onStart(BedwarsGameStartEvent e) {
        for (Player player : e.getGame().getPlayers()) {
            cooldown.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (e.getItem() == null || game == null) {
            return;
        }
        String gameName = game.getName();
        if (!Config.getBoolean(gameName, "tnt_launch.enabled", Config.items_tnt_launch_enabled)) {
            return;
        }
        if (!game.getPlayers().contains(player)) {
            return;
        }
        if (game.getState() == GameState.RUNNING) {
            String itemMaterial = Config.getString(gameName, "tnt_launch.item", Config.items_tnt_launch_item);
            if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                    && e.getItem().getType() == Material.valueOf(itemMaterial)) {
                double cooldownTime = Config.getDouble(gameName, "tnt_launch.cooldown",
                        Config.items_tnt_launch_cooldown);
                if ((System.currentTimeMillis() - cooldown.getOrDefault(player, (long) 0)) <= cooldownTime * 1000) {
                    e.setCancelled(true);
                    player.sendMessage(
                            Config.message_cooling
                                    .replace(
                                            "{time}", String
                                                    .format("%.1f",
                                                            (((cooldownTime * 1000 - System.currentTimeMillis()
                                                                    + cooldown.getOrDefault(player, (long) 0)) / 1000)))
                                                    + ""));
                } else {
                    ItemStack stack = e.getItem();
                    BedwarsUseItemEvent bedwarsUseItemEvent = new BedwarsUseItemEvent(game, player, EnumItem.TNT_LAUNCH,
                            stack);
                    Bukkit.getPluginManager().callEvent(bedwarsUseItemEvent);
                    if (!bedwarsUseItemEvent.isCancelled()) {
                        cooldown.put(player, System.currentTimeMillis());
                        TNTPrimed tnt = player.getWorld().spawn(player.getLocation().clone().add(0, 1, 0),
                                TNTPrimed.class);
                        double range = Config.getDouble(gameName, "tnt_launch.range", Config.items_tnt_launch_range);
                        tnt.setYield((float) range);
                        tnt.setIsIncendiary(false);
                        double launchVelocity = Config.getDouble(gameName, "tnt_launch.launch_velocity",
                                Config.items_tnt_launch_launch_velocity);
                        tnt.setVelocity(player.getLocation().getDirection().multiply(launchVelocity));
                        int fuseTicks = Config.getInt(gameName, "tnt_launch.fuse_ticks",
                                Config.items_tnt_launch_fuse_ticks);
                        tnt.setFuseTicks(fuseTicks);
                        tnt.setMetadata("TNTLaunch",
                                new FixedMetadataValue(Main.getInstance(), game.getName() + "." + player.getName()));
                        TakeItemUtil.TakeItem(player, stack);
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (!damager.hasMetadata("TNTLaunch")) {
            return;
        }
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) {
            return;
        }
        String gameName = game.getName();
        if (!Config.getBoolean(gameName, "tnt_launch.enabled", Config.items_tnt_launch_enabled)) {
            return;
        }
        if (damager instanceof TNTPrimed) {
            if (!game.getPlayers().contains(player)) {
                return;
            }
            if (game.isSpectator(player)) {
                return;
            }
            if (game.getState() == GameState.RUNNING) {
                if (Config.getBoolean(gameName, "tnt_launch.ejection.enabled",
                        Config.items_tnt_launch_ejection_enabled)) {
                    double ejectionVelocity = Config.getDouble(gameName, "tnt_launch.ejection.velocity",
                            Config.items_tnt_launch_ejection_velocity);
                    player.setVelocity(LocationUtil.getPosition(player.getLocation(), damager.getLocation(), 1)
                            .multiply(ejectionVelocity));
                    if (Config.getBoolean(gameName, "tnt_launch.ejection.no_fall",
                            Config.items_tnt_launch_ejection_no_fall)) {
                        Main.getInstance().getNoFallManage().addPlayer(player);
                    }
                }
                int damage = Config.getInt(gameName, "tnt_launch.damage", Config.items_tnt_launch_damage);
                e.setDamage(damage);
            }
        }
    }
}
