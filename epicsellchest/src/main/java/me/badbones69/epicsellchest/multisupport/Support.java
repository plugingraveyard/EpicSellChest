package me.badbones69.epicsellchest.multisupport;

import org.bukkit.Bukkit;

public class Support {
	
	public static boolean hasVault() {
		return Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;
	}
	
	public static boolean hasDakata() {
		return Bukkit.getServer().getPluginManager().getPlugin("DakataAntiCheat") != null;
	}
	
	public static boolean hasNoCheatPlus() {
		return Bukkit.getServer().getPluginManager().getPlugin("NoCheatPlus") != null;
	}
	
	public static boolean hasSpartan() {
		return Bukkit.getServer().getPluginManager().getPlugin("Spartan") != null;
	}
	
}