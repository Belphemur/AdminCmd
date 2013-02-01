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

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Antoine
 * 
 */
public class WalkSpeed extends PlayerCommand {

	/**
	 * 
	 */
	public WalkSpeed() {
		super("bal_walkspeed", "admincmd.player.walk");
		other = true;
	}

	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		final Player target = Utils.getUser(sender, args, permNode);
		float speed;
		if (args.length == 1) {
			speed = args.getFloat(0);
		} else {
			speed = args.getFloat(1);
		}
		final float speedFinal = speed;
		ACPluginManager.scheduleSyncTask(new Runnable() {
			@Override
			public void run() {
				target.setWalkSpeed(speedFinal);
			}
		});
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", Utils.getPlayerName(target));
		replace.put("value", String.valueOf(speed));
		LocaleHelper.WALK_SPEED_SET.sendLocale(sender, replace);
		if (!sender.equals(target)) {
			LocaleHelper.WALK_SPEED_SET.sendLocale(target, replace);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

}
