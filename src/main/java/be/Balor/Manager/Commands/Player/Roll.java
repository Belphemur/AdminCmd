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
import java.util.Random;

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
public class Roll extends PlayerCommand {

	/**
	 *
	 */
	public Roll() {
		permNode = "admincmd.player.roll";
		cmdName = "bal_roll";
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
		int dice = 6;
		if (args.length >= 1) {
			try {
				dice = args.getInt(0);
			} catch (final NumberFormatException e) {}
		}

		final Random rand = new Random();
		if (dice < 1) {
			dice = rand.nextInt(19) + 1;
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("face", String.valueOf(dice));
		if (Utils.isPlayer(sender, false)) {
			replace.put("player", Utils.getPlayerName((Player) sender));
		} else {
			replace.put("player", "Server Admin");
		}
		replace.put("result", String.valueOf(rand.nextInt(dice) + 1));
		Utils.broadcastMessage(Utils.I18n("roll", replace));

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
