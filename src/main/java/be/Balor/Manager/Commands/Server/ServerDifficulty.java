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
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class ServerDifficulty extends CoreCommand{

	/**
	 *
	 */
	public ServerDifficulty() {
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
	public void execute(CommandSender sender, CommandArgs args) {
		ACWorld world = null;
		try {
			if (args.getString(0) != null)
				world = ACWorld.getWorld(args.getString(0));
			else
				if (Utils.isPlayer(sender, false))
					world = ACWorld.getWorld(((Player) sender).getWorld().getName());
				else {
					HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("argument", "world");
					Utils.sI18n(sender, "errorInsufficientArguments", replace);
					return;
				}
		} catch (WorldNotLoaded e) {
			Utils.sI18n(sender, "worldNotFound");
			return;
		}
		String dif;
		Difficulty toSet = Difficulty.NORMAL;
		Map<String, String> replace = new HashMap<String, String>();
		if (args.hasFlag('g')) {
				dif = world.getDifficulty().toString();
				replace.put("world", world.getName());
				replace.put("difficulty", dif);
				Utils.sI18n(sender, "getDifficulty", replace);
		} else if (args.hasFlag('s')) {
			if (args.length >= 2)
				toSet = Difficulty.getByValue(args.getInt(1));
			try {
				toSet = Difficulty.getByValue(args.getInt(0));
			} catch (NumberFormatException e) {
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
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 1;
	}

}