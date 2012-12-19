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
package be.Balor.Manager.Terminal;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import be.Balor.Manager.Permissions.PermissionManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class TerminalCommand {
	protected String commandName;
	protected String execution;
	protected String args;
	protected Permission bukkitPerm;
	protected File workingDir;

	/**
	 * 
	 */
	public TerminalCommand(final String commandName, final String execution,
			final String args, final File workingDir) {
		this.commandName = commandName;
		this.execution = execution;
		this.args = args;
		this.workingDir = workingDir;
	}

	public TerminalCommand(final String commandName, final String execution,
			final String args, final String workingDir) {
		this(commandName, execution, args, new File(workingDir));
	}

	/**
	 * @param bukkitPerm
	 *            the bukkitPerm to set
	 */
	public void setBukkitPerm(final Permission bukkitPerm) {
		this.bukkitPerm = bukkitPerm;
	}

	/**
	 * Check if the user has the perm to execute the command
	 * 
	 * @param sender
	 * @param msg
	 * @return
	 */
	public boolean permCheck(final CommandSender sender, final boolean msg) {
		return PermissionManager.hasPerm(sender, bukkitPerm, msg);
	}

	public boolean permCheck(final CommandSender sender) {
		return PermissionManager.hasPerm(sender, bukkitPerm, false);
	}

	/**
	 * Execute the command
	 */
	public abstract void execute(CommandSender sender);
}
