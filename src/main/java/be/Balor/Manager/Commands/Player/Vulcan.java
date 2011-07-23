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

import com.Balor.bukkit.AdminCmd.ACHelper;

import be.Balor.Manager.ACCommands;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Vulcan extends ACCommands {
	/**
	 * 
	 */
	public Vulcan() {
		permNode = "admincmd.player.vulcan";
		cmdName = "bal_vulcan";
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
		Player player = null;
		float power = 4.0F;
		if (args.length >= 1) {
			try {
				player = ACHelper.getInstance().getUser(args, permNode, 1, false);
				power = Float.parseFloat(args[0]);
			} catch (NumberFormatException e) {
				power = 4.0F;
				player = ACHelper.getInstance().getUser(args, permNode);
			}
			if (args.length >= 2)
				player = ACHelper.getInstance().getUser(args, permNode, 1, true);
		} else
			player = ACHelper.getInstance().getUser(args, permNode);
		if (player != null) {
			if (ACHelper.getInstance().isPowerUser("vulcan", player.getName())) {
				ACHelper.getInstance().removeVulcan(player.getName());
				player.sendMessage(ChatColor.DARK_RED + "Vulcan mode disabled.");
				if (!player.equals(sender))
					sender.sendMessage(ChatColor.DARK_RED + "Vulcan mode disabled for "
							+ player.getName());
			} else {
				ACHelper.getInstance().addVulcain((player.getName()), power);
				player.sendMessage(ChatColor.DARK_RED + "Vulcan mode enabled.");
				if (!player.equals(sender))
					sender.sendMessage(ChatColor.DARK_RED + "Fireball mode enabled for "
							+ player.getName());
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
		return args != null;
	}

}
