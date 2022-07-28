package com.badbones69.epicsellchest.commands;

import com.badbones69.epicsellchest.Methods;
import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.currency.Currency;
import com.badbones69.epicsellchest.api.currency.CustomCurrency;
import com.badbones69.epicsellchest.api.enums.Messages;
import com.badbones69.epicsellchest.api.enums.SellType;
import com.badbones69.epicsellchest.api.events.SellChestEvent;
import com.badbones69.epicsellchest.api.objects.SellItem;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Command(value = "sellchest", alias = {"sc"})
public class SellChestCmdNew extends BaseCommand {
    
    private CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Default
    @Permission("epicsellchest.single")
    public void onSingleSell(Player player) {
        UUID uuid = player.getUniqueId();
        //Gets the block they are looking at.
        Block block = player.getTargetBlock(null, 5);
        //Checks to see if the block is a chest or trap chest.
        if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
            //If it isn't then it will set the block as the block they are standing on.
            block = player.getLocation().getBlock();
        }
        //Checks to see if the block is a chest or trap chest.
        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            BlockBreakEvent check = new BlockBreakEvent(block, player);
            Bukkit.getPluginManager().callEvent(check);
            if (!check.isCancelled()) {
                Chest chest = (Chest) block.getState();
                if (!Methods.isInvEmpty(chest.getInventory())) {
                    if (!crazyManager.needsTwoFactorAuth(uuid)) {
                        crazyManager.removeTwoFactorAuth(uuid);
                        ArrayList<SellItem> items = crazyManager.getSellableItems(chest.getInventory());
                        if (items.size() > 0) {
                            SellChestEvent event = new SellChestEvent(player, items, SellType.SINGLE);
                            Bukkit.getPluginManager().callEvent(event);
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
                        player.sendMessage(Messages.TWO_FACTOR_AUTH.getMessage());
                    }
                } else {
                    player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                }
            } else {
                player.sendMessage(Messages.CANT_SELL_CHEST.getMessage());
            }
        } else {
            player.sendMessage(Messages.NOT_A_CHEST.getMessage());
        }
    }
    
    @SubCommand("help")
    @Permission("epicsellchest.help")
    public void helpCommand(Player player) {
        player.sendMessage(Messages.NO_PERMISSION.getMessageNoPrefix());
        System.out.println(1);
    }
    
}