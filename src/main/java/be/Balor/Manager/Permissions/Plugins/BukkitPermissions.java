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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import be.Balor.Player.ACPlayer;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class BukkitPermissions extends SuperPermissions {
	protected PermissionsPlugin permBukkit = null;

	/**
	 *
	 */
	public BukkitPermissions(final PermissionsPlugin plugin) {
		permBukkit = plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#isInGroup(org.java.lang
	 * .String, org.bukkit.entity.Player)
	 */
	@Override
	public boolean isInGroup(final String groupName, final Player player) {
		List<Group> groups = new ArrayList<Group>();
		try {
			groups = permBukkit.getGroups(player.getName());
		} catch (final Exception e) {
			return false;
		}
		if (groups.isEmpty()) {
			return false;
		}
		for (final Group group : groups) {
			if (group.getName().equalsIgnoreCase(groupName)) {
				return true;
			}
		}
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
	public Set<Player> getUsers(final String groupName) {
		final Set<Player> players = new HashSet<Player>();
		List<String> playersString;
		try {
			playersString = permBukkit.getGroup(groupName).getPlayers();
		} catch (final Exception e) {
			return players;
		}
		if (playersString != null) {
			for (final String player : playersString) {
				players.add(ACPlayer.getPlayer(player).getHandler());
			}
			return players;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.SuperPermissions#getGroup(org.bukkit
	 * .entity.Player)
	 */
	@Override
	public be.Balor.Manager.Permissions.Group getGroup(final Player player) {
		final List<Group> groups = permBukkit.getGroups(player.getName());
		if (groups.isEmpty()) {
			return new be.Balor.Manager.Permissions.Group();
		}
		final Group group = groups.get(groups.size() - 1);
		return new be.Balor.Manager.Permissions.Group(group.getName(), player);
	}
}
