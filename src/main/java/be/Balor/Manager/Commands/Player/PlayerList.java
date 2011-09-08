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

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerList extends CoreCommand {

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
	public void execute(CommandSender sender, CommandArgs args) {
		Player[] online = sender.getServer().getOnlinePlayers();
		int amount = online.length;
		if (!PermissionManager.hasPerm(sender, "admincmd.invisible.cansee", false))
			amount -= InvisibleWorker.getInstance().nbInvisibles();
		sender.sendMessage(Utils.I18n("onlinePlayers") + " " + ChatColor.WHITE + amount);
		String buffer = "";
		for (int i = 0; i < online.length; ++i) {
			Player p = online[i];
			if (InvisibleWorker.getInstance().hasInvisiblePowers(p.getName())
					&& !PermissionManager.hasPerm(sender, "admincmd.invisible.cansee", false))
				continue;
			String name = Utils.getPrefix(p, sender) + p.getName();
			if (buffer.length() + name.length() + 2 >= 256) {
				sender.sendMessage(buffer);
				buffer = "";
			}
			buffer += name + ", ";
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
