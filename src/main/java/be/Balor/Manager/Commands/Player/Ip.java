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

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Ip extends PlayerCommand {

	/**
	 *
	 */
	public Ip() {
		permNode = "admincmd.player.ip";
		cmdName = "bal_ip";
		other = true;
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
		final Player target = Utils.getUser(sender, args, permNode, 0, false);
		final HashMap<String, String> replace = new HashMap<String, String>();
		if (target != null) {
			replace.put("player", Utils.getPlayerName(target));
			replace.put("ip", target.getAddress().getAddress().toString());
			Utils.sI18n(sender, "ip", replace);
		} else {
			final ACPlayer acp = ACPlayer.getPlayer(args.getString(0));
			if (acp == null) {
				replace.put("player", args.getString(0));
				Utils.sI18n(sender, "playerNotFound", replace);
			}
			replace.put("player", acp.getName());
			replace.put("ip", acp.getInformation("last-ip").getString());
			Utils.sI18n(sender, "ip", replace);
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
