package com.brac.config;

public enum ConfigType {
  
    CONFIG("config", "config.yml", "BedwarsRel"),
   
    SHOP("shop", "shop.yml", "BedwarsRel"),
   
    TEAMSHOP("teamshop", "team_shop.yml", "BedwarsScoreBoardAddon"),
    
    SBACONFIG("SBAconfig", "config.yml", "BedwarsScoreBoardAddon"),

    ITEMS("items", "items.yml", "BedwarsItemAddon");

    private final String name;
    private final String fileName;
    private final String plugin;
    
    ConfigType(String name, String fileName, String plugin) {
        this.name = name;
        this.fileName = fileName;
        this.plugin = plugin;
    }
    
  
    public String getName() {
        return name;
    }
    
    
    public String getFileName() {
        return fileName;
    }
    
  
    public String getPlugin() {
        return plugin;
    }
    
    public static ConfigType fromString(String name) {
        for (ConfigType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    public static boolean isValid(String name) {
        return fromString(name) != null;
    }
    
    public static String[] getTypeNames() {
        ConfigType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getName();
        }
        return names;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

