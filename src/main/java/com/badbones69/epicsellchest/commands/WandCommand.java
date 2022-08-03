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

public class WandCommand extends SellChestCmdNew {
    
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Default
    @Permission("epicsellchest.wand")
    // Using "Player player" forces it to be a player only command without needing to check if it's a player or not.
    // All handled for you & it uses the message key you defined in EpicSellChest.
    public void defaultWandCommand(Player player) {
        player.getInventory().addItem(crazyManager.getChestSellingItem(1));
        player.updateInventory();

        //HashMap<String, String> placeholders = new HashMap<>();
        //placeholders.put("%player%", player.getName());
        //placeholders.put("%amount%", amount + "");
        //player.sendMessage(Messages.WAND_GIVE.getMessage(placeholders));

        // TODO() New message for being given a wand.
    }
    
    @SubCommand(value = "wand", alias = {"w"})
    @Permission("epicsellchest.wand")
    // Using "CommandSender sender" allows it to run in console.
    // Should already check if Player target is null.
    // Check above comment, so you don't have to check if sender is instanceof Player.
    public void wandCommandOthers(CommandSender sender, int amount, @ArgName("player") Player target) {
        //if (target == null && !(sender instanceof Player)) {
        //    sender.sendMessage(Messages.PLAYER_ONLY.getMessage());
        //    return;
        //}
        // This commented out code shouldn't be needed because of this in EpicSellChest main class.
        // commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage()));
        // commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage(Messages.NOT_ONLINE.getMessage().replace("%player%", context.getTypedArgument())));

        target.getInventory().addItem(crazyManager.getChestSellingItem(amount));
        target.updateInventory();
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", target.getName());
        placeholders.put("%amount%", amount + "");
        sender.sendMessage(Messages.WAND_GIVE.getMessage(placeholders));
    }
}