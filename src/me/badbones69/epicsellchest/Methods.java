package me.badbones69.epicsellchest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.badbones69.epicsellchest.api.Version;

public class Methods {
	
	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static String prefix(String msg) {
		return ChatColor.translateAlternateColorCodes('&', Main.settings.getConfig().getString("Settings.Prefix") + msg);
	}
	
	public static ItemStack makeItem(String type) {
		try {
			int ty = 0;
			if(type.contains(":")) {
				String[] b = type.split(":");
				type = b[0];
				ty = Integer.parseInt(b[1]);
			}
			Material m = Material.matchMaterial(type);
			ItemStack item = new ItemStack(m, 1, (short) ty);
			return item;
		}catch(Exception e) {
			return null;
		}
	}
	
	public static ItemStack makeItem(String type, int amount, String name) {
		int ty = 0;
		if(type.contains(":")) {
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = new ItemStack(m, amount, (short) ty);
		ItemMeta me = item.getItemMeta();
		me.setDisplayName(color(name));
		item.setItemMeta(me);
		return item;
	}
	
	public static ItemStack makeItem(String type, int amount, String name, List<String> lore) {
		ArrayList<String> l = new ArrayList<String>();
		int ty = 0;
		if(type.contains(":")) {
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = new ItemStack(m, amount, (short) ty);
		ItemMeta me = item.getItemMeta();
		me.setDisplayName(color(name));
		for(String L : lore)
			l.add(color(L));
		me.setLore(l);
		item.setItemMeta(me);
		return item;
	}
	
	public static ItemStack addGlowing(ItemStack item) {
		ItemStack it = item.clone();
		try {
			if(item != null) {
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasEnchants()) {
						return item;
					}
				}
				item.addUnsafeEnchantment(Enchantment.LUCK, 1);
				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
			return item;
		}catch(NoClassDefFoundError e) {
			return it;
		}
	}
	
	public static ItemStack addGlowing(ItemStack item, Boolean glowing) {
		ItemStack it = item.clone();
		if(glowing) {
			try {
				if(item != null) {
					if(item.hasItemMeta()) {
						if(item.getItemMeta().hasEnchants()) {
							return item;
						}
					}
					item.addUnsafeEnchantment(Enchantment.LUCK, 1);
					ItemMeta meta = item.getItemMeta();
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
				}
				return item;
			}catch(NoClassDefFoundError e) {
				return it;
			}
		}
		return it;
	}
	
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		}catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public static Location getLoc(Player player) {
		return player.getLocation();
	}
	
	public static boolean isInvFull(Player player) {
		if(player.getInventory().firstEmpty() == -1) {
			return true;
		}
		return false;
	}
	
	public static boolean isInvEmpty(Inventory inv) {
		if(inv != null) {
			for(ItemStack item : inv.getContents()) {
				if(item != null && item.getType() != Material.AIR) {
					return false;
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player) {
		if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()) {
			return player.getInventory().getItemInMainHand();
		}else {
			return player.getItemInHand();
		}
	}
	
	public static boolean isSimilar(ItemStack one, ItemStack two) {
		if(one.getType() == two.getType()) {
			if(one.hasItemMeta() && two.hasItemMeta()) {
				if(one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName()) {
					if(one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName())) {
						if(one.getItemMeta().hasLore() && two.getItemMeta().hasLore()) {
							int i = 0;
							for(String lore : one.getItemMeta().getLore()) {
								if(!lore.equals(two.getItemMeta().getLore().get(i))) {
									return false;
								}
								i++;
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
}