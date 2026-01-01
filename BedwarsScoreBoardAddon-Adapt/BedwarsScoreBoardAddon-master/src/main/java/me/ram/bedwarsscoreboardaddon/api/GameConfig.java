package me.ram.bedwarsscoreboardaddon.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Per-game configuration storage
 * Stores all configurations (main config and team shop) for a specific game
 *
 * @author BRAC Team
 */
public class GameConfig {

    private final String gameName;
    private final FileConfiguration mainConfig;
    private final FileConfiguration teamShopConfig;

    // ==================== MAIN CONFIG FIELDS ====================

    // Basic settings
    public final boolean hide_player;
    public final boolean tab_health;
    public final boolean tag_health;
    public final boolean item_merge;
    public final boolean hunger_change;
    public final boolean clear_bottle;
    public final boolean fast_respawn;
    public final String date_format;

    // Chat format settings
    public final boolean chat_format_enabled;
    public final boolean chat_format_chat_lobby;
    public final boolean chat_format_chat_all;
    public final boolean chat_format_chat_spectator;
    public final String chat_format_lobby;
    public final String chat_format_lobby_team;
    public final List<String> chat_format_all_prefix;
    public final String chat_format_ingame;
    public final String chat_format_ingame_all;
    public final String chat_format_spectator;

    // Final killed settings
    public final boolean final_killed_enabled;
    public final String final_killed_message;
    public final List<String> timecommand_startcommand;

    // Select team settings
    public final boolean select_team_enabled;
    public final String select_team_status_select;
    public final String select_team_status_inteam;
    public final String select_team_status_team_full;
    public final String select_team_no_players;
    public final String select_team_item_name;
    public final List<String> select_team_item_lore;

    // Lobby block settings
    public final boolean lobby_block_enabled;
    public final int lobby_block_position_1_x;
    public final int lobby_block_position_1_y;
    public final int lobby_block_position_1_z;
    public final int lobby_block_position_2_x;
    public final int lobby_block_position_2_y;
    public final int lobby_block_position_2_z;

    // Rejoin settings
    public final boolean rejoin_enabled;
    public final String rejoin_message_rejoin;
    public final String rejoin_message_error;

    // Bow damage settings
    public final boolean bowdamage_enabled;
    public final String bowdamage_title;
    public final String bowdamage_subtitle;
    public final String bowdamage_message;

    // Damage title settings
    public final boolean damagetitle_enabled;
    public final String damagetitle_title;
    public final String damagetitle_subtitle;

    // Join title settings
    public final boolean jointitle_enabled;
    public final String jointitle_title;
    public final String jointitle_subtitle;

    // Die out title settings
    public final boolean die_out_title_enabled;
    public final String die_out_title_title;
    public final String die_out_title_subtitle;

    // Destroyed title settings
    public final boolean destroyed_title_enabled;
    public final String destroyed_title_title;
    public final String destroyed_title_subtitle;

    // Start title settings
    public final boolean start_title_enabled;
    public final List<String> start_title_title;
    public final String start_title_subtitle;

    // Victory title settings
    public final boolean victory_title_enabled;
    public final List<String> victory_title_title;
    public final String victory_title_subtitle;

    // Play sound settings
    public final boolean play_sound_enabled;
    public final List<String> play_sound_sound_start;
    public final List<String> play_sound_sound_death;
    public final List<String> play_sound_sound_kill;
    public final List<String> play_sound_sound_upgrade;
    public final List<String> play_sound_sound_no_resource;
    public final List<String> play_sound_sound_sethealth;
    public final List<String> play_sound_sound_enable_witherbow;
    public final List<String> play_sound_sound_witherbow;
    public final List<String> play_sound_sound_deathmode;
    public final List<String> play_sound_sound_over;

    // Spectator settings
    public final boolean spectator_enabled;
    public final boolean spectator_centre_enabled;
    public final double spectator_centre_height;
    public final String spectator_spectator_target_title;
    public final String spectator_spectator_target_subtitle;
    public final String spectator_quit_spectator_title;
    public final String spectator_quit_spectator_subtitle;
    public final boolean spectator_speed_enabled;
    public final int spectator_speed_slot;
    public final int spectator_speed_item;
    public final String spectator_speed_item_name;
    public final List<String> spectator_speed_item_lore;
    public final String spectator_speed_gui_title;
    public final String spectator_speed_no_speed;
    public final String spectator_speed_speed_1;
    public final String spectator_speed_speed_2;
    public final String spectator_speed_speed_3;
    public final String spectator_speed_speed_4;
    public final boolean spectator_fast_join_enabled;
    public final int spectator_fast_join_slot;
    public final int spectator_fast_join_item;
    public final String spectator_fast_join_item_name;
    public final List<String> spectator_fast_join_item_lore;
    public final String spectator_fast_join_group;

    // Compass settings
    public final boolean compass_enabled;
    public final String compass_item_name;
    public final String compass_back;
    public final List<String> compass_item_lore;
    public final List<String> compass_lore_send_message;
    public final List<String> compass_lore_select_team;
    public final List<String> compass_lore_select_resources;
    public final List<String> compass_resources;
    public final Map<String, String> compass_resources_name;
    public final String compass_gui_title;
    public final String compass_item_III_II;
    public final String compass_item_IV_II;
    public final String compass_item_V_II;
    public final String compass_item_VI_II;
    public final String compass_item_VII_II;
    public final String compass_item_VIII_II;
    public final String compass_item_III_III;
    public final String compass_item_IV_III;
    public final String compass_item_V_III;
    public final String compass_item_VI_III;
    public final String compass_item_VII_III;
    public final String compass_message_III_II;
    public final String compass_message_IV_II;
    public final String compass_message_V_II;
    public final String compass_message_VI_II;
    public final String compass_message_VII_II;
    public final String compass_message_VIII_II;
    public final String compass_message_III_III;
    public final String compass_message_IV_III;
    public final String compass_message_V_III;
    public final String compass_message_VI_III;
    public final String compass_message_VII_III;

    // Graffiti settings
    public final boolean graffiti_enabled;
    public final boolean graffiti_holographic_enabled;
    public final String graffiti_holographic_text;

    // Shop settings
    public final boolean shop_enabled;
    public final String shop_item_shop_type;
    public final String shop_item_shop_skin;
    public final boolean shop_item_shop_look;
    public final String shop_team_shop_type;
    public final String shop_team_shop_skin;
    public final boolean shop_team_shop_look;
    public final List<String> shop_item_shop_name;
    public final List<String> shop_team_shop_name;

    // Respawn settings
    public final boolean respawn_enabled;
    public final boolean respawn_centre_enabled;
    public final double respawn_centre_height;
    public final boolean respawn_protected_enabled;
    public final int respawn_protected_time;
    public final int respawn_respawn_delay;
    public final String respawn_countdown_title;
    public final String respawn_countdown_subtitle;
    public final String respawn_countdown_message;
    public final String respawn_respawn_title;
    public final String respawn_respawn_subtitle;
    public final String respawn_respawn_message;

    // Give item settings
    public final boolean giveitem_keeparmor;
    public final Map<String, Object> giveitem_armor_helmet_item;
    public final Map<String, Object> giveitem_armor_chestplate_item;
    public final Map<String, Object> giveitem_armor_leggings_item;
    public final Map<String, Object> giveitem_armor_boots_item;
    public final String giveitem_armor_helmet_give;
    public final String giveitem_armor_chestplate_give;
    public final String giveitem_armor_leggings_give;
    public final String giveitem_armor_boots_give;
    public final boolean giveitem_armor_helmet_move;
    public final boolean giveitem_armor_chestplate_move;
    public final boolean giveitem_armor_leggings_move;
    public final boolean giveitem_armor_boots_move;

    // Set health settings
    public final boolean sethealth_start_enabled;
    public final int sethealth_start_health;

    // Resource limit settings
    public final boolean resourcelimit_enabled;
    public final List<String[]> resourcelimit_limit;

    // Spread resource settings
    public final boolean spread_resource_enabled;
    public final boolean spread_resource_launch;
    public final double spread_resource_range;

    // Game chest settings
    public final boolean game_chest_enabled;
    public final int game_chest_range;
    public final String game_chest_message;

    // Invisibility player settings
    public final boolean invisibility_player_enabled;
    public final boolean invisibility_player_footstep;
    public final boolean invisibility_player_hide_particles;
    public final boolean invisibility_player_damage_show_player;

    // Wither bow settings
    public final boolean witherbow_enabled;
    public final int witherbow_gametime;
    public final String witherbow_already_starte;
    public final String witherbow_title;
    public final String witherbow_subtitle;
    public final String witherbow_message;

    // Death mode settings
    public final boolean deathmode_enabled;
    public final int deathmode_gametime;
    public final String deathmode_title;
    public final String deathmode_subtitle;
    public final String deathmode_message;

    // Death item settings
    public final boolean deathitem_enabled;
    public final List<String> deathitem_items;
    public final boolean deathitem_item_name_chinesize;
    public final String deathitem_message;

    // No break bed settings
    public final boolean nobreakbed_enabled;
    public final int nobreakbed_gametime;
    public final String nobreakbed_nobreakmessage;
    public final String nobreakbed_title;
    public final String nobreakbed_subtitle;
    public final String nobreakbed_message;

    // Spawn no build settings
    public final boolean spawn_no_build_spawn_enabled;
    public final int spawn_no_build_spawn_range;
    public final boolean spawn_no_build_resource_enabled;
    public final int spawn_no_build_resource_range;
    public final String spawn_no_build_message;

    // Holographic settings
    public final boolean holographic_resource_enabled;
    public final boolean holographic_bed_title_bed_alive_enabled;
    public final boolean holographic_bed_title_bed_destroyed_enabled;
    public final double holographic_resource_speed;
    public final List<String> holographic_resource;
    public final String holographic_bedtitle_bed_alive_title;
    public final String holographic_bedtitle_bed_destroyed_title;

    // Over stats settings
    public final boolean overstats_enabled;
    public final List<String> overstats_message;

    // Scoreboard settings
    public final String actionbar;
    public final Map<String, Integer> timer;
    public final List<String> planinfo;
    public final String playertag_prefix;
    public final String playertag_suffix;
    public final int scoreboard_interval;
    public final List<String> scoreboard_title;
    public final String scoreboard_you;
    public final String scoreboard_team_bed_status_bed_alive;
    public final String scoreboard_team_bed_status_bed_destroyed;
    public final String scoreboard_team_status_format_bed_alive;
    public final String scoreboard_team_status_format_bed_destroyed;
    public final String scoreboard_team_status_format_team_dead;
    public final Map<String, List<String>> scoreboard_lines;

    // Lobby scoreboard settings
    public final boolean lobby_scoreboard_enabled;
    public final int lobby_scoreboard_interval;
    public final String lobby_scoreboard_state_waiting;
    public final String lobby_scoreboard_state_countdown;
    public final List<String> lobby_scoreboard_title;
    public final List<String> lobby_scoreboard_lines;

    // ==================== TEAM SHOP CONFIG FIELDS ====================

    // Team shop basic settings
    public final boolean teamshop_enabled;
    public final String teamshop_upgrade_shop_title;
    public final List<String> teamshop_upgrade_shop_frame;
    public final String teamshop_upgrade_shop_trap_item;
    public final String teamshop_upgrade_shop_trap_name;
    public final List<String> teamshop_upgrade_shop_trap_lore;
    public final String teamshop_trap_shop_title;
    public final List<String> teamshop_trap_shop_back;
    public final String teamshop_message_upgrade;
    public final String teamshop_message_no_resource;
    public final String teamshop_state_no_resource;
    public final String teamshop_state_lock;
    public final String teamshop_state_unlock;
    public final int teamshop_trap_cooldown;
    
    // Trap lists
    public final List<String> teamshop_trap_trap_list_trap_1_lock;
    public final List<String> teamshop_trap_trap_list_trap_1_unlock;
    public final List<String> teamshop_trap_trap_list_trap_2_lock;
    public final List<String> teamshop_trap_trap_list_trap_2_unlock;
    public final List<String> teamshop_trap_trap_list_trap_3_lock;
    public final List<String> teamshop_trap_trap_list_trap_3_unlock;
    
    // Trap costs
    public final Map<Integer, String> teamshop_trap_level_cost;
    
    // Upgrade settings
    public final Map<String, Boolean> teamshop_upgrade_enabled;
    public final Map<String, String> teamshop_upgrade_item;
    public final Map<String, String> teamshop_upgrade_name;
    public final Map<String, Map<Integer, String>> teamshop_upgrade_level_cost;
    public final Map<String, Map<Integer, List<String>>> teamshop_upgrade_level_lore;
    
    // Specific upgrade settings
    public final Map<Integer, List<String>> teamshop_upgrade_iron_forge_level_resources;
    public final int teamshop_upgrade_heal_trigger_range;
    public final int teamshop_upgrade_trap_trigger_range;
    public final String teamshop_upgrade_trap_trigger_title;
    public final String teamshop_upgrade_trap_trigger_subtitle;
    public final String teamshop_upgrade_trap_trigger_message;
    public final int teamshop_upgrade_counter_offensive_trap_trigger_range;
    public final int teamshop_upgrade_counter_offensive_trap_effect_range;
    public final int teamshop_upgrade_alarm_trap_trigger_range;
    public final String teamshop_upgrade_alarm_trap_trigger_title;
    public final String teamshop_upgrade_alarm_trap_trigger_subtitle;
    public final String teamshop_upgrade_alarm_trap_trigger_message;
    public final int teamshop_upgrade_defense_trigger_range;
    
    /**
     * Constructor for per-game configuration
     *
     * @param gameName Name of the game
     * @param mainConfig Main configuration file (config.yml), can be null
     * @param teamShopConfig Team shop configuration file (team_shop.yml), can be null
     */
    public GameConfig(String gameName, FileConfiguration mainConfig, FileConfiguration teamShopConfig) {
        this.gameName = gameName;

        // If either config is null, try to use default config's FileConfiguration
        // This ensures per-game configs always have complete functionality
        // IMPORTANT: Only do this for non-default configs to avoid infinite recursion
        if (!"default".equals(gameName)) {
            GameConfig defaultCfg = ConfigAPI.getDefaultConfig();
            if (mainConfig == null && defaultCfg != null) {
                mainConfig = defaultCfg.mainConfig;
            }
            if (teamShopConfig == null && defaultCfg != null) {
                teamShopConfig = defaultCfg.teamShopConfig;
            }
        }

        this.mainConfig = mainConfig;
        this.teamShopConfig = teamShopConfig;

        // ==================== LOAD MAIN CONFIG ====================
        if (mainConfig != null) {
            // Basic settings
            hide_player = mainConfig.getBoolean("hide_player");
            tab_health = mainConfig.getBoolean("tab_health");
            tag_health = mainConfig.getBoolean("tag_health");
            item_merge = mainConfig.getBoolean("item_merge");
            hunger_change = mainConfig.getBoolean("hunger_change");
            clear_bottle = mainConfig.getBoolean("clear_bottle");
            fast_respawn = mainConfig.getBoolean("fast_respawn");
            date_format = mainConfig.getString("date_format");

            // Chat format settings
            chat_format_enabled = mainConfig.getBoolean("chat_format.enabled");
            chat_format_chat_lobby = mainConfig.getBoolean("chat_format.chat.lobby");
            chat_format_chat_all = mainConfig.getBoolean("chat_format.chat.all");
            chat_format_chat_spectator = mainConfig.getBoolean("chat_format.chat.spectator");
            chat_format_lobby = colorize(mainConfig.getString("chat_format.lobby"));
            chat_format_lobby_team = colorize(mainConfig.getString("chat_format.lobby_team"));
            chat_format_all_prefix = mainConfig.getStringList("chat_format.all_prefix");
            chat_format_ingame = colorize(mainConfig.getString("chat_format.ingame"));
            chat_format_ingame_all = colorize(mainConfig.getString("chat_format.ingame_all"));
            chat_format_spectator = colorize(mainConfig.getString("chat_format.spectator"));

            // Final killed settings
            final_killed_enabled = mainConfig.getBoolean("final_killed.enabled");
            final_killed_message = colorize(mainConfig.getString("final_killed.message"));
            timecommand_startcommand = colorizeList(mainConfig.getStringList("timecommand.startcommand"));

            // Select team settings
            select_team_enabled = mainConfig.getBoolean("select_team.enabled");
            select_team_status_select = colorize(mainConfig.getString("select_team.status.select"));
            select_team_status_inteam = colorize(mainConfig.getString("select_team.status.inteam"));
            select_team_status_team_full = colorize(mainConfig.getString("select_team.status.team_full"));
            select_team_no_players = colorize(mainConfig.getString("select_team.no_players"));
            select_team_item_name = colorize(mainConfig.getString("select_team.item.name"));
            select_team_item_lore = colorizeList(mainConfig.getStringList("select_team.item.lore"));

            // Lobby block settings
            lobby_block_enabled = mainConfig.getBoolean("lobby_block.enabled");
            lobby_block_position_1_x = mainConfig.getInt("lobby_block.position_1.x");
            lobby_block_position_1_y = mainConfig.getInt("lobby_block.position_1.y");
            lobby_block_position_1_z = mainConfig.getInt("lobby_block.position_1.z");
            lobby_block_position_2_x = mainConfig.getInt("lobby_block.position_2.x");
            lobby_block_position_2_y = mainConfig.getInt("lobby_block.position_2.y");
            lobby_block_position_2_z = mainConfig.getInt("lobby_block.position_2.z");

            // Rejoin settings
            rejoin_enabled = mainConfig.getBoolean("rejoin.enabled");
            rejoin_message_rejoin = colorize(mainConfig.getString("rejoin.message.rejoin"));
            rejoin_message_error = colorize(mainConfig.getString("rejoin.message.error"));

            // Bow damage settings
            bowdamage_enabled = mainConfig.getBoolean("bowdamage.enabled");
            bowdamage_title = colorize(mainConfig.getString("bowdamage.title"));
            bowdamage_subtitle = colorize(mainConfig.getString("bowdamage.subtitle"));
            bowdamage_message = colorize(mainConfig.getString("bowdamage.message"));

            // Damage title settings
            damagetitle_enabled = mainConfig.getBoolean("damagetitle.enabled");
            damagetitle_title = colorize(mainConfig.getString("damagetitle.title"));
            damagetitle_subtitle = colorize(mainConfig.getString("damagetitle.subtitle"));

            // Join title settings
            jointitle_enabled = mainConfig.getBoolean("jointitle.enabled");
            jointitle_title = colorize(mainConfig.getString("jointitle.title"));
            jointitle_subtitle = colorize(mainConfig.getString("jointitle.subtitle"));

            // Die out title settings
            die_out_title_enabled = mainConfig.getBoolean("die_out_title.enabled");
            die_out_title_title = colorize(mainConfig.getString("die_out_title.title"));
            die_out_title_subtitle = colorize(mainConfig.getString("die_out_title.subtitle"));

            // Destroyed title settings
            destroyed_title_enabled = mainConfig.getBoolean("destroyed_title.enabled");
            destroyed_title_title = colorize(mainConfig.getString("destroyed_title.title"));
            destroyed_title_subtitle = colorize(mainConfig.getString("destroyed_title.subtitle"));

            // Start title settings
            start_title_enabled = mainConfig.getBoolean("start_title.enabled");
            start_title_title = colorizeList(mainConfig.getStringList("start_title.title"));
            start_title_subtitle = colorize(mainConfig.getString("start_title.subtitle"));

            // Victory title settings
            victory_title_enabled = mainConfig.getBoolean("victory_title.enabled");
            victory_title_title = colorizeList(mainConfig.getStringList("victory_title.title"));
            victory_title_subtitle = colorize(mainConfig.getString("victory_title.subtitle"));

            // Play sound settings
            play_sound_enabled = mainConfig.getBoolean("play_sound.enabled");
            play_sound_sound_start = mainConfig.getStringList("play_sound.sound.start");
            play_sound_sound_death = mainConfig.getStringList("play_sound.sound.death");
            play_sound_sound_kill = mainConfig.getStringList("play_sound.sound.kill");
            play_sound_sound_upgrade = mainConfig.getStringList("play_sound.sound.upgrade");
            play_sound_sound_no_resource = mainConfig.getStringList("play_sound.sound.no_resource");
            play_sound_sound_sethealth = mainConfig.getStringList("play_sound.sound.sethealth");
            play_sound_sound_enable_witherbow = mainConfig.getStringList("play_sound.sound.enable_witherbow");
            play_sound_sound_witherbow = mainConfig.getStringList("play_sound.sound.witherbow");
            play_sound_sound_deathmode = mainConfig.getStringList("play_sound.sound.deathmode");
            play_sound_sound_over = mainConfig.getStringList("play_sound.sound.over");

            // Spectator settings
            spectator_enabled = mainConfig.getBoolean("spectator.enabled");
            spectator_centre_enabled = mainConfig.getBoolean("spectator.centre.enabled");
            spectator_centre_height = mainConfig.getDouble("spectator.centre.height");
            spectator_spectator_target_title = colorize(mainConfig.getString("spectator.spectator_target.title"));
            spectator_spectator_target_subtitle = colorize(mainConfig.getString("spectator.spectator_target.subtitle"));
            spectator_quit_spectator_title = colorize(mainConfig.getString("spectator.quit_spectator.title"));
            spectator_quit_spectator_subtitle = colorize(mainConfig.getString("spectator.quit_spectator.subtitle"));
            spectator_speed_enabled = mainConfig.getBoolean("spectator.speed.enabled");
            spectator_speed_slot = mainConfig.getInt("spectator.speed.slot");
            spectator_speed_item = mainConfig.getInt("spectator.speed.item");
            spectator_speed_item_name = colorize(mainConfig.getString("spectator.speed.item.name"));
            spectator_speed_item_lore = colorizeList(mainConfig.getStringList("spectator.speed.item.lore"));
            spectator_speed_gui_title = colorize(mainConfig.getString("spectator.speed.gui_title"));
            spectator_speed_no_speed = colorize(mainConfig.getString("spectator.speed.no_speed"));
            spectator_speed_speed_1 = colorize(mainConfig.getString("spectator.speed.speed_1"));
            spectator_speed_speed_2 = colorize(mainConfig.getString("spectator.speed.speed_2"));
            spectator_speed_speed_3 = colorize(mainConfig.getString("spectator.speed.speed_3"));
            spectator_speed_speed_4 = colorize(mainConfig.getString("spectator.speed.speed_4"));
            spectator_fast_join_enabled = mainConfig.getBoolean("spectator.fast_join.enabled");
            spectator_fast_join_slot = mainConfig.getInt("spectator.fast_join.slot");
            spectator_fast_join_item = mainConfig.getInt("spectator.fast_join.item");
            spectator_fast_join_item_name = colorize(mainConfig.getString("spectator.fast_join.item.name"));
            spectator_fast_join_item_lore = colorizeList(mainConfig.getStringList("spectator.fast_join.item.lore"));
            spectator_fast_join_group = mainConfig.getString("spectator.fast_join.group");

            // Compass settings
            compass_enabled = mainConfig.getBoolean("compass.enabled");
            compass_item_name = colorize(mainConfig.getString("compass.item.name"));
            compass_back = colorize(mainConfig.getString("compass.back"));
            compass_item_lore = colorizeList(mainConfig.getStringList("compass.item.lore"));
            compass_lore_send_message = colorizeList(mainConfig.getStringList("compass.lore.send_message"));
            compass_lore_select_team = colorizeList(mainConfig.getStringList("compass.lore.select_team"));
            compass_lore_select_resources = colorizeList(mainConfig.getStringList("compass.lore.select_resources"));
            compass_resources = mainConfig.getStringList("compass.resources");
            compass_resources_name = new HashMap<>();
            for (String resource : compass_resources) {
                compass_resources_name.put(resource, colorize(mainConfig.getString("compass.resources_name." + resource)));
            }
            compass_gui_title = colorize(mainConfig.getString("compass.gui_title"));
            compass_item_III_II = colorize(mainConfig.getString("compass.item.III_II"));
            compass_item_IV_II = colorize(mainConfig.getString("compass.item.IV_II"));
            compass_item_V_II = colorize(mainConfig.getString("compass.item.V_II"));
            compass_item_VI_II = colorize(mainConfig.getString("compass.item.VI_II"));
            compass_item_VII_II = colorize(mainConfig.getString("compass.item.VII_II"));
            compass_item_VIII_II = colorize(mainConfig.getString("compass.item.VIII_II"));
            compass_item_III_III = colorize(mainConfig.getString("compass.item.III_III"));
            compass_item_IV_III = colorize(mainConfig.getString("compass.item.IV_III"));
            compass_item_V_III = colorize(mainConfig.getString("compass.item.V_III"));
            compass_item_VI_III = colorize(mainConfig.getString("compass.item.VI_III"));
            compass_item_VII_III = colorize(mainConfig.getString("compass.item.VII_III"));
            compass_message_III_II = colorize(mainConfig.getString("compass.message.III_II"));
            compass_message_IV_II = colorize(mainConfig.getString("compass.message.IV_II"));
            compass_message_V_II = colorize(mainConfig.getString("compass.message.V_II"));
            compass_message_VI_II = colorize(mainConfig.getString("compass.message.VI_II"));
            compass_message_VII_II = colorize(mainConfig.getString("compass.message.VII_II"));
            compass_message_VIII_II = colorize(mainConfig.getString("compass.message.VIII_II"));
            compass_message_III_III = colorize(mainConfig.getString("compass.message.III_III"));
            compass_message_IV_III = colorize(mainConfig.getString("compass.message.IV_III"));
            compass_message_V_III = colorize(mainConfig.getString("compass.message.V_III"));
            compass_message_VI_III = colorize(mainConfig.getString("compass.message.VI_III"));
            compass_message_VII_III = colorize(mainConfig.getString("compass.message.VII_III"));

            // Graffiti settings
            graffiti_enabled = mainConfig.getBoolean("graffiti.enabled");
            graffiti_holographic_enabled = mainConfig.getBoolean("graffiti.holographic.enabled");
            graffiti_holographic_text = colorize(mainConfig.getString("graffiti.holographic.text"));

            // Shop settings
            shop_enabled = mainConfig.getBoolean("shop.enabled");
            shop_item_shop_type = mainConfig.getString("shop.item_shop.type");
            shop_item_shop_skin = mainConfig.getString("shop.item_shop.skin");
            shop_item_shop_look = mainConfig.getBoolean("shop.item_shop.look");
            shop_team_shop_type = mainConfig.getString("shop.team_shop.type");
            shop_team_shop_skin = mainConfig.getString("shop.team_shop.skin");
            shop_team_shop_look = mainConfig.getBoolean("shop.team_shop.look");
            shop_item_shop_name = colorizeList(mainConfig.getStringList("shop.item_shop.name"));
            shop_team_shop_name = colorizeList(mainConfig.getStringList("shop.team_shop.name"));

            // Respawn settings
            respawn_enabled = mainConfig.getBoolean("respawn.enabled");
            respawn_centre_enabled = mainConfig.getBoolean("respawn.centre.enabled");
            respawn_centre_height = mainConfig.getDouble("respawn.centre.height");
            respawn_protected_enabled = mainConfig.getBoolean("respawn.protected.enabled");
            respawn_protected_time = mainConfig.getInt("respawn.protected.time");
            respawn_respawn_delay = mainConfig.getInt("respawn.respawn_delay");
            respawn_countdown_title = colorize(mainConfig.getString("respawn.countdown.title"));
            respawn_countdown_subtitle = colorize(mainConfig.getString("respawn.countdown.subtitle"));
            respawn_countdown_message = colorize(mainConfig.getString("respawn.countdown.message"));
            respawn_respawn_title = colorize(mainConfig.getString("respawn.respawn.title"));
            respawn_respawn_subtitle = colorize(mainConfig.getString("respawn.respawn.subtitle"));
            respawn_respawn_message = colorize(mainConfig.getString("respawn.respawn.message"));

            // Give item settings
            giveitem_keeparmor = mainConfig.getBoolean("giveitem.keeparmor");
            giveitem_armor_helmet_item = (Map<String, Object>) mainConfig.getList("giveitem.armor.helmet.item").get(0);
            giveitem_armor_chestplate_item = (Map<String, Object>) mainConfig.getList("giveitem.armor.chestplate.item").get(0);
            giveitem_armor_leggings_item = (Map<String, Object>) mainConfig.getList("giveitem.armor.leggings.item").get(0);
            giveitem_armor_boots_item = (Map<String, Object>) mainConfig.getList("giveitem.armor.boots.item").get(0);
            giveitem_armor_helmet_give = mainConfig.getString("giveitem.armor.helmet.give");
            giveitem_armor_chestplate_give = mainConfig.getString("giveitem.armor.chestplate.give");
            giveitem_armor_leggings_give = mainConfig.getString("giveitem.armor.leggings.give");
            giveitem_armor_boots_give = mainConfig.getString("giveitem.armor.boots.give");
            giveitem_armor_helmet_move = mainConfig.getBoolean("giveitem.armor.helmet.move");
            giveitem_armor_chestplate_move = mainConfig.getBoolean("giveitem.armor.chestplate.move");
            giveitem_armor_leggings_move = mainConfig.getBoolean("giveitem.armor.leggings.move");
            giveitem_armor_boots_move = mainConfig.getBoolean("giveitem.armor.boots.move");

            // Set health settings
            sethealth_start_enabled = mainConfig.getBoolean("sethealth.start.enabled");
            sethealth_start_health = mainConfig.getInt("sethealth.start.health");

            // Resource limit settings
            resourcelimit_enabled = mainConfig.getBoolean("resourcelimit.enabled");
            resourcelimit_limit = new ArrayList<>();
            // Note: This is a complex structure, may need special handling

            // Spread resource settings
            spread_resource_enabled = mainConfig.getBoolean("spread_resource.enabled");
            spread_resource_launch = mainConfig.getBoolean("spread_resource.launch");
            spread_resource_range = mainConfig.getDouble("spread_resource.range");

            // Game chest settings
            game_chest_enabled = mainConfig.getBoolean("game_chest.enabled");
            game_chest_range = mainConfig.getInt("game_chest.range");
            game_chest_message = colorize(mainConfig.getString("game_chest.message"));

            // Invisibility player settings
            invisibility_player_enabled = mainConfig.getBoolean("invisibility_player.enabled");
            invisibility_player_footstep = mainConfig.getBoolean("invisibility_player.footstep");
            invisibility_player_hide_particles = mainConfig.getBoolean("invisibility_player.hide_particles");
            invisibility_player_damage_show_player = mainConfig.getBoolean("invisibility_player.damage_show_player");

            // Wither bow settings
            witherbow_enabled = mainConfig.getBoolean("witherbow.enabled");
            witherbow_gametime = mainConfig.getInt("witherbow.gametime");
            witherbow_already_starte = colorize(mainConfig.getString("witherbow.already_starte"));
            witherbow_title = colorize(mainConfig.getString("witherbow.title"));
            witherbow_subtitle = colorize(mainConfig.getString("witherbow.subtitle"));
            witherbow_message = colorize(mainConfig.getString("witherbow.message"));

            // Death mode settings
            deathmode_enabled = mainConfig.getBoolean("deathmode.enabled");
            deathmode_gametime = mainConfig.getInt("deathmode.gametime");
            deathmode_title = colorize(mainConfig.getString("deathmode.title"));
            deathmode_subtitle = colorize(mainConfig.getString("deathmode.subtitle"));
            deathmode_message = colorize(mainConfig.getString("deathmode.message"));

            // Death item settings
            deathitem_enabled = mainConfig.getBoolean("deathitem.enabled");
            deathitem_items = mainConfig.getStringList("deathitem.items");
            deathitem_item_name_chinesize = mainConfig.getBoolean("deathitem.item_name_chinesize");
            deathitem_message = colorize(mainConfig.getString("deathitem.message"));

            // No break bed settings
            nobreakbed_enabled = mainConfig.getBoolean("nobreakbed.enabled");
            nobreakbed_gametime = mainConfig.getInt("nobreakbed.gametime");
            nobreakbed_nobreakmessage = colorize(mainConfig.getString("nobreakbed.nobreakmessage"));
            nobreakbed_title = colorize(mainConfig.getString("nobreakbed.title"));
            nobreakbed_subtitle = colorize(mainConfig.getString("nobreakbed.subtitle"));
            nobreakbed_message = colorize(mainConfig.getString("nobreakbed.message"));

            // Spawn no build settings
            spawn_no_build_spawn_enabled = mainConfig.getBoolean("spawn_no_build.spawn.enabled");
            spawn_no_build_spawn_range = mainConfig.getInt("spawn_no_build.spawn.range");
            spawn_no_build_resource_enabled = mainConfig.getBoolean("spawn_no_build.resource.enabled");
            spawn_no_build_resource_range = mainConfig.getInt("spawn_no_build.resource.range");
            spawn_no_build_message = colorize(mainConfig.getString("spawn_no_build.message"));

            // Holographic settings
            holographic_resource_enabled = mainConfig.getBoolean("holographic.resource.enabled");
            holographic_bed_title_bed_alive_enabled = mainConfig.getBoolean("holographic.bed_title.bed_alive.enabled");
            holographic_bed_title_bed_destroyed_enabled = mainConfig.getBoolean("holographic.bed_title.bed_destroyed.enabled");
            holographic_resource_speed = mainConfig.getDouble("holographic.resource.speed");
            holographic_resource = mainConfig.getStringList("holographic.resource");
            holographic_bedtitle_bed_alive_title = colorize(mainConfig.getString("holographic.bedtitle.bed_alive.title"));
            holographic_bedtitle_bed_destroyed_title = colorize(mainConfig.getString("holographic.bedtitle.bed_destroyed.title"));

            // Over stats settings
            overstats_enabled = mainConfig.getBoolean("overstats.enabled");
            overstats_message = colorizeList(mainConfig.getStringList("overstats.message"));

            // Scoreboard settings
            actionbar = colorize(mainConfig.getString("actionbar"));
            timer = new HashMap<>();
            // Load timer map
            planinfo = colorizeList(mainConfig.getStringList("planinfo"));
            playertag_prefix = colorize(mainConfig.getString("playertag.prefix"));
            playertag_suffix = colorize(mainConfig.getString("playertag.suffix"));
            scoreboard_interval = mainConfig.getInt("scoreboard.interval");
            scoreboard_title = colorizeList(mainConfig.getStringList("scoreboard.title"));
            scoreboard_you = colorize(mainConfig.getString("scoreboard.you"));
            scoreboard_team_bed_status_bed_alive = colorize(mainConfig.getString("scoreboard.team_bed_status.bed_alive"));
            scoreboard_team_bed_status_bed_destroyed = colorize(mainConfig.getString("scoreboard.team_bed_status.bed_destroyed"));
            scoreboard_team_status_format_bed_alive = colorize(mainConfig.getString("scoreboard.team_status_format.bed_alive"));
            scoreboard_team_status_format_bed_destroyed = colorize(mainConfig.getString("scoreboard.team_status_format.bed_destroyed"));
            scoreboard_team_status_format_team_dead = colorize(mainConfig.getString("scoreboard.team_status_format.team_dead"));
            scoreboard_lines = new HashMap<>();
            // Load scoreboard lines map

            // Lobby scoreboard settings
            lobby_scoreboard_enabled = mainConfig.getBoolean("lobby_scoreboard.enabled");
            lobby_scoreboard_interval = mainConfig.getInt("lobby_scoreboard.interval");
            lobby_scoreboard_state_waiting = colorize(mainConfig.getString("lobby_scoreboard.state.waiting"));
            lobby_scoreboard_state_countdown = colorize(mainConfig.getString("lobby_scoreboard.state.countdown"));
            lobby_scoreboard_title = colorizeList(mainConfig.getStringList("lobby_scoreboard.title"));
            lobby_scoreboard_lines = colorizeList(mainConfig.getStringList("lobby_scoreboard.lines"));
        } else {
            // Use default values if mainConfig is null
            hide_player = false;
            tab_health = false;
            tag_health = false;
            item_merge = false;
            hunger_change = false;
            clear_bottle = false;
            fast_respawn = false;
            date_format = "yyyy-MM-dd HH:mm:ss";
            chat_format_enabled = false;
            chat_format_chat_lobby = false;
            chat_format_chat_all = false;
            chat_format_chat_spectator = false;
            chat_format_lobby = "";
            chat_format_lobby_team = "";
            chat_format_all_prefix = new ArrayList<>();
            chat_format_ingame = "";
            chat_format_ingame_all = "";
            chat_format_spectator = "";
            final_killed_enabled = false;
            final_killed_message = "";
            timecommand_startcommand = new ArrayList<>();
            select_team_enabled = false;
            select_team_status_select = "";
            select_team_status_inteam = "";
            select_team_status_team_full = "";
            select_team_no_players = "";
            select_team_item_name = "";
            select_team_item_lore = new ArrayList<>();
            lobby_block_enabled = false;
            lobby_block_position_1_x = 0;
            lobby_block_position_1_y = 0;
            lobby_block_position_1_z = 0;
            lobby_block_position_2_x = 0;
            lobby_block_position_2_y = 0;
            lobby_block_position_2_z = 0;
            rejoin_enabled = false;
            rejoin_message_rejoin = "";
            rejoin_message_error = "";
            bowdamage_enabled = false;
            bowdamage_title = "";
            bowdamage_subtitle = "";
            bowdamage_message = "";
            damagetitle_enabled = false;
            damagetitle_title = "";
            damagetitle_subtitle = "";
            jointitle_enabled = false;
            jointitle_title = "";
            jointitle_subtitle = "";
            die_out_title_enabled = false;
            die_out_title_title = "";
            die_out_title_subtitle = "";
            destroyed_title_enabled = false;
            destroyed_title_title = "";
            destroyed_title_subtitle = "";
            start_title_enabled = false;
            start_title_title = new ArrayList<>();
            start_title_subtitle = "";
            victory_title_enabled = false;
            victory_title_title = new ArrayList<>();
            victory_title_subtitle = "";
            play_sound_enabled = false;
            play_sound_sound_start = new ArrayList<>();
            play_sound_sound_death = new ArrayList<>();
            play_sound_sound_kill = new ArrayList<>();
            play_sound_sound_upgrade = new ArrayList<>();
            play_sound_sound_no_resource = new ArrayList<>();
            play_sound_sound_sethealth = new ArrayList<>();
            play_sound_sound_enable_witherbow = new ArrayList<>();
            play_sound_sound_witherbow = new ArrayList<>();
            play_sound_sound_deathmode = new ArrayList<>();
            play_sound_sound_over = new ArrayList<>();
            spectator_enabled = false;
            spectator_centre_enabled = false;
            spectator_centre_height = 0.0;
            spectator_spectator_target_title = "";
            spectator_spectator_target_subtitle = "";
            spectator_quit_spectator_title = "";
            spectator_quit_spectator_subtitle = "";
            spectator_speed_enabled = false;
            spectator_speed_slot = 0;
            spectator_speed_item = 0;
            spectator_speed_item_name = "";
            spectator_speed_item_lore = new ArrayList<>();
            spectator_speed_gui_title = "";
            spectator_speed_no_speed = "";
            spectator_speed_speed_1 = "";
            spectator_speed_speed_2 = "";
            spectator_speed_speed_3 = "";
            spectator_speed_speed_4 = "";
            spectator_fast_join_enabled = false;
            spectator_fast_join_slot = 0;
            spectator_fast_join_item = 0;
            spectator_fast_join_item_name = "";
            spectator_fast_join_item_lore = new ArrayList<>();
            spectator_fast_join_group = "";
            compass_enabled = false;
            compass_item_name = "";
            compass_back = "";
            compass_item_lore = new ArrayList<>();
            compass_lore_send_message = new ArrayList<>();
            compass_lore_select_team = new ArrayList<>();
            compass_lore_select_resources = new ArrayList<>();
            compass_resources = new ArrayList<>();
            compass_resources_name = new HashMap<>();
            compass_gui_title = "";
            compass_item_III_II = "";
            compass_item_IV_II = "";
            compass_item_V_II = "";
            compass_item_VI_II = "";
            compass_item_VII_II = "";
            compass_item_VIII_II = "";
            compass_item_III_III = "";
            compass_item_IV_III = "";
            compass_item_V_III = "";
            compass_item_VI_III = "";
            compass_item_VII_III = "";
            compass_message_III_II = "";
            compass_message_IV_II = "";
            compass_message_V_II = "";
            compass_message_VI_II = "";
            compass_message_VII_II = "";
            compass_message_VIII_II = "";
            compass_message_III_III = "";
            compass_message_IV_III = "";
            compass_message_V_III = "";
            compass_message_VI_III = "";
            compass_message_VII_III = "";
            graffiti_enabled = false;
            graffiti_holographic_enabled = false;
            graffiti_holographic_text = "";
            shop_enabled = false;
            shop_item_shop_type = "";
            shop_item_shop_skin = "";
            shop_item_shop_look = false;
            shop_team_shop_type = "";
            shop_team_shop_skin = "";
            shop_team_shop_look = false;
            shop_item_shop_name = new ArrayList<>();
            shop_team_shop_name = new ArrayList<>();
            respawn_enabled = false;
            respawn_centre_enabled = false;
            respawn_centre_height = 0.0;
            respawn_protected_enabled = false;
            respawn_protected_time = 0;
            respawn_respawn_delay = 0;
            respawn_countdown_title = "";
            respawn_countdown_subtitle = "";
            respawn_countdown_message = "";
            respawn_respawn_title = "";
            respawn_respawn_subtitle = "";
            respawn_respawn_message = "";
            giveitem_keeparmor = false;
            giveitem_armor_helmet_item = new HashMap<>();
            giveitem_armor_chestplate_item = new HashMap<>();
            giveitem_armor_leggings_item = new HashMap<>();
            giveitem_armor_boots_item = new HashMap<>();
            giveitem_armor_helmet_give = "";
            giveitem_armor_chestplate_give = "";
            giveitem_armor_leggings_give = "";
            giveitem_armor_boots_give = "";
            giveitem_armor_helmet_move = false;
            giveitem_armor_chestplate_move = false;
            giveitem_armor_leggings_move = false;
            giveitem_armor_boots_move = false;
            sethealth_start_enabled = false;
            sethealth_start_health = 0;
            resourcelimit_enabled = false;
            resourcelimit_limit = new ArrayList<>();
            spread_resource_enabled = false;
            spread_resource_launch = false;
            spread_resource_range = 0.0;
            game_chest_enabled = false;
            game_chest_range = 0;
            game_chest_message = "";
            invisibility_player_enabled = false;
            invisibility_player_footstep = false;
            invisibility_player_hide_particles = false;
            invisibility_player_damage_show_player = false;
            witherbow_enabled = false;
            witherbow_gametime = 0;
            witherbow_already_starte = "";
            witherbow_title = "";
            witherbow_subtitle = "";
            witherbow_message = "";
            deathmode_enabled = false;
            deathmode_gametime = 0;
            deathmode_title = "";
            deathmode_subtitle = "";
            deathmode_message = "";
            deathitem_enabled = false;
            deathitem_items = new ArrayList<>();
            deathitem_item_name_chinesize = false;
            deathitem_message = "";
            nobreakbed_enabled = false;
            nobreakbed_gametime = 0;
            nobreakbed_nobreakmessage = "";
            nobreakbed_title = "";
            nobreakbed_subtitle = "";
            nobreakbed_message = "";
            spawn_no_build_spawn_enabled = false;
            spawn_no_build_spawn_range = 0;
            spawn_no_build_resource_enabled = false;
            spawn_no_build_resource_range = 0;
            spawn_no_build_message = "";
            holographic_resource_enabled = false;
            holographic_bed_title_bed_alive_enabled = false;
            holographic_bed_title_bed_destroyed_enabled = false;
            holographic_resource_speed = 0.0;
            holographic_resource = new ArrayList<>();
            holographic_bedtitle_bed_alive_title = "";
            holographic_bedtitle_bed_destroyed_title = "";
            overstats_enabled = false;
            overstats_message = new ArrayList<>();
            actionbar = "";
            timer = new HashMap<>();
            planinfo = new ArrayList<>();
            playertag_prefix = "";
            playertag_suffix = "";
            scoreboard_interval = 0;
            scoreboard_title = new ArrayList<>();
            scoreboard_you = "";
            scoreboard_team_bed_status_bed_alive = "";
            scoreboard_team_bed_status_bed_destroyed = "";
            scoreboard_team_status_format_bed_alive = "";
            scoreboard_team_status_format_bed_destroyed = "";
            scoreboard_team_status_format_team_dead = "";
            scoreboard_lines = new HashMap<>();
            lobby_scoreboard_enabled = false;
            lobby_scoreboard_interval = 0;
            lobby_scoreboard_state_waiting = "";
            lobby_scoreboard_state_countdown = "";
            lobby_scoreboard_title = new ArrayList<>();
            lobby_scoreboard_lines = new ArrayList<>();
        }

        // ==================== LOAD TEAM SHOP CONFIG ====================
        if (teamShopConfig != null) {
            teamshop_enabled = teamShopConfig.getBoolean("enabled");
            teamshop_upgrade_shop_title = colorize(teamShopConfig.getString("upgrade_shop.title"));
            teamshop_upgrade_shop_frame = colorizeList(teamShopConfig.getStringList("upgrade_shop.frame"));
            teamshop_upgrade_shop_trap_item = teamShopConfig.getString("upgrade_shop.trap.item");
            teamshop_upgrade_shop_trap_name = colorize(teamShopConfig.getString("upgrade_shop.trap.name"));
            teamshop_upgrade_shop_trap_lore = colorizeList(teamShopConfig.getStringList("upgrade_shop.trap.lore"));
            teamshop_trap_shop_title = colorize(teamShopConfig.getString("trap_shop.title"));
            teamshop_trap_shop_back = colorizeList(teamShopConfig.getStringList("trap_shop.back"));
            teamshop_message_upgrade = colorize(teamShopConfig.getString("message.upgrade"));
            teamshop_message_no_resource = colorize(teamShopConfig.getString("message.no_resource"));
            teamshop_state_no_resource = colorize(teamShopConfig.getString("state.no_resource"));
            teamshop_state_lock = colorize(teamShopConfig.getString("state.lock"));
            teamshop_state_unlock = colorize(teamShopConfig.getString("state.unlock"));
            teamshop_trap_cooldown = teamShopConfig.getInt("trap_cooldown");

            teamshop_trap_trap_list_trap_1_lock = colorizeList(teamShopConfig.getStringList("trap.trap_list.trap_1.lock"));
            teamshop_trap_trap_list_trap_1_unlock = colorizeList(teamShopConfig.getStringList("trap.trap_list.trap_1.unlock"));
            teamshop_trap_trap_list_trap_2_lock = colorizeList(teamShopConfig.getStringList("trap.trap_list.trap_2.lock"));
            teamshop_trap_trap_list_trap_2_unlock = colorizeList(teamShopConfig.getStringList("trap.trap_list.trap_2.unlock"));
            teamshop_trap_trap_list_trap_3_lock = colorizeList(teamShopConfig.getStringList("trap.trap_list.trap_3.lock"));
            teamshop_trap_trap_list_trap_3_unlock = colorizeList(teamShopConfig.getStringList("trap.trap_list.trap_3.unlock"));

            teamshop_trap_level_cost = new HashMap<>();
            teamshop_trap_level_cost.put(1, teamShopConfig.getString("trap.cost.level_1"));
            teamshop_trap_level_cost.put(2, teamShopConfig.getString("trap.cost.level_2"));
            teamshop_trap_level_cost.put(3, teamShopConfig.getString("trap.cost.level_3"));

            teamshop_upgrade_enabled = new LinkedHashMap<>();
            teamshop_upgrade_item = new HashMap<>();
            teamshop_upgrade_name = new HashMap<>();
            teamshop_upgrade_level_cost = new HashMap<>();
            teamshop_upgrade_level_lore = new HashMap<>();

            // Load all upgrade types
            loadUpgradeConfig(teamShopConfig, "SHARPNESS", "sword_sharpness", new int[]{1, 2});
            loadUpgradeConfig(teamShopConfig, "PROTECTION", "armor_protection", new int[]{1, 2, 3, 4});
            loadUpgradeConfig(teamShopConfig, "FAST_DIG", "fast_dig", new int[]{1, 2});
            loadUpgradeConfigIronForge(teamShopConfig);
            loadUpgradeConfig(teamShopConfig, "HEAL", "heal", new int[]{1});
            loadUpgradeConfig(teamShopConfig, "TRAP", "trap", null);
            loadUpgradeConfig(teamShopConfig, "COUNTER_OFFENSIVE_TRAP", "counter_offensive_trap", null);
            loadUpgradeConfig(teamShopConfig, "ALARM_TRAP", "alarm_trap", null);
            loadUpgradeConfig(teamShopConfig, "DEFENSE", "defense", null);

            // Load specific upgrade settings
            teamshop_upgrade_heal_trigger_range = teamShopConfig.getInt("upgrade.heal.trigger_range");
            teamshop_upgrade_trap_trigger_range = teamShopConfig.getInt("upgrade.trap.trigger_range");
            teamshop_upgrade_trap_trigger_title = colorize(teamShopConfig.getString("upgrade.trap.trigger.title"));
            teamshop_upgrade_trap_trigger_subtitle = colorize(teamShopConfig.getString("upgrade.trap.trigger.subtitle"));
            teamshop_upgrade_trap_trigger_message = colorize(teamShopConfig.getString("upgrade.trap.trigger.message"));
            teamshop_upgrade_counter_offensive_trap_trigger_range = teamShopConfig.getInt("upgrade.counter_offensive_trap.trigger_range");
            teamshop_upgrade_counter_offensive_trap_effect_range = teamShopConfig.getInt("upgrade.counter_offensive_trap.effect_range");
            teamshop_upgrade_alarm_trap_trigger_range = teamShopConfig.getInt("upgrade.alarm_trap.trigger_range");
            teamshop_upgrade_alarm_trap_trigger_title = colorize(teamShopConfig.getString("upgrade.alarm_trap.trigger.title"));
            teamshop_upgrade_alarm_trap_trigger_subtitle = colorize(teamShopConfig.getString("upgrade.alarm_trap.trigger.subtitle"));
            teamshop_upgrade_alarm_trap_trigger_message = colorize(teamShopConfig.getString("upgrade.alarm_trap.trigger.message"));
            teamshop_upgrade_defense_trigger_range = teamShopConfig.getInt("upgrade.defense.trigger_range");

            // Iron forge resources
            teamshop_upgrade_iron_forge_level_resources = new HashMap<>();
            for (int i = 0; i < 5; i++) {
                teamshop_upgrade_iron_forge_level_resources.put(i, teamShopConfig.getStringList("upgrade.iron_forge.level_" + i + ".resources"));
            }
        } else {
            // Use default values if teamShopConfig is null
            // Note: If this is a per-game config, teamShopConfig should have been replaced
            // with defaultConfig's teamShopConfig at the beginning of constructor
            teamshop_enabled = false;
            teamshop_upgrade_shop_title = "";
            teamshop_upgrade_shop_frame = new ArrayList<>();
            teamshop_upgrade_shop_trap_item = "";
            teamshop_upgrade_shop_trap_name = "";
            teamshop_upgrade_shop_trap_lore = new ArrayList<>();
            teamshop_trap_shop_title = "";
            teamshop_trap_shop_back = new ArrayList<>();
            teamshop_message_upgrade = "";
            teamshop_message_no_resource = "";
            teamshop_state_no_resource = "";
            teamshop_state_lock = "";
            teamshop_state_unlock = "";
            teamshop_trap_cooldown = 0;
            teamshop_trap_trap_list_trap_1_lock = new ArrayList<>();
            teamshop_trap_trap_list_trap_1_unlock = new ArrayList<>();
            teamshop_trap_trap_list_trap_2_lock = new ArrayList<>();
            teamshop_trap_trap_list_trap_2_unlock = new ArrayList<>();
            teamshop_trap_trap_list_trap_3_lock = new ArrayList<>();
            teamshop_trap_trap_list_trap_3_unlock = new ArrayList<>();
            teamshop_trap_level_cost = new HashMap<>();
            teamshop_upgrade_enabled = new LinkedHashMap<>();
            teamshop_upgrade_item = new HashMap<>();
            teamshop_upgrade_name = new HashMap<>();
            teamshop_upgrade_level_cost = new HashMap<>();
            teamshop_upgrade_level_lore = new HashMap<>();
            teamshop_upgrade_iron_forge_level_resources = new HashMap<>();
            teamshop_upgrade_heal_trigger_range = 0;
            teamshop_upgrade_trap_trigger_range = 0;
            teamshop_upgrade_trap_trigger_title = "";
            teamshop_upgrade_trap_trigger_subtitle = "";
            teamshop_upgrade_trap_trigger_message = "";
            teamshop_upgrade_counter_offensive_trap_trigger_range = 0;
            teamshop_upgrade_counter_offensive_trap_effect_range = 0;
            teamshop_upgrade_alarm_trap_trigger_range = 0;
            teamshop_upgrade_alarm_trap_trigger_title = "";
            teamshop_upgrade_alarm_trap_trigger_subtitle = "";
            teamshop_upgrade_alarm_trap_trigger_message = "";
            teamshop_upgrade_defense_trigger_range = 0;
        }
    }
    
    private void loadUpgradeConfig(FileConfiguration config, String type, String path, int[] levels) {
        String fullPath = "upgrade." + path;
        teamshop_upgrade_enabled.put(type, config.getBoolean(fullPath + ".enabled"));
        teamshop_upgrade_item.put(type, config.getString(fullPath + ".item"));
        teamshop_upgrade_name.put(type, colorize(config.getString(fullPath + ".name")));
        
        if (levels != null) {
            Map<Integer, String> costMap = new HashMap<>();
            Map<Integer, List<String>> loreMap = new HashMap<>();
            
            for (int level : levels) {
                costMap.put(level, config.getString(fullPath + ".level_" + level + ".cost"));
                loreMap.put(level, colorizeList(config.getStringList(fullPath + ".level_" + level + ".lore")));
            }
            loreMap.put(levels.length + 1, colorizeList(config.getStringList(fullPath + ".level_full.lore")));
            
            teamshop_upgrade_level_cost.put(type, costMap);
            teamshop_upgrade_level_lore.put(type, loreMap);
        } else {
            // For trap types, load level_1 and level_full
            Map<Integer, List<String>> loreMap = new HashMap<>();
            loreMap.put(1, colorizeList(config.getStringList(fullPath + ".level_1.lore")));
            loreMap.put(2, colorizeList(config.getStringList(fullPath + ".level_full.lore")));
            teamshop_upgrade_level_lore.put(type, loreMap);
        }
    }
    
    private void loadUpgradeConfigIronForge(FileConfiguration config) {
        String type = "IRON_FORGE";
        String path = "upgrade.iron_forge";
        
        teamshop_upgrade_enabled.put(type, config.getBoolean(path + ".enabled"));
        teamshop_upgrade_item.put(type, config.getString(path + ".item"));
        teamshop_upgrade_name.put(type, colorize(config.getString(path + ".name")));
        
        Map<Integer, String> costMap = new HashMap<>();
        Map<Integer, List<String>> loreMap = new HashMap<>();
        
        for (int i = 1; i < 5; i++) {
            costMap.put(i, config.getString(path + ".level_" + i + ".cost"));
            loreMap.put(i, colorizeList(config.getStringList(path + ".level_" + i + ".lore")));
        }
        loreMap.put(5, colorizeList(config.getStringList(path + ".level_full.lore")));
        
        teamshop_upgrade_level_cost.put(type, costMap);
        teamshop_upgrade_level_lore.put(type, loreMap);
    }
    
    private String colorize(String text) {
        if (text == null) return null;
        return text.replace("&", "§");
    }
    
    private List<String> colorizeList(List<String> list) {
        if (list == null) return null;
        List<String> result = new ArrayList<>();
        for (String s : list) {
            result.add(colorize(s));
        }
        return result;
    }
    
    public String getGameName() {
        return gameName;
    }
    
    public FileConfiguration getTeamShopConfig() {
        return teamShopConfig;
    }
}

