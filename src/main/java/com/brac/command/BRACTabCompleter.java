package com.brac.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.brac.BedwarsRelAutoConfig;
import com.brac.config.ConfigType;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;

/**
 * Tab completer for BRAC commands
 * 
 */
public class BRACTabCompleter implements TabCompleter {
    
    private final BedwarsRelAutoConfig plugin;
    
    public BRACTabCompleter(BedwarsRelAutoConfig plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("create", "enable", "disable", "delete", "list", "reload", "randomenable", "randomout", "help"));
            return filterCompletions(completions, args[0]);
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "create":
                case "delete":
                    completions.addAll(Arrays.asList(ConfigType.getTypeNames()));
                    break;

                case "enable":
                case "disable":
                case "randomenable":
                case "re":
                case "randomout":
                case "ro":
                    completions.addAll(getGameNames());
                    break;
            }
            
            return filterCompletions(completions, args[1]);
        }
        
        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "create":               
                   break;

                case "delete":
                    String type = args[1];
                    ConfigType configType = ConfigType.fromString(type);
                    if (configType != null) {
                        completions.addAll(getTemplateNames(configType));
                    }
                    break;

                case "enable":
                case "disable":
                case "randomenable":
                case "re":
                case "randomout":
                case "ro":
                  
                    completions.addAll(Arrays.asList(ConfigType.getTypeNames()));
                    break;
            }
            
            return filterCompletions(completions, args[2]);
        }
        
        if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "enable":
         
                    String type = args[2];
                    ConfigType configType = ConfigType.fromString(type);
                    if (configType != null) {
                        completions.addAll(getTemplateNames(configType));
                    }
                    break;

                case "randomout":
                case "ro":
                   
                    String randomType = args[2];
                    ConfigType randomConfigType = ConfigType.fromString(randomType);
                    if (randomConfigType != null) {
                        completions.addAll(getTemplateNames(randomConfigType));
                    }
                    break;

                case "delete":
                  
                    completions.add("confirm");
                    break;
            }
            
            return filterCompletions(completions, args[3]);
        }
        
        return completions;
    }
    
  
    private List<String> filterCompletions(List<String> completions, String input) {
        List<String> filtered = new ArrayList<>();
        String lowerInput = input.toLowerCase();
        
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(lowerInput)) {
                filtered.add(completion);
            }
        }
        
        return filtered;
    }
    
  
    private List<String> getGameNames() {
        List<String> gameNames = new ArrayList<>();
        
        try {
            BedwarsRel bedwarsRel = BedwarsRel.getInstance();
            if (bedwarsRel != null && bedwarsRel.getGameManager() != null) {
                for (Game game : bedwarsRel.getGameManager().getGames()) {
                    gameNames.add(game.getName());
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting game names: " + e.getMessage());
        }
        
        return gameNames;
    }
    
   
    private List<String> getTemplateNames(ConfigType type) {
        List<String> templateNames = new ArrayList<>();
        
        Set<String> allTemplates = plugin.getConfigManager().getTemplateNames();
        for (String templateName : allTemplates) {
            if (plugin.getConfigManager().templateExists(templateName, type)) {
                templateNames.add(templateName);
            }
        }
        
        return templateNames;
    }
}

