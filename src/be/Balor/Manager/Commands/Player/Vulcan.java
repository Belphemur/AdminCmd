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

import com.Balor.bukkit.AdminCmd.AdminCmdWorker;

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
				player = AdminCmdWorker.getInstance().getUser(args, permNode);
				power = Float.parseFloat(args[0]);
			} catch (NumberFormatException e) {
				power = 4.0F;
			}
			if (args.length >= 2)
				player = AdminCmdWorker.getInstance().getUser(args, permNode, 1);
		}
		else
			player = AdminCmdWorker.getInstance().getUser(args, permNode);
		if (player != null) {
			if (AdminCmdWorker.getInstance().getVulcainExplosionPower(player.getName()) != null) {
				AdminCmdWorker.getInstance().removeVulcan(player.getName());
				sender.sendMessage(ChatColor.DARK_RED + "Vulcan mode disabled.");
			} else {
				AdminCmdWorker.getInstance().addVulcain((player.getName()), power);
				sender.sendMessage(ChatColor.DARK_RED + "Vulcan mode enabled.");
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
