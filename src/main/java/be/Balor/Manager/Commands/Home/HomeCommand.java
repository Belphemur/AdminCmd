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
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Home;
import be.Balor.Tools.Type;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.bukkit.AdminCmd.ACHelper;
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
		this.permParent = plugin.getPermissionLinker().getPermParent("admincmd.tp.*");
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
			if (Users.isPlayer(sender, false)) {
				final Player p = (Player) sender;
				if (!p.getName().equals(result.player) && !PermissionManager.hasPerm(p, "admincmd.admin.home")) {
					return null;
				}
			}
		} else {
			if (!Users.isPlayer(sender)) {
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

	/**
	 * Check if the player have reached the homelimit and then can't do the
	 * command.
	 * 
	 * @param player
	 * @throws ActionNotPermitedException
	 *             if the player have reached the home limit
	 */
	protected void verifyCanExecute(final CommandSender sender, final Player player) throws ActionNotPermitedException {
		if (PermissionManager.hasPerm(sender, "admincmd.admin.home", false)) {
			return;
		}
		final int limit = ACHelper.getInstance().getLimit(player, Type.Limit.MAX_HOME);
		final int nbHomes = ACPlayer.getPlayer(player).getHomeList().size();
		if (nbHomes > limit) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("number", String.valueOf(nbHomes - limit));
			throw new ActionNotPermitedException(sender, LocaleHelper.ERROR_LIMIT_REACHED.getLocale(replace));
		}

	}

}
