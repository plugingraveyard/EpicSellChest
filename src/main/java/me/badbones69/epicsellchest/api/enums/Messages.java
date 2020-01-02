package me.badbones69.epicsellchest.api.enums;

import me.badbones69.epicsellchest.Methods;
import me.badbones69.epicsellchest.api.objects.FileManager.Files;

import java.util.HashMap;

public enum Messages {
    
    NO_PERMISSION("No-Permission"),
    NOT_A_NUMBER("Not-A-Number"),
    NOT_ONLINE("Not-Online"),
    RELOADED("Reloaded"),
    PLAYER_ONLY("Player-Only"),
    SOLD_CHEST("Sold-Chest"),
    NO_SELLABLE_ITEMS("No-Sellable-Items"),
    CANT_SELL_CHEST("Cant-Sell-Chest"),
    NOT_A_CHEST("Not-A-Chest"),
    LOADING_REGION_CHESTS("Loading-Region-Chests"),
    STILL_LOADING_CHESTS("Still-Loading-Chests"),
    REGION_TO_BIG("Region-To-Big"),
    MISSING_POSITION("Missing-Position"),
    NO_CHESTS_IN_REGION("No-Chests-In-Region"),
    SOLD_REGION_CHESTS("Sold-Region-Chests"),
    POSITION_1("Position-1"),
    POSITION_2("Position-2"),
    LOADING_CHUNK_CHESTS("Loading-Chunk-Chests"),
    NO_CHESTS_IN_CHUNK("No-Chests-In-Chunk"),
    SOLD_CHUNK_CHESTS("Sold-Chunk-Chests"),
    TWO_FACTOR_AUTH("Two-Factor-Auth"),
    WAND_GIVE("Wand-Give"),
    WAND_TWO_FACTOR_AUTH("Wand-Two-Factor-Auth");
    
    private String path;
    
    private Messages(String path) {
        this.path = path;
    }
    
    public String getMessage() {
        return Methods.prefix(Files.MESSAGES.getFile().getString("Messages." + path));
    }
    
    public String getMessage(HashMap<String, String> placeholders) {
        String msg = Methods.prefix(Files.MESSAGES.getFile().getString("Messages." + path));
        for (String placeholder : placeholders.keySet()) {
            msg = msg.replaceAll(placeholder, placeholders.get(placeholder));
        }
        return msg;
    }
    
    public String getMessageInt(HashMap<String, Integer> placeholders) {
        String msg = Methods.prefix(Files.MESSAGES.getFile().getString("Messages." + path));
        for (String placeholder : placeholders.keySet()) {
            msg = msg.replaceAll(placeholder, placeholders.get(placeholder) + "");
        }
        return msg;
    }
    
    public String getMessageDouble(HashMap<String, Double> placeholders) {
        String msg = Methods.prefix(Files.MESSAGES.getFile().getString("Messages." + path));
        for (String placeholder : placeholders.keySet()) {
            msg = msg.replaceAll(placeholder, placeholders.get(placeholder) + "");
        }
        return msg;
    }
    
}