package com.badbones69.epicsellchest;

import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.FileManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EpicSellChest extends JavaPlugin {

    // private final HashMap<UUID, Location> pos1 = new HashMap<>();

    public final FileManager fileManager = FileManager.getInstance();
    public final CrazyManager crazyManager = CrazyManager.getInstance();

    // private final HashMap<UUID, Location> pos2 = new HashMap<>();
    
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