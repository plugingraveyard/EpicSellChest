package com.badbones69.epicsellchest.api.enums;

public enum RegisterType {
    
    EPICSELLCHEST("EpicSellChest"),
    SHOP_GUI_PLUS("ShopGUIPlus");
    
    private final String name;
    
    RegisterType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
}