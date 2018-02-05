package me.badbones69.epicsellchest.multisupport;

import org.bukkit.Bukkit;

public class Support {
	
	public static boolean hasVault() {
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			return true;
		}
		return false;
	}
	
	public static boolean hasDakata() {
		if(Bukkit.getServer().getPluginManager().getPlugin("DakataAntiCheat") != null) {
			return true;
		}
		return false;
	}
	
	public static boolean hasNoCheatPlus() {
		if(Bukkit.getServer().getPluginManager().getPlugin("NoCheatPlus") != null) {
			return true;
		}
		return false;
	}
	
	public static boolean hasSpartan() {
		if(Bukkit.getServer().getPluginManager().getPlugin("Spartan") != null) {
			return true;
		}
		return false;
	}
	
}