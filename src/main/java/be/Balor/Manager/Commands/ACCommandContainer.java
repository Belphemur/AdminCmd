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
package be.Balor.Manager.Commands;

import org.bukkit.command.CommandSender;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACCommandContainer {
	private CommandSender sender;
	private CoreCommand cmd;
	private CommandArgs args;

	/**
 * 
 */
	public ACCommandContainer(CommandSender sender, CoreCommand cmd, String[] args) {
		this.sender = sender;
		this.cmd = cmd;
		this.args = new CommandArgs(args);
	}

	/**
	 * Execute the command
	 */
	public void execute() {
		cmd.execute(sender, args);
	}

	/**
	 * Debug display
	 * 
	 * @return
	 */
	public String debug() {
		return "The command "
				+ cmd.getCmdName()
				+ " "
				+ args.toString()
				+ " throw an Exception please report the log in a ticket : http://dev.bukkit.org/server-mods/admincmd/tickets/";
	}
}
