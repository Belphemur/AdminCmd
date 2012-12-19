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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Difficulty;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class WorldDifficulty extends ServerCommand {

	/**
	 *
	 */
	public WorldDifficulty() {
		cmdName = "bal_difficulty";
		permNode = "admincmd.server.difficulty";
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
		ACWorld world = null;
		boolean worldGiven = false;
		int difValue = -1;
		try {
			try {
				difValue = args.getInt(0);
			} catch (final NumberFormatException e) {
				worldGiven = true;
			}
			if (args.length >= 1 && worldGiven) {
				world = ACWorld.getWorld(args.getString(0));
			} else if (Utils.isPlayer(sender, false)) {
				world = ACWorld
						.getWorld(((Player) sender).getWorld().getName());
			} else {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("argument", "world");
				Utils.sI18n(sender, "errorInsufficientArguments", replace);
				return;
			}
		} catch (final WorldNotLoaded e) {
			Utils.sI18n(sender, "worldNotFound");
			return;
		}

		Difficulty toSet = Difficulty.NORMAL;
		final Map<String, String> replace = new HashMap<String, String>();
		if (args.hasFlag('g')) {
			replace.put("world", world.getName());
			replace.put("difficulty", world.getDifficulty().toString());
			Utils.sI18n(sender, "getDifficulty", replace);
		} else if (args.hasFlag('s')) {
			if (args.length >= 2) {
				toSet = Difficulty.getByValue(args.getInt(1));
			} else if (difValue != -1) {
				toSet = Difficulty.getByValue(difValue);
			}

			if (toSet == null) {
				toSet = Difficulty.NORMAL;
			}

			replace.put("world", world.getName());
			replace.put("difficulty", toSet.toString());
			world.setDifficulty(toSet);
			Utils.sI18n(sender, "setDifficulty", replace);
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

}
