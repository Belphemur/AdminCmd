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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import be.Balor.Manager.ACCommands;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerLocation extends ACCommands {

	/**
	 * 
	 */
	public PlayerLocation() {
		permNode = "admincmd.player.loc";
		cmdName = "bal_playerloc";
		other = true;
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
		Location loc;
		String msg;
		if (args.length == 0) {
			if (Utils.isPlayer(sender)) {
				loc = ((Player) sender).getLocation();
				msg = "You are";
			} else
				return;
		} else
			try {
				loc = sender.getServer().getPlayer(args[0]).getLocation();
				msg = sender.getServer().getPlayer(args[0]).getName() + " is";
			} catch (Exception ex) {
				sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0]
						+ ChatColor.RED + " not found!");
				return;
			}
		sender.sendMessage(loc.getBlockX() + " N, " + loc.getBlockZ() + " E, " + loc.getBlockY()
				+ " H");
		String facing[] = { "W", "NW", "N", "NE", "E", "SE", "S", "SW" };
		double yaw = ((loc.getYaw() + 22.5) % 360);
		if (yaw < 0)
			yaw += 360;
		sender.sendMessage(msg + " facing " + ChatColor.RED + facing[(int) (yaw / 45)]);

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
