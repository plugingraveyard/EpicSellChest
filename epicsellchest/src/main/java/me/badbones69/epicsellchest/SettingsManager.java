package me.badbones69.epicsellchest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class SettingsManager {
	
	private static SettingsManager instance = new SettingsManager();
	
	public static SettingsManager getInstance() {
		return instance;
	}
	
	private FileConfiguration config;
	private File cfile;
	
	private FileConfiguration msgs;
	private File mfile;
	
	public void setup(Plugin p) {
		if(!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}
		cfile = new File(p.getDataFolder(), "Config.yml");
		if(!cfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Config.yml");
				InputStream E = getClass().getResourceAsStream("/Config.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(cfile);
		mfile = new File(p.getDataFolder(), "Messages.yml");
		if(!mfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Messages.yml");
				InputStream E = getClass().getResourceAsStream("/Messages.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		msgs = YamlConfiguration.loadConfiguration(mfile);
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public FileConfiguration getMessages() {
		return msgs;
	}
	
	public void saveConfig() {
		try {
			config.save(cfile);
		}catch(IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Config.yml!");
		}
	}
	
	public void saveMessages() {
		try {
			msgs.save(mfile);
		}catch(IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Messages.yml!");
		}
	}
	
	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(cfile);
	}
	
	public void reloadMessages() {
		msgs = YamlConfiguration.loadConfiguration(mfile);
	}
	
	public static void copyFile(InputStream in, File out) throws Exception { // https://bukkit.org/threads/extracting-file-from-jar.16962/
		InputStream fis = in;
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		}catch(Exception e) {
			throw e;
		}finally {
			if(fis != null) {
				fis.close();
			}
			if(fos != null) {
				fos.close();
			}
		}
	}
	
}