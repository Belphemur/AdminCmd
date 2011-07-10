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
package be.Balor.Manager;

import java.util.HashMap;

import org.bukkit.command.CommandSender;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CommandManager {
	private HashMap<String, ACCommands> commands = new HashMap<String, ACCommands>();
	private static CommandManager instance = null;

	/**
	 * @return the instance
	 */
	public static CommandManager getInstance() {
		if (instance == null)
			instance = new CommandManager();
		return instance;
	}

	public void registerCommand(String cmd, Class<?> clazz) {
		try {
			ACCommands command = (ACCommands) clazz.newInstance();
			commands.put(cmd, command);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public boolean execCmd(String name, CommandSender sender, String... args) {
		ACCommands cmd = null;
		if (commands.containsKey(name) && (cmd = commands.get(name)).permissionCheck(sender)
				&& cmd.argsCheck(args)) {
			cmd.execute(sender, args);
			return true;
		} else
			return false;

	}
}
