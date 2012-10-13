package me.mango.firegods;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FGListener implements Listener {

	private FireGods plugin;

	public FGListener(FireGods plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (plugin.firePlayers.contains(event.getPlayer())) {
			Location loc = event.getPlayer().getLocation();
			Block b = event.getPlayer().getLocation().getWorld().getBlockAt(loc);
			if (!b.isLiquid() && b.isEmpty()) {
				b.setTypeId(51); // fire = 51
			}
		}
	}
}
