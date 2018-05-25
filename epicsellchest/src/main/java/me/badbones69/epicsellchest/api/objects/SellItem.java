package me.badbones69.epicsellchest.api.objects;

import me.badbones69.epicsellchest.api.currency.Currency;
import me.badbones69.epicsellchest.api.currency.CustomCurrency;
import org.bukkit.inventory.ItemStack;

public class SellItem {
	
	private int price, sellingAmount, sellingMinimum;
	private String command;
	private ItemStack item;
	private Currency currency;
	private CustomCurrency custom;
	
	public SellItem(ItemStack item, int sellingAmount, int sellingMinimum, int price, Currency currency, CustomCurrency custom, String command) {
		this.item = item;
		this.price = price;
		this.custom = custom;
		this.command = command;
		this.currency = currency;
		this.sellingAmount = sellingAmount;
		this.sellingMinimum = sellingMinimum;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public int getSellingAmount() {
		return sellingAmount;
	}
	
	public int getSellingMinimum() {
		return sellingMinimum;
	}
	
	public Boolean usesSellingAmount() {
		return sellingAmount > 0;
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
		return this.command;
	}
	
}