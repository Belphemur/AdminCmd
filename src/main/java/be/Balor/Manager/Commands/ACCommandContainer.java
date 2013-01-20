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
import org.bukkit.entity.Player;

import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACCommandContainer {
	private final CommandSender sender;
	private final CoreCommand cmd;
	private CommandArgs args = null;
	private final String[] argsStrings;

	/**
 *
 */
	public ACCommandContainer(final CommandSender sender,
			final CoreCommand cmd, final String[] args) {
		this.sender = sender;
		this.cmd = cmd;
		this.argsStrings = args;
	}

	/**
	 * Parse the arguments, flags, etc ... by creating the CommandArgs
	 */
	public void processArguments() {
		if (args == null) {
			try {
				args = new CommandArgs(argsStrings);
			} catch (final Exception e) {
				ACLogger.severe("Problem in parsing the commandString", e);
			}
		}
	}

	/**
	 * Execute the command
	 * 
	 * @throws PlayerNotFound
	 */
	public void execute() throws PlayerNotFound, ActionNotPermitedException {
		if (ConfigEnum.LOG_CMD.getBoolean()) {
			String name = "Console";
			if (sender instanceof Player) {
				name = ((Player) sender).getName();
			}
			ACLogger.info(name + " [CMD: " + cmd.getCmdName() + "] (ARGS:"
					+ args.toString() + ")");
		}
		cmd.execute(sender, args);
	}

	/**
	 * Debug display
	 * 
	 * @return
	 */
	public String debug() {
		return "[Plugin Version: "
				+ ACHelper.getInstance().getCoreInstance().getDescription()
						.getVersion()
				+ "]["
				+ Thread.currentThread().getName()
				+ "] The command "
				+ (cmd != null ? cmd.getCmdName() : "command=null")
				+ " "
				+ (args != null ? args.toString() : "args=null")
				+ " throw an Exception please report the log in a ticket : http://bug.admincmd.com/";
	}

	/**
	 * @return the sender
	 */
	public CommandSender getSender() {
		return sender;
	}

	public Class<? extends CoreCommand> getCommandClass() {
		return cmd.getClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ACCommandContainer [sender=" + sender + ", cmd=" + cmd
				+ ", args=" + args + "]";
	}
}
