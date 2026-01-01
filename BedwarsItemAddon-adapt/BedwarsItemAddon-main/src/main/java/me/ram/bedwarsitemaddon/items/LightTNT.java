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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

public class LightTNT implements Listener {
    private final Map<Player, Long> cooldown = new HashMap<Player, Long>();

    @EventHandler
    public void onStart(BedwarsGameStartEvent e) {
        for (Player player : e.getGame().getPlayers()) {
            cooldown.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) {
            return;
        }
        String gameName = game.getName();
        if (!Config.getBoolean(gameName, "tnt.enabled", Config.items_tnt_enabled)) {
            return;
        }
        if (!game.getPlayers().contains(player)) {
            return;
        }
        if (game.getState() == GameState.RUNNING) {
            if (e.getBlock().getType() == new ItemStack(Material.TNT).getType() && !e.isCancelled()) {
                double cooldownTime = Config.getDouble(gameName, "tnt.cooldown", Config.items_tnt_cooldown);
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
                    BedwarsUseItemEvent bedwarsUseItemEvent = new BedwarsUseItemEvent(game, player, EnumItem.LIGHT_TNT,
                            new ItemStack(Material.TNT));
                    Bukkit.getPluginManager().callEvent(bedwarsUseItemEvent);
                    if (!bedwarsUseItemEvent.isCancelled()) {
                        cooldown.put(player, System.currentTimeMillis());
                        e.getBlock().setType(Material.AIR);
                        TNTPrimed tnt = e.getBlock().getLocation().getWorld()
                                .spawn(e.getBlock().getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
                        double range = Config.getDouble(gameName, "tnt.range", Config.items_tnt_range);
                        int fuseTicks = Config.getInt(gameName, "tnt.fuse_ticks", Config.items_tnt_fuse_ticks);
                        tnt.setYield((float) range);
                        tnt.setIsIncendiary(false);
                        tnt.setFuseTicks(fuseTicks);
                        tnt.setMetadata("LightTNT",
                                new FixedMetadataValue(Main.getInstance(), game.getName() + "." + player.getName()));
                    } else {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (!(damager instanceof TNTPrimed) || !damager.hasMetadata("LightTNT")) {
            return;
        }
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null || game.getState() != GameState.RUNNING) {
            return;
        }
        String gameName = game.getName();
        if (!Config.getBoolean(gameName, "tnt.enabled", Config.items_tnt_enabled)) {
            return;
        }
        if (game.isSpectator(player)) {
            return;
        }
        int damage = Config.getInt(gameName, "tnt.damage", Config.items_tnt_damage);
        e.setDamage(damage);
        if (Config.getBoolean(gameName, "tnt.ejection.enabled", Config.items_tnt_ejection_enabled)) {
            double velocity = Config.getDouble(gameName, "tnt.ejection.velocity", Config.items_tnt_ejection_velocity);
            player.setVelocity(
                    LocationUtil.getPosition(player.getLocation(), damager.getLocation(), 1).multiply(velocity));
            if (Config.getBoolean(gameName, "tnt.ejection.no_fall", Config.items_tnt_ejection_no_fall)) {
                Main.getInstance().getNoFallManage().addPlayer(player);
            }
        }
    }
}
