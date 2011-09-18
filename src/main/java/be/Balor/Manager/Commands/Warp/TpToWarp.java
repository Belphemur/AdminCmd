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

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;
import static be.Balor.Tools.Utils.sendMessage;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class TpToWarp extends CoreCommand {

	/**
	 * 
	 */
	public TpToWarp() {
		permNode = "admincmd.warp.tp";
		cmdName = "bal_tpwarp";
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
	public void execute(CommandSender sender, CommandArgs args) {
		Player target = Utils.getUser(sender, args, permNode, 1, true);
		if (Utils.isPlayer(sender)) {
			Player p = (Player) sender;			
			if (target != null) {
				HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("name", args.getString(0));
				Location loc = ACWorld.getWorld(p.getWorld().getName()).getWarp(args.getString(0));
				if (loc == null)
					Utils.sI18n(sender, "errorWarp", replace);
				else {
					ACPlayer.getPlayer(target.getName()).setLastLocation(target.getLocation());
					target.teleport(loc);
					sendMessage(sender, target, "tpWarp", replace);
				}
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
		return args != null && args.length >= 1;
	}

}
