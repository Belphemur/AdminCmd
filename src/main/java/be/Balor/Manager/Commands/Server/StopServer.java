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

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class StopServer extends ServerCommand {

	/**
	 *
	 */
	public StopServer() {
		permNode = "admincmd.server.stop";
		cmdName = "bal_stop";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		final String timeOut = args.getValueFlag('t');
		int timeOutValue;
		if (timeOut != null) {
			try {
				timeOutValue = Integer.parseInt(timeOut);
			} catch (final Exception e) {
				Utils.sI18n(sender, "NaN", "number", timeOut);
				return;
			}
		} else {
			timeOutValue = ConfigEnum.TIME_STOP.getInt();
		}
		Utils.broadcastMessage(Utils.I18n("serverWillStop", "sec",
				String.valueOf(timeOutValue)));
		ACHelper.getInstance().setServerLocked(true);
		final List<Player> onlinePlayers = Utils.getOnlinePlayers();
		ACPluginManager.getScheduler().scheduleSyncDelayedTask(
				ACPluginManager.getCorePlugin(), new Runnable() {

					@Override
					public void run() {
						for (final Player p : onlinePlayers) {
							p.kickPlayer(Utils.I18n("serverStop"));
						}
						Bukkit.getServer().shutdown();
					}
				}, Utils.secInTick * timeOutValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return true;
	}

}