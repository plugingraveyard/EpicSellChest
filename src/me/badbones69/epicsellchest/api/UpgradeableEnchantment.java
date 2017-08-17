package me.badbones69.epicsellchest.api;

import java.util.HashMap;

import org.bukkit.enchantments.Enchantment;

public class UpgradeableEnchantment {
	
	private Enchantment enchantment;
	private HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();
	
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