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

import be.Balor.Manager.ACCommand;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Fireball extends ACCommand {
	public Fireball() {
		permNode = "admincmd.player.fireball";
		cmdName = "bal_fireball";
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
		float power = ACHelper.getInstance().getConfFloat("DefaultFireBallPower");
		if (args.length >= 1) {
			try {
				player = Utils.getUser(sender, args, permNode, 1, false);
				power = Float.parseFloat(args[0]);
			} catch (NumberFormatException e) {
				power = ACHelper.getInstance().getConfFloat("DefaultFireBallPower");
				player = Utils.getUser(sender, args, permNode);
			}
			if (args.length >= 2)
				player = Utils.getUser(sender, args, permNode, 1, true);
		} else
			player = Utils.getUser(sender, args, permNode);
		if (player != null) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", player.getName());
			if (ACHelper.getInstance().isValueSet(Type.FIREBALL, player.getName())) {
				ACHelper.getInstance().removeValue(Type.FIREBALL, player);
				Utils.sI18n(player, "fireballDisabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "fireballDisabledTarget", replace);
			} else {
				ACHelper.getInstance().addValue(Type.FIREBALL, player, power);
				Utils.sI18n(player, "fireballEnabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "fireballEnabledTarget", replace);
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
