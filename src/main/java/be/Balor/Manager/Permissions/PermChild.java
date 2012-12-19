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

import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PermChild {
	protected Permission bukkitPerm;

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
		if (permName == null) {
			return;
		}
		if (ACPluginManager.getServer() == null) {
			return;
		}
		if ((bukkitPerm = ACPluginManager.getServer().getPluginManager()
				.getPermission(permName)) != null) {
			bukkitPerm.setDefault(permDefault);
			return;
		}
		bukkitPerm = new Permission(permName, permDefault);
		ACPluginManager.getServer().getPluginManager()
				.addPermission(bukkitPerm);
	}

	/**
	 * @return the permName
	 */
	public String getPermName() {
		return bukkitPerm.getName();
	}

	/**
	 * @return the permDefault
	 */
	public PermissionDefault getPermDefault() {
		return bukkitPerm.getDefault();
	}

	/**
	 * @return the bukkitPerm
	 */
	public Permission getBukkitPerm() {
		return bukkitPerm;
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
		result = prime * result
				+ ((bukkitPerm == null) ? 0 : bukkitPerm.hashCode());
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
		if (!(obj instanceof PermChild)) {
			return false;
		}
		final PermChild other = (PermChild) obj;
		if (bukkitPerm == null) {
			if (other.bukkitPerm != null) {
				return false;
			}
		} else if (!bukkitPerm.equals(other.bukkitPerm)) {
			return false;
		}
		return true;
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

}
