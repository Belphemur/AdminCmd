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
package be.Balor.Manager.Permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PermChild {
	protected final String permName;
	protected final PermissionDefault permissionDefault;
	protected PermParent parent = null;
	protected CoreCommand pluginCommand = null;

	public PermChild(final String permName) {
		this(permName, PermissionDefault.OP);
	}

	/**
	 * @param permName
	 * @param parent
	 * @param value
	 * @param permDefault
	 */
	public PermChild(final String permName, final PermissionDefault permDefault) {
		DebugLog.beginInfo("Creation of a PermChild for : " + permName);
		try {
			this.permName = permName;
			this.permissionDefault = permDefault;
		} finally {
			DebugLog.endInfo();
		}
	}

	public boolean hasPermission(final CommandSender player) {
		return PermissionManager.hasPerm(player, this);
	}

	/**
	 * @param pluginCommand
	 *            the pluginCommand to set
	 */
	public void setPluginCommand(final CoreCommand pluginCommand) {
		this.pluginCommand = pluginCommand;
	}

	/**
	 * @return the pluginCommand
	 */
	public CoreCommand getPluginCommand() {
		return pluginCommand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PermChild [getPermName()=" + getPermName() + ", getPermDefault()=" + getPermDefault() + "]";
	}

	/**
	 * @return the parent
	 */
	public PermParent getParent() {
		return parent;
	}

	/**
	 * @return the permName
	 */
	public String getPermName() {
		return permName;
	}

	/**
	 * @return the permissionDefault
	 */
	public PermissionDefault getPermDefault() {
		return permissionDefault;
	}

	/**
	 * @return the bukkitPerm
	 */
	public Permission getBukkitPerm() {
		DebugLog.beginInfo("Creation of a BukkitPerm for : " + permName);
		try {
			if (permName == null) {
				return null;
			}
			Permission bukkitPerm;
			if ((bukkitPerm = ACPluginManager.getServer().getPluginManager().getPermission(permName)) != null) {
				bukkitPerm.setDefault(permissionDefault);
				return bukkitPerm;
			}
			bukkitPerm = new Permission(permName, permissionDefault);
			ACPluginManager.getServer().getPluginManager().addPermission(bukkitPerm);
			return bukkitPerm;
		} finally {
			DebugLog.endInfo();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((permName == null) ? 0 : permName.hashCode());
		result = prime * result + ((permissionDefault == null) ? 0 : permissionDefault.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PermChild other = (PermChild) obj;
		if (permName == null) {
			if (other.permName != null) {
				return false;
			}
		} else if (!permName.equals(other.permName)) {
			return false;
		}
		if (permissionDefault != other.permissionDefault) {
			return false;
		}
		return true;
	}

}
