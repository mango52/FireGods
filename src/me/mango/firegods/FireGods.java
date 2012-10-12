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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class FireGods extends JavaPlugin implements Listener {
	Logger log = Logger.getLogger("Minecraft");
	public final ArrayList<Player> firePlayers = new ArrayList<Player>();	 
	
	public static void download(Logger log, URL url, File file) throws IOException { //borrowed from Logblock source (and then modified); credit to DiddiZ
		if (!file.getParentFile().exists())
			file.getParentFile().mkdir();
		if (file.exists())
			file.delete();
		file.createNewFile();
		final int size = url.openConnection().getContentLength();
		log.info("Downloading " + file.getName() + " (" + size / 1024 + "kb) ...");
		final InputStream in = url.openStream();
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		final byte[] buffer = new byte[1024];
		int len, downloaded = 0, msgs = 0;
		final long start = System.currentTimeMillis();
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
			downloaded += len;
			if ((int)((System.currentTimeMillis() - start) / 500) > msgs) {
				log.info((int)(downloaded / (double)size * 100d) + "%");
				msgs++;
			}
		}
		in.close();
		out.close();
		log.info("Download finished");
	} 
	
    public static Permission permission = null;
	private Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String commandName = cmd.getName().toLowerCase();
	    	if(commandName.equalsIgnoreCase("fire")){
	    		if((permission.has(sender, "firegods.use")) || ((sender.isOp()) && (sender instanceof Player))){
		    		if (!firePlayers.contains((Player)sender)){
		    			firePlayers.add((Player)sender);
		    			sender.sendMessage(ChatColor.GREEN + "You have enabled Fire God mode!");
		    			return true;
		    		}else{
			    		if (firePlayers.contains((Player)sender)){
			    			firePlayers.remove((Player)sender);
			    			sender.sendMessage(ChatColor.DARK_RED + "You have disabled Fire God mode.");
			    			return true;
			    		}else{
		    				sender.sendMessage("Unknown error!");
		    			}
		    		}
	    	    }else{
	    	    	sender.sendMessage(ChatColor.RED + "You can't use that command!");
	    	    	return true;
	    	    }
	    	}
	    	return false;
		}

	public void onEnable(){ 
		PluginManager pm = this.getServer().getPluginManager();
		getServer().getPluginManager().registerEvents(this, this);
		if (pm.getPlugin("Vault") == null && !new File("plugins/Vault.jar").exists())
			try {
				download(log, new URL("http://dev.bukkit.org/media/files/571/472/Vault.jar"), new File("plugins/Vault.jar"));
				//log.info("[FireGods] Restart/reload your server now!");
				getServer().reload();
				return;
			} catch (final Exception ex) {
				log.warning("[FireGods] Failed to download Vault. Please install it manually.");
				pm.disablePlugin(this);
		}
		setupPermissions();
	    PluginDescriptionFile pdfFile = getDescription();
	    this.log.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " by Mango is enabled.");
	} 
	public void onDisable(){ 
	    PluginDescriptionFile pdfFile = getDescription();
	    this.log.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " by Mango is disabled.");
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(firePlayers.contains((Player)player)){
			org.bukkit.Location loc = event.getPlayer().getLocation();
			World w = loc.getWorld();
			loc.setY(loc.getY() + 0);
			Block b = w.getBlockAt(loc);
				if (!b.isLiquid()) {
					b.setTypeId(51); //fire = 51
				}
		}
	}
}

