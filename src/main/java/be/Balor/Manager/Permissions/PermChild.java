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
	protected final String permName;
	protected PermParent parent;
	protected final boolean set;
	protected final PermissionDefault permDefault;
	protected boolean registered = false;
	protected Permission bukkitPerm;

	public PermChild(String permName) {
		this(permName, PermissionDefault.OP);
	}

	/**
	 * 
	 */
	protected PermChild(String permName, PermParent parent, PermissionDefault permDefault) {
		this.permName = permName;
		this.parent = parent;
		this.set = true;
		this.permDefault = permDefault;
	}

	public PermChild(String permName, PermissionDefault permDefault) {
		this(permName, true, permDefault);
	}

	/**
	 * @param permName
	 * @param parent
	 * @param value
	 * @param permDefault
	 */
	public PermChild(String permName, boolean value, PermissionDefault permDefault) {
		this.permName = permName;
		this.parent = PermParent.ALONE;
		this.set = value;
		this.permDefault = permDefault;
	}

	/**
	 * @return the permName
	 */
	public String getPermName() {
		return permName;
	}

	/**
	 * @return the parent
	 */
	public PermParent getParent() {
		return parent;
	}

	/**
	 * @return the set
	 */
	public boolean isSet() {
		return set;
	}

	/**
	 * @return the permDefault
	 */
	public PermissionDefault getPermDefault() {
		return permDefault;
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
		result = prime * result + ((permDefault == null) ? 0 : permDefault.hashCode());
		result = prime * result + ((permName == null) ? 0 : permName.hashCode());
		result = prime * result + (registered ? 1231 : 1237);
		result = prime * result + (set ? 1231 : 1237);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PermChild))
			return false;
		PermChild other = (PermChild) obj;
		if (permDefault != other.permDefault)
			return false;
		if (permName == null) {
			if (other.permName != null)
				return false;
		} else if (!permName.equals(other.permName))
			return false;
		if (registered != other.registered)
			return false;
		if (set != other.set)
			return false;
		return true;
	}

	/**
	 * Register the permission in the bukkit system.
	 */
	void registerPermission() {
		if (registered)
			return;
		if (permName == null)
			return;
		if (ACPluginManager.getServer() == null)
			return;
		if ((bukkitPerm = ACPluginManager.getServer().getPluginManager().getPermission(permName)) != null) {
			bukkitPerm.setDefault(permDefault);
			return;
		}
		bukkitPerm = new Permission(permName, permDefault);
		ACPluginManager.getServer().getPluginManager().addPermission(bukkitPerm);
		registered = true;
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PermChild [permName=" + permName + ", set=" + set + ", permDefault=" + permDefault
				+ ", registered=" + registered + "]";
	}

}
