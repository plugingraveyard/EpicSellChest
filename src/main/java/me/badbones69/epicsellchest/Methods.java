package me.badbones69.epicsellchest;

import me.badbones69.epicsellchest.api.enums.Version;
import me.badbones69.epicsellchest.api.objects.FileManager.Files;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Methods {
    
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    public static String prefix(String msg) {
        return ChatColor.translateAlternateColorCodes('&', Files.CONFIG.getFile().getString("Settings.Prefix") + msg);
    }
    
    public static ItemStack makeItem(String type) {
        try {
            int ty = 0;
            if (type.contains(":")) {
                String[] b = type.split(":");
                type = b[0];
                ty = Integer.parseInt(b[1]);
            }
            Material m = Material.matchMaterial(type);
            return new ItemStack(m, 1, (short) ty);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static ItemStack makeItem(String type, int amount, String name) {
        int ty = 0;
        if (type.contains(":")) {
            String[] b = type.split(":");
            type = b[0];
            ty = Integer.parseInt(b[1]);
        }
        Material m = Material.matchMaterial(type);
        ItemStack item = new ItemStack(m, amount, (short) ty);
        ItemMeta me = item.getItemMeta();
        me.setDisplayName(color(name));
        item.setItemMeta(me);
        return item;
    }
    
    public static ItemStack makeItem(String type, int amount, String name, List<String> lore) {
        ArrayList<String> l = new ArrayList<>();
        int ty = 0;
        if (type.contains(":")) {
            String[] b = type.split(":");
            type = b[0];
            ty = Integer.parseInt(b[1]);
        }
        Material m = Material.matchMaterial(type);
        ItemStack item = new ItemStack(m, amount, (short) ty);
        ItemMeta me = item.getItemMeta();
        me.setDisplayName(color(name));
        for (String L : lore)
            l.add(color(L));
        me.setLore(l);
        item.setItemMeta(me);
        return item;
    }
    
    public static ItemStack addGlowing(ItemStack item) {
        ItemStack it = item.clone();
        try {
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasEnchants()) {
                    return item;
                }
            }
            item.addUnsafeEnchantment(Enchantment.LUCK, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            return item;
        } catch (NoClassDefFoundError e) {
            return it;
        }
    }
    
    public static ItemStack addGlowing(ItemStack item, boolean glowing) {
        ItemStack it = item.clone();
        if (glowing) {
            try {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasEnchants()) {
                        return item;
                    }
                }
                item.addUnsafeEnchantment(Enchantment.LUCK, 1);
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
                return item;
            } catch (NoClassDefFoundError e) {
                return it;
            }
        }
        return it;
    }
    
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    public static Location getLoc(Player player) {
        return player.getLocation();
    }
    
    public static boolean isInvFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
    
    public static boolean isInvEmpty(Inventory inv) {
        if (inv != null) {
            for (ItemStack item : inv.getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
    public static ItemStack getItemInHand(Player player) {
        if (Version.getCurrentVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()) {
            return player.getInventory().getItemInMainHand();
        } else {
            return player.getItemInHand();
        }
    }
    
    public static String getEnchantmentName(Enchantment en) {
        HashMap<String, String> enchants = new HashMap<>();
        enchants.put("ARROW_DAMAGE", "Power");
        enchants.put("ARROW_FIRE", "Flame");
        enchants.put("ARROW_INFINITE", "Infinity");
        enchants.put("ARROW_KNOCKBACK", "Punch");
        enchants.put("DAMAGE_ALL", "Sharpness");
        enchants.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchants.put("DAMAGE_UNDEAD", "Smite");
        enchants.put("DEPTH_STRIDER", "Depth_Strider");
        enchants.put("DIG_SPEED", "Efficiency");
        enchants.put("DURABILITY", "Unbreaking");
        enchants.put("FIRE_ASPECT", "Fire_Aspect");
        enchants.put("KNOCKBACK", "KnockBack");
        enchants.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchants.put("LOOT_BONUS_MOBS", "Looting");
        enchants.put("LUCK", "Luck_Of_The_Sea");
        enchants.put("LURE", "Lure");
        enchants.put("OXYGEN", "Respiration");
        enchants.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchants.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchants.put("PROTECTION_FALL", "Feather_Falling");
        enchants.put("PROTECTION_FIRE", "Fire_Protection");
        enchants.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchants.put("SILK_TOUCH", "Silk_Touch");
        enchants.put("THORNS", "Thorns");
        enchants.put("WATER_WORKER", "Aqua_Affinity");
        enchants.put("BINDING_CURSE", "Curse_Of_Binding");
        enchants.put("MENDING", "Mending");
        enchants.put("FROST_WALKER", "Frost_Walker");
        enchants.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        if (enchants.get(en.getName()) == null) {
            return "None Found";
        }
        return enchants.get(en.getName());
    }
    
}