package com.badbones69.epicsellchest.api.currency;

public record CustomCurrency(String name, String command) {

    /**
     * A custom currency object.
     *
     * @param name    Name of the custom currency.
     * @param command The command that is run for this currency.
     */
    public CustomCurrency {}
}