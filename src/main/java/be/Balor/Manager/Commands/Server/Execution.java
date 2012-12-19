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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.CommandNotFound;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Terminal.TerminalCommandManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Execution extends ServerCommand {

	/**
	 * 
	 */
	public Execution() {
		permNode = "admincmd.server.exec";
		cmdName = "bal_exec";
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
		if (args.length == 0) {
			sender.sendMessage("Possibles Cmds : " + getCmdList(sender));
			return;
		}
		if (args.hasFlag('r')) {
			if (args.getString(0).equals("all")) {
				TerminalCommandManager.getInstance().reloadScripts();
				sender.sendMessage(ChatColor.YELLOW + "All scripts reloaded");
				sender.sendMessage("Possibles Cmds : " + getCmdList(sender));
			} else {
				try {
					TerminalCommandManager.getInstance().execute(sender,
							args.getString(0), true);
				} catch (final CommandNotFound e) {
					sender.sendMessage(e.getMessage());

					sender.sendMessage("Possibles Cmds : " + getCmdList(sender));
				}
			}
			return;
		}
		try {

			TerminalCommandManager.getInstance().execute(sender,
					args.getString(0), false);
		} catch (final CommandNotFound e) {
			sender.sendMessage(e.getMessage());

			sender.sendMessage("Possibles Cmds : " + getCmdList(sender));
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

	private String getCmdList(final CommandSender sender) {
		String cmds = "";
		for (final String cmd : TerminalCommandManager.getInstance()
				.getCommandList()) {
			if (TerminalCommandManager.getInstance().checkCommand(cmd, sender)) {
				cmds += cmd + ", ";
			}
		}
		return cmds.trim();
	}

}
