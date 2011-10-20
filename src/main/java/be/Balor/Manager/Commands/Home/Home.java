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
public class Home extends CoreCommand {
	public Home() {
		permNode = "admincmd.tp.home";
		cmdName = "bal_home";
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
		if (Utils.isPlayer(sender)) {
			Player player = (Player) sender;
			be.Balor.Tools.Home home = null;
			home = Utils.getHome(sender, args.getString(0));
			if (home == null)
				return;
			Location loc = ACPlayer.getPlayer(home.player).getHome(home.home);
			if (loc == null) {
				Utils.sI18n(sender, "errorMultiHome", "home", home.home);
				return;
			}
			else {
				ACPluginManager.getScheduler().scheduleSyncDelayedTask(
						ACHelper.getInstance().getCoreInstance(),
						new DelayedTeleport(player.getLocation(), loc, player, home, sender),
						ACHelper.getInstance().getConfLong("teleportDelay"));
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
		return args != null;
	}

	private class DelayedTeleport implements Runnable {

		protected Location locBefore, teleportToLoc;
		protected Player target;
		protected be.Balor.Tools.Home home;
		protected CommandSender sender;

		public DelayedTeleport(Location locBefore, Location teleportLoc, Player target,
				be.Balor.Tools.Home home, CommandSender sender) {
			this.target = target;
			this.locBefore= locBefore;
			this.teleportToLoc = teleportLoc;
			this.home = home;
			this.sender = sender;
		}

		@Override
		public void run() {
			if (locBefore.equals(target.getLocation()) && ACHelper.getInstance().getConfBoolean("checkTeleportLocation")) {
				ACPlayer.getPlayer(target.getName()).setLastLocation(target.getLocation());
				target.teleport(teleportToLoc);
				Utils.sI18n(sender, "multiHome", "home", home.home);
			} else if (!ACHelper.getInstance().getConfBoolean("teleportDelay")) {
				ACPlayer.getPlayer(target.getName()).setLastLocation(target.getLocation());
				target.teleport(teleportToLoc);
				Utils.sI18n(sender, "multiHome", "home", home.home);
			} else {
				Utils.sI18n(sender, "errorMoved", "cmdname", "Home");
			}
		}
	}
}
