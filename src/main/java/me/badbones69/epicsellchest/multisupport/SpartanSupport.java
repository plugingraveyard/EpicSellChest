package me.badbones69.epicsellchest.multisupport;

import me.vagdedes.spartan.api.API;
import me.vagdedes.spartan.system.Enums.HackType;
import org.bukkit.entity.Player;

public class SpartanSupport {
    
    public static void cancelBlockChecker(Player player) {
        API.cancelCheck(player, HackType.Nuker, 40);
        API.cancelCheck(player, HackType.NoSwing, 40);
        API.cancelCheck(player, HackType.BlockReach, 40);
    }
    
}