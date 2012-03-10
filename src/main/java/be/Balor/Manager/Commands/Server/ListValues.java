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
package be.Balor.Manager.Commands.Server;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ListValues extends ServerCommand {

	/**
	 * 
	 */
	public ListValues() {
		permNode = "admincmd.server.list";
		cmdName = "bal_list";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Possibles Types :");
			sender.sendMessage(Arrays.toString(Type.values()));
			return;
		}
		String arg = "";
		for (final String str : args)
			arg += str + " ";
		arg = arg.trim();
		if (Type.matchType(arg) == null) {
			Utils.sI18n(sender, "emptyList");
			return;
		}
		final List<ACPlayer> list = ACPlayer.getPlayers(arg);
		if (list != null) {
			sender.sendMessage(ChatColor.AQUA + Type.matchType(arg).display() + ChatColor.WHITE
					+ " (" + list.size() + ") " + ChatColor.AQUA + ":");
			String buffer = "";
			for (final ACPlayer value : list)
				buffer += value.getName() + ", ";
			if (!buffer.equals("")) {
				if (buffer.endsWith(", "))
					buffer = buffer.substring(0, buffer.lastIndexOf(","));
				sender.sendMessage(buffer);
			}
		} else
			Utils.sI18n(sender, "emptyList");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
