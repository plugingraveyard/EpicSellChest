package me.badbones69.epicsellchest.multisupport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import DAKATA.CheatType;
import DAKATA.PlayerCheatEvent;
import me.badbones69.epicsellchest.api.EpicSellChest;

public class DakataAntiCheatSupport implements Listener {
	
	private EpicSellChest sc = EpicSellChest.getInstance();
	
	@EventHandler
	public void onCheatDetect(PlayerCheatEvent e) {
		Player player = e.getPlayer();
		CheatType cheatType = e.getCheatType();
		if(cheatType == CheatType.AUTOCLICKER || cheatType == CheatType.INVALIDBLOCK_BREAK || cheatType == CheatType.NOBREAKDELAY || cheatType == CheatType.REACH_BLOCK) {
			if(sc.hasChestQuery(player.getUniqueId())) {
				e.setCancelled(true);
			}
		}
	}
	
}