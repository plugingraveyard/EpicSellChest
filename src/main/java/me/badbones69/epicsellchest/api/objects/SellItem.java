package me.badbones69.epicsellchest.api.objects;

import me.badbones69.epicsellchest.api.currency.Currency;
import me.badbones69.epicsellchest.api.currency.CustomCurrency;
import org.bukkit.inventory.ItemStack;

public class SellItem {
    
    private double price;
    private int sellingAmount, sellingMinimum;
    private String command;
    private ItemStack item;
    private Currency currency;
    private CustomCurrency custom;
    
    /**
     *
     * @param item The item that is being sold.
     * @param sellingAmount The amount of times the minium amount goes into the total amount.
     * @param sellingMinimum The minmum amount the itemstack has to have to be sold.
     * @param price The worth of the items.
     * @param currency The type of currency that is being used.
     * @param custom If the currency is custom.
     * @param command The command that is being ran by the custom currency.
     */
    public SellItem(ItemStack item, int sellingAmount, int sellingMinimum, double price, Currency currency, CustomCurrency custom, String command) {
        this.item = item;
        this.price = price;
        this.custom = custom;
        this.command = command;
        this.currency = currency;
        this.sellingAmount = sellingAmount;
        this.sellingMinimum = sellingMinimum;
    }
    
    /**
     * @return The itemstack being sold.
     */
    public ItemStack getItem() {
        return this.item;
    }
    
    /**
     * @return The amount of times the minimum amount goes into the item anount.
     */
    public int getSellingAmount() {
        return sellingAmount;
    }
    
    /**
     * @return True if the item needs multiple items to be sold.
     */
    public boolean usesSellingAmount() {
        return sellingAmount > 0;
    }
    
    /**
     * @return The minimum amount of the itemstack has to have to be sold.
     */
    public int getSellingMinimum() {
        return sellingMinimum;
    }
    
    /**
     * @return The worth of the itemstack.
     */
    public double getPrice() {
        return this.price;
    }
    
    /**
     * @return The currency used for this itemstack.
     */
    public Currency getCurrency() {
        return this.currency;
    }
    
    /**
     * @return The custom currency used for this itemstack.
     */
    public CustomCurrency getCustomCurrency() {
        return custom;
    }
    
    /**
     * @return The command ran by the custom currency.
     */
    public String getCommand() {
        return this.command;
    }
    
}