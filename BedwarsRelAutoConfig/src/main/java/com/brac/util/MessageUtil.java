package com.brac.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageUtil {
    private static FileConfiguration messages;
    private static String prefix;
    
  
    public static void loadMessages(File messagesFile) {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        prefix = colorize(messages.getString("prefix", "&8[&6BRAC&8] &r"));
    }
    
  
    public static String getMessage(String path) {
        if (messages == null) {
            return colorize("&cMessages not loaded!");
        }
        String message = messages.getString(path);
        if (message == null) {
            return colorize("&cMessage not found: " + path);
        }
        return colorize(message);
    }
    
  
    public static String getMessage(String path, Map<String, String> placeholders) {
        String message = getMessage(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
    
  
    public static void send(CommandSender sender, String path) {
        sender.sendMessage(prefix + getMessage(path));
    }
    
 
    public static void send(CommandSender sender, String path, Map<String, String> placeholders) {
        sender.sendMessage(prefix + getMessage(path, placeholders));
    }
    
 
    public static void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }
   
    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    
    public static Map<String, String> placeholder(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
    
    
    public static Map<String, String> placeholder(String key1, String value1, String key2, String value2) {
        Map<String, String> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }
    
    
    public static Map<String, String> placeholder(String key1, String value1, String key2, String value2, 
                                                   String key3, String value3) {
        Map<String, String> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }
}

