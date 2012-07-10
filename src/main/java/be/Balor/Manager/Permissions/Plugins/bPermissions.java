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
import java.util.Set;

import org.bukkit.entity.Player;

import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Manager.Permissions.Group;
import be.Balor.Tools.Utils;
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class bPermissions extends SuperPermissions {

	public bPermissions() {
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
		final String[] groups = ApiLayer.getGroups(player.getWorld().getName(),
				CalculableType.USER, player.getName());
		if (groups == null) {
			return false;
		}
		if (groups.length == 0) {
			return false;
		}
		for (final String group : groups) {
			if (group.equalsIgnoreCase(groupName)) {
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
	public Set<Player> getUsers(final String groupName)
			throws NoPermissionsPlugin {
		final Set<Player> players = new HashSet<Player>();
		for (final Player player : Utils.getOnlinePlayers()) {
			if (ApiLayer.hasGroup(player.getWorld().getName(),
					CalculableType.USER, player.getName(), groupName)) {
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
	public String getPermissionLimit(final Player p, final String limit) {
		String result = ApiLayer.getValue(p.getWorld().getName(),
				CalculableType.USER, p.getName(), limit);
		if (result == null || (result != null && result.isEmpty())) {
			result = super.getPermissionLimit(p, limit);
		}
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
	public String getPrefix(final Player player) {
		String prefix = super.getPrefix(player);
		if (prefix == null || prefix.isEmpty()) {
			prefix = ApiLayer.getValue(player.getWorld().getName(),
					CalculableType.USER, player.getName(), "prefix");
		}
		return prefix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.SuperPermissions#getSuffix(org.bukkit
	 * .entity.Player)
	 */
	@Override
	public String getSuffix(final Player player) {
		String suffix = super.getSuffix(player);
		if (suffix == null || suffix.isEmpty()) {
			suffix = ApiLayer.getValue(player.getWorld().getName(),
					CalculableType.USER, player.getName(), "suffix");
		}
		return suffix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.SuperPermissions#getGroup(org.bukkit
	 * .entity.Player)
	 */
	@Override
	public Group getGroup(final Player player) {
		final String[] groups = ApiLayer.getGroups(player.getWorld().getName(),
				CalculableType.USER, player.getName());
		if (groups.length == 0) {
			return new Group();
		}
		int maxPriority = Integer.MIN_VALUE;
		String bestGroup = groups[0];
		for (final String group : groups) {
			try {
				final int currentPriority = Integer.parseInt(ApiLayer.getValue(
						player.getWorld().getName(), CalculableType.GROUP,
						group, "priority"));
				if (currentPriority > maxPriority) {
					maxPriority = currentPriority;
					bestGroup = group;
				}
			} catch (final NumberFormatException e) {}
		}
		final String priority = ApiLayer.getValue(player.getWorld().getName(),
				CalculableType.USER, player.getName(), "priority");
		return new Group(bestGroup, priority == null
				|| (priority != null && priority.isEmpty())
				? 0
				: Integer.parseInt(priority));
	}

}
