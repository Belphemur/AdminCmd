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
package be.Balor.Manager.Commands.Warp;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class AddWarp extends CoreCommand {

	/**
	 *
	 */
	public AddWarp() {
		permNode = "admincmd.warp.create";
		cmdName = "bal_createwarp";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		if (Utils.isPlayer(sender)) {
			Player p = (Player) sender;
			HashMap<String, String> replace = new HashMap<String, String>();
			if (args.hasFlag('g')) {
				ACWorld.getWorld(p.getWorld().getName()).addWarp("spawn" + args.getString(0).toLowerCase(), p.getLocation());
				replace.put("name", args.getString(0));
				Utils.sI18n(sender, "addSpawnWarp", replace);
				return;
			}
			ACWorld.getWorld(p.getWorld().getName()).addWarp(args.getString(0), p.getLocation());
			replace.put("name", args.getString(0));
			Utils.sI18n(sender, "addWarp", replace);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 1;
	}
}
