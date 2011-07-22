/************************************************************************
 * This file is part of AdminCmd.									
 *																		
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * AdminCmd is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Manager.Commands.Player;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Balor.files.utils.Utils;

import be.Balor.Manager.ACCommands;
import be.Balor.Manager.PermissionManager;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerList extends ACCommands {

	/**
	 * 
	 */
	public PlayerList() {
		permNode = "admincmd.player.list";
		cmdName = "bal_playerlist";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String... args) {
		Player[] online = sender.getServer().getOnlinePlayers();
		sender.sendMessage(ChatColor.RED + "Online players: " + ChatColor.WHITE
				+ (online.length - InvisibleWorker.getInstance().nbInvisibles()));
		String buffer = "";
		if (PermissionManager.getPermission() == null) {
			for (int i = 0; i < online.length; ++i) {
				Player p = online[i];
				if (!InvisibleWorker.getInstance().hasInvisiblePowers(p.getName())) {
					String name = p.getDisplayName();
					if (buffer.length() + name.length() + 2 >= 256) {
						sender.sendMessage(buffer);
						buffer = "";
					}
					buffer += name + ", ";
				}
			}
		} else {
			// changed the playerlist, now support prefixes from groups!!! @foxy
			for (int i = 0; i < online.length; ++i) {
				String name = online[i].getName();
				String prefixstring;
				String world = "";
				world = online[i].getWorld().getName();
				if (!InvisibleWorker.getInstance().hasInvisiblePowers(name)) {
					try {
						prefixstring = PermissionManager.getPermission().safeGetUser(world, name)
								.getPrefix();
					} catch (Exception e) {
						String group = PermissionManager.getPermission().getGroup(world, name);
						prefixstring = PermissionManager.getPermission().getGroupPrefix(world, group);
					} catch (NoSuchMethodError e) {
						String group = PermissionManager.getPermission().getGroup(world, name);
						prefixstring = PermissionManager.getPermission().getGroupPrefix(world, group);
					}

					if (prefixstring != null && prefixstring.length() > 1) {
						String result = Utils.colorParser(prefixstring);
						if (result == null)
							buffer += prefixstring + name + ChatColor.WHITE + ", ";
						else
							buffer += result + name + ChatColor.WHITE + ", ";

					} else {
						buffer += name + ", ";
					}
					if (buffer.length() >= 256) {
						sender.sendMessage(buffer);
						buffer = "";
					}
				}
			}

		}
		if (buffer != "")
			sender.sendMessage(buffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return true;
	}

}
