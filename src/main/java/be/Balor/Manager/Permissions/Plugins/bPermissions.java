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
package be.Balor.Manager.Permissions.Plugins;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Tools.Utils;
import de.bananaco.permissions.info.InfoReader;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class bPermissions extends SuperPermissions {
	protected WorldPermissionsManager worldPermManager;
	protected InfoReader infoReader;

	/**
	 * @param plugin
	 * @param infoReader
	 * 
	 */
	public bPermissions(WorldPermissionsManager plugin, InfoReader infoReader) {
		worldPermManager = plugin;
		this.infoReader = infoReader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#isInGroup(org.java.lang
	 * .String, org.bukkit.entity.Player)
	 */
	@Override
	public boolean isInGroup(String groupName, Player player) {
		List<String> groups = worldPermManager.getPermissionSet(player.getWorld().getName())
				.getGroups(player);
		if (groups == null)
			return false;
		if (groups.isEmpty())
			return false;
		if (groups.contains(groupName))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getUsers(org.java.lang
	 * .String)
	 */
	@Override
	public Set<Player> getUsers(String groupName) throws NoPermissionsPlugin {
		Set<Player> players = new HashSet<Player>();
		for (Player player : Utils.getOnlinePlayers()) {
			for (String group : worldPermManager.getPermissionSet(player.getWorld().getName())
					.getGroups(player)) {
				if (!group.equals(groupName))
					continue;
				players.add(player);

			}

		}
		return players;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getPermissionLimit(org
	 * .bukkit.entity.Player, java.lang.String)
	 */
	@Override
	public String getPermissionLimit(Player p, String limit) {
		String result = null;
		if (result == null || result.isEmpty()) {
			result = infoReader.getValue(p, limit);
		}
		if (result == null || result.isEmpty())
			result = super.getPermissionLimit(p, limit);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getPrefix(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public String getPrefix(Player player) {
		String prefix = super.getPrefix(player);
		if (prefix == null || prefix.isEmpty())
			prefix = infoReader.getPrefix(player);
		return prefix;
	}

}
