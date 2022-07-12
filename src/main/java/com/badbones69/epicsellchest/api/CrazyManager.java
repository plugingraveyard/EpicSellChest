package com.badbones69.epicsellchest.api;

import com.badbones69.epicsellchest.EpicSellChest;
import com.badbones69.epicsellchest.Methods;
import com.badbones69.epicsellchest.api.FileManager.Files;
import com.badbones69.epicsellchest.api.currency.Currency;
import com.badbones69.epicsellchest.api.currency.CurrencyAPI;
import com.badbones69.epicsellchest.api.currency.CustomCurrency;
import com.badbones69.epicsellchest.api.enums.RegisterType;
import com.badbones69.epicsellchest.api.objects.SellItem;
import com.badbones69.epicsellchest.api.objects.SellableItem;
import com.badbones69.epicsellchest.api.objects.UpgradeableEnchantment;
import com.badbones69.epicsellchest.multisupport.ServerProtocol;
import com.badbones69.epicsellchest.multisupport.Support;
import com.badbones69.epicsellchest.multisupport.shopplugins.ShopGUIPlus;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class CrazyManager {
    
    public static CrazyManager getInstance() {
        return instance;
    }
    
    private static final CrazyManager instance = new CrazyManager();
    
    private double basePrice;
    private boolean useMetrics;
    private boolean checkUpdates;
    private Currency baseCurrency;
    private ItemBuilder sellingWand;
    private CustomCurrency baseCustomCurrency;
    private final ArrayList<String> brokeItems = new ArrayList<>();
    private final ArrayList<String> duplicateItems = new ArrayList<>();
    private final ArrayList<UUID> twoFactorAuth = new ArrayList<>();
    private final ArrayList<Material> blackListItems = new ArrayList<>();
    private final HashMap<UUID, ArrayList<Chest>> queryChests = new HashMap<>();
    private final ArrayList<CustomCurrency> customCurrencies = new ArrayList<>();
    private final ArrayList<Enchantment> blackListEnchantments = new ArrayList<>();
    private final ArrayList<SellableItem> registeredSellableItems = new ArrayList<>();
    private final HashMap<ItemBuilder, RegisterType> registeredMaterials = new HashMap<>();
    private final ArrayList<UpgradeableEnchantment> upgradeableEnchantments = new ArrayList<>();
    
    private EpicSellChest plugin;
    
    public EpicSellChest getPlugin() {
        return this.plugin;
    }
    
    public void loadPlugin(EpicSellChest plugin) {
        this.plugin = plugin;
    }
    
    public void load() {
        
        brokeItems.clear();
        duplicateItems.clear();
        registeredMaterials.clear();
        registeredSellableItems.clear();
        blackListItems.clear();
        customCurrencies.clear();
        blackListEnchantments.clear();
        upgradeableEnchantments.clear();
        FileConfiguration config = Files.CONFIG.getFile();
        basePrice = config.getDouble("Settings.Base-Price");
        useMetrics = config.getBoolean("Settings.Metrics");
        checkUpdates = config.getBoolean("Settings.Check-For-Updates");
        
        sellingWand = new ItemBuilder()
        .setMaterial(Objects.requireNonNull(config.getString("Settings.Chest-Selling-Item.Item")))
        .setName(config.getString("Settings.Chest-Selling-Item.Name"))
        .setLore(config.getStringList("Settings.Chest-Selling-Item.Lore"))
        .setGlow(config.getBoolean("Settings.Chest-Selling-Item.Glowing"));
        
        for (String currency : Objects.requireNonNull(config.getConfigurationSection("Settings.Custom-Currencies")).getKeys(false)) {
            customCurrencies.add(new CustomCurrency(currency, config.getString("Settings.Custom-Currencies." + currency + ".Command")));
        }
        
        baseCurrency = Currency.getCurrency(config.getString("Settings.Base-Currency"));
        
        if (baseCurrency == Currency.CUSTOM) {
            baseCustomCurrency = getCustomCurrency(config.getString("Settings.Base-Currency"));
        }
        
        for (String id : config.getStringList("Settings.Black-List-Items")) {
            int md = 0;
            
            if (id.contains(":")) {
                md = Integer.parseInt(id.split(":")[1]);
                id = id.split(":")[0];
            }
            
            Material material = Material.matchMaterial(id);
            
            if (material != null) {
                blackListItems.add(new ItemStack(material, 1, (short) md).getType());
            }
        }
        
        for (String name : config.getStringList("Settings.Black-List-Enchantments")) {
            Enchantment enchant = Enchantment.getByName(name);
            if (enchant != null) {
                blackListEnchantments.add(enchant);
            }
        }
        
        for (String line : config.getStringList("Settings.Item-Cost")) {
            double cost = 0.0;
            ItemBuilder item = new ItemBuilder();
            Material material = null;
            String command = "";
            boolean checkAmount = false;
            Currency currency = null;
            CustomCurrency custom = null;
            
            for (String i : line.split(", ")) {
                i = i.toLowerCase();
                if (i.startsWith("item:")) {
                    i = i.substring(5);
                    
                    int md = 0;
                    String id = i;
                    
                    if (i.contains(":")) {
                        md = Integer.parseInt(i.split(":")[1]);
                        id = i.split(":")[0];
                    }
                    
                    material = Material.matchMaterial(id);
                    //if (material != null) {
                    //    item.setMaterial(material).setMetaData(md);
                    //} else {
                    //    brokeItems.add("&cBroken Line: &a" + line + " &7: &cBroken ID: &a" + i);
                    //}
                } else if (i.startsWith("cost:")) {
                    i = i.substring(5);
                    
                    if (Methods.isDouble(i)) {
                        cost = Double.parseDouble(i);
                    }
                } else if (i.startsWith("currency:")) {
                    i = i.substring(9);
                    Currency c = Currency.getCurrency(i);
                    
                    if (Currency.isCurrency(i)) {
                        if (c != null) {
                            currency = Currency.getCurrency(i);
                            
                            if (c == Currency.CUSTOM) {
                                CustomCurrency cu = getCustomCurrency(i);
                                
                                if (cu != null) {
                                    custom = cu;
                                    command = cu.command();
                                }
                            }
                        }
                    }
                } else if (i.startsWith("amount:")) {
                    i = i.substring(7);
                    
                    try {
                        int amount = Integer.parseInt(i);
                        
                        if (amount > 1) {
                            if (item != null) {
                                item.setAmount(amount);
                            }
                            
                            checkAmount = true;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            
            if (material != null && currency != null) {
                if (isRegisteredMaterial(item)) {
                    duplicateItems.add("&cDuplicate Item: &a" + line + " &7: &cAlready Registered With: &a" + getRegisteredPlugin(item).getName() + " &7: &cTryed To Register With: &a" + RegisterType.EPICSELLCHEST.getName());
                } else {
                    registeredSellableItems.add(new SellableItem(item, cost, currency, custom, command, checkAmount));
                    registeredMaterials.put(item, RegisterType.EPICSELLCHEST);
                }
            }
        }
        
        if (Support.SHOP_GUI_PLUS.isEnabled()) {
            for (SellableItem item : ShopGUIPlus.getSellableItems()) {
                if (isRegisteredMaterial(item.getItem())) {
                    duplicateItems.add("&cDuplicate Item: &a" + item.getItem().getType() + (item.getItem().getDurability() > 0 ? (":" + item.getItem().getDurability()) : "") + " &7: &cAlready Registered With: &a" + getRegisteredPlugin(item.getItem()).getName() + " &7: &cTried To Register With: &a" + RegisterType.SHOP_GUI_PLUS.getName());
                } else {
                    registeredSellableItems.add(item);
                    registeredMaterials.put(ItemBuilder.convertItemStack(item.getItem()), RegisterType.SHOP_GUI_PLUS);
                }
            }
        }
        
        for (String name : config.getConfigurationSection("Settings.Enchantment-Cost").getKeys(false)) {
            Enchantment enchant = Enchantment.getByName(name);
            
            if (enchant != null) {
                HashMap<Integer, Integer> levels = new HashMap<>();
                
                for (String line : config.getStringList("Settings.Enchantment-Cost." + name)) {
                    int cost = 0;
                    int level = 0;
                    
                    for (String i : line.split(", ")) {
                        i = i.toLowerCase();
                        
                        if (i.startsWith("level:")) {
                            i = i.replaceAll("level:", "");
                            
                            if (Methods.isInt(i)) {
                                level = Integer.parseInt(i);
                            }
                        } else if (i.startsWith("cost:")) {
                            i = i.replaceAll("cost:", "");
                            
                            if (Methods.isInt(i)) {
                                cost = Integer.parseInt(i);
                            }
                        }
                    }
                    
                    levels.put(level, cost);
                }
                
                upgradeableEnchantments.add(new UpgradeableEnchantment(enchant, levels));
            }
        }
    }
    
    public ArrayList<String> getDuplicateItems() {
        return duplicateItems;
    }
    
    public ArrayList<String> getBrokeItems() {
        return brokeItems;
    }
    
    public FileManager getFileManager() {
        return FileManager.getInstance();
    }
    
    public boolean useMetrics() {
        return useMetrics;
    }
    
    public boolean checkForUpdates() {
        return checkUpdates;
    }
    
    public ArrayList<CustomCurrency> getCustomCurrencies() {
        return customCurrencies;
    }
    
    public CustomCurrency getCustomCurrency(String currency) {
        for (CustomCurrency custom : customCurrencies) {
            if (custom.name().equalsIgnoreCase(currency)) {
                return custom;
            }
        }
        
        return null;
    }
    
    public ItemStack getSellingWand() {
        return sellingWand.build();
    }
    
    public ItemStack getChestSellingItem(int amount) {
        return sellingWand.clone().setAmount(amount).build();
    }
    
    public ArrayList<SellItem> getSellableItems(Inventory inv) {
        ArrayList<SellItem> items = new ArrayList<>();
        
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                if (canSellItem(item)) {
                    double price = 0.0;
                    int sellingMinimum = 0, sellingAmount = 0;
                    String command = "";
                    Currency currency = getBaseCurrency();
                    CustomCurrency customCurrency = baseCustomCurrency;
                    boolean found = false;

                    /*for (SellableItem sellItem : getRegisteredSellableItems()) {
                        if (sellItem.getItemBuilder().isSimilar(item)) {
                            command = sellItem.getCommand();
                            currency = sellItem.getCurrency();
                            customCurrency = sellItem.getCustomCurrency();

                            if (sellItem.usesCheckAmount()) {
                                int amount = item.getAmount();
                                sellingMinimum = sellItem.getCheckAmount();

                                while (amount >= sellItem.getCheckAmount()) {
                                    amount -= sellItem.getCheckAmount();
                                    sellingAmount++;
                                }

                                price = sellItem.getPrice() * sellingAmount;
                            } else {
                                price = sellItem.getPrice() * item.getAmount();
                            }

                            if (item.hasItemMeta()) {
                                if (item.getItemMeta().hasEnchants()) {
                                    for (UpgradeableEnchantment enchantment : getUpgradeableEnchantment()) {
                                        if (item.getItemMeta().hasEnchant(enchantment.getEnchantment())) {
                                            if (enchantment.getLevels().containsKey(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment()))) {
                                                price += enchantment.getLevels().get(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment())) * item.getAmount();
                                            }
                                        }
                                    }
                                }
                            }

                            found = true;
                            break;
                        }
                    }*/
                    
                    if (!found) {
                        price = getBasePrice() * item.getAmount();
                        
                        if (currency == Currency.CUSTOM) {
                            command = customCurrency.command();
                        }
                        
                        if (item.hasItemMeta()) {
                            if (item.getItemMeta().hasEnchants()) {
                                for (UpgradeableEnchantment enchantment : getUpgradeableEnchantment()) {
                                    if (item.getItemMeta().hasEnchant(enchantment.enchantment())) {
                                        if (enchantment.levels().containsKey(item.getItemMeta().getEnchantLevel(enchantment.enchantment()))) {
                                            price += enchantment.levels().get(item.getItemMeta().getEnchantLevel(enchantment.enchantment())) * item.getAmount();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    items.add(new SellItem(item, sellingAmount, sellingMinimum, price, currency, customCurrency, command));
                }
            }
        }
        
        return items;
    }
    
    public ArrayList<SellItem> getSellableItems(Inventory inv, ArrayList<ItemStack> selling) {
        ArrayList<SellItem> items = new ArrayList<>();
        
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                for (ItemStack sell : selling) {
                    if (sell != null) {
                        if (item.getType() == sell.getType()) {
                            if (item.getDurability() == sell.getDurability()) {
                                if (canSellItem(item)) {
                                    double price = 0.0;
                                    int sellingMinimum = 0, sellingAmount = 0;
                                    String command = "";
                                    Currency currency = getBaseCurrency();
                                    CustomCurrency customCurrency = getBaseCustomCurrency();
                                    boolean found = false;

                                    /*for (SellableItem sellItem : getRegisteredSellableItems()) {
                                        if (sellItem.getItemBuilder().isSimilar(item)) {
                                            command = sellItem.getCommand();
                                            currency = sellItem.getCurrency();
                                            customCurrency = sellItem.getCustomCurrency();

                                            if (sellItem.usesCheckAmount()) {
                                                int amount = item.getAmount();
                                                sellingMinimum = sellItem.getCheckAmount();

                                                while (amount >= sellItem.getCheckAmount()) {
                                                    amount -= sellItem.getCheckAmount();
                                                    sellingAmount++;
                                                }

                                                price = sellItem.getPrice() * sellingAmount;
                                            } else {
                                                price = sellItem.getPrice() * item.getAmount();
                                            }

                                            if (item.hasItemMeta()) {
                                                if (item.getItemMeta().hasEnchants()) {
                                                    for (UpgradeableEnchantment enchantment : getUpgradeableEnchantment()) {
                                                        if (item.getItemMeta().hasEnchant(enchantment.getEnchantment())) {
                                                            if (enchantment.getLevels().containsKey(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment()))) {
                                                                price += enchantment.getLevels().get(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment())) * item.getAmount();
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            found = true;
                                            break;
                                        }
                                    }*/
                                    
                                    if (!found) {
                                        price = getBasePrice() * item.getAmount();
                                        
                                        if (currency == Currency.CUSTOM) {
                                            command = customCurrency.command();
                                        }
                                        
                                        if (item.hasItemMeta()) {
                                            if (item.getItemMeta().hasEnchants()) {
                                                for (UpgradeableEnchantment enchantment : getUpgradeableEnchantment()) {
                                                    if (item.getItemMeta().hasEnchant(enchantment.enchantment())) {
                                                        if (enchantment.levels().containsKey(item.getItemMeta().getEnchantLevel(enchantment.enchantment()))) {
                                                            price += enchantment.levels().get(item.getItemMeta().getEnchantLevel(enchantment.enchantment())) * item.getAmount();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    
                                    items.add(new SellItem(item, sellingAmount, sellingMinimum, price, currency, customCurrency, command));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return items;
    }
    
    public void sellSellableItems(Player player, ArrayList<SellItem> items) {
        HashMap<Currency, Double> amounts = new HashMap<>();
        HashMap<Currency, String> commands = new HashMap<>();
        
        for (SellItem item : items) {
            if (amounts.containsKey(item.getCurrency())) {
                amounts.put(item.getCurrency(), (amounts.get(item.getCurrency()) + item.getPrice()));
            } else {
                amounts.put(item.getCurrency(), item.getPrice());
                commands.put(item.getCurrency(), item.getCommand());
            }
        }
        
        for (Currency currency : amounts.keySet()) {
            CurrencyAPI.giveCurrency(player, currency, amounts.get(currency), commands.get(currency));
        }
    }
    
    public double getFullCost(ArrayList<SellItem> items, Currency currency) {
        double cost = 0.0;
        
        for (SellItem item : items) {
            if (item.getCurrency() == currency) {
                cost += item.getPrice();
            }
        }
        
        return cost;
    }
    
    public double getFullCost(ArrayList<SellItem> items, CustomCurrency currency) {
        double cost = 0.0;
        
        for (SellItem item : items) {
            if (item.getCurrency() == Currency.CUSTOM) {
                if (currency.name().equalsIgnoreCase(item.getCustomCurrency().name())) {
                    cost += item.getPrice();
                }
            }
        }
        
        return cost;
    }
    
    public boolean canSellItem(ItemStack item) {
        for (Material blocked : getBlackListItems()) {
            if (blocked == item.getType()) {
                return false;
            }
        }
        
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasEnchants()) {
                for (Enchantment blocked : getBlackListEnchantments()) {
                    if (item.getItemMeta().hasEnchant(blocked)) {
                        return false;
                    }
                }
            }
        }
        
        if (!Files.CONFIG.getFile().getBoolean("Settings.Allow-Damaged-Items")) {
            if (getDamageableItems().contains(item.getType())) {
                if (item.getDurability() > 0) {
                    return false;
                }
            }
        }
        
        boolean pricesOnly = Files.CONFIG.getFile().getBoolean("Settings.Selling-Options.Price-Selling-Only");
        // boolean canSell = !pricesOnly;
/*
        for (SellableItem sellable : getRegisteredSellableItems()) {
            if (sellable.getItemBuilder().isSimilar(item)) {
                if (pricesOnly) {
                    canSell = true;
                }

                if (canSell) {
                    if (sellable.usesCheckAmount()) {
                        canSell = item.getAmount() >= sellable.getCheckAmount();
                    }
                }

                break;
            }
        }
 */
        
        return false;
    }
    
    public ArrayList<SellableItem> getRegisteredSellableItems() {
        return registeredSellableItems;
    }
    
    public ArrayList<Material> getBlackListItems() {
        return blackListItems;
    }
    
    public ArrayList<Enchantment> getBlackListEnchantments() {
        return blackListEnchantments;
    }
    
    public ArrayList<UpgradeableEnchantment> getUpgradeableEnchantment() {
        return upgradeableEnchantments;
    }
    
    public double getBasePrice() {
        return this.basePrice;
    }
    
    public Currency getBaseCurrency() {
        return this.baseCurrency;
    }
    
    public CustomCurrency getBaseCustomCurrency() {
        return this.baseCustomCurrency;
    }
    
    public boolean isRadiusAcceptable(Location pos1, Location pos2) {
        int blocks = 0;
        int maxBlocks = Files.CONFIG.getFile().getInt("Settings.Region-Options.Max-Block-Area");
        Location min = getMinimumPoint(pos1, pos2).toLocation(pos1.getWorld());
        Location max = getMaximumPoint(pos1, pos2).toLocation(pos1.getWorld());
        
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    blocks++;
                    
                    if (blocks >= maxBlocks) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    public boolean hasChestQuery(UUID uuid) {
        return queryChests.containsKey(uuid);
    }
    
    public ArrayList<Chest> getChestQuery(UUID uuid) {
        return queryChests.get(uuid);
    }
    
    public void removeChestQuery(UUID uuid) {
        queryChests.remove(uuid);
    }
    
    public void queryChests(final Player player, final Location pos1, final Location pos2) {
        ArrayList<Chest> chests = new ArrayList<>();
        int maxChestSell = Files.CONFIG.getFile().getInt("Settings.Region-Options.Max-Chest-Sell");
        boolean maxChestToggle = Files.CONFIG.getFile().getBoolean("Settings.Region-Options.Chest-Sell-Toggle");
        Location min = getMinimumPoint(pos1, pos2).toLocation(pos1.getWorld());
        Location max = getMaximumPoint(pos1, pos2).toLocation(pos1.getWorld());
        
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block block = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
                    
                    if (!block.getChunk().isLoaded()) {
                        block.getChunk().load();
                    }
                    
                    if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                        Chest chest = (Chest) block.getState();
                        
                        if (!Methods.isInvEmpty(chest.getInventory())) {
                            if (getSellableItems(chest.getInventory()).size() > 0) {
                                if (maxChestToggle) {
                                    if (chests.size() >= maxChestSell) {
                                        break;
                                    }
                                }
                                
                                chests.add(chest);
                            }
                        }
                    }
                }
            }
        }
        
        queryChests.put(player.getUniqueId(), chests);
    }
    
    public boolean useTwoFactorAuth() {
        return Files.CONFIG.getFile().getBoolean("Settings.Use-Two-Factor-Auth");
    }
    
    public boolean needsTwoFactorAuth(UUID uuid) {
        return useTwoFactorAuth() && !twoFactorAuth.contains(uuid);
    }
    
    public void addTwoFactorAuth(UUID uuid) {
        twoFactorAuth.add(uuid);
    }
    
    public void removeTwoFactorAuth(UUID uuid) {
        twoFactorAuth.remove(uuid);
    }
    
    public void openSellChestGUI(Player player) {
        // player.openInventory(Bukkit.createInventory(null, 54, Methods.color(getConfig().getString("Settings.Sign-Options.Inventory-Name"))));
    }
    
    public boolean isRegisteredMaterial(ItemBuilder itemBuilder) {
        //for (ItemBuilder item : registeredMaterials.keySet()) {
        //if (item.getMaterial() == itemBuilder.getMaterial() &&
        //item.getMetaData().equals(itemBuilder.getMetaData())) {
        //    return true;
        //}
        //}
        
        return false;
    }
    
    public boolean isRegisteredMaterial(ItemStack itemStack) {
        ItemBuilder item = ItemBuilder.convertItemStack(itemStack);
        
        for (ItemBuilder itemBuilder : registeredMaterials.keySet()) {
            if (item.getMaterial() == itemBuilder.getMaterial() &&
            item.getMaterial().data.equals(itemBuilder.getMaterial().data)) {
                return true;
            }
        }
        
        return false;
    }
    
    public RegisterType getRegisteredPlugin(ItemBuilder itemBuilder) {
        for (ItemBuilder item : registeredMaterials.keySet()) {
            if (item.getMaterial() == itemBuilder.getMaterial() &&
            item.getMaterial().data.equals(itemBuilder.getMaterial().data)) {
                return registeredMaterials.get(item);
            }
        }
        
        return null;
    }
    
    public RegisterType getRegisteredPlugin(ItemStack item) {
        //for (ItemBuilder itemBuilder : registeredMaterials.keySet()) {
        //if (item.getType() == itemBuilder.getMaterial() &&
        //item.getDurability() == itemBuilder.getMetaData()) {
        //    return registeredMaterials.get(itemBuilder);
        //}
        //}
        
        return null;
    }
    
    private Vector getMinimumPoint(Location pos1, Location pos2) {
        return new Vector(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
    }
    
    private Vector getMaximumPoint(Location pos1, Location pos2) {
        return new Vector(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }
    
    private ArrayList<Material> getDamageableItems() {
        ArrayList<Material> materials = new ArrayList<>();
        
        if (ServerProtocol.isNewer(ServerProtocol.v1_12_R1)) {
            materials.add(Material.matchMaterial("GOLDEN_HELMET"));
            materials.add(Material.matchMaterial("GOLDEN_CHESTPLATE"));
            materials.add(Material.matchMaterial("GOLDEN_LEGGINGS"));
            materials.add(Material.matchMaterial("GOLDEN_BOOTS"));
            materials.add(Material.matchMaterial("WOODEN_SWORD"));
            materials.add(Material.matchMaterial("WOODEN_AXE"));
            materials.add(Material.matchMaterial("WOODEN_PICKAXE"));
            materials.add(Material.matchMaterial("WOODEN_AXE"));
            materials.add(Material.matchMaterial("WOODEN_SHOVEL"));
            materials.add(Material.matchMaterial("STONE_SHOVEL"));
            materials.add(Material.matchMaterial("IRON_SHOVEL"));
            materials.add(Material.matchMaterial("DIAMOND_SHOVEL"));
            materials.add(Material.matchMaterial("WOODEN_HOE"));
            materials.add(Material.matchMaterial("GOLDEN_HOE"));
        } else {
            materials.add(Material.matchMaterial("GOLD_HELMET"));
            materials.add(Material.matchMaterial("GOLD_CHESTPLATE"));
            materials.add(Material.matchMaterial("GOLD_LEGGINGS"));
            materials.add(Material.matchMaterial("GOLD_BOOTS"));
            materials.add(Material.matchMaterial("WOOD_SWORD"));
            materials.add(Material.matchMaterial("WOOD_AXE"));
            materials.add(Material.matchMaterial("WOOD_PICKAXE"));
            materials.add(Material.matchMaterial("WOOD_AXE"));
            materials.add(Material.matchMaterial("WOOD_SPADE"));
            materials.add(Material.matchMaterial("STONE_SPADE"));
            materials.add(Material.matchMaterial("IRON_SPADE"));
            materials.add(Material.matchMaterial("DIAMOND_SPADE"));
            materials.add(Material.matchMaterial("WOOD_HOE"));
            materials.add(Material.matchMaterial("GOLD_HOE"));
        }
        
        materials.add(Material.DIAMOND_HELMET);
        materials.add(Material.DIAMOND_CHESTPLATE);
        materials.add(Material.DIAMOND_LEGGINGS);
        materials.add(Material.DIAMOND_BOOTS);
        materials.add(Material.CHAINMAIL_HELMET);
        materials.add(Material.CHAINMAIL_CHESTPLATE);
        materials.add(Material.CHAINMAIL_LEGGINGS);
        materials.add(Material.CHAINMAIL_BOOTS);
        materials.add(Material.IRON_HELMET);
        materials.add(Material.IRON_CHESTPLATE);
        materials.add(Material.IRON_LEGGINGS);
        materials.add(Material.IRON_BOOTS);
        materials.add(Material.LEATHER_HELMET);
        materials.add(Material.LEATHER_CHESTPLATE);
        materials.add(Material.LEATHER_LEGGINGS);
        materials.add(Material.LEATHER_BOOTS);
        materials.add(Material.BOW);
        materials.add(Material.STONE_SWORD);
        materials.add(Material.IRON_SWORD);
        materials.add(Material.DIAMOND_SWORD);
        materials.add(Material.STONE_AXE);
        materials.add(Material.IRON_AXE);
        materials.add(Material.DIAMOND_AXE);
        materials.add(Material.STONE_PICKAXE);
        materials.add(Material.IRON_PICKAXE);
        materials.add(Material.DIAMOND_PICKAXE);
        materials.add(Material.STONE_AXE);
        materials.add(Material.IRON_AXE);
        materials.add(Material.DIAMOND_AXE);
        materials.add(Material.STONE_HOE);
        materials.add(Material.IRON_HOE);
        materials.add(Material.DIAMOND_HOE);
        materials.add(Material.FLINT_AND_STEEL);
        materials.add(Material.ANVIL);
        materials.add(Material.FISHING_ROD);
        return materials;
    }
    
}