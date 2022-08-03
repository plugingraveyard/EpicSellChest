package com.badbones69.epicsellchest.commands;

import com.badbones69.epicsellchest.Methods;
import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.FileManager;
import com.badbones69.epicsellchest.api.FileManager.Files;
import com.badbones69.epicsellchest.api.ItemBuilder;
import com.badbones69.epicsellchest.api.currency.Currency;
import com.badbones69.epicsellchest.api.currency.CustomCurrency;
import com.badbones69.epicsellchest.api.enums.Messages;
import com.badbones69.epicsellchest.api.enums.SellType;
import com.badbones69.epicsellchest.api.events.SellChestEvent;
import com.badbones69.epicsellchest.api.objects.SellItem;
import com.badbones69.epicsellchest.multisupport.Support;
import com.badbones69.epicsellchest.multisupport.anticheats.SpartanSupport;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.ArgName;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Command(value = "sellchest", alias = {"sc"})
public class SellChestCmdNew extends BaseCommand {
    
    private final HashMap<UUID, Location> pos1 = new HashMap<>();
    private final HashMap<UUID, Location> pos2 = new HashMap<>();
    
    private CrazyManager crazyManager = CrazyManager.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    
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
                                HashMap<String, String> placeholders = getPlaceholders(items);
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
    
    @SubCommand(value = "chunk")
    @Permission("epicsellchest.chunk")
    public void chunkCommand(CommandSender sender, @ArgName("[ID:MD] [ID:MD] [ID:MD]...") List<String> materials) {
        final Player player = (Player) sender;
        final UUID uuid = player.getUniqueId();
        if (!crazyManager.needsTwoFactorAuth(uuid)) {
            crazyManager.removeTwoFactorAuth(uuid);
            Location p1 = player.getLocation().getChunk().getBlock(0, 0, 0).getLocation();
            Location p2 = player.getLocation().getChunk().getBlock(15, 255, 15).getLocation();
            crazyManager.queryChests(player, p1, p2);
            player.sendMessage(Messages.LOADING_CHUNK_CHESTS.getMessage());
            new BukkitRunnable() {
                @Override
                public void run() {
                    List<Chest> chests = crazyManager.getChestQuery(player.getUniqueId());
                    if (chests != null) {
                        if (!chests.isEmpty()) {
                            if (Support.SPARTAN.isEnabled()) {
                                SpartanSupport.cancelBlockChecker(player);
                            }
                            HashMap<String, String> placeholders = new HashMap<>();
                            for (Chest chest : chests) {
                                BlockBreakEvent check = new BlockBreakEvent(chest.getBlock(), player);
                                Bukkit.getPluginManager().callEvent(check);
                                if (!check.isCancelled()) {
                                    ArrayList<SellItem> items;
                                    if (!materials.isEmpty()) {
                                        ArrayList<ItemStack> selling = new ArrayList<>();
                                        materials.forEach(id -> selling.add(new ItemBuilder().setMaterial(id).build()));
                                        items = crazyManager.getSellableItems(chest.getInventory(), selling);
                                    } else {
                                        items = crazyManager.getSellableItems(chest.getInventory());
                                    }
                                    SellChestEvent event = new SellChestEvent(player, items, SellType.CHUNK);
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (!event.isCancelled()) {
                                        placeholders = getPlaceholders(items);
                                        crazyManager.sellSellableItems(player, items);
                                        items.forEach(item -> chest.getInventory().remove(item.item()));
                                    }
                                }
                            }
                            player.sendMessage(placeholders.size() == 0 ? Messages.NO_CHESTS_IN_CHUNK.getMessage() : Messages.SOLD_CHUNK_CHESTS.getMessage(placeholders));
                        } else {
                            player.sendMessage(Messages.NO_CHESTS_IN_CHUNK.getMessage());
                        }
                        crazyManager.removeChestQuery(player.getUniqueId());
                        cancel();
                    } else {
                        player.sendMessage(Messages.STILL_LOADING_CHESTS.getMessage());
                    }
                }
            }.runTaskTimer(crazyManager.getPlugin(), 3, 5);
        } else {
            crazyManager.addTwoFactorAuth(uuid);
            player.sendMessage(Messages.TWO_FACTOR_AUTH.getMessage());
        }
    }
    
    @SubCommand(value = "all")
    @Permission("epicsellchest.region")
    public void allCommand(CommandSender sender, @ArgName("[ID:MD] [ID:MD] [ID:MD]...") List<String> materials) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if (pos1.containsKey(uuid) && pos2.containsKey(uuid)) {
            if (crazyManager.isRadiusAcceptable(pos1.get(uuid), pos2.get(uuid))) {
                if (!crazyManager.needsTwoFactorAuth(uuid)) {
                    crazyManager.removeTwoFactorAuth(uuid);
                    crazyManager.queryChests(player, pos1.get(uuid), pos2.get(uuid));
                    player.sendMessage(Messages.LOADING_REGION_CHESTS.getMessage());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ArrayList<Chest> chests = crazyManager.getChestQuery(player.getUniqueId());
                            if (chests != null) {
                                if (!chests.isEmpty()) {
                                    if (Support.SPARTAN.isEnabled()) {
                                        SpartanSupport.cancelBlockChecker(player);
                                    }
                                    HashMap<String, String> placeholders = new HashMap<>();
                                    for (Chest chest : chests) {
                                        BlockBreakEvent check = new BlockBreakEvent(chest.getBlock(), player);
                                        Bukkit.getPluginManager().callEvent(check);
                                        if (!check.isCancelled()) {
                                            ArrayList<SellItem> items;
                                            if (!materials.isEmpty()) {
                                                ArrayList<ItemStack> selling = new ArrayList<>();
                                                materials.forEach(id -> selling.add(new ItemBuilder().setMaterial(id).build()));
                                                items = crazyManager.getSellableItems(chest.getInventory(), selling);
                                            } else {
                                                items = crazyManager.getSellableItems(chest.getInventory());
                                            }
                                            SellChestEvent event = new SellChestEvent(player, items, SellType.REGION);
                                            Bukkit.getPluginManager().callEvent(event);
                                            if (!event.isCancelled()) {
                                                placeholders = getPlaceholders(items);
                                                crazyManager.sellSellableItems(player, items);
                                                items.forEach(item -> chest.getInventory().remove(item.item()));
                                            }
                                        }
                                    }
                                    player.sendMessage(placeholders.size() == 0 ? Messages.NO_CHESTS_IN_CHUNK.getMessage() : Messages.SOLD_CHUNK_CHESTS.getMessage(placeholders));
                                } else {
                                    player.sendMessage(Messages.NO_CHESTS_IN_REGION.getMessage());
                                }
                                crazyManager.removeChestQuery(player.getUniqueId());
                                cancel();
                            } else {
                                player.sendMessage(Messages.STILL_LOADING_CHESTS.getMessage());
                            }
                        }
                    }.runTaskTimer(crazyManager.getPlugin(), 3, 5);
                } else {
                    crazyManager.addTwoFactorAuth(uuid);
                    player.sendMessage(Messages.TWO_FACTOR_AUTH.getMessage());
                }
            } else {
                player.sendMessage(Messages.REGION_TO_BIG.getMessage());
            }
        } else {
            player.sendMessage(Messages.MISSING_POSITION.getMessage());
        }
    }
    
    @SubCommand(value = "help", alias = {"h"})
    @Permission("epicsellchest.help")
    public void helpCommand(CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessageNoPrefix());
    }
    
    @SubCommand(value = "reload", alias = {"r"})
    @Permission("epicsellchest.reload")
    public void reloadCommand(CommandSender sender) {
        Files.CONFIG.reloadFile();
        Files.MESSAGES.reloadFile();
        fileManager.setup(crazyManager.getPlugin());
        crazyManager.load();
        sender.sendMessage(Messages.RELOADED.getMessage());
    }
    
    @SubCommand(value = "debug")
    @Permission("epicsellchest.debug")
    public void debugCommand(CommandSender sender) {
        if (!crazyManager.getBrokeItems().isEmpty()) {
            sender.sendMessage(Methods.prefix("&7List of all broken items:"));
            for (String item : crazyManager.getBrokeItems()) {
                sender.sendMessage(Methods.color(item));
            }
        } else {
            sender.sendMessage(Methods.prefix("&aYour Config.yml contains no broken items."));
        }
        if (!crazyManager.getDuplicateItems().isEmpty()) {
            sender.sendMessage(Methods.prefix("&7List of all duplicate items:"));
            for (String item : crazyManager.getDuplicateItems()) {
                sender.sendMessage(Methods.color(item));
            }
            if (Support.SHOP_GUI_PLUS.isEnabled()) {
                sender.sendMessage("");
                sender.sendMessage(Methods.color("&7&l(&c&l!&7&l) &7If the EpicSellChest's config.yml contains items that the ShopGUIPlus plugin has, EpicSellChest's prices will override ShopGUIPlus's price of that item."));
                sender.sendMessage("");
                sender.sendMessage(Methods.color("&7&l(&c&l!&7&l) &7If you wish to not use EpicSellChest's price just go into the config.yml and remove that item from the list."));
                sender.sendMessage("");
                sender.sendMessage(Methods.color("&7&l(&c&l!&7&l) &7If you want to make sure EpicSellChest doesn't get the price of multiple of the same items from ShopGUIPlus, just set that items price in the EpicSellChest's config.yml."));
            }
        } else {
            sender.sendMessage(Methods.prefix("&aThere are no duplicate items."));
        }
    }
    
    @SubCommand(value = "gui")
    @Permission("epicsellchest.gui")
    public void guiCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            crazyManager.openSellChestGUI(player);
        } else {
            sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
        }
    }
    
    @SubCommand(value = "pos1")
    @Permission("epicsellchest.region")
    public void pos1Command(CommandSender sender) {
        if (sender instanceof Player player) {
            pos1.put(player.getUniqueId(), player.getLocation());
            player.sendMessage(Messages.POSITION_1.getMessage());
        } else {
            sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
        }
    }
    
    @SubCommand(value = "pos2")
    @Permission("epicsellchest.region")
    public void pos2Command(CommandSender sender) {
        if (sender instanceof Player player) {
            pos2.put(player.getUniqueId(), player.getLocation());
            player.sendMessage(Messages.POSITION_2.getMessage());
        } else {
            sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
        }
    }
    
    private HashMap<String, String> getPlaceholders(ArrayList<SellItem> items) {
        HashMap<String, String> placeholders = new HashMap<>();
        for (Currency currency : Currency.values()) {
            String name = currency.getName();
            String fullCost = crazyManager.getFullCost(items, currency) + "";
            if (placeholders.containsKey("%" + name.toLowerCase() + "%") || placeholders.containsKey("%" + name + "%")) {
                placeholders.put("%" + name.toLowerCase() + "%", placeholders.get("%" + name.toLowerCase() + "%") + fullCost);
                placeholders.put("%" + name + "%", placeholders.get("%" + name + "%") + fullCost);
            } else {
                placeholders.put("%" + name.toLowerCase() + "%", fullCost);
                placeholders.put("%" + name + "%", fullCost);
            }
        }
        for (CustomCurrency currency : crazyManager.getCustomCurrencies()) {
            String name = currency.name();
            String fullCost = crazyManager.getFullCost(items, currency) + "";
            if (placeholders.containsKey("%" + name.toLowerCase() + "%") || placeholders.containsKey("%" + name + "%")) {
                placeholders.put("%" + name.toLowerCase() + "%", placeholders.get("%" + name.toLowerCase() + "%") + fullCost);
                placeholders.put("%" + name + "%", placeholders.get("%" + name + "%") + fullCost);
            } else {
                placeholders.put("%" + name.toLowerCase() + "%", fullCost);
                placeholders.put("%" + name + "%", fullCost);
            }
        }
        return placeholders;
    }
    
}