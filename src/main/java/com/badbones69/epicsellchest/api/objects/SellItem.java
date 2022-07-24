package com.badbones69.epicsellchest.api.objects;

import com.badbones69.epicsellchest.api.currency.Currency;
import com.badbones69.epicsellchest.api.currency.CustomCurrency;
import org.bukkit.inventory.ItemStack;

public record SellItem(ItemStack item, int sellingAmount, int sellingMinimum, double price, Currency currency, CustomCurrency customCurrency, String command) {
    
    /**
     * @return True if the item needs multiple items to be sold.
     */
    public boolean hasSellingAmount() {
        return sellingAmount > 0;
    }
    
}