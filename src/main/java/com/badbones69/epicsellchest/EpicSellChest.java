package com.badbones69.epicsellchest;

import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.FileManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EpicSellChest extends JavaPlugin {
    
    public final FileManager fileManager = FileManager.getInstance();
    public final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Override
    public void onEnable() {
        
        crazyManager.loadPlugin(this);
        
        fileManager.logInfo(true).setup(this);
        
        crazyManager.load();
        
        PluginManager pluginManager = getServer().getPluginManager();
    }
    
    @Override
    public void onDisable() {
    
    }
    
}