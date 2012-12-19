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

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
public class PlayerLocation extends PlayerCommand {

	/**
	 *
	 */
	public PlayerLocation() {
		permNode = "admincmd.player.loc";
		cmdName = "bal_playerloc";
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
		Location loc;
		String msg;
		Player target;
		if (args.length == 0) {
			if (Utils.isPlayer(sender)) {
				target = (Player) sender;
				loc = target.getLocation();
				msg = "You are";
			} else {
				return;
			}
		} else {
			try {
				target = Utils.getUser(sender, args, permNode);
				loc = target.getLocation();
				msg = Utils.getPlayerName(target) + " is";
			} catch (final NullPointerException ex) {
				Utils.sI18n(sender, "playerNotFound", "player",
						args.getString(0));
				return;
			}
		}
		sender.sendMessage(loc.getBlockX() + " X, " + loc.getBlockZ() + " Z, "
				+ loc.getBlockY() + " Y");
		final String facing[] = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
		double yaw = ((loc.getYaw() + 22.5) % 360);
		if (yaw < 0) {
			yaw += 360;
		}
		sender.sendMessage(msg + " facing " + ChatColor.RED
				+ facing[(int) (yaw / 45)] + ChatColor.WHITE + " in World "
				+ ChatColor.AQUA + target.getWorld().getName());

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
