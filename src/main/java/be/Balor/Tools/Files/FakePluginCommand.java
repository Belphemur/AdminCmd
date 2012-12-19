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
package be.Balor.Tools.Files;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FakePluginCommand extends Command {

	private final Plugin owningPlugin;

	/**
	 * @param name
	 */
	public FakePluginCommand(final String name, final Plugin plugin) {
		super(name);
		owningPlugin = plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.command.Command#execute(org.bukkit.command.CommandSender,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(final CommandSender sender,
			final String commandLabel, final String[] args) {
		return false;
	}

	/**
	 * Gets the owner of this PluginCommand
	 * 
	 * @return Plugin that owns this command
	 */
	public Plugin getPlugin() {
		return owningPlugin;
	}

}
