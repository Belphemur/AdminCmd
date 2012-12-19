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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class LockServer extends ServerCommand {

	/**
	 *
	 */
	public LockServer() {
		super("bal_lockdown", "admincmd.server.lock");
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
		if (ACHelper.getInstance().isServerLocked()) {
			ACHelper.getInstance().setServerLocked(false);
			Utils.sI18n(sender, "serverUnlock");
		} else {
			final String bcast = Utils.I18n("serverLock");
			if (bcast != null) {
				Utils.broadcastMessage(bcast);
				ACLogger.info(bcast);
			}
			ACHelper.getInstance().setServerLocked(true);
			final List<Player> onlinePlayers = Utils.getOnlinePlayers();
			ACPluginManager.getScheduler().scheduleSyncDelayedTask(getPlugin(),
					new Runnable() {
						@Override
						public void run() {
							for (final Player p : onlinePlayers) {
								if (PermissionManager.hasPerm(p,
										"admincmd.server.lockdown")) {
									continue;
								}
								p.kickPlayer(Utils.I18n("serverLockMessage"));
							}
						}
					}, 100);
		}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		plugin.getPermissionLinker().addPermChild("admincmd.server.lockdown");
		super.registerBukkitPerm();
	}

}
