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
package be.Balor.Manager.Commands.Home;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Home;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class HomeCommand extends CoreCommand {
	/**
 *
 */
	public HomeCommand() {
		super();
		this.permParent = plugin.getPermissionLinker().getPermParent(
				"admincmd.tp.*");
	}

	/**
	 * Get the home by checking the colon
	 * 
	 * @param sender
	 *            who send the command
	 * @param toParse
	 *            what args was send
	 * @return the home containing the player and the home name
	 */
	protected Home getHome(final CommandSender sender, final String toParse) {
		final Home result = new Home();
		if (toParse != null && toParse.contains(":")) {
			try {
				final String[] split = toParse.split(":");
				result.player = split[0];
				if (split[1] == null) {
					final HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("arg", "home name after the ':'");
					replace.put("cmdName", "/home");
					LocaleHelper.MISSING_ARG.sendLocale(sender, replace);
					return null;
				}
				result.home = split[1];
			} catch (final ArrayIndexOutOfBoundsException e) {
			}
			if (Utils.isPlayer(sender, false)) {
				final Player p = (Player) sender;
				if (!p.getName().equals(result.player)
						&& !PermissionManager.hasPerm(p, "admincmd.admin.home")) {
					return null;
				}
			}
		} else {
			if (!Utils.isPlayer(sender)) {
				return null;
			}
			final Player p = ((Player) sender);
			result.player = p.getName();
			if (toParse != null) {
				result.home = toParse;
			} else {
				result.home = p.getWorld().getName();
			}
		}
		if (result.home.contains(".")) {
			LocaleHelper.ERROR_DOT_HOME.sendLocale(sender);
			return null;
		}
		return result;
	}

}
