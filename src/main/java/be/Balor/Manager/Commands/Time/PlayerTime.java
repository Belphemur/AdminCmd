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
package be.Balor.Manager.Commands.Time;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Antoine
 * 
 */
public class PlayerTime extends TimeCommand {

	/**
	 * 
	 */
	public PlayerTime() {
		super("bal_ptime", "admincmd.time.ptime");
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		final Player target = Users.getUser(sender, args, permNode, 1, true);
		if (target == null) {
			return;
		}

		final String option = args.getString(0);
		long newTime;
		boolean relative = false;
		if (option.equalsIgnoreCase("normal")) {
			newTime = 0;
			relative = true;
			ACPlayer.getPlayer(target).removePower(Type.BLOCK_IN_TIME);
		} else {
			newTime = TimeCommand.calculNewTime(target.getWorld(), option);
			ACPlayer.getPlayer(target).setPower(Type.BLOCK_IN_TIME);
		}
		final long finalNewTime = newTime;
		final boolean finalRelative = relative;
		ACPluginManager.scheduleSyncTask(new Runnable() {
			@Override
			public void run() {
				target.setPlayerTime(finalNewTime, finalRelative);
			}
		});
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("time", option);
		replace.put("player", Users.getPlayerName(target, sender));
		LocaleHelper.PTIME_SET.sendLocale(sender, replace);
		if (!sender.equals(target)) {
			LocaleHelper.PTIME_SET.sendLocale(target, replace);
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
