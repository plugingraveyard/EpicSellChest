package com.badbones69.epicsellchest.api.currency;

/**
 * A custom currency object.
 *
 * @param name Name of the custom currency.
 * @param command The command that is run for this currency.
 */
public record CustomCurrency(String name, String command) {}