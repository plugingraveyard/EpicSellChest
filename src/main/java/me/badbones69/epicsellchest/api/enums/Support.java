package me.badbones69.epicsellchest.api.enums;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum Support {
    
    VAULT("Vault"),
    DAKATA_ANTI_CHEAT("DakataAntiCheat"),
    NO_CHEAT_PLUS("NoCheatPlus"),
    SPARTAN("Spartan"),
    SHOP_GUI_PLUS("ShopGUIPlus");
    
    private String name;
    
    private Support(String name) {
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