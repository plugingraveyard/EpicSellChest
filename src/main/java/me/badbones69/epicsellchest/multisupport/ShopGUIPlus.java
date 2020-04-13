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
        Plugin shopGUI = Support.SHOP_GUI_PLUS.getPlugin();
        String currencyString = YamlConfiguration.loadConfiguration(new File(shopGUI.getDataFolder() + "/config.yml")).getString("economyType");
        Currency currency = Currency.getCurrency(currencyString);
        CustomCurrency customCurrency = esc.getCustomCurrency(currencyString);
        if (isSingleFile()) {
            FileConfiguration shops = YamlConfiguration.loadConfiguration(new File(shopGUI.getDataFolder() + "/shops.yml"));
            for (String shop : shops.getConfigurationSection("shops").getKeys(false)) {
                for (String item : shops.getConfigurationSection("shops." + shop + ".items").getKeys(false)) {
                    String path = "shops." + shop + ".items." + item;
                    if (shops.contains(path + ".type") && shops.getString(path + ".type").equalsIgnoreCase("item") && shops.getDouble(path + ".sellPrice") >= 0.0) {
                        SellableItem sellableItem = buildItem(path, shops, currency, customCurrency);
                        if (sellableItem != null) {
                            items.add(sellableItem);
                        }
                    }
                }
            }
        } else {
            for (File shop : getShopFiles()) {
                FileConfiguration file = YamlConfiguration.loadConfiguration(shop);
                for (String shopName : file.getConfigurationSection("").getKeys(false)) {
                    for (String item : file.getConfigurationSection(shopName + ".items").getKeys(false)) {
                        String path = shopName + ".items." + item;
                        if (file.contains(path + ".type") && file.getString(path + ".type").equalsIgnoreCase("item") && file.getDouble(path + ".sellPrice") >= 0.0) {
                            SellableItem sellableItem = buildItem(path, file, currency, customCurrency);
                            if (sellableItem != null) {
                                items.add(sellableItem);
                            }
                        }
                    }
                }
            }
        }
        return items;
    }
    
    private static SellableItem buildItem(String path, FileConfiguration shop, Currency currency, CustomCurrency customCurrency) {
        try {
            ItemBuilder builder = new ItemBuilder()
            .setMaterial(shop.getString(path + ".item.material"))
            .setMetaData(shop.getInt(path + ".item.damage"))
            .setAmount(shop.getInt(path + ".item.quantity"))
            .setName(shop.getString(path + ".item.name"))
            .setLore(shop.getStringList(path + ".item.lore"));
            if (shop.getBoolean(path + ".item.spawner")) {
                builder.setEntityType(shop.getString(path + ".item.mob"));
                if (builder.getEntityType() == null) {
                    return null;
                }
            }
            for (String enchantments : shop.getStringList(path + ".item.enchantments")) {
                for (Enchantment enchantment : Enchantment.values()) {
                    if (enchantment.getName() != null && (enchantments.toLowerCase().startsWith(enchantment.getName().toLowerCase() + ":") || enchantments.toLowerCase().startsWith(Methods.getEnchantmentName(enchantment).toLowerCase() + ":"))) {
                        String[] breakdown = enchantments.split(":");
                        builder.addEnchantments(enchantment, Integer.parseInt(breakdown[1]));
                    }
                }
            }
            return new SellableItem(builder, shop.getDouble(path + ".sellPrice"),
            currency,
            customCurrency,
            customCurrency.getCommand(),
            builder.getAmount() > 1);
        } catch (Exception e) {
            //Debunging what things are erroring.
            //System.out.println(path);
            //e.printStackTrace();
        }
        return null;
    }
    
    private static List<File> getShopFiles() {
        List<File> files = new ArrayList<>();
        File folder = new File(Support.SHOP_GUI_PLUS.getPlugin().getDataFolder() + "/shops");
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.getName().endsWith(".yml")) {
                    files.add(file);
                }
            }
        }
        return files;
    }
    
    private static boolean isSingleFile() {
        return new File(Support.SHOP_GUI_PLUS.getPlugin().getDataFolder() + "/shops.yml").exists();
    }
    
}