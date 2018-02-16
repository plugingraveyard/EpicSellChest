package me.badbones69.epicsellchest.controlers;

import me.badbones69.epicsellchest.Methods;
import me.badbones69.epicsellchest.api.EpicSellChest;
import me.badbones69.epicsellchest.api.Messages;
import me.badbones69.epicsellchest.api.SellItem;
import me.badbones69.epicsellchest.api.SellType;
import me.badbones69.epicsellchest.api.currency.Currency;
import me.badbones69.epicsellchest.api.currency.CustomCurrency;
import me.badbones69.epicsellchest.api.event.SellChestEvent;
import me.badbones69.epicsellchest.multisupport.NoCheatPlusSupport;
import me.badbones69.epicsellchest.multisupport.Support;
import org.bukkit.Bukkit;
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
	
	private EpicSellChest sc = EpicSellChest.getInstance();
	
	@EventHandler
	public void useWand(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		Block block = e.getClickedBlock();
		ItemStack wand = Methods.getItemInHand(player);
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(wand != null) {
				if(Methods.isSimilar(sc.getChestSellingItem(), wand)) {
					if(block != null) {
						if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
							e.setCancelled(true);
							if(Support.hasNoCheatPlus()) {
								NoCheatPlusSupport.exemptPlayer(player);
							}
							BlockBreakEvent check = new BlockBreakEvent(block, player);
							Bukkit.getPluginManager().callEvent(check);
							if(!check.isCancelled()) {
								Chest chest = (Chest) block.getState();
								if(!Methods.isInvEmpty(chest.getInventory())) {
									if(!sc.needsTwoFactorAuth(uuid)) {
										sc.removeTwoFactorAuth(uuid);
										ArrayList<SellItem> items = sc.getSellableItems(chest.getInventory());
										if(items.size() > 0) {
											SellChestEvent event = new SellChestEvent(player, items, SellType.SINGLE);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												HashMap<String, Integer> placeholders = new HashMap<>();
												for(Currency currency : Currency.values()) {
													placeholders.put("%" + currency.getName().toLowerCase() + "%", sc.getFullCost(player, items, currency));
													placeholders.put("%" + currency.getName() + "%", sc.getFullCost(player, items, currency));
												}
												for(CustomCurrency currency : sc.getCustomCurrencies()) {
													placeholders.put("%" + currency.getName().toLowerCase() + "%", sc.getFullCost(player, items, currency));
													placeholders.put("%" + currency.getName() + "%", sc.getFullCost(player, items, currency));
												}
												sc.sellSellableItems(player, items);
												for(SellItem item : items) {
													chest.getInventory().remove(item.getItem());
												}
												player.sendMessage(Messages.SOLD_CHEST.getMessageInt(placeholders));
											}
										}else {
											player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
										}
									}else {
										sc.addTwoFactorAuth(uuid);
										player.sendMessage(Messages.WAND_TWO_FACTOR_AUTH.getMessage());
									}
								}else {
									player.sendMessage(Messages.NO_SELLABLE_ITEMS.getMessage());
								}
							}else {
								player.sendMessage(Messages.CANT_SELL_CHEST.getMessage());
							}
						}
					}
				}
			}
		}
	}
	
}