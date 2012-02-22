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
import com.platymuus.bukkit.permissions.data.DataAccessException;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class BukkitPermissions extends SuperPermissions {
	protected PermissionsPlugin permBukkit = null;

	/**
	 *
	 */
	public BukkitPermissions(PermissionsPlugin plugin) {
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
	public boolean isInGroup(String groupName, Player player) {
		List<Group> groups = new ArrayList<Group>();
		try {
			groups = permBukkit.getGroups(player.getName());
		} catch (DataAccessException e) {
			return false;
		}
		if (groups.isEmpty())
			return false;
		for (Group group : groups)
			if (group.getName().equalsIgnoreCase(groupName))
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
	public Set<Player> getUsers(String groupName) {
		Set<Player> players = new HashSet<Player>();
		List<String> playersString;
		try {
			playersString = permBukkit.getGroup(groupName).getPlayers();
		} catch (DataAccessException e) {
			return players;
		}
		if (playersString != null) {
			for (String player : playersString) {
				players.add(ACPlayer.getPlayer(player).getHandler());
			}
			return players;
		}
		return null;
	}
}
