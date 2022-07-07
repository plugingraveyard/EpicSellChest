package com.badbones69.epicsellchest.api.enums;

public enum SellType {
    
    GUI("GUI"),
    WAND("Wand"),
    CHUNK("Chunk"),
    SINGLE("Single"),
    REGION("Region");
    
    private final String name;
    
    SellType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
}