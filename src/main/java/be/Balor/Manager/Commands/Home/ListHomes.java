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
package be.Balor.Manager.Commands.Home;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ListHomes extends HomeCommand {

	/**
	 * 
	 */
	public ListHomes() {
		permNode = "admincmd.tp.home";
		cmdName = "bal_homelist";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (Utils.isPlayer(sender)) {
			String msg = "";
			String player = "serverConsole";
			if (Utils.isPlayer(sender, false)) {
				player = ((Player) sender).getName();
			}
			if (args.length >= 1) {
				if (!PermissionManager.hasPerm(sender, "admincmd.admin.home")) {
					return;
				}
				player = args.getString(0);
			}
			final Set<String> homes = ACPlayer.getPlayer(player).getHomeList();
			sender.sendMessage(ChatColor.GOLD + "Home(s) : " + ChatColor.WHITE
					+ homes.size());
			for (final String name : homes) {
				msg += name + ", ";
				if (msg.length() >= ACMinecraftFontWidthCalculator.chatwidth) {
					sender.sendMessage(msg);
					msg = "";
				}
			}
			if (!msg.equals("")) {
				if (msg.endsWith(", ")) {
					msg = msg.substring(0, msg.lastIndexOf(","));
				}
				sender.sendMessage(msg);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return true;
	}

}
