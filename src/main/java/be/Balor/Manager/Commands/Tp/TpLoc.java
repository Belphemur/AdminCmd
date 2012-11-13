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
package be.Balor.Manager.Commands.Tp;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Threads.TeleportTask;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class TpLoc extends TeleportCommand {

	/**
	 * 
	 */
	public TpLoc() {
		permNode = "admincmd.tp.location";
		cmdName = "bal_tpthere";
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
	public void execute(final CommandSender sender, final CommandArgs args)
			throws PlayerNotFound, ActionNotPermitedException {
		final Player target = Utils.getUserParam(sender, args, permNode);
		if (target == null) {
			return;
		}
		final double x;
		final double y;
		final double z;
		try {
			x = args.getDouble(0);
			y = args.getDouble(1);
			z = args.getDouble(2);
		} catch (final Exception e) {
			Utils.sI18n(sender, "errorLocation");
			return;
		}
		if (args.length <= 3){
			ACPluginManager.scheduleSyncTask(new TeleportTask(target, new Location(
				target.getWorld(), x, y, z)));
		} else {
			final float rot;
			try {
				rot = args.getFloat(3);
			} catch (final Exception e) {
				Utils.sI18n(sender, "errorLocation");
				return;
			}
			ACPluginManager.scheduleSyncTask(new TeleportTask(target, new Location(
					target.getWorld(), x, y, z, rot, 0F)));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 3;
	}

}
