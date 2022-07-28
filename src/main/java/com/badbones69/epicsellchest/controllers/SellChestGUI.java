package com.badbones69.epicsellchest.controllers;

import com.badbones69.epicsellchest.Methods;
import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.FileManager.Files;
import com.badbones69.epicsellchest.api.ItemBuilder;
import com.badbones69.epicsellchest.api.currency.Currency;
import com.badbones69.epicsellchest.api.currency.CustomCurrency;
import com.badbones69.epicsellchest.api.enums.Messages;
import com.badbones69.epicsellchest.api.enums.SellType;
import com.badbones69.epicsellchest.api.events.SellChestEvent;
import com.badbones69.epicsellchest.api.objects.SellItem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SellChestGUI implements Listener {
    
    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    private final HashMap<UUID, ArrayList<SellItem>> sellables = new HashMap<>();
    private final HashMap<UUID, ArrayList<ItemStack>> nonsellables = new HashMap<>();
    
    public static void openSellChestGUI(Player player) {
        crazyManager.openSellChestGUI(player);
    }
    
    @EventHandler
    public void onSellChest(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();
        FileConfiguration config = Files.CONFIG.getFile();
        
        if (e.getView().getTitle().equalsIgnoreCase(Methods.color(config.getString("Settings.Sign-Options.Inventory-Name")))) {
            if (!Methods.isInvEmpty(inv)) {
                if (!crazyManager.needsTwoFactorAuth(uuid)) {
                    ArrayList<SellItem> items = crazyManager.getSellableItems(inv);
                    
                    if (items.size() > 0) {
                        SellChestEvent event = new SellChestEvent(player, items, SellType.GUI);
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
                                    inv.remove(item.item());
                                }
                            }
                            
                            crazyManager.removeTwoFactorAuth(uuid);
                            player.sendMessage(Messages.SOLD_CHEST.getMessage(placeholders));
                        }
                    } else {
                        player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                    }
                    
                    for (ItemStack item : inv.getContents()) {
                        if (item != null) {
                            if (Methods.isInvFull(player)) {
                                player.getWorld().dropItemNaturally(player.getLocation(), item);
                            } else {
                                player.getInventory().addItem(item);
                            }
                        }
                    }
                    
                    inv.clear();
                } else {
                    ArrayList<SellItem> items = crazyManager.getSellableItems(inv);
                    sellables.put(uuid, items);
                    
                    for (SellItem item : items) {
                        if (item.hasSellingAmount()) {
                            item.item().setAmount(item.item().getAmount() - (item.sellingAmount() * item.sellingMinimum()));
                        } else {
                            inv.remove(item.item());
                        }
                    }
                    
                    ArrayList<ItemStack> others = new ArrayList<>();
                    
                    for (ItemStack item : inv.getContents()) {
                        if (item != null) {
                            others.add(item);
                        }
                    }
                    
                    nonsellables.put(uuid, others);
                    inv.clear();
                    openTwoFactorAuth(player);
                }
            }
        } else if (e.getView().getTitle().equalsIgnoreCase(Methods.color(Files.CONFIG.getFile().getString("Settings.Sign-Options.Two-Factor-Auth-Options.Inventory-Name")))) {
            if (sellables.containsKey(uuid) && nonsellables.containsKey(uuid)) {
                ArrayList<SellItem> items = sellables.get(uuid);
                
                if (items.size() > 0) {
                    SellChestEvent event = new SellChestEvent(player, items, SellType.GUI);
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
                        player.sendMessage(Messages.SOLD_CHEST.getMessage(placeholders));
                    }
                } else {
                    player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                }
                
                for (ItemStack item : nonsellables.get(uuid)) {
                    if (item != null) {
                        if (Methods.isInvFull(player)) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        } else {
                            player.getInventory().addItem(item);
                        }
                    }
                }
                
                inv.clear();
                sellables.remove(uuid);
                nonsellables.remove(uuid);
            }
        }
    }
    
    @EventHandler
    public void onTwoFactorAuth(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();
        Inventory inv = e.getInventory();
        
        if (e.getView().getTitle().equalsIgnoreCase(Methods.color(Files.CONFIG.getFile().getString("Settings.Sign-Options.Two-Factor-Auth-Options.Inventory-Name")))) {
            e.setCancelled(true);
            
            if (sellables.containsKey(uuid) && nonsellables.containsKey(uuid)) {
                ItemStack check = e.getCurrentItem();
                
                if (check != null) {
                    if (check.isSimilar(getAcceptItem())) {
                        ArrayList<SellItem> items = sellables.get(uuid);
                        
                        if (items.size() > 0) {
                            SellChestEvent event = new SellChestEvent(player, items, SellType.GUI);
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
                                player.sendMessage(Messages.SOLD_CHEST.getMessage(placeholders));
                            }
                        } else {
                            player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
                        }
                        
                        for (ItemStack item : nonsellables.get(uuid)) {
                            if (item != null) {
                                if (Methods.isInvFull(player)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                                } else {
                                    player.getInventory().addItem(item);
                                }
                            }
                        }
                        
                        inv.clear();
                        sellables.remove(uuid);
                        nonsellables.remove(uuid);
                        player.closeInventory();
                    } else if (check.isSimilar(getDenyItem())) {
                        for (SellItem item : sellables.get(uuid)) {
                            if (item != null) {
                                if (Methods.isInvFull(player)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), item.item());
                                } else {
                                    player.getInventory().addItem(item.item());
                                }
                            }
                        }
                        
                        for (ItemStack item : nonsellables.get(uuid)) {
                            if (item != null) {
                                if (Methods.isInvFull(player)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                                } else {
                                    player.getInventory().addItem(item);
                                }
                            }
                        }
                        
                        inv.clear();
                        sellables.remove(uuid);
                        nonsellables.remove(uuid);
                        player.closeInventory();
                    }
                }
            }
        }
    }
    
    private void openTwoFactorAuth(Player player) {
        Inventory inv = crazyManager.getPlugin().getServer().createInventory(null, 9, Methods.color(Files.CONFIG.getFile().getString("Settings.Sign-Options.Two-Factor-Auth-Options.Inventory-Name")));
        ItemStack accept = getAcceptItem();
        ItemStack deny = getDenyItem();
        inv.setItem(0, accept.clone());
        inv.setItem(1, accept.clone());
        inv.setItem(2, accept.clone());
        inv.setItem(3, accept.clone());
        inv.setItem(4, getInfoItem());
        inv.setItem(5, deny.clone());
        inv.setItem(6, deny.clone());
        inv.setItem(7, deny.clone());
        inv.setItem(8, deny.clone());
        
        player.openInventory(inv);
    }
    
    private ItemStack getAcceptItem() {
        FileConfiguration config = Files.CONFIG.getFile();
        String path = "Settings.Sign-Options.Two-Factor-Auth-Options.Accept.";
        return new ItemBuilder()
        .setMaterial(config.getString(path + "Item", ""))
        .setName(config.getString(path + "Name", ""))
        .setLore(config.getStringList(path + "Lore"))
        .build();
    }
    
    private ItemStack getInfoItem() {
        FileConfiguration config = Files.CONFIG.getFile();
        String path = "Settings.Sign-Options.Two-Factor-Auth-Options.Info.";
        return new ItemBuilder()
        .setMaterial(config.getString(path + "Item", ""))
        .setName(config.getString(path + "Name", ""))
        .setLore(config.getStringList(path + "Lore"))
        .build();
    }
    
    private ItemStack getDenyItem() {
        FileConfiguration config = Files.CONFIG.getFile();
        String path = "Settings.Sign-Options.Two-Factor-Auth-Options.Deny.";
        return new ItemBuilder()
        .setMaterial(config.getString(path + "Item", ""))
        .setName(config.getString(path + "Name", ""))
        .setLore(config.getStringList(path + "Lore"))
        .build();
    }
    
}