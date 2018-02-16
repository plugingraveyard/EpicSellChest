package me.badbones69.epicsellchest.api;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;

public class UpgradeableEnchantment {
	
	private Enchantment enchantment;
	private HashMap<Integer, Integer> levels;
	
	public UpgradeableEnchantment(Enchantment enchantment, HashMap<Integer, Integer> levels) {
		this.levels = levels;
		this.enchantment = enchantment;
	}
	
	public Enchantment getEnchantment() {
		return this.enchantment;
	}
	
	public HashMap<Integer, Integer> getLevels() {
		return this.levels;
	}
	
}