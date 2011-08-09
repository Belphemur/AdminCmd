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

import org.bukkit.command.CommandSender;


import be.Balor.Manager.ACCommand;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class Day extends ACCommand {
	
	/**
	 * 
	 */
	public Day() {
		permNode = "admincmd.time.day";
		cmdName = "bal_timeday";
	}

	/* (non-Javadoc)
	 * @see be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender, java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, String... args) {
		Utils.timeSet(sender, "day");
	}

	/* (non-Javadoc)
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return true;
	}

}
