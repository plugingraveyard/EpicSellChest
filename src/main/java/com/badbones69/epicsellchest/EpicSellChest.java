package com.badbones69.epicsellchest;

import com.badbones69.epicsellchest.api.CrazyManager;
import com.badbones69.epicsellchest.api.FileManager;
import com.badbones69.epicsellchest.api.enums.Messages;
import com.badbones69.epicsellchest.commands.SellChestCmdNew;
import com.badbones69.epicsellchest.commands.WandCommand;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EpicSellChest extends JavaPlugin {
    
    public final FileManager fileManager = FileManager.getInstance();
    public final CrazyManager crazyManager = CrazyManager.getInstance();
    BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);
    
    @Override
    public void onEnable() {
        
        crazyManager.loadPlugin(this);
        
        fileManager.logInfo(true).setup(this);
        
        crazyManager.load();
        
        PluginManager pluginManager = getServer().getPluginManager();

//        getCommand("sellchest").setExecutor(new SellChestCommands());
        registerCommands();
    }
    
    @Override
    public void onDisable() {
    
    }
    
    private void registerCommands() {
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage(Messages.UNKNOWN_COMMAND.getMessage()));
        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.sendMessage(Messages.TOO_MANY_ARGS.getMessage()));
        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.sendMessage(Messages.NOT_ENOUGH_ARGS.getMessage()));
        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage(Messages.NOT_ONLINE.getMessage().replace("%player%", context.getTypedArgument())));
        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage(Messages.NO_PERMISSION.getMessage()));
        commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage()));
        commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage(Messages.MUST_BE_A_CONSOLE_SENDER.getMessage()));
        commandManager.registerCommand(new SellChestCmdNew());
        commandManager.registerCommand(new WandCommand());
    }
    
}