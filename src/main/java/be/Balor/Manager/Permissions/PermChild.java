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

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PermChild [getPermName()=" + getPermName()
				+ ", getPermDefault()=" + getPermDefault() + "]";
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
			if ((bukkitPerm = ACPluginManager.getServer().getPluginManager()
					.getPermission(permName)) != null) {
				bukkitPerm.setDefault(permissionDefault);
				return bukkitPerm;
			}
			bukkitPerm = new Permission(permName, permissionDefault);
			ACPluginManager.getServer().getPluginManager()
					.addPermission(bukkitPerm);
			return bukkitPerm;
		} finally {
			DebugLog.endInfo();
		}
	}

}
