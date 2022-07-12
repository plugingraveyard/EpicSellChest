package com.badbones69.epicsellchest.api.objects;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;

public record UpgradeableEnchantment(Enchantment enchantment, HashMap<Integer, Integer> levels) {}