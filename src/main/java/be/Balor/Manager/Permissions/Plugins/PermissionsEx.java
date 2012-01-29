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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;

/**
 * @authors Balor, Lathanael
 * 
 */
public class PermissionsEx implements IPermissionPlugin {
	private PermissionManager PEX;

	/**
	 *
	 */
	public PermissionsEx(PermissionManager PEX) {
		this.PEX = PEX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#hasPerm(org.bukkit.command
	 * .CommandSender, java.lang.String, boolean)
	 */
	@Override
	public boolean hasPerm(CommandSender player, String perm, boolean errorMsg) {
		if (!(player instanceof Player))
			return true;
		if (PEX.has((Player) player, perm))
			return true;
		else {
			if (errorMsg)
				Utils.sI18n(player, "errorNotPerm", "p", perm);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#hasPerm(org.bukkit.command
	 * .CommandSender, org.bukkit.permissions.Permission, boolean)
	 */
	@Override
	public boolean hasPerm(CommandSender player, Permission perm, boolean errorMsg) {
		if (!(player instanceof Player))
			return true;
		if (PEX.has((Player) player, perm.getName()))
			return true;
		else {
			if (errorMsg)
				Utils.sI18n(player, "errorNotPerm", "p", perm.getName());
			return false;
		}
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
		PermissionGroup[] groups;
		groups = PEX.getUser(player).getGroups(player.getWorld().getName());
		if (groups.length == 0)
			return false;
		for (PermissionGroup group : groups)
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
	public Set<Player> getUsers(String groupName) throws NoPermissionsPlugin {
		PermissionUser[] users = null;
		users = PEX.getUsers(groupName);
		Set<Player> players = new HashSet<Player>();
		if (users != null) {
			Player player = null;
			for (PermissionUser user : users) {
				player = ACPlayer.getPlayer(user.getName()).getHandler();
				if (player == null)
					continue;
				players.add(player);
			}
			return players;
		}
		return null;
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
		return PEX.getUser(p).getOption("admincmd." + limit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.AbstractPermission#getPrefix(org.bukkit.
	 * entity.Player)
	 */
	@Override
	public String getPrefix(Player player) {
		PermissionUser user = PEX.getUser(player);
		if (user != null)
			return user.getPrefix() == null ? "" : user.getPrefix();

		String prefix = "";
		for (PermissionGroup group : PEX.getUser(player).getGroups())
			if ((prefix = group.getPrefix()) != null && !prefix.isEmpty())
				break;
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
	public String getSuffix(Player player) {
		PermissionUser user = PEX.getUser(player);
		if (user != null)
			return user.getSuffix() == null ? "" : user.getSuffix();

		String suffix = "";
		for (PermissionGroup group : PEX.getUser(player).getGroups())
			if ((suffix = group.getSuffix()) != null && !suffix.isEmpty())
				break;
		return suffix;
	}

}
