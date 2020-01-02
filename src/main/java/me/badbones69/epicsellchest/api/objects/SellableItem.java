package me.badbones69.epicsellchest.api.objects;

import me.badbones69.epicsellchest.api.currency.Currency;
import me.badbones69.epicsellchest.api.currency.CustomCurrency;
import org.bukkit.inventory.ItemStack;

public class SellableItem {
    
    private Double price;
    private ItemBuilder itemBuilder;
    private ItemStack item;
    private String command;
    private Currency currency;
    private CustomCurrency customCurrency;
    private Boolean checkAmount;
    
    public SellableItem(ItemBuilder itemBuilder, Double price, Currency currency, CustomCurrency customCurrency, String command, Boolean checkAmount) {
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
    
    public Double getPrice() {
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
    
    public Boolean usesCheckAmount() {
        return checkAmount;
    }
    
    public int getCheckAmount() {
        return item.getAmount();
    }
    
}