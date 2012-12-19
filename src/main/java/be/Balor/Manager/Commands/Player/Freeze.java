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
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Threads.RemovePowerTask;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Freeze extends PlayerCommand {

	/**
	 *
	 */
	public Freeze() {
		permNode = "admincmd.player.freeze";
		cmdName = "bal_freeze";
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
		final String timeOut = args.getValueFlag('t');
		final Player player = sender.getServer().getPlayer(args.getString(0));
		if (player != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Utils.getPlayerName(player));
			final ACPlayer acp = ACPlayer.getPlayer(player);
			if (acp.hasPower(Type.FROZEN)) {
				acp.removePower(Type.FROZEN);
				Utils.sI18n(player, "freezeDisabled");
				if (!player.equals(sender)) {
					Utils.sI18n(sender, "freezeDisabledTarget", replace);
				}
			} else {
				acp.setPower(Type.FROZEN);
				Utils.sI18n(player, "freezeEnabled");
				if (!player.equals(sender)) {
					Utils.sI18n(sender, "freezeEnabledTarget", replace);
				}
				if (timeOut == null) {
					return;
				}
				int timeOutValue;
				try {
					timeOutValue = Integer.parseInt(timeOut);
				} catch (final Exception e) {
					Utils.sI18n(sender, "NaN", "number", timeOut);
					return;
				}
				ACPluginManager.getScheduler().runTaskLaterAsynchronously(
						ACPluginManager.getCorePlugin(),
						new RemovePowerTask(acp, Type.FROZEN, sender),
						Utils.secInTick * ConfigEnum.SCALE_TIMEOUT.getInt()
								* timeOutValue);
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
		return args != null && args.length >= 1;
	}

}
