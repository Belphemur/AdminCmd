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
package be.Balor.Manager.Commands.Warp;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class WarpList extends CoreCommand {

	/**
	 * 
	 */
	public WarpList() {
		permNode = "admincmd.warp.tp";
		cmdName = "bal_warplist";
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
		if (Utils.isPlayer(sender)) {
			Player p = (Player) sender;
			
			String msg = "";
			List<String> wp = ACWorld.getWorld(p.getWorld().getName()).getWarpList();
			sender.sendMessage(ChatColor.GOLD + "Warp Point(s) : " + ChatColor.WHITE + wp.size());
			for (String name : wp) {
				msg += name + ", ";
				if (msg.length() >= 256) {
					sender.sendMessage(msg);
					msg = "";
				}
			}
			if (!msg.equals("")) {
				if (msg.endsWith(", "))
					msg = msg.substring(0, msg.lastIndexOf(","));
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
	public boolean argsCheck(String... args) {
		return true;
	}

}
