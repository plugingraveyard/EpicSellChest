package me.badbones69.epicsellchest.api.currency;

import me.badbones69.epicsellchest.api.EpicSellChest;

public enum Currency {
    
    CUSTOM("Custom"), XP_LEVEL("XP_Level"), XP_TOTAL("XP_Total");
    
    private String name;
    private static EpicSellChest sc = EpicSellChest.getInstance();
    
    private Currency(String name) {
        this.name = name;
    }
    
    /**
     * Get the name of the currency.
     * @return The name of the currency.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Checks if it is a compatable curency.
     * @param currency The currency name you are checking.
     * @return True if it is supported and false if not.
     */
    public static Boolean isCurrency(String currency) {
        for (Currency c : Currency.values()) {
            if (currency.equalsIgnoreCase(c.getName())) {
                return true;
            }
        }
        for (CustomCurrency customCurrency : sc.getCustomCurrencies()) {
            if (currency.equalsIgnoreCase(customCurrency.getName())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get a currceny enum.
     * @param currency The currency you want.
     * @return The currency enum.
     */
    public static Currency getCurrency(String currency) {
        for (Currency c : Currency.values()) {
            if (currency.equalsIgnoreCase(c.getName())) {
                return c;
            }
        }
        for (CustomCurrency customCurrency : sc.getCustomCurrencies()) {
            if (currency.equalsIgnoreCase(customCurrency.getName())) {
                return CUSTOM;
            }
        }
        return null;
    }
    
}