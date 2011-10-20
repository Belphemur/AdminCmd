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

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @authors Balor, Lathanael
 *
 */
public class Spawn extends CoreCommand {

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
	public void execute(CommandSender sender, CommandArgs args) {
		if (Utils.isPlayer(sender, true)) {
			Player target = (Player) sender;
			ACPluginManager.getScheduler().scheduleSyncDelayedTask(
					ACHelper.getInstance().getCoreInstance(),
					new DelayedTeleport(target.getLocation(), target, sender),
					ACHelper.getInstance().getConfLong("teleportDelay"));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return true;
	}

	private class DelayedTeleport implements Runnable {

		protected Location locBefore;
		protected Player target;
		protected HashMap<String, String> replace;
		protected CommandSender sender;

		public DelayedTeleport(Location locBefore, Player target, CommandSender sender) {
			this.target = target;
			this.locBefore= locBefore;
			this.sender = sender;
		}

		@Override
		public void run() {
				if (locBefore.equals(target.getLocation()) && ACHelper.getInstance().getConfBoolean("checkTeleportLocation")) {
					ACPlayer.getPlayer(target.getName()).setLastLocation(target.getLocation());
					ACHelper.getInstance().spawn((Player) sender);
					sendMessage(sender, target, "spawn");
				} else if (!ACHelper.getInstance().getConfBoolean("teleportDelay")) {
					ACPlayer.getPlayer(target.getName()).setLastLocation(target.getLocation());
					ACHelper.getInstance().spawn((Player) sender);
					sendMessage(sender, target, "spawn");
				} else {
					replace.put("cmdname", "Warp");
					sendMessage(sender, target, "errorMoved", replace);
				}
		}
	}
}
