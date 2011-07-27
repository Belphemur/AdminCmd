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

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Balor.files.utils.Utils;

import be.Balor.Manager.ACCommands;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ClearInventory extends ACCommands {

	/**
	 * 
	 */
	public ClearInventory() {
		permNode = "admincmd.player.clear";
		cmdName = "bal_pclear";
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
		Player target = Utils.getUser(sender, args, permNode);
		if (target == null)
			return;
		target.getInventory().clear();
		target.getInventory().setHelmet(null);
		target.getInventory().setChestplate(null);
		target.getInventory().setLeggings(null);
		target.getInventory().setBoots(null);
		if (!sender.equals(target)) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", target.getName());
			Utils.sI18n(sender, "clearTarget", replace);
		}
		Utils.sI18n(target, "clear");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		// TODO Auto-generated method stub
		return args != null;
	}

}
