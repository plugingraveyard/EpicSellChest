package com.badbones69.epicsellchest.multisupport;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum Support {
    
    VAULT("Vault"),
    NO_CHEAT_PLUS("NoCheatPlus"),
    SPARTAN("Spartan"),
    SHOP_GUI_PLUS("ShopGUIPlus");
    
    private final String name;
    
    Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isEnabled() {
        return Bukkit.getServer().getPluginManager().getPlugin(name) != null;
    }
    
    public Plugin getPlugin() {
        return Bukkit.getServer().getPluginManager().getPlugin(name);
    }
    
}