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

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Manager.Permissions.Group;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public interface IPermissionPlugin {

	public abstract String getPermissionLimit(Player p, String limit);

	public abstract String getPrefix(Player player);

	public abstract String getSuffix(Player player);

	public abstract Set<Player> getUsers(String groupName)
			throws NoPermissionsPlugin;

	public abstract boolean hasPerm(CommandSender player, Permission perm,
			boolean errorMsg);

	/**
	 * Check the permission with the possibility to disable the error msg
	 * 
	 * @param player
	 * @param perm
	 * @param errorMsg
	 * @return
	 */
	public abstract boolean hasPerm(CommandSender player, String perm,
			boolean errorMsg);

	public abstract boolean isInGroup(String groupName, Player player)
			throws NoPermissionsPlugin;

	/**
	 * Get the group of the player
	 * 
	 * @param player
	 * @return
	 */
	Group getGroup(Player player);
}