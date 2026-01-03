package com.brac.command;

import com.brac.BedwarsRelAutoConfig;
import com.brac.config.ConfigType;
import com.brac.util.MessageUtil;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BRACCommand implements CommandExecutor {
    
    private final BedwarsRelAutoConfig plugin;
    
    public BRACCommand(BedwarsRelAutoConfig plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                return handleCreate(sender, args);
            case "enable":
                return handleEnable(sender, args);
            case "disable":
                return handleDisable(sender, args);
            case "delete":
                return handleDelete(sender, args);
            case "list":
                return handleList(sender, args);
            case "reload":
                return handleReload(sender, args);
            case "randomenable":
            case "re":
                return handleRandomEnable(sender, args);
            case "randomout":
            case "ro":
                return handleRandomOut(sender, args);
            case "help":
                sendHelp(sender);
                return true;
            default:
                MessageUtil.send(sender, "command.invalid-usage");
                return true;
        }
    }
    
    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("brac.create")) {
            MessageUtil.send(sender, "command.no-permission");
            return true;
        }
        
        if (args.length < 3) {
            MessageUtil.sendRaw(sender, "&cUsage: /brac create <type> <name>");
            MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.types"));
            return true;
        }
        
        String typeName = args[1];
        String templateName = args[2];
        
        ConfigType type = ConfigType.fromString(typeName);
        if (type == null) {
            MessageUtil.send(sender, "create.invalid-type");
            return true;
        }
        
        if (plugin.getConfigManager().templateExists(templateName, type)) {
            MessageUtil.send(sender, "create.already-exists", 
                MessageUtil.placeholder("name", templateName));
            return true;
        }
        // Create template
        if (plugin.getConfigManager().createTemplate(templateName, type)) {
            MessageUtil.send(sender, "create.success", 
                MessageUtil.placeholder("name", templateName, "type", type.getName()));
        } else {
            MessageUtil.send(sender, "create.source-not-found", 
                MessageUtil.placeholder("plugin", type.getPlugin()));
        }
        
        return true;
    }
    
    private boolean handleEnable(CommandSender sender, String[] args) {
        if (!sender.hasPermission("brac.enable")) {
            MessageUtil.send(sender, "command.no-permission");
            return true;
        }
        
        if (args.length < 4) {
            MessageUtil.sendRaw(sender, "&cUsage: /brac enable <game> <type> <name>");
            return true;
        }
        
        String gameName = args[1];
        String typeName = args[2];
        String templateName = args[3];
        
        Game game = BedwarsRel.getInstance().getGameManager().getGame(gameName);
        if (game == null) {
            MessageUtil.send(sender, "enable.game-not-found", 
                MessageUtil.placeholder("game", gameName));
            return true;
        }
        
        ConfigType type = ConfigType.fromString(typeName);
        if (type == null) {
            MessageUtil.send(sender, "create.invalid-type");
            return true;
        }
        
        if (!plugin.getConfigManager().templateExists(templateName, type)) {
            MessageUtil.send(sender, "enable.config-not-found", 
                MessageUtil.placeholder("name", templateName));
            return true;
        }
        
        // Enable config
        if (plugin.getMappingManager().enableConfig(gameName, type, templateName)) {
            MessageUtil.send(sender, "enable.success", 
                MessageUtil.placeholder("type", type.getName(), "name", templateName, "game", gameName));
        } else {
            MessageUtil.send(sender, "enable.error", 
                MessageUtil.placeholder("error", "Unknown error"));
        }
        
        return true;
    }
    
    private boolean handleDisable(CommandSender sender, String[] args) {
        if (!sender.hasPermission("brac.disable")) {
            MessageUtil.send(sender, "command.no-permission");
            return true;
        }
        
        if (args.length < 3) {
            MessageUtil.sendRaw(sender, "&cUsage: /brac disable <game> <type>");
            return true;
        }
        
        String gameName = args[1];
        String typeName = args[2];
        
        Game game = BedwarsRel.getInstance().getGameManager().getGame(gameName);
        if (game == null) {
            MessageUtil.send(sender, "disable.game-not-found", 
                MessageUtil.placeholder("game", gameName));
            return true;
        }
        
        ConfigType type = ConfigType.fromString(typeName);
        if (type == null) {
            MessageUtil.send(sender, "create.invalid-type");
            return true;
        }
        
        // Check if config is enabled
        if (!plugin.getMappingManager().hasCustomConfig(gameName, type)) {
            MessageUtil.send(sender, "disable.not-enabled", 
                MessageUtil.placeholder("game", gameName));
            return true;
        }
        
        // Disable config
        if (plugin.getMappingManager().disableConfig(gameName, type)) {
            MessageUtil.send(sender, "disable.success", 
                MessageUtil.placeholder("type", type.getName(), "game", gameName));
        } else {
            MessageUtil.send(sender, "disable.error", 
                MessageUtil.placeholder("error", "Unknown error"));
        }
        
        return true;
    }
    
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("brac.delete")) {
            MessageUtil.send(sender, "command.no-permission");
            return true;
        }
        
        if (args.length < 3) {
            MessageUtil.sendRaw(sender, "&cUsage: /brac delete <type> <name> [confirm]");
            return true;
        }
        
        String typeName = args[1];
        String templateName = args[2];
        boolean confirm = args.length >= 4 && args[3].equalsIgnoreCase("confirm");
        
        ConfigType type = ConfigType.fromString(typeName);
        if (type == null) {
            MessageUtil.send(sender, "create.invalid-type");
            return true;
        }
        
        // Check if template exists
        if (!plugin.getConfigManager().templateExists(templateName, type)) {
            MessageUtil.send(sender, "delete.not-found", 
                MessageUtil.placeholder("name", templateName));
            return true;
        }
        
        // Check if template is in use
        List<String> gamesUsing = plugin.getMappingManager().getGamesUsingTemplate(templateName, type);
        if (!gamesUsing.isEmpty() && !confirm) {
            MessageUtil.send(sender, "delete.in-use", 
                MessageUtil.placeholder("name", templateName, "games", String.join(", ", gamesUsing)));
            return true;
        }
        
        // Require confirmation
        if (!confirm) {
            MessageUtil.send(sender, "delete.confirm", 
                MessageUtil.placeholder("name", templateName, "type", type.getName()));
            return true;
        }
        
        // Disable for all games using this template
        for (String gameName : gamesUsing) {
            plugin.getMappingManager().disableConfig(gameName, type);
        }
        
        // Delete template
        if (plugin.getConfigManager().deleteTemplate(templateName, type)) {
            MessageUtil.send(sender, "delete.success", 
                MessageUtil.placeholder("name", templateName));
        } else {
            MessageUtil.send(sender, "delete.error", 
                MessageUtil.placeholder("error", "Failed to delete file"));
        }
        
        return true;
    }
    
    private boolean handleList(CommandSender sender, String[] args) {
        if (!sender.hasPermission("brac.list")) {
            MessageUtil.send(sender, "command.no-permission");
            return true;
        }

        // Header
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("list.header"));

        // List templates
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("list.templates-header"));
        Map<String, Set<ConfigType>> templates = plugin.getConfigManager().getAllTemplates();

        if (templates.isEmpty()) {
            MessageUtil.sendRaw(sender, MessageUtil.getMessage("list.no-templates"));
        } else {
            for (Map.Entry<String, Set<ConfigType>> entry : templates.entrySet()) {
                String templateName = entry.getKey();
                Set<ConfigType> types = entry.getValue();

                for (ConfigType type : types) {
                    MessageUtil.sendRaw(sender, MessageUtil.getMessage("list.template-item",
                        MessageUtil.placeholder("name", templateName, "type", type.getName())));
                }
            }
        }

        // List mappings
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("list.mappings-header"));
        Map<String, Map<ConfigType, String>> mappings = plugin.getMappingManager().getAllMappings();

        if (mappings.isEmpty()) {
            MessageUtil.sendRaw(sender, MessageUtil.getMessage("list.no-mappings"));
        } else {
            for (Map.Entry<String, Map<ConfigType, String>> entry : mappings.entrySet()) {
                String gameName = entry.getKey();
                Map<ConfigType, String> configs = entry.getValue();

                for (Map.Entry<ConfigType, String> configEntry : configs.entrySet()) {
                    ConfigType type = configEntry.getKey();
                    String templateName = configEntry.getValue();

                    MessageUtil.sendRaw(sender, MessageUtil.getMessage("list.mapping-item",
                        MessageUtil.placeholder("game", gameName, "type", type.getName(), "name", templateName)));
                }
            }
        }

        // Footer
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("list.footer"));

        return true;
    }

    private boolean handleReload(CommandSender sender, String[] args) {
        if (!sender.hasPermission("brac.reload")) {
            MessageUtil.send(sender, "command.no-permission");
            return true;
        }

        // Reload plugin config
        plugin.reloadConfig();

        // Reload managers
        plugin.getConfigManager().reload();
        plugin.getMappingManager().reload();

        // Reload messages
        MessageUtil.loadMessages(plugin.getMessagesFile());

        MessageUtil.send(sender, "command.reload-success");

        return true;
    }

    private boolean handleRandomEnable(CommandSender sender, String[] args) {
        // Check permission
        if (!sender.hasPermission("brac.randomenable")) {
            MessageUtil.send(sender, "command.no-permission");
            return true;
        }

        // Check arguments
        if (args.length < 2) {
            MessageUtil.send(sender, "random.invalid-usage-enable");
            return true;
        }

        String gameName = args[1];
        ConfigType type = null;

        // Parse type if provided
        if (args.length >= 3) {
            type = ConfigType.fromString(args[2]);
            if (type == null) {
                MessageUtil.send(sender, "create.invalid-type");
                return true;
            }
        }

        // Check if game exists
        Game game = BedwarsRel.getInstance().getGameManager().getGame(gameName);
        if (game == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("game", gameName);
            MessageUtil.send(sender, "enable.game-not-found", placeholders);
            return true;
        }

        // Enable random config selection for this game
        plugin.getMappingManager().enableRandomConfig(gameName, type);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("game", gameName);
        placeholders.put("type", type != null ? type.getName() : "所有类型");
        MessageUtil.send(sender, "random.enable-success", placeholders);

        return true;
    }

    private boolean handleRandomOut(CommandSender sender, String[] args) {
        // Check permission
        if (!sender.hasPermission("brac.randomout")) {
            MessageUtil.send(sender, "command.no-permission");
            return true;
        }

        // Check arguments
        if (args.length < 3) {
            MessageUtil.send(sender, "random.invalid-usage-out");
            return true;
        }

        String gameName = args[1];
        ConfigType type = ConfigType.fromString(args[2]);

        if (type == null) {
            MessageUtil.send(sender, "create.invalid-type");
            return true;
        }

        // Check if game exists
        Game game = BedwarsRel.getInstance().getGameManager().getGame(gameName);
        if (game == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("game", gameName);
            MessageUtil.send(sender, "enable.game-not-found", placeholders);
            return true;
        }

        // If name is provided, add exclusion
        if (args.length >= 4) {
            String templateName = args[3];

            // Check if template exists
            if (!plugin.getConfigManager().templateExists(templateName, type)) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("name", templateName);
                MessageUtil.send(sender, "enable.config-not-found", placeholders);
                return true;
            }

            // Add exclusion
            plugin.getRandomManager().addExclusion(gameName, type, templateName);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("game", gameName);
            placeholders.put("type", type.getName());
            placeholders.put("name", templateName);
            MessageUtil.send(sender, "random.out-add-success", placeholders);
        } else {
            // Remove all exclusions for this game+type
            Map<ConfigType, Set<String>> exclusions = plugin.getRandomManager().getExclusions(gameName);
            Set<String> typeExclusions = exclusions.get(type);

            if (typeExclusions == null || typeExclusions.isEmpty()) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("game", gameName);
                placeholders.put("type", type.getName());
                MessageUtil.send(sender, "random.out-none", placeholders);
                return true;
            }

            // Remove all
            for (String name : new HashSet<>(typeExclusions)) {
                plugin.getRandomManager().removeExclusion(gameName, type, name);
            }

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("game", gameName);
            placeholders.put("type", type.getName());
            MessageUtil.send(sender, "random.out-clear-success", placeholders);
        }

        return true;
    }

    /**
     * Send help message
     */
    private void sendHelp(CommandSender sender) {
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.header"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.create"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.enable"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.disable"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.delete"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.list"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.reload"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.randomenable"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.randomout"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.types"));
        MessageUtil.sendRaw(sender, MessageUtil.getMessage("help.footer"));
    }
}

