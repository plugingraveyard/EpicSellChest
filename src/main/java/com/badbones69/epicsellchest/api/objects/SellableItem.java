package com.badbones69.epicsellchest.api.objects;

import com.badbones69.epicsellchest.api.ItemBuilder;
import com.badbones69.epicsellchest.api.currency.Currency;
import com.badbones69.epicsellchest.api.currency.CustomCurrency;
import org.bukkit.inventory.ItemStack;

public record SellableItem(ItemBuilder itemBuilder, double price, Currency currency, CustomCurrency customCurrency, String command, boolean checkAmount) {
    
    static ItemStack item;
    
    public SellableItem {
        item = itemBuilder.build();
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public int getCheckAmount() {
        return item.getAmount();
    }
    
}