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

import org.bukkit.command.CommandSender;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public interface ACCommands {
	/**
	 * Execute the command
	 * 
	 * @param args
	 */
	public void execute(CommandSender sender, String... args);

	/**
	 * Check if the command can be executed
	 * 
	 * @param args
	 * @return
	 */
	public boolean argsCheck(String... args);
	/**
	 * Check if the sender have the permission for the command.
	 * @param sender
	 * @return
	 */
	public boolean permissionCheck(CommandSender sender);
}
