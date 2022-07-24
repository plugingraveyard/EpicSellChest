package com.badbones69.epicsellchest.commands;

import com.badbones69.epicsellchest.Methods;
import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.FileManager;
import com.badbones69.epicsellchest.api.ItemBuilder;
import com.badbones69.epicsellchest.api.currency.Currency;
import com.badbones69.epicsellchest.api.currency.CustomCurrency;
import com.badbones69.epicsellchest.api.enums.Messages;
import com.badbones69.epicsellchest.api.enums.SellType;
import com.badbones69.epicsellchest.api.events.SellChestEvent;
import com.badbones69.epicsellchest.api.objects.SellItem;
import com.badbones69.epicsellchest.multisupport.Support;
import com.badbones69.epicsellchest.multisupport.anticheats.SpartanSupport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SellChestCommands {
    
    private final HashMap<UUID, Location> pos1 = new HashMap<>();
    private final HashMap<UUID, Location> pos2 = new HashMap<>();
    
    private CrazyManager crazyManager = CrazyManager.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("sellchest") || commandLabel.equalsIgnoreCase("sc")) {
            if (args.length == 0) {
                if (sender.hasPermission("epicsellchest.single") || sender.hasPermission("epicsellchest.admin")) {
                    if (sender instanceof Player player) {
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
                                                HashMap<String, Double> placeholders = new HashMap<>();
                                                for (Currency currency : Currency.values()) {
                                                    placeholders.put("%" + currency.getName().toLowerCase() + "%", crazyManager.getFullCost(items, currency));
                                                    placeholders.put("%" + currency.getName() + "%", crazyManager.getFullCost(items, currency));
                                                }
                                                for (CustomCurrency currency : crazyManager.getCustomCurrencies()) {
                                                    placeholders.put("%" + currency.name().toLowerCase() + "%", crazyManager.getFullCost(items, currency));
                                                    placeholders.put("%" + currency.name() + "%", crazyManager.getFullCost(items, currency));
                                                }
                                                crazyManager.sellSellableItems(player, items);
                                                for (SellItem item : items) {
                                                    if (item.usesSellingAmount()) {
                                                        item.getItem().setAmount(item.getItem().getAmount() - (item.getSellingAmount() * item.getSellingMinimum()));
                                                    } else {
                                                        chest.getInventory().remove(item.getItem());
                                                    }
                                                }
                                                player.sendMessage(Messages.SOLD_CHEST.getMessageDouble(placeholders));
                                                return true;
                                            }
                                        } else {
                                            player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                                            return true;
                                        }
                                    } else {
                                        crazyManager.addTwoFactorAuth(uuid);
                                        player.sendMessage(Messages.TWO_FACTOR_AUTH.getMessage());
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                                    return true;
                                }
                            } else {
                                player.sendMessage(Messages.CANT_SELL_CHEST.getMessage());
                                return true;
                            }
                        } else {
                            player.sendMessage(Messages.NOT_A_CHEST.getMessage());
                            return true;
                        }
                    } else {
                        sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
                        return true;
                    }
                } else {
                    sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                    return true;
                }
            } else {
                if (args[0].equalsIgnoreCase("help")) {
                    if (sender.hasPermission("epicsellchest.help") || sender.hasPermission("epicsellchest.admin")) {
                        for (String msg : FileManager.Files.MESSAGES.getFile().getStringList("Messages.Help")) {
                            sender.sendMessage(Methods.color(msg));
                        }
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                    }
                    return true;
                    //==== This is just for simple converting for when needed ====//
                    //}else if(args[0].equalsIgnoreCase("convert")) {
                    //	FileConfiguration data = Files.DATA.getFile();
                    //	data.set("Item-Cost", null);
                    //	List<String> items = new ArrayList<>();
                    //	for(SellableItem item : sc.getRegisteredSellableItems()) {
                    //		items.add("Item:" + item.getItem().getType().name().replace("LEGACY_", "")
                    //		+ ", Cost:" + item.getPrice()
                    //		+ ", Currency:" + (item.getCurrency() == Currency.CUSTOM ? item.getCustomCurrency().getName() : item.getCurrency().getName())
                    //		+ ", Amount:1");
                    //	}
                    //	data.set("Item-Cost", items);
                    //	Files.DATA.saveFile();
                    //	return true;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("epicsellchest.reload") || sender.hasPermission("epicsellchest.admin")) {
                        FileManager.Files.CONFIG.reloadFile();
                        FileManager.Files.MESSAGES.reloadFile();
                        fileManager.setup(crazyManager.getPlugin());
                        crazyManager.load();
                        sender.sendMessage(Messages.RELOADED.getMessage());
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("debug")) {
                    if (sender.hasPermission("epicsellchest.debug") || sender.hasPermission("epicsellchest.admin")) {
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
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("wand")) {// /sc wand [Amount] [Player]
                    if (sender.hasPermission("epicsellchest.wand") || sender.hasPermission("epicsellchest.admin")) {
                        if (args.length == 1) {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
                                return true;
                            }
                        }
                        Player player;
                        int amount = 1;
                        if (args.length >= 2) {
                            if (Methods.isInt(args[1])) {
                                amount = Integer.parseInt(args[1]);
                            } else {
                                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage());
                                return true;
                            }
                        }
                        if (args.length >= 3) {
                            if (Bukkit.getPlayer(args[2]) != null) {
                                player = Bukkit.getPlayer(args[2]);
                            } else {
                                sender.sendMessage(Messages.NOT_ONLINE.getMessage());
                                return true;
                            }
                        } else {
                            player = (Player) sender;
                        }
                        player.getInventory().addItem(crazyManager.getChestSellingItem(amount));
                        player.updateInventory();
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%player%", player.getName());
                        placeholders.put("%amount%", amount + "");
                        sender.sendMessage(Messages.WAND_GIVE.getMessage(placeholders));
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("gui")) {
                    if (sender.hasPermission("epicsellchest.gui") || sender.hasPermission("epicsellchest.admin")) {
                        if (sender instanceof Player) {
                            crazyManager.openSellChestGUI((Player) sender);
                        } else {
                            sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
                        }
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("chunk")) {
                    if (sender.hasPermission("epicsellchest.chunk") || sender.hasPermission("epicsellchest.admin")) {
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
                                    ArrayList<Chest> chests = crazyManager.getChestQuery(player.getUniqueId());
                                    if (chests != null) {
                                        if (!chests.isEmpty()) {
                                            if (Support.SPARTAN.isEnabled()) {
                                                SpartanSupport.cancelBlockChecker(player);
                                            }
                                            HashMap<String, Double> placeholders = new HashMap<>();
                                            for (Chest chest : chests) {
                                                BlockBreakEvent check = new BlockBreakEvent(chest.getBlock(), player);
                                                Bukkit.getPluginManager().callEvent(check);
                                                if (!check.isCancelled()) {
                                                    ArrayList<SellItem> items;
                                                    if (args.length >= 2) {
                                                        ArrayList<ItemStack> selling = new ArrayList<>();
                                                        for (int i = 1; i < args.length; i++) {
                                                            selling.add(new ItemBuilder().setMaterial(args[i]).build());
                                                        }
                                                        items = crazyManager.getSellableItems(chest.getInventory(), selling);
                                                    } else {
                                                        items = crazyManager.getSellableItems(chest.getInventory());
                                                    }
                                                    SellChestEvent event = new SellChestEvent(player, items, SellType.CHUNK);
                                                    Bukkit.getPluginManager().callEvent(event);
                                                    if (!event.isCancelled()) {
                                                        for (Currency currency : Currency.values()) {
                                                            if (placeholders.containsKey("%" + currency.getName().toLowerCase() + "%") || placeholders.containsKey("%" + currency.getName() + "%")) {
                                                                placeholders.put("%" + currency.getName().toLowerCase() + "%", placeholders.get("%" + currency.getName().toLowerCase() + "%") + crazyManager.getFullCost(items, currency));
                                                                placeholders.put("%" + currency.getName() + "%", placeholders.get("%" + currency.getName() + "%") + crazyManager.getFullCost(items, currency));
                                                            } else {
                                                                placeholders.put("%" + currency.getName().toLowerCase() + "%", crazyManager.getFullCost(items, currency));
                                                                placeholders.put("%" + currency.getName() + "%", crazyManager.getFullCost(items, currency));
                                                            }
                                                        }
                                                        for (CustomCurrency currency : crazyManager.getCustomCurrencies()) {
                                                            if (placeholders.containsKey("%" + currency.name().toLowerCase() + "%") || placeholders.containsKey("%" + currency.name() + "%")) {
                                                                placeholders.put("%" + currency.name().toLowerCase() + "%", placeholders.get("%" + currency.name().toLowerCase() + "%") + crazyManager.getFullCost(items, currency));
                                                                placeholders.put("%" + currency.name() + "%", placeholders.get("%" + currency.name() + "%") + crazyManager.getFullCost(items, currency));
                                                            } else {
                                                                placeholders.put("%" + currency.name().toLowerCase() + "%", crazyManager.getFullCost(items, currency));
                                                                placeholders.put("%" + currency.name() + "%", crazyManager.getFullCost(items, currency));
                                                            }
                                                        }
                                                        crazyManager.sellSellableItems(player, items);
                                                        for (SellItem item : items) {
                                                            chest.getInventory().remove(item.getItem());
                                                        }
                                                    }
                                                }
                                            }
                                            if (placeholders.size() == 0) {
                                                player.sendMessage(Messages.NO_CHESTS_IN_CHUNK.getMessage());
                                            } else {
                                                player.sendMessage(Messages.SOLD_CHUNK_CHESTS.getMessageDouble(placeholders));
                                            }
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
                            return true;
                        }
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                        return true;
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("all")) {
                    if (sender.hasPermission("epicsellchest.region") || sender.hasPermission("epicsellchest.admin")) {
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
                                                    HashMap<String, Double> placeholders = new HashMap<>();
                                                    for (Chest chest : chests) {
                                                        BlockBreakEvent check = new BlockBreakEvent(chest.getBlock(), player);
                                                        Bukkit.getPluginManager().callEvent(check);
                                                        if (!check.isCancelled()) {
                                                            ArrayList<SellItem> items;
                                                            if (args.length >= 2) {
                                                                ArrayList<ItemStack> selling = new ArrayList<>();
                                                                for (int i = 1; i < args.length; i++) {
                                                                    selling.add(new ItemBuilder().setMaterial(args[i]).build());
                                                                }
                                                                items = crazyManager.getSellableItems(chest.getInventory(), selling);
                                                            } else {
                                                                items = crazyManager.getSellableItems(chest.getInventory());
                                                            }
                                                            SellChestEvent event = new SellChestEvent(player, items, SellType.REGION);
                                                            Bukkit.getPluginManager().callEvent(event);
                                                            if (!event.isCancelled()) {
                                                                for (Currency currency : Currency.values()) {
                                                                    if (placeholders.containsKey("%" + currency.getName().toLowerCase() + "%") || placeholders.containsKey("%" + currency.getName() + "%")) {
                                                                        placeholders.put("%" + currency.getName().toLowerCase() + "%", placeholders.get("%" + currency.getName().toLowerCase() + "%") + crazyManager.getFullCost(items, currency));
                                                                        placeholders.put("%" + currency.getName() + "%", placeholders.get("%" + currency.getName() + "%") + crazyManager.getFullCost(items, currency));
                                                                    } else {
                                                                        placeholders.put("%" + currency.getName().toLowerCase() + "%", crazyManager.getFullCost(items, currency));
                                                                        placeholders.put("%" + currency.getName() + "%", crazyManager.getFullCost(items, currency));
                                                                    }
                                                                }
                                                                for (CustomCurrency currency : crazyManager.getCustomCurrencies()) {
                                                                    if (placeholders.containsKey("%" + currency.name().toLowerCase() + "%") || placeholders.containsKey("%" + currency.name() + "%")) {
                                                                        placeholders.put("%" + currency.name().toLowerCase() + "%", placeholders.get("%" + currency.name().toLowerCase() + "%") + crazyManager.getFullCost(items, currency));
                                                                        placeholders.put("%" + currency.name() + "%", placeholders.get("%" + currency.name() + "%") + crazyManager.getFullCost(items, currency));
                                                                    } else {
                                                                        placeholders.put("%" + currency.name().toLowerCase() + "%", crazyManager.getFullCost(items, currency));
                                                                        placeholders.put("%" + currency.name() + "%", crazyManager.getFullCost(items, currency));
                                                                    }
                                                                }
                                                                crazyManager.sellSellableItems(player, items);
                                                                for (SellItem item : items) {
                                                                    chest.getInventory().remove(item.getItem());
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (placeholders.size() == 0) {
                                                        player.sendMessage(Messages.NO_CHESTS_IN_CHUNK.getMessage());
                                                    } else {
                                                        player.sendMessage(Messages.SOLD_CHUNK_CHESTS.getMessageDouble(placeholders));
                                                    }
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
                                    return true;
                                }
                            } else {
                                player.sendMessage(Messages.REGION_TO_BIG.getMessage());
                                return true;
                            }
                        } else {
                            player.sendMessage(Messages.MISSING_POSITION.getMessage());
                        }
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                        return true;
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("pos1") || args[0].equalsIgnoreCase("p1")) {
                    if (sender.hasPermission("epicsellchest.region") || sender.hasPermission("epicsellchest.admin")) {
                        if (sender instanceof Player player) {
                            pos1.put(player.getUniqueId(), player.getLocation());
                            player.sendMessage(Messages.POSITION_1.getMessage());
                        } else {
                            sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
                            return true;
                        }
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("pos2") || args[0].equalsIgnoreCase("p2")) {
                    if (sender.hasPermission("epicsellchest.region") || sender.hasPermission("epicsellchest.admin")) {
                        if (sender instanceof Player player) {
                            pos2.put(player.getUniqueId(), player.getLocation());
                            player.sendMessage(Messages.POSITION_2.getMessage());
                        } else {
                            sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
                            return true;
                        }
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                        return true;
                    }
                } else {
                    if (sender.hasPermission("epicsellchest.single") || sender.hasPermission("epicsellchest.admin")) {
                        if (sender instanceof Player player) {
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
                                            ArrayList<ItemStack> selling = new ArrayList<>();
                                            for (String arg : args) {
                                                selling.add(new ItemBuilder().setMaterial(arg).build());
                                            }
                                            ArrayList<SellItem> items = crazyManager.getSellableItems(chest.getInventory(), selling);
                                            if (items.size() > 0) {
                                                SellChestEvent event = new SellChestEvent(player, items, SellType.SINGLE);
                                                Bukkit.getPluginManager().callEvent(event);
                                                if (!event.isCancelled()) {
                                                    HashMap<String, Double> placeholders = new HashMap<>();
                                                    for (Currency currency : Currency.values()) {
                                                        placeholders.put("%" + currency.getName().toLowerCase() + "%", crazyManager.getFullCost(items, currency));
                                                        placeholders.put("%" + currency.getName() + "%", crazyManager.getFullCost(items, currency));
                                                    }
                                                    for (CustomCurrency currency : crazyManager.getCustomCurrencies()) {
                                                        placeholders.put("%" + currency.name().toLowerCase() + "%", crazyManager.getFullCost(items, currency));
                                                        placeholders.put("%" + currency.name() + "%", crazyManager.getFullCost(items, currency));
                                                    }
                                                    crazyManager.sellSellableItems(player, items);
                                                    for (SellItem item : items) {
                                                        chest.getInventory().remove(item.getItem());
                                                    }
                                                    player.sendMessage(Messages.SOLD_CHEST.getMessageDouble(placeholders));
                                                    return true;
                                                }
                                            } else {
                                                player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                                                return true;
                                            }
                                        } else {
                                            crazyManager.addTwoFactorAuth(uuid);
                                            player.sendMessage(Messages.TWO_FACTOR_AUTH.getMessage());
                                            return true;
                                        }
                                    } else {
                                        player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(Messages.CANT_SELL_CHEST.getMessage());
                                    return true;
                                }
                            } else {
                                player.sendMessage(Messages.NOT_A_CHEST.getMessage());
                                return true;
                            }
                        } else {
                            sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
                            return true;
                        }
                    } else {
                        sender.sendMessage(Messages.NO_PERMISSION.getMessage());
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
}