package com.badbones69.epicsellchest.api.objects;

import com.badbones69.epicsellchest.api.ItemBuilder;
import com.badbones69.epicsellchest.api.currency.Currency;
import com.badbones69.epicsellchest.api.currency.CustomCurrency;
import org.bukkit.inventory.ItemStack;

public class SellableItem {
    
    private final double price;
    private final ItemBuilder itemBuilder;
    private final ItemStack item;
    private final String command;
    private final Currency currency;
    private final CustomCurrency customCurrency;
    private final boolean checkAmount;
    
    public SellableItem(ItemBuilder itemBuilder, double price, Currency currency, CustomCurrency customCurrency, String command, boolean checkAmount) {
        this.itemBuilder = itemBuilder;
        this.item = itemBuilder.build();
        this.price = price;
        this.customCurrency = customCurrency;
        this.command = command;
        this.currency = currency;
        this.checkAmount = checkAmount;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }
    
    public double getPrice() {
        return this.price;
    }
    
    public Currency getCurrency() {
        return this.currency;
    }
    
    public CustomCurrency getCustomCurrency() {
        return customCurrency;
    }
    
    public String getCommand() {
        return command;
    }
    
    public boolean usesCheckAmount() {
        return checkAmount;
    }
    
    public int getCheckAmount() {
        return item.getAmount();
    }
    
}