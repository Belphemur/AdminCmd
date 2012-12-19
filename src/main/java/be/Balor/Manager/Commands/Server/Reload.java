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
package be.Balor.Manager.Commands.Server;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Reload extends ServerCommand {

	/**
	 * 
	 */
	public Reload() {
		permNode = "admincmd.server.reload";
		cmdName = "bal_reload";
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
			throws ActionNotPermitedException, PlayerNotFound {
		if (args.length >= 1 && !args.getString(0).equals("AdminCmd")) {
			final Plugin plugin = sender.getServer().getPluginManager()
					.getPlugin(args.getString(0));
			if (plugin == null) {
				Utils.sI18n(sender, "pluginNotFound", "plugin",
						args.getString(0));
				return;
			}
			ACPluginManager.scheduleSyncTask(new Runnable() {
				@Override
				public void run() {
					ACPluginManager.getServer().getPluginManager()
							.disablePlugin(plugin);
					ACPluginManager.getServer().getPluginManager()
							.enablePlugin(plugin);

				}
			});
			Utils.sI18n(sender, "pluginReloaded", "plugin", args.getString(0));
		} else {
			ACHelper.getInstance().reload();
			Utils.sI18n(sender, "pluginReloaded", "plugin", "AdminCmd");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
