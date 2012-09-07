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

import static be.Balor.Tools.Utils.sendMessage;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Warp;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @authors Balor, Lathanael
 * 
 */
public class TpToWarp extends WarpCommand {
	private PermChild tpAll;

	/**
	 *
	 */
	public TpToWarp() {
		permNode = "admincmd.warp.tp";
		cmdName = "bal_tpwarp";
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
		if (args.length == 0) {
			Utils.sI18n(sender, "errorWarp", new HashMap<String, String>());
			return;
		}
		final Player target = Utils.getUser(sender, args, permNode, 1, true);
		if (Utils.isPlayer(sender)) {
			final Player p = (Player) sender;
			Location loc = null;
			if (target != null) {
				final HashMap<String, String> replace = new HashMap<String, String>();

				if (args.getString(0).contains(":")) {
					if (!PermissionManager.hasPerm(sender,
							tpAll.getBukkitPerm())) {
						return;
					}
					final String[] split = args.getString(0).split(":");
					final String world = split[0];
					final String warp = split[1];
					replace.put("name", world + ":" + warp);
					try {
						final ACWorld acWorld = ACWorld.getWorld(world);
						final Warp warpPoint = acWorld.getWarp(warp);
						if (warpPoint == null) {
							replace.put("name", args.getString(0));
							Utils.sI18n(sender, "errorWarp", replace);
							return;
						}
						if (warpPoint.permission != null
								&& !warpPoint.permission.isEmpty()
								&& !warpPoint.permission.equalsIgnoreCase("")
								&& !PermissionManager.hasPerm(sender, 
										permNode + "." + warpPoint.permission, false)) {
							replace.put("point", warp);
							LocaleHelper.WARP_NO_PERM.sendLocale(sender, replace);
							return;
						}
						loc = warpPoint.loc;
						replace.put("name", acWorld.getName() + ":"
								+ warpPoint.name);
					} catch (final WorldNotLoaded e) {
						Utils.sI18n(sender, "worldNotFound", "world", world);
						return;
					}
				} else {
					replace.put("name", args.getString(0));

					try {
						final Warp warpPoint = ACWorld.getWorld(
								p.getWorld().getName()).getWarp(
								args.getString(0));
						if (warpPoint == null) {
							replace.put("name", args.getString(0));
							Utils.sI18n(sender, "errorWarp", replace);
							return;
						}
						if (warpPoint.permission != null
								&& !warpPoint.permission.isEmpty()
								&& !warpPoint.permission.equalsIgnoreCase("")
								&& !PermissionManager.hasPerm(sender, 
										permNode + "." + warpPoint.permission, false)) {
							replace.put("point", args.getString(0));
							LocaleHelper.WARP_NO_PERM.sendLocale(sender, replace);
							return;
						}
						loc = warpPoint.loc;
						replace.put("name", warpPoint.name);
					} catch (final WorldNotLoaded e) {
					}
				}
				if (loc == null) {
					Utils.sI18n(sender, "errorWarp", replace);
					return;
				} else {
					ACPluginManager.getScheduler().scheduleSyncDelayedTask(
							ACHelper.getInstance().getCoreInstance(),
							new DelayedTeleport(target.getLocation(), loc,
									target, replace, sender),
							ConfigEnum.TP_DELAY.getLong());
				}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		super.registerBukkitPerm();
		tpAll = new PermChild("admincmd.warp.tp.all", bukkitDefault);
		permParent.addChild(tpAll);
	}

	private class DelayedTeleport implements Runnable {

		protected Location locBefore, teleportToLoc;
		protected Player target;
		protected HashMap<String, String> replace;
		protected CommandSender sender;

		public DelayedTeleport(final Location locBefore,
				final Location teleportLoc, final Player target,
				final HashMap<String, String> replace,
				final CommandSender sender) {
			this.target = target;
			this.locBefore = locBefore;
			this.teleportToLoc = teleportLoc;
			this.replace = replace;
			this.sender = sender;
		}

		@Override
		public void run() {
			if (locBefore.equals(target.getLocation())
					&& ConfigEnum.CHECKTP.getBoolean()) {
				Utils.teleportWithChunkCheck(target, teleportToLoc);
				sendMessage(sender, target, "tpWarp", replace);
			} else if (!ConfigEnum.CHECKTP.getBoolean()) {
				Utils.teleportWithChunkCheck(target, teleportToLoc);
				sendMessage(sender, target, "tpWarp", replace);
			} else {
				replace.clear();
				replace.put("cmdname", "Warp");
				sendMessage(sender, target, "errorMoved", replace);
			}
		}
	}
}
