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
package be.Balor.Manager.Commands.Weather;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Balor.bukkit.AdminCmd.AdminCmdWorker;

import be.Balor.Manager.ACCommands;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Thor extends ACCommands {

	/**
	 * 
	 */
	public Thor() {
		permNode = "admincmd.weather.thor";
		cmdName = "bal_thor";
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
		Player player = null;
		if (AdminCmdWorker.getInstance().isPlayer(false)) {
			player = ((Player) sender);
			if (args.length >= 1
					&& AdminCmdWorker.getInstance().hasPerm(sender, "admincmd.weather.thor"))
				player = sender.getServer().getPlayer(args[0]);
		} else if (args.length >= 1)
			player = sender.getServer().getPlayer(args[0]);
		else
			sender.sendMessage("You must set the player name !");
		if (player != null) {
			if (AdminCmdWorker.getInstance().hasThorPowers(player.getName())) {
				AdminCmdWorker.getInstance().removeThor(player.getName());
				sender.sendMessage(ChatColor.DARK_AQUA + "THOR mode disabled.");
			} else {
				AdminCmdWorker.getInstance().addThor(player.getName());
				sender.sendMessage(ChatColor.DARK_AQUA + "THOR mode enabled.");
			}

		} else
			sender.sendMessage(ChatColor.RED + "No such player: " + ChatColor.WHITE + args[0]);

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
