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

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class AddWarp extends WarpCommand {

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
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (Users.isPlayer(sender)) {
			final Player p = (Player) sender;
			final HashMap<String, String> replace = new HashMap<String, String>();
			if (args.hasFlag('g')) {
				ACWorld.getWorld(p.getWorld()).addWarp(
						"spawn" + args.getString(0).toLowerCase(),
						p.getLocation());
				replace.put("name", args.getString(0));
				LocaleManager.sI18n(sender, "addSpawnWarp", replace);
				return;
			} else if (args.getValueFlag('p') != null) {
				final String perm = args.getValueFlag('p');
				ACWorld.getWorld(p.getWorld().getName()).addPermWarp(args.getString(0),
						p.getLocation(), perm);
				replace.put("name", args.getString(0));
				LocaleManager.sI18n(sender, "addWarp", replace);
				return;
			}
			ACWorld.getWorld(p.getWorld().getName()).addWarp(args.getString(0),
					p.getLocation());
			replace.put("name", args.getString(0));
			LocaleManager.sI18n(sender, "addWarp", replace);
		}

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
