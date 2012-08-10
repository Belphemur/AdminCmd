/*************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package be.Balor.Manager.Commands.Spawn;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class SetGroupSpawn extends SpawnCommand {

	/**
		 *
		 */
	public SetGroupSpawn() {
		permNode = "admincmd.spawn.gset";
		cmdName = "bal_setgroupspawn";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args) {
		final HashMap<String, String> replace = new HashMap<String, String>();

		if (args.length < 1) {
			if (Utils.isPlayer(sender)) {
				final Player player = (Player) sender;
				for (String groupName : ACHelper.getInstance().getGroupList()) {
					groupName = groupName.toLowerCase();
					if (PermissionManager.hasPerm(player, "admincmd.respawn."
							+ groupName)) {
						ACWorld.getWorld(player.getWorld()).setGroupSpawn(
								groupName, player.getLocation());
						replace.put("groupName", groupName);
						LocaleHelper.GROUP_SPAWN_SET
								.sendLocale(sender, replace);
						return;
					}
				}
			}
		} else if (args.length >= 1) {
			if (Utils.isPlayer(sender)) {
				final Player player = (Player) sender;
				final String groupName = args.getString(0).toLowerCase();
				if (ACHelper.getInstance().getGroupList().contains(groupName)) {
					ACWorld.getWorld(player.getWorld().getName())
							.setGroupSpawn(groupName, player.getLocation());
					replace.put("groupName", groupName);
					LocaleHelper.GROUP_SPAWN_SET.sendLocale(sender, replace);
					return;
				}
				LocaleHelper.NO_SUCH_GROUP.sendLocale(sender, replace);
				return;
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
		return args != null;
	}

}
