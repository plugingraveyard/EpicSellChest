package me.badbones69.epicsellchest.api;

import org.bukkit.inventory.ItemStack;

import me.badbones69.epicsellchest.api.currency.Currency;
import me.badbones69.epicsellchest.api.currency.CustomCurrency;

public class SellableItem {
	
	private int price;
	private ItemStack item;
	private String command;
	private Currency currency;
	private CustomCurrency custom;
	
	public SellableItem(ItemStack item, int price, Currency currency, CustomCurrency custom, String command) {
		this.item = item;
		this.price = price;
		this.custom = custom;
		this.command = command;
		this.currency = currency;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public Currency getCurrency() {
		return this.currency;
	}
	
	public CustomCurrency getCustomCurrency() {
		return custom;
	}

	public String getCommand() {
		return command;
	}
	
}