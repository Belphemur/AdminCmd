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

import be.Balor.Manager.ACCommand;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerList extends ACCommand {

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

	@Override
	public void execute(CommandSender sender, String... args) {
		Player[] online = sender.getServer().getOnlinePlayers();
		int amount = online.length;
		if (!PermissionManager.hasPerm(sender, "admincmd.invisible.cansee", false))
			amount -= InvisibleWorker.getInstance().nbInvisibles();
		sender.sendMessage(Utils.I18n("onlinePlayers") + " " + ChatColor.WHITE + amount);
		String buffer = "";
		if (!PermissionManager.hasInfoNode()) {
			boolean isInv = false;
			for (int i = 0; i < online.length; ++i) {
				Player p = online[i];
				if ((isInv = InvisibleWorker.getInstance().hasInvisiblePowers(p.getName()))
						&& !PermissionManager.hasPerm(sender, "admincmd.invisible.cansee", false))
					continue;
				String name = "";
				if (isInv)
					name = Utils.I18n("invTitle") + p.getDisplayName();
				else
					name = p.getDisplayName();
				if (AFKWorker.getInstance().isAfk(p))
					name = Utils.I18n("afkTitle") + name;
				if (buffer.length() + name.length() + 2 >= 256) {
					sender.sendMessage(buffer);
					buffer = "";
				}
				buffer += name + ", ";

			}
		} else {
			// changed the playerlist, now support prefixes from groups!!! @foxy
			boolean isInv = false;
			for (int i = 0; i < online.length; ++i) {
				String name = online[i].getName();
				String prefixstring;
				String world = "";
				String invPrefix = "";
				world = online[i].getWorld().getName();
				if ((isInv = InvisibleWorker.getInstance().hasInvisiblePowers(name))
						&& !PermissionManager.hasPerm(sender, "admincmd.invisible.cansee", false))
					continue;
				if (isInv)
					invPrefix = Utils.I18n("invTitle");
				if (AFKWorker.getInstance().isAfk(online[i]))
					invPrefix = Utils.I18n("afkTitle") + invPrefix;
				prefixstring = PermissionManager.getPrefix(world, name);
				if (prefixstring != null && prefixstring.length() > 1) {
					String result = Utils.colorParser(prefixstring);
					if (result == null)
						buffer += invPrefix + prefixstring + online[i].getDisplayName()
								+ ChatColor.WHITE + ", ";
					else
						buffer += invPrefix + result + online[i].getDisplayName() + ChatColor.WHITE
								+ ", ";

				} else {
					buffer += invPrefix + online[i].getDisplayName() + ", ";
				}
				if (buffer.length() >= 256) {
					sender.sendMessage(buffer);
					buffer = "";
				}

			}

		}
		if (!buffer.equals("")) {
			if (buffer.endsWith(", "))
				buffer = buffer.substring(0, buffer.lastIndexOf(","));
			sender.sendMessage(buffer);
		}

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
