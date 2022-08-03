package com.badbones69.epicsellchest.commands;

import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.enums.Messages;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.ArgName;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Command(value = "sellchest", alias = {"sc"})
public class WandCommand extends SellChestCmdNew {
    
    private CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Default(alias = {"wand", "w"})
    @Permission("epicsellchest.wand")
    public void wandCommand(CommandSender sender) {
        wandCommand(sender, 0, null);
    }
    
    @SubCommand(value = "wand", alias = {"w"})
    @Permission("epicsellchest.wand")
    public void wandCommand(CommandSender sender, int amount, @ArgName("player") Player target) {
        if (target == null && !(sender instanceof Player)) {
            sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
            return;
        }
        target.getInventory().addItem(crazyManager.getChestSellingItem(amount));
        target.updateInventory();
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", target.getName());
        placeholders.put("%amount%", amount + "");
        sender.sendMessage(Messages.WAND_GIVE.getMessage(placeholders));
    }
    
}