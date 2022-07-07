package com.badbones69.epicsellchest.api.currency;

import com.badbones69.epicsellchest.api.CrazyManager;

public enum Currency {
    
    CUSTOM("Custom"), XP_LEVEL("XP_Level"), XP_TOTAL("XP_Total");
    
    private final String name;
    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    
    Currency(String name) {
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
     * Checks if it is a complete currency.
     * @param currency The currency name you are checking.
     * @return True if it is supported and false if not.
     */
    public static boolean isCurrency(String currency) {
        for (Currency c : Currency.values()) {
            if (currency.equalsIgnoreCase(c.getName())) {
                return true;
            }
        }

        for (CustomCurrency customCurrency : crazyManager.getCustomCurrencies()) {
            if (currency.equalsIgnoreCase(customCurrency.name())) {
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

        for (CustomCurrency customCurrency : crazyManager.getCustomCurrencies()) {
            if (currency.equalsIgnoreCase(customCurrency.name())) {
                return CUSTOM;
            }
        }

        return null;
    }
    
}