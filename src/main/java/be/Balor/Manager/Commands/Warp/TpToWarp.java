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
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @authors Balor, Lathanael
 *
 */
public class TpToWarp extends CoreCommand {

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
	public void execute(CommandSender sender, CommandArgs args) {
		Player target = Utils.getUser(sender, args, permNode, 1, true);
		if (Utils.isPlayer(sender)) {
			Player p = (Player) sender;
			if (target != null) {
				HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("name", args.getString(0));
				Location loc = null;
				try {
					loc = ACWorld.getWorld(p.getWorld().getName()).getWarp(args.getString(0));
				} catch (WorldNotLoaded e) {
				}
				if (loc == null) {
					Utils.sI18n(sender, "errorWarp", replace);
					return;
				}
				else {
					ACPluginManager.getScheduler().scheduleSyncDelayedTask(
							ACHelper.getInstance().getCoreInstance(),
							new DelayedTeleport(target.getLocation(), loc, target, replace, sender),
							ACHelper.getInstance().getConfLong("teleportDelay"));
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
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 1;
	}

	private class DelayedTeleport implements Runnable {

		protected Location locBefore, teleportToLoc;
		protected Player target;
		protected HashMap<String, String> replace;
		protected CommandSender sender;

		public DelayedTeleport(Location locBefore, Location teleportLoc, Player target,
				HashMap<String, String> replace, CommandSender sender) {
			this.target = target;
			this.locBefore= locBefore;
			this.teleportToLoc = teleportLoc;
			this.replace = replace;
			this.sender = sender;
		}

		@Override
		public void run() {
			if (locBefore.equals(target.getLocation()) && ACHelper.getInstance().getConfBoolean("checkTeleportLocation")) {
				ACPlayer.getPlayer(target.getName()).setLastLocation(target.getLocation());
				target.teleport(teleportToLoc);
				sendMessage(sender, target, "tpWarp", replace);
			} else if (!ACHelper.getInstance().getConfBoolean("teleportDelay")) {
				ACPlayer.getPlayer(target.getName()).setLastLocation(target.getLocation());
				target.teleport(teleportToLoc);
				sendMessage(sender, target, "tpWarp", replace);
			} else {
				replace.clear();
				replace.put("cmdname", "Warp");
				sendMessage(sender, target, "errorMoved", replace);
			}
		}
	}
}
