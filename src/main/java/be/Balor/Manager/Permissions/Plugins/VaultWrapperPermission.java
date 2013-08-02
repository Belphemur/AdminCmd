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

import net.milkbowl.vault.chat.Chat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Manager.Permissions.Group;

/**
 * @author Antoine
 * 
 */
public class VaultWrapperPermission implements IPermissionPlugin {
	protected net.milkbowl.vault.permission.Permission vaultPerm;
	protected Chat vaultChat;

	/**
	 * @param vaultPerm
	 * @param vaultChat
	 */
	public VaultWrapperPermission(
			final net.milkbowl.vault.permission.Permission vaultPerm,
			final Chat vaultChat) {
		super();
		this.vaultPerm = vaultPerm;
		this.vaultChat = vaultChat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.IPermissionPlugin#getPermissionLimit
	 * (org.bukkit.entity.Player, java.lang.String)
	 */
	@Override
	public String getPermissionLimit(final Player p, final String limit) {
		if (!vaultChat.isEnabled()) {
			return "";
		}
		return vaultChat.getPlayerInfoString(p, "admincmd." + limit, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.IPermissionPlugin#getPrefix(org.
	 * bukkit.entity.Player)
	 */
	@Override
	public String getPrefix(final Player player) {
		if (!vaultChat.isEnabled()) {
			return "";
		}
		return vaultChat.getPlayerPrefix(player);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.IPermissionPlugin#getSuffix(org.
	 * bukkit.entity.Player)
	 */
	@Override
	public String getSuffix(final Player player) {
		if (!vaultChat.isEnabled()) {
			return "";
		}
		return vaultChat.getPlayerSuffix(player);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.IPermissionPlugin#getUsers(java.
	 * lang.String)
	 */
	@Override
	public Set<Player> getUsers(final String groupName)
			throws NoPermissionsPlugin {
		throw new NoPermissionsPlugin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.IPermissionPlugin#hasPerm(org.bukkit
	 * .command.CommandSender, org.bukkit.permissions.Permission, boolean)
	 */
	@Override
	public boolean hasPerm(final CommandSender player, final Permission perm,
			final boolean errorMsg) {
		return hasPerm(player, perm.getName(), errorMsg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.IPermissionPlugin#hasPerm(org.bukkit
	 * .command.CommandSender, java.lang.String, boolean)
	 */
	@Override
	public boolean hasPerm(final CommandSender player, final String perm,
			final boolean errorMsg) {
		if (vaultPerm.has(player, perm)) {
			return true;
		} else if (errorMsg) {
			LocaleManager.sI18n(player, "errorNotPerm", "p", perm);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.IPermissionPlugin#isInGroup(java
	 * .lang.String, org.bukkit.entity.Player)
	 */
	@Override
	public boolean isInGroup(final String groupName, final Player player)
			throws NoPermissionsPlugin {
		return vaultPerm.playerInGroup(player, groupName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Permissions.Plugins.IPermissionPlugin#getGroup(org.bukkit
	 * .entity.Player)
	 */
	@Override
	public Group getGroup(final Player player) {
		return new Group(vaultPerm.getPrimaryGroup(player), player);
	}

}
