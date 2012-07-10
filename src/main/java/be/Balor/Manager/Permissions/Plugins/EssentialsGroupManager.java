/*************************************************************************
 * Copyright (C) 2012 Philippe Leipold
 *
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

package be.Balor.Manager.Permissions.Plugins;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import be.Balor.Manager.Permissions.Group;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class EssentialsGroupManager extends SuperPermissions {

	private final GroupManager groupManager;

	public EssentialsGroupManager(final Plugin manager) {
		groupManager = (GroupManager) manager;
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
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder()
				.getWorldPermissions(player);
		if (handler == null) {
			return false;
		}
		return handler.inGroup(player.getName(), groupName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getPrefix(org.bukkit.
	 * entity.Player)
	 */
	@Override
	public String getPrefix(final Player base) {
		final String prefix = super.getPrefix(base);
		if (prefix == null || prefix.isEmpty()) {
			final AnjoPermissionsHandler handler = groupManager
					.getWorldsHolder().getWorldPermissions(base);
			if (handler == null) {
				return "";
			}
			return handler.getUserPrefix(base.getName());
		}
		return prefix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.IPermissionPlugin#getSuffix(org.bukkit.entity
	 * .Player)
	 */
	@Override
	public String getSuffix(final Player base) {
		final String suffix = super.getSuffix(base);
		if (suffix == null || suffix.isEmpty()) {
			final AnjoPermissionsHandler handler = groupManager
					.getWorldsHolder().getWorldPermissions(base);
			if (handler == null) {
				return "";
			}
			return handler.getUserSuffix(base.getName());
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
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder()
				.getWorldPermissions(player);
		final String group = handler.getGroup(player.getName());
		if (group == null) {
			return new Group();
		}
		return new Group(group, player);
	}
}
