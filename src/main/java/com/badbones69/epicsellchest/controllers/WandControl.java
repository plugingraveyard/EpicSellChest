package com.badbones69.epicsellchest.controllers;

import com.badbones69.epicsellchest.Methods;
import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.currency.Currency;
import com.badbones69.epicsellchest.api.currency.CustomCurrency;
import com.badbones69.epicsellchest.api.enums.Messages;
import com.badbones69.epicsellchest.api.enums.SellType;
import com.badbones69.epicsellchest.api.events.SellChestEvent;
import com.badbones69.epicsellchest.api.objects.SellItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WandControl implements Listener {
    
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @EventHandler
    public void useWand(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Block block = e.getClickedBlock();
        ItemStack wand = Methods.getItemInHand(player);
        
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && wand != null && crazyManager.getSellingWand().isSimilar(wand) && block != null && (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST)) {
            e.setCancelled(true);
            BlockBreakEvent check = new BlockBreakEvent(block, player);
            crazyManager.getPlugin().getServer().getPluginManager().callEvent(check);
            
            if (!check.isCancelled()) {
                Chest chest = (Chest) block.getState();
                
                if (!Methods.isInvEmpty(chest.getInventory())) {
                    if (!crazyManager.needsTwoFactorAuth(uuid)) {
                        crazyManager.removeTwoFactorAuth(uuid);
                        ArrayList<SellItem> items = crazyManager.getSellableItems(chest.getInventory());
                        
                        if (items.size() > 0) {
                            SellChestEvent event = new SellChestEvent(player, items, SellType.SINGLE);
                            crazyManager.getPlugin().getServer().getPluginManager().callEvent(event);
                            
                            if (!event.isCancelled()) {
                                HashMap<String, String> placeholders = new HashMap<>();
                                for (Currency currency : Currency.values()) {
                                    placeholders.put("%" + currency.getName().toLowerCase() + "%", crazyManager.getFullCost(items, currency) + "");
                                    placeholders.put("%" + currency.getName() + "%", crazyManager.getFullCost(items, currency) + "");
                                }
                                
                                for (CustomCurrency currency : crazyManager.getCustomCurrencies()) {
                                    placeholders.put("%" + currency.name().toLowerCase() + "%", crazyManager.getFullCost(items, currency) + "");
                                    placeholders.put("%" + currency.name() + "%", crazyManager.getFullCost(items, currency) + "");
                                }
                                
                                crazyManager.sellSellableItems(player, items);
                                for (SellItem item : items) {
                                    if (item.hasSellingAmount()) {
                                        item.item().setAmount(item.item().getAmount() - (item.sellingAmount() * item.sellingMinimum()));
                                    } else {
                                        chest.getInventory().remove(item.item());
                                    }
                                }
                                player.sendMessage(Messages.SOLD_CHEST.getMessage(placeholders));
                            }
                        } else {
                            player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                        }
                    } else {
                        crazyManager.addTwoFactorAuth(uuid);
                        player.sendMessage(Messages.WAND_TWO_FACTOR_AUTH.getMessage());
                    }
                } else {
                    player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                }
            } else {
                player.sendMessage(Messages.CANT_SELL_CHEST.getMessage());
            }
        }
    }
    
}