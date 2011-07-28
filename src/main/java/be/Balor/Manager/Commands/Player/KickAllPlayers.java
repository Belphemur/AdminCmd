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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import be.Balor.Manager.ACCommands;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.AdminCmd;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class KickAllPlayers extends ACCommands {

	/**
	 * 
	 */
	public KickAllPlayers() {
		permNode = "admincmd.player.kick";
		cmdName = "bal_kickall";
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
		String message = "";
		String playerName = "";
		if (args.length >= 1)
			for (int i = 0; i < args.length; i++)
				message += args[i] + " ";
		else {
			message = "You have been kick by ";
			if (!Utils.isPlayer(sender, false))
				playerName = "Server Admin";
			else
				playerName = ((Player) sender).getName();
			message += playerName;
		}
		message = message.trim();
		for (Player toKick : AdminCmd.getBukkitServer().getOnlinePlayers())
			if (!toKick.getName().equals(playerName))
				toKick.kickPlayer(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

}
