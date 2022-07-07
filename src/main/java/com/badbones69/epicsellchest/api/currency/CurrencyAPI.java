package com.badbones69.epicsellchest.api.currency;

import com.badbones69.epicsellchest.multisupport.ServerProtocol;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CurrencyAPI {
    
    /**
     * Give an amount to a player's currency.
     * @param player The player you are giving to.
     * @param currency The currency you want to use.
     * @param amount The amount you are giving to the player.
     * @param command The command used for the Custom currency. Leave null if nothing.
     */
    public static void giveCurrency(Player player, Currency currency, double amount, String command) {
        // Checks to see if the amount variable is a whole number and if so removes the decimal. This fixes issues with some custom currency commands.
        String stringAmount = amount % 1 == 0 ? ((int) amount + "") : (amount + "");

        switch (currency) {
            case XP_LEVEL -> player.setLevel((int) (player.getLevel() + amount));
            case XP_TOTAL -> giveTotalXP(player, amount);
            case CUSTOM -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())
                    .replaceAll("%Cost%", stringAmount).replaceAll("%cost%", stringAmount)
                    .replaceAll("%Amount%", stringAmount).replaceAll("%amount%", stringAmount));
        }
    }
    
    private static void giveTotalXP(Player player, double amount) {
        if (ServerProtocol.isAtLeast(ServerProtocol.v1_8_R1)) {
            int total = (int) (getTotalExperience(player) + amount);
            player.setTotalExperience(0);
            player.setTotalExperience(total);
            player.setLevel(0);
            player.setExp(0);

            while (total > player.getExpToLevel()) {
                total -= player.getExpToLevel();
                player.setLevel(player.getLevel() + 1);
            }

            float xp = (float) total / (float) player.getExpToLevel();
            player.setExp(xp);
        } else {
            player.giveExp((int) -amount);
        }
    }
    
    private static int getTotalExperience(Player player) {
        int experience;
        int level = player.getLevel();

        if (level >= 0 && level <= 15) {
            experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
            int requiredExperience = 2 * level + 7;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += Math.ceil(currentExp * requiredExperience);

            return experience;
        } else if (level > 15 && level <= 30) {
            experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
            int requiredExperience = 5 * level - 38;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += Math.ceil(currentExp * requiredExperience);

            return experience;
        } else {
            experience = (int) Math.ceil(((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220)));
            int requiredExperience = 9 * level - 158;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += Math.ceil(currentExp * requiredExperience);

            return experience;
        }
    }
    
}