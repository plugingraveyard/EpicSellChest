package com.badbones69.epicsellchest.controllers;

import com.badbones69.epicsellchest.Methods;
import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.FileManager.Files;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignControl implements Listener {
    
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @EventHandler
    public void onSignMake(SignChangeEvent e) {
        Player player = e.getPlayer();
        FileConfiguration config = Files.CONFIG.getFile();
        
        if (e.getLine(0).equalsIgnoreCase(config.getString("Settings.Sign-Options.Sign-Maker"))) {
            if (player.hasPermission("epicsellchest.sign.make") || player.hasPermission("epicsellchest.admin")) {
                e.setLine(0, Methods.color(config.getString("Settings.Sign-Options.Lines.1")));
                e.setLine(1, Methods.color(config.getString("Settings.Sign-Options.Lines.2")));
                e.setLine(2, Methods.color(config.getString("Settings.Sign-Options.Lines.3")));
                e.setLine(3, Methods.color(config.getString("Settings.Sign-Options.Lines.4")));
            } else {
                //player.sendMessage(Messages.NO_PERMISSION.getMessage());
            }
        }
    }
    
    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        FileConfiguration config = Files.CONFIG.getFile();
        
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getState() instanceof Sign sign) {
            
            if (sign.getLine(0).equals(Methods.color(config.getString("Settings.Sign-Options.Lines.1")))
            && sign.getLine(1).equals(Methods.color(config.getString("Settings.Sign-Options.Lines.2")))
            && sign.getLine(2).equals(Methods.color(config.getString("Settings.Sign-Options.Lines.3")))
            && sign.getLine(3).equals(Methods.color(config.getString("Settings.Sign-Options.Lines.4")))) {
                if (player.hasPermission("epicsellchest.sign.use") || player.hasPermission("epicsellchest.admin")) {
                    crazyManager.openSellChestGUI(player);
                } else {
                    //player.sendMessage(Messages.NO_PERMISSION.getMessage());
                }
            }
        }
    }
    
}