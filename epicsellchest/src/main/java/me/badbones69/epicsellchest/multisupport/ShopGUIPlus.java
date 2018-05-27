package me.badbones69.epicsellchest.multisupport;

import me.badbones69.epicsellchest.Methods;
import me.badbones69.epicsellchest.api.EpicSellChest;
import me.badbones69.epicsellchest.api.currency.Currency;
import me.badbones69.epicsellchest.api.currency.CustomCurrency;
import me.badbones69.epicsellchest.api.enums.Support;
import me.badbones69.epicsellchest.api.objects.ItemBuilder;
import me.badbones69.epicsellchest.api.objects.SellableItem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShopGUIPlus {
	
	private static EpicSellChest esc = EpicSellChest.getInstance();
	
	public static List<SellableItem> getSellableItems() {
		List<SellableItem> items = new ArrayList<>();
		if(Support.SHOP_GUI_PLUS.isEnabled()) {
			Plugin shopGUI = Support.SHOP_GUI_PLUS.getPlugin();
			String cur = YamlConfiguration.loadConfiguration(new File(shopGUI.getDataFolder() + "/config.yml")).getString("economyType");
			Currency currency = Currency.getCurrency(cur);
			CustomCurrency customCurrency = esc.getCustomCurrency(cur);
			FileConfiguration shops = YamlConfiguration.loadConfiguration(new File(shopGUI.getDataFolder() + "/shops.yml"));
			for(String shop : shops.getConfigurationSection("shops").getKeys(false)) {
				String path = "shops." + shop;
				for(String item : shops.getConfigurationSection(path + ".items").getKeys(false)) {
					path += ".items." + item;
					if(shops.contains(path + ".type")) {
						if(shops.getString(path + ".type").equalsIgnoreCase("item")) {
							if(shops.getInt(path + ".sellPrice") >= 0) {
								ItemBuilder builder = new ItemBuilder()
								.setMaterial(shops.getString(path + ".item.material"))
								.setMetaData(shops.getInt(path + ".item.damage"))
								.setAmount(shops.getInt(path + ".item.quantity"))
								.setName(shops.getString(path + ".item.name"))
								.setLore(shops.getStringList(path + ".item.lore"));
								for(String i : shops.getStringList(path + ".item.enchantments")) {
									for(Enchantment enc : Enchantment.values()) {
										if(enc.getName() != null) {
											if(i.toLowerCase().startsWith(enc.getName().toLowerCase() + ":") || i.toLowerCase().startsWith(Methods.getEnchantmentName(enc).toLowerCase() + ":")) {
												String[] breakdown = i.split(":");
												int lvl = Integer.parseInt(breakdown[1]);
												builder.addEnchantments(enc, lvl);
											}
										}
									}
								}
								items.add(new SellableItem(builder.build(), shops.getDouble(path + ".sellPrice"),
								currency,
								customCurrency,
								customCurrency.getCommand(),
								builder.getAmount() > 1));
							}
						}
					}
				}
			}
		}
		return items;
	}
	
}