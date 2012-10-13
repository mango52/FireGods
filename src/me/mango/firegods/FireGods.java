/*
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.mango.firegods;

import java.util.ArrayList;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class FireGods extends JavaPlugin implements Listener {

	private ArrayList<Player> firePlayers = new ArrayList<Player>();
	public static Permission permission = null;

	private Boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
	
	@Override
	public void onEnable() {
		setupPermissions();
		getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " by Mango enabled.");
	}

	@Override
	public void onDisable() {
		Bukkit.getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " by Mango disabled.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String commandName = cmd.getName().toLowerCase();
		if (commandName.equalsIgnoreCase("fire")) {
			if (((permission.has(sender, "firegods.use")) || (sender.isOp())) && (sender instanceof Player)) {
				if (!firePlayers.contains((Player) sender)) {
					firePlayers.add((Player) sender);
					sender.sendMessage(ChatColor.GREEN
							+ "You have enabled fire god mode!");
					return true;
				} else {
					if (firePlayers.contains((Player) sender)) {
						firePlayers.remove((Player) sender);
						sender.sendMessage(ChatColor.DARK_RED
								+ "You have disabled fire god mode.");
						return true;
					} else {
						sender.sendMessage("Unknown error!");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You can't use that command!");
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (firePlayers.contains((Player) player)) {
			Location loc = event.getPlayer().getLocation();
			Block b = event.getPlayer().getLocation().getWorld().getBlockAt(loc);
			if (!b.isLiquid() && b.isEmpty()) {
				b.setTypeId(51); // fire = 51
			}
		}
	}
}
