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
import belgium.Balor.Workers.AFKWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Afk extends ACCommands {

	/**
	 * 
	 */
	public Afk() {
		permNode = "admincmd.player.afk";
		cmdName = "bal_afk";
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
		if (Utils.isPlayer(sender)) {
			Player player = (Player) sender;
			if (player != null) {
				if (AFKWorker.getInstance().isAfk(player))
					AFKWorker.getInstance().setOnline(player);
				else
					AFKWorker.getInstance().setAfk(player);

			}
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
