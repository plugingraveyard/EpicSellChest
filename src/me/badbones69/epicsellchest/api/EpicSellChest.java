package me.badbones69.epicsellchest.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.badbones69.epicsellchest.Main;
import me.badbones69.epicsellchest.Methods;
import me.badbones69.epicsellchest.SettingsManager;
import me.badbones69.epicsellchest.api.currency.Currency;
import me.badbones69.epicsellchest.api.currency.CurrencyAPI;
import me.badbones69.epicsellchest.api.currency.CustomCurrency;

public class EpicSellChest {
	
	private Integer basePrice;
	private Boolean useMetrics;
	private Boolean checkUpdates;
	private Currency baseCurrency;
	private ItemStack chestSellingItem;
	private static EpicSellChest instance;
	private ArrayList<UUID> twoFactorAuth = new ArrayList<>();
	private ArrayList<Material> blackListItems = new ArrayList<>();
	private ArrayList<SellableItem> sellableItems = new ArrayList<>();
	private HashMap<UUID, ArrayList<Chest>> queryChests = new HashMap<>();
	private ArrayList<CustomCurrency> customCurrencies = new ArrayList<>();
	private ArrayList<Enchantment> blackListEnchantments = new ArrayList<>();
	private ArrayList<UpgradeableEnchantment> upgradeableEnchantments = new ArrayList<>();
	
	public static EpicSellChest getInstance() {
		if(instance != null) {
			return instance;
		}else {
			instance = new EpicSellChest();
			return instance;
		}
	}
	
	public void loadEpicSellChest() {
		sellableItems.clear();
		chestSellingItem = null;
		blackListItems.clear();
		customCurrencies.clear();
		blackListEnchantments.clear();
		upgradeableEnchantments.clear();
		FileConfiguration config = Main.settings.getConfig();
		basePrice = config.getInt("Settings.Base-Price");
		useMetrics = config.getBoolean("Settings.Metrics");
		checkUpdates = config.getBoolean("Settings.Check-For-Updates");
		baseCurrency = Currency.getCurrency(config.getString("Settings.Base-Currency"));
		chestSellingItem = Methods.addGlowing(Methods.makeItem(config.getString("Settings.Chest-Selling-Item.Item"), 1, config.getString("Settings.Chest-Selling-Item.Name"), config.getStringList("Settings.Chest-Selling-Item.Lore")), config.getBoolean("Settings.Chest-Selling-Item.Glowing"));
		for(String currency : config.getConfigurationSection("Settings.Custom-Currencies").getKeys(false)) {
			customCurrencies.add(new CustomCurrency(currency, config.getString("Settings.Custom-Currencies." + currency + ".Command")));
		}
		for(String id : config.getStringList("Settings.Black-List-Items")) {
			int md = 0;
			if(id.contains(":")) {
				md = Integer.parseInt(id.split(":")[1]);
				id = id.split(":")[0];
			}
			Material m = Material.matchMaterial(id);
			if(m != null) {
				blackListItems.add(new ItemStack(m, 1, (short) md).getType());
			}
		}
		for(String name : config.getStringList("Settings.Black-List-Enchantments")) {
			Enchantment enchant = Enchantment.getByName(name);
			if(enchant != null) {
				blackListEnchantments.add(enchant);
			}
		}
		for(String line : config.getStringList("Settings.Item-Cost")) {
			int cost = 0;
			ItemStack item = null;
			Material m = null;
			String command = "";
			Currency currency = null;
			CustomCurrency custom = null;
			for(String i : line.split(", ")) {
				i = i.toLowerCase();
				if(i.startsWith("item:")) {
					i = i.replaceAll("item:", "");
					int md = 0;
					String id = i;
					if(i.contains(":")) {
						md = Integer.parseInt(i.split(":")[1]);
						id = i.split(":")[0];
					}
					m = Material.matchMaterial(id);
					item = new ItemStack(m, 1, (short) md);
				}else if(i.startsWith("cost:")) {
					i = i.replaceAll("cost:", "");
					if(Methods.isInt(i)) {
						cost = Integer.parseInt(i);
					}
				}else if(i.startsWith("currency:")) {
					i = i.replaceAll("currency:", "");
					Currency c = Currency.getCurrency(i);
					if(Currency.isCurrency(i)) {
						if(c != null) {
							currency = Currency.getCurrency(i);
							if(c == Currency.CUSTOM) {
								CustomCurrency cu = getCustomCurrency(i);
								if(cu != null) {
									custom = cu;
									command = cu.getCommand();
								}
							}
						}
					}
				}
			}
			if(m != null && currency != null) {
				sellableItems.add(new SellableItem(item, cost, currency, custom, command));
			}
		}
		for(String name : config.getConfigurationSection("Settings.Enchantment-Cost").getKeys(false)) {
			Enchantment enchant = Enchantment.getByName(name);
			if(enchant != null) {
				HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();
				for(String line : config.getStringList("Settings.Enchantment-Cost." + name)) {
					int cost = 0;
					int level = 0;
					for(String i : line.split(", ")) {
						i = i.toLowerCase();
						if(i.startsWith("level:")) {
							i = i.replaceAll("level:", "");
							if(Methods.isInt(i)) {
								level = Integer.parseInt(i);
							}
						}else if(i.startsWith("cost:")) {
							i = i.replaceAll("cost:", "");
							if(Methods.isInt(i)) {
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
	
	public SettingsManager getSettingsManager() {
		return Main.settings;
	}
	
	public FileConfiguration getConfig() {
		return getSettingsManager().getConfig();
	}
	
	public FileConfiguration getMessages() {
		return getSettingsManager().getMessages();
	}
	
	public Boolean useMetrics() {
		return useMetrics;
	}
	
	public Boolean checkForUpdates() {
		return checkUpdates;
	}
	
	public ArrayList<CustomCurrency> getCustomCurrencies() {
		return customCurrencies;
	}
	
	public CustomCurrency getCustomCurrency(String currency) {
		for(CustomCurrency custom : customCurrencies) {
			if(custom.getName().equalsIgnoreCase(currency)) {
				return custom;
			}
		}
		return null;
	}
	
	public ItemStack getChestSellingItem() {
		return chestSellingItem.clone();
	}
	
	public ItemStack getChestSellingItem(int amount) {
		ItemStack item = chestSellingItem.clone();
		item.setAmount(amount);
		return item;
	}
	
	public ArrayList<SellItem> getSellableItems(Inventory inv) {
		ArrayList<SellItem> items = new ArrayList<SellItem>();
		for(ItemStack item : inv.getContents()) {
			if(item != null) {
				if(canSellItem(item)) {
					int price = 0;
					String command = "";
					Currency currency = getBaseCurrency();
					CustomCurrency custom = null;
					Boolean found = false;
					for(SellableItem sellItem : getSellableItems()) {
						if(sellItem.getItem().getType() == item.getType()
						&& sellItem.getItem().getDurability() == item.getDurability()) {
							command = sellItem.getCommand();
							currency = sellItem.getCurrency();
							custom = sellItem.getCustomCurrency();
							price = sellItem.getPrice() * item.getAmount();
							if(item.hasItemMeta()) {
								if(item.getItemMeta().hasEnchants()) {
									for(UpgradeableEnchantment enchantment : getUpgradeableEnchantment()) {
										if(item.getItemMeta().hasEnchant(enchantment.getEnchantment())) {
											if(enchantment.getLevels().containsKey(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment()))) {
												price += enchantment.getLevels().get(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment())) * item.getAmount();
											}
										}
									}
								}
							}
							found = true;
							break;
						}
					}
					if(!found) {
						price = getBasePrice() * item.getAmount();
						if(item.hasItemMeta()) {
							if(item.getItemMeta().hasEnchants()) {
								for(UpgradeableEnchantment enchantment : getUpgradeableEnchantment()) {
									if(item.getItemMeta().hasEnchant(enchantment.getEnchantment())) {
										if(enchantment.getLevels().containsKey(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment()))) {
											price += enchantment.getLevels().get(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment())) * item.getAmount();
										}
									}
								}
							}
						}
					}
					items.add(new SellItem(item, price, currency, custom, command));
				}
			}
		}
		return items;
	}
	
	public ArrayList<SellItem> getSellableItems(Inventory inv, ArrayList<ItemStack> selling) {
		ArrayList<SellItem> items = new ArrayList<SellItem>();
		for(ItemStack item : inv.getContents()) {
			if(item != null) {
				for(ItemStack sell : selling) {
					if(sell != null) {
						if(item.getType() == sell.getType()) {
							if(item.getDurability() == sell.getDurability()) {
								if(canSellItem(item)) {
									int price = 0;
									String command = "";
									Currency currency = getBaseCurrency();
									CustomCurrency custom = null;
									Boolean found = false;
									for(SellableItem sellItem : getSellableItems()) {
										if(sellItem.getItem().getType() == item.getType()
										&& sellItem.getItem().getDurability() == item.getDurability()) {
											command = sellItem.getCommand();
											currency = sellItem.getCurrency();
											custom = sellItem.getCustomCurrency();
											price = sellItem.getPrice() * item.getAmount();
											if(item.hasItemMeta()) {
												if(item.getItemMeta().hasEnchants()) {
													for(UpgradeableEnchantment enchantment : getUpgradeableEnchantment()) {
														if(item.getItemMeta().hasEnchant(enchantment.getEnchantment())) {
															if(enchantment.getLevels().containsKey(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment()))) {
																price += enchantment.getLevels().get(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment())) * item.getAmount();
															}
														}
													}
												}
											}
											found = true;
											break;
										}
									}
									if(!found) {
										price = getBasePrice() * item.getAmount();
										if(item.hasItemMeta()) {
											if(item.getItemMeta().hasEnchants()) {
												for(UpgradeableEnchantment enchantment : getUpgradeableEnchantment()) {
													if(item.getItemMeta().hasEnchant(enchantment.getEnchantment())) {
														if(enchantment.getLevels().containsKey(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment()))) {
															price += enchantment.getLevels().get(item.getItemMeta().getEnchantLevel(enchantment.getEnchantment())) * item.getAmount();
														}
													}
												}
											}
										}
									}
									items.add(new SellItem(item, price, currency, custom, command));
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
		for(SellItem item : items) {
			CurrencyAPI.giveCurrency(player, item.getCurrency(), item.getPrice(), item.getCommand());
		}
	}
	
	public Integer getFullCost(Player player, ArrayList<SellItem> items, Currency currency) {
		int cost = 0;
		for(SellItem item : items) {
			if(item.getCurrency() == currency) {
				cost += item.getPrice();
			}
		}
		return cost;
	}
	
	public Integer getFullCost(Player player, ArrayList<SellItem> items, CustomCurrency currency) {
		int cost = 0;
		for(SellItem item : items) {
			if(item.getCurrency() == Currency.CUSTOM) {
				if(currency.getName().equalsIgnoreCase(item.getCustomCurrency().getName())) {
					cost += item.getPrice();
				}
			}
		}
		return cost;
	}
	
	public Boolean canSellItem(ItemStack item) {
		for(Material blocked : getBlackListItems()) {
			if(blocked == item.getType()) {
				return false;
			}
		}
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasEnchants()) {
				for(Enchantment blocked : getBlackListEnchantments()) {
					if(item.getItemMeta().hasEnchant(blocked)) {
						return false;
					}
				}
			}
		}
		if(!Main.settings.getConfig().getBoolean("Settings.Allow-Damaged-Items")) {
			if(getDamageableItems().contains(item.getType())) {
				if(item.getDurability() > 0) {
					return false;
				}
			}
		}
		if(Main.settings.getConfig().getBoolean("Settings.Selling-Options.Price-Selling-Only")) {
			Boolean found = false;
			for(SellableItem sellable : getSellableItems()) {
				if(sellable.getItem().getType() == item.getType()
				&& sellable.getItem().getDurability() == item.getDurability()) {
					found = true;
					break;
				}
			}
			if(!found) {
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<SellableItem> getSellableItems() {
		return sellableItems;
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
	
	public int getBasePrice() {
		return this.basePrice;
	}
	
	public Currency getBaseCurrency() {
		return this.baseCurrency;
	}
	
	public boolean isRadiusAcceptable(Location pos1, Location pos2) {
		int blocks = 0;
		int maxBlocks = Main.settings.getConfig().getInt("Settings.Region-Options.Max-Block-Area");
		Location min = getMinimumPoint(pos1, pos2).toLocation(pos1.getWorld());
		Location max = getMaximumPoint(pos1, pos2).toLocation(pos1.getWorld());
		for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
			for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
				for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					blocks++;
					if(blocks >= maxBlocks) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public Boolean hasChestQuery(UUID uuid) {
		return queryChests.containsKey(uuid);
	}
	
	public ArrayList<Chest> getChestQuery(UUID uuid) {
		return queryChests.get(uuid);
	}
	
	public void removeChestQuery(UUID uuid) {
		if(queryChests.containsKey(uuid)) {
			queryChests.remove(uuid);
		}
	}
	
	public void queryChests(final Player player, final Location pos1, final Location pos2) {
		//https://bukkit.org/threads/worldedit-api-plugin.119565/
		new BukkitRunnable() {
			@Override
			public void run() {
				ArrayList<Chest> chests = new ArrayList<Chest>();
				int maxChestSell = Main.settings.getConfig().getInt("Settings.Region-Options.Max-Chest-Sell");
				boolean maxChestToggle = Main.settings.getConfig().getBoolean("Settings.Region-Options.Chest-Sell-Toggle");
				Location min = getMinimumPoint(pos1, pos2).toLocation(pos1.getWorld());
				Location max = getMaximumPoint(pos1, pos2).toLocation(pos1.getWorld());
				for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
					for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
						for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
							Block block = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
							if(!block.getChunk().isLoaded()) {
								block.getChunk().load();
							}
							if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
								Chest chest = (Chest) block.getState();
								if(!Methods.isInvEmpty(chest.getInventory())) {
									if(getSellableItems(chest.getInventory()).size() > 0) {
										if(maxChestToggle) {
											if(chests.size() >= maxChestSell) {
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
		}.runTaskAsynchronously(Bukkit.getServer().getPluginManager().getPlugin("EpicSellChest"));
	}
	
	public Boolean useTwoFactorAuth() {
		return Main.settings.getConfig().getBoolean("Settings.Use-Two-Factor-Auth");
	}
	
	public Boolean needsTwoFactorAuth(UUID uuid) {
		if(useTwoFactorAuth()) {
			return !twoFactorAuth.contains(uuid);
		}
		return false;
	}
	
	public void addTwoFactorAuth(UUID uuid) {
		twoFactorAuth.add(uuid);
	}
	
	public void removeTwoFactorAuth(UUID uuid) {
		if(twoFactorAuth.contains(uuid)) {
			twoFactorAuth.remove(uuid);
		}
	}
	
	public void openSellChestGUI(Player player) {
		player.openInventory(Bukkit.createInventory(null, 54, Methods.color(getConfig().getString("Settings.Sign-Options.Inventory-Name"))));
	}
	
	public Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin("EpicSellChest");
	}
	
	private Vector getMinimumPoint(Location pos1, Location pos2) {
		//https://github.com/sk89q/WorldEdit/blob/34c31dc020307a45482ad53ae4d1459b5f653a94/worldedit-core/src/main/java/com/sk89q/worldedit/regions/CuboidRegion.java#L163
		return new Vector(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
	}
	
	private Vector getMaximumPoint(Location pos1, Location pos2) {
		//https://github.com/sk89q/WorldEdit/blob/34c31dc020307a45482ad53ae4d1459b5f653a94/worldedit-core/src/main/java/com/sk89q/worldedit/regions/CuboidRegion.java#L170
		return new Vector(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
	}
	
	private ArrayList<Material> getDamageableItems() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.DIAMOND_CHESTPLATE);
		ma.add(Material.DIAMOND_LEGGINGS);
		ma.add(Material.DIAMOND_BOOTS);
		ma.add(Material.CHAINMAIL_HELMET);
		ma.add(Material.CHAINMAIL_CHESTPLATE);
		ma.add(Material.CHAINMAIL_LEGGINGS);
		ma.add(Material.CHAINMAIL_BOOTS);
		ma.add(Material.GOLD_HELMET);
		ma.add(Material.GOLD_CHESTPLATE);
		ma.add(Material.GOLD_LEGGINGS);
		ma.add(Material.GOLD_BOOTS);
		ma.add(Material.IRON_HELMET);
		ma.add(Material.IRON_CHESTPLATE);
		ma.add(Material.IRON_LEGGINGS);
		ma.add(Material.IRON_BOOTS);
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.DIAMOND_CHESTPLATE);
		ma.add(Material.DIAMOND_LEGGINGS);
		ma.add(Material.DIAMOND_BOOTS);
		ma.add(Material.BOW);
		ma.add(Material.WOOD_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.DIAMOND_AXE);
		ma.add(Material.WOOD_PICKAXE);
		ma.add(Material.STONE_PICKAXE);
		ma.add(Material.IRON_PICKAXE);
		ma.add(Material.DIAMOND_PICKAXE);
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.DIAMOND_AXE);
		ma.add(Material.WOOD_SPADE);
		ma.add(Material.STONE_SPADE);
		ma.add(Material.IRON_SPADE);
		ma.add(Material.DIAMOND_SPADE);
		ma.add(Material.WOOD_HOE);
		ma.add(Material.STONE_HOE);
		ma.add(Material.IRON_HOE);
		ma.add(Material.DIAMOND_HOE);
		ma.add(Material.FLINT_AND_STEEL);
		ma.add(Material.ANVIL);
		ma.add(Material.FISHING_ROD);
		return ma;
	}
	
}