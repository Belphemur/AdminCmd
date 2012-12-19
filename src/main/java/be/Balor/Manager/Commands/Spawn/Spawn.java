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
package be.Balor.Manager.Commands.Spawn;

import static be.Balor.Tools.Utils.sendMessage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.SimplifiedLocation;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @authors Balor, Lathanael
 * 
 */
public class Spawn extends SpawnCommand {

	/**
	 *
	 */
	public Spawn() {
		permNode = "admincmd.spawn.tp";
		cmdName = "bal_spawn";
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
		if (args.length >= 1 && Utils.isPlayer(sender, true)) {
			final ACWorld w = ACWorld.getWorld(args.getString(0));
			final Player target = (Player) sender;
			if (!target.getWorld().equals(w.getHandler())
					&& !PermissionManager.hasPerm(sender, "admincmd.spawn.tp."
							+ w.getName().toLowerCase())) {
				return;
			}
			ACPluginManager.getScheduler().scheduleSyncDelayedTask(
					ACHelper.getInstance().getCoreInstance(),
					new DelayedTeleport(target, sender, w),
					ConfigEnum.TP_DELAY.getLong());
		} else if (Utils.isPlayer(sender, true)) {
			final Player target = (Player) sender;
			ACPluginManager.getScheduler().scheduleSyncDelayedTask(
					ACHelper.getInstance().getCoreInstance(),
					new DelayedTeleport(target, sender, null),
					ConfigEnum.TP_DELAY.getLong());
		}
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

	private class DelayedTeleport implements Runnable {

		protected SimplifiedLocation locBefore;
		protected Player target;
		protected HashMap<String, String> replace;
		protected CommandSender sender;
		protected ACWorld world;

		public DelayedTeleport(final Player target, final CommandSender sender,
				final ACWorld world) {
			this.target = target;
			this.locBefore = new SimplifiedLocation(target.getLocation());
			this.sender = sender;
			this.world = world;
			if (ConfigEnum.TP_DELAY.getLong() > 0) {
				final Map<String, String> replace = new HashMap<String, String>();
				replace.put(
						"sec",
						String.valueOf(ConfigEnum.TP_DELAY.getLong()
								/ Utils.secInTick));
				LocaleHelper.TELEPORT_SOON.sendLocale(target, replace);
			}
		}

		@Override
		public void run() {
			if (!ConfigEnum.CHECKTP.getBoolean()) {
				ACHelper.getInstance().spawn((Player) sender, world);
				sendMessage(sender, target, "spawn");
				return;
			}

			if (locBefore.equals(target.getLocation())) {
				ACHelper.getInstance().spawn((Player) sender, world);
				sendMessage(sender, target, "spawn");
			} else {
				replace = new HashMap<String, String>();
				replace.put("cmdname", "Warp");
				sendMessage(sender, target, "errorMoved", replace);
			}
		}
	}
}
