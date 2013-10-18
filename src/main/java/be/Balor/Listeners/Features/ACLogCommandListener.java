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
package be.Balor.Listeners.Features;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import be.Balor.Tools.Compatibility.ACMinecraftReflection;
import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Antoine
 * 
 */
public class ACLogCommandListener implements Listener {
	private final CommandMap cmdMap = FieldUtils.getCommandMap();

	@EventHandler(priority = EventPriority.LOW)
	public void onCommandPreprocess(final PlayerCommandPreprocessEvent event) {

		String cmd = event.getMessage();
		if (cmd.charAt(0) == '/') {
			cmd = cmd.replaceFirst("/", "");
		}
		cmd = cmd.split(" ")[0];

		final Command registeredCmd = cmdMap.getCommand(cmd);
		if (!(registeredCmd instanceof PluginCommand)) {
			return;
		}
		if (!((PluginCommand) registeredCmd).getPlugin().equals(
				ACPluginManager.getCorePlugin())) {
			return;
		}
		event.setCancelled(true);

		try {
			if (Bukkit.getServer().dispatchCommand(event.getPlayer(),
					event.getMessage().substring(1))) {
				return;
			}
		} catch (final org.bukkit.command.CommandException ex) {
			event.getPlayer()
					.sendMessage(
							org.bukkit.ChatColor.RED
									+ "An internal error occurred while attempting to perform this command");
			java.util.logging.Logger.getLogger(
					ACMinecraftReflection.getNetServerHandlerClass().getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
			return;
		}

	}
}
