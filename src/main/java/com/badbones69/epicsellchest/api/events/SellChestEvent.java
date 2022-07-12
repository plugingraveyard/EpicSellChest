package com.badbones69.epicsellchest.api.events;

import com.badbones69.epicsellchest.api.enums.SellType;
import com.badbones69.epicsellchest.api.objects.SellItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class SellChestEvent extends Event implements Cancellable {
    
    private final Player player;
    private boolean cancel;
    private final SellType sellType;
    private final ArrayList<SellItem> items;
    private static final HandlerList handlers = new HandlerList();
    
    public SellChestEvent(Player player, ArrayList<SellItem> items, SellType sellType) {
        this.items = items;
        this.cancel = false;
        this.player = player;
        this.sellType = sellType;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public ArrayList<SellItem> getItems() {
        return this.items;
    }
    
    public SellType getSellType() {
        return sellType;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}