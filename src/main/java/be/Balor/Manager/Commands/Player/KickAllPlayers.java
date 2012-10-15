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
package be.Balor.Manager.Commands.Player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class KickAllPlayers extends PlayerCommand {

	/**
	 *
	 */
	public KickAllPlayers() {
		permNode = "admincmd.player.kickall";
		cmdName = "bal_kickall";
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
		String message = "";
		String playerName = "";
		if (args.length >= 1) {
			for (int i = 0; i < args.length; i++) {
				message += args.getString(i) + " ";
			}
		} else {
			message = "You have been kick by ";
			if (!Utils.isPlayer(sender, false)) {
				playerName = "Server Admin";
			} else {
				playerName = Utils.getPlayerName(((Player) sender));
			}
			message += playerName;
		}
		message = message.trim();
		for (final Player toKick : Utils.getOnlinePlayers()) {
			if (!toKick.getName().equals(((Player) sender).getName())) {
				toKick.kickPlayer(message);
			}
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
