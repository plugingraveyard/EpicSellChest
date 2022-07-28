package com.badbones69.epicsellchest.api.enums;

import com.badbones69.epicsellchest.Methods;
import com.badbones69.epicsellchest.api.FileManager;
import com.badbones69.epicsellchest.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.Map.Entry;

public enum Messages {
    
    NO_PERMISSION("No-Permission", "&cYou do not have permission to use this command."),
    NOT_A_NUMBER("Not-A-Number", "&cThat is not a number."),
    NOT_ONLINE("Not-Online", "&cThat player is not currently online."),
    RELOADED("Reloaded", "&7You have just reloaded the config and message files."),
    PLAYER_ONLY("Player-Only", "&cYou must be a player to use this command."),
    SOLD_CHEST("Sold-Chest", "&7You have sold the contents of that chest for &a$%vault%&7"),
    NO_SELLABLE_ITEMS("No-Sellable-Items", "&cThat chest has no sellable items."),
    CANT_SELL_CHEST("Cant-Sell-Chest", "&cYou can''t sell that chest because it doesn't belong to you."),
    NOT_A_CHEST("Not-A-Chest", "&cYou are not looking at or standing on a chest."),
    LOADING_REGION_CHESTS("Loading-Region-Chests", "&aLoading chests from the region."),
    STILL_LOADING_CHESTS("Still-Loading-Chests", "&a....."),
    REGION_TO_BIG("Region-To-Big", "&cThe region you have selected is too big."),
    MISSING_POSITION("Missing-Position", "&cYou are missing your 1st and(or) 2nd position."),
    NO_CHESTS_IN_REGION("No-Chests-In-Region", "&cThere are no chests that can be sold in your region."),
    POSITION_1("Position-1", "&7You have set your 1st position."),
    POSITION_2("Position-2", "&7You have set your 2nd position."),
    LOADING_CHUNK_CHESTS("Loading-Chunk-Chests", "&aLoading chests from the chunk."),
    NO_CHESTS_IN_CHUNK("No-Chests-In-Chunk", "&cThere are no chests that can be sold in your chunk."),
    SOLD_CHUNK_CHESTS("Sold-Chunk-Chests", "&7You have sold the contents of all chests in your chunk for &a$%vault%&7."),
    SOLD_REGION_CHESTS("Sold-Region-Chests", "&7You have sold the contents of all the chests in your region for &a$%vault%&7."),
    TWO_FACTOR_AUTH("Two-Factor-Auth", "&7Please type the command again to confirm the sell."),
    WAND_GIVE("Wand-Give", "&7You have just given &6%player% %amount% &7Epic Sell Wand(s)."),
    WAND_TWO_FACTOR_AUTH("Wand-Two-Factor-Auth", "&7Please click the block again to confirm the sell."),
    INTERNAL_ERROR("Internal-Error", "&cAn internal error has occurred. Please check the console for the full error."),
    NOT_ENOUGH_ARGS("Not-Enough-Args", "&cYou did not supply enough arguments."),
    TOO_MANY_ARGS("Too-Many-Args", "&cYou put more arguments then I can handle."),
    UNKNOWN_COMMAND("Unknown-Command", "&cThis command is not known."),
    MUST_BE_A_PLAYER("Must-Be-A-Player", "&cYou must be a player to use this command."),
    MUST_BE_A_CONSOLE_SENDER("Must-Be-A-Console-Sender", "&cYou must be using console to use this command."),
    HELP("Help",
    Arrays.asList(
    "&6/sc &7- Sells the chest you are currently looking at or standing on.",
    "&6/sc Reload &7- Reloads the config and messages files.",
    "&6/sc Debug &7- Shows all broken items and duplicated items.",
    "&6/sc Wand [Amount] [Player] &7- Give a player a Chest Selling Wand.",
    "&6/sc [ID:MD] [ID:MD].... &7- Allows you to sell specific items in a chest you are looking at or standing on.",
    "&6/sc Chunk &7- Sell all items in all the chests in your current chunk.",
    "&6/sc Chunk [ID:MD] [ID:MD].... &7- Sell only specific items in all the chests in your chunk.",
    "&6/sc Pos1/Pos2 &7- Set the area of selling for the /SellChest All command.",
    "&6/sc All &7- Sell all items in all chests that are in your area.",
    "&6/sc All [ID:MD] [ID:MD].... &7- Sell only specific items in all chests in your area.",
    "&6/sc GUI &7- Opens a GUI for you to throw your items you want to sell into.",
    "",
    "&6Information: &7[] - optional, [ID:MD] [ID:MD].... - multiple items you can sell."));
    
    private final String path;
    private String defaultMessage;
    private List<String> defaultListMessage;
    
    Messages(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }
    
    Messages(String path, List<String> defaultListMessage) {
        this.path = path;
        this.defaultListMessage = defaultListMessage;
    }
    
    public static String convertList(List<String> list) {
        StringBuilder message = new StringBuilder();
        for (String line : list) {
            message.append(Methods.color(line)).append("\n");
        }
        return message.toString();
    }
    
    public static void addMissingMessages() {
        FileConfiguration messages = FileManager.Files.MESSAGES.getFile();
        boolean saveFile = false;
        for (Messages message : values()) {
            if (!messages.contains("Messages." + message.getPath())) {
                saveFile = true;
                if (message.getDefaultMessage() != null) {
                    messages.set("Messages." + message.getPath(), message.getDefaultMessage());
                } else {
                    messages.set("Messages." + message.getPath(), message.getDefaultListMessage());
                }
            }
        }
        
        if (saveFile) {
            FileManager.Files.MESSAGES.saveFile();
        }
    }
    
    public static String replacePlaceholders(String placeholder, String replacement, String message) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return replacePlaceholders(placeholders, message);
    }
    
    public static String replacePlaceholders(Map<String, String> placeholders, String message) {
        for (Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replace(placeholder.getKey(), placeholder.getValue())
            .replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
        }
        return message;
    }
    
    public static List<String> replacePlaceholders(String placeholder, String replacement, List<String> messageList) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return replacePlaceholders(placeholders, messageList);
    }
    
    public static List<String> replacePlaceholders(Map<String, String> placeholders, List<String> messageList) {
        List<String> newMessageList = new ArrayList<>();
        for (String message : messageList) {
            for (Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue())
                .replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }
        return newMessageList;
    }
    
    public String getMessage() {
        return getMessage(true);
    }
    
    public String getMessage(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return getMessage(placeholders, true);
    }
    
    public String getMessage(Map<String, String> placeholders) {
        return getMessage(placeholders, true);
    }
    
    public String getMessageNoPrefix() {
        return getMessage(false);
    }
    
    public String getMessageNoPrefix(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return getMessage(placeholders, false);
    }
    
    public String getMessageNoPrefix(Map<String, String> placeholders) {
        return getMessage(placeholders, false);
    }
    
    private String getMessage(boolean prefix) {
        return getMessage(new HashMap<>(), prefix);
    }
    
    private String getMessage(Map<String, String> placeholders, boolean prefix) {
        String message;
        boolean isList = isList();
        boolean exists = exists();
        
        if (isList) {
            if (exists) {
                message = Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
            } else {
                message = Methods.color(convertList(getDefaultListMessage()));
            }
        } else {
            if (exists) {
                message = Methods.color(Files.MESSAGES.getFile().getString("Messages." + path));
            } else {
                message = Methods.color(getDefaultMessage());
            }
        }
        
        for (Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
        }
        
        if (isList) { // Don't want to add a prefix to a list of messages.
            return Methods.color(message);
        } else { // If the message isn't a list.
            if (prefix) { // If the message needs a prefix.
                return Methods.prefix(message);
            } else { // If the message doesn't need a prefix.
                return Methods.color(message);
            }
        }
    }
    
    private boolean exists() {
        return FileManager.Files.MESSAGES.getFile().contains("Messages." + path);
    }
    
    private boolean isList() {
        if (FileManager.Files.MESSAGES.getFile().contains("Messages." + path)) {
            return !FileManager.Files.MESSAGES.getFile().getStringList("Messages." + path).isEmpty();
        } else {
            return defaultMessage == null;
        }
    }
    
    private String getPath() {
        return path;
    }
    
    private String getDefaultMessage() {
        return defaultMessage;
    }
    
    private List<String> getDefaultListMessage() {
        return defaultListMessage;
    }
}