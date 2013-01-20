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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PermParent extends PermChild {
	protected final String compareName;
	protected String permName;
	protected PermissionDefault permissionDefault;
	private final Map<String, Boolean> children = new HashMap<String, Boolean>();
	private final Set<PermParent> permParentChildren = new HashSet<PermParent>();

	public PermParent(final String perm) {
		this(perm, perm == null ? null : perm.substring(0, perm.length() - 1),
				PermissionDefault.OP);
	}

	public PermParent(final String perm, final String compare,
			final PermissionDefault def) {
		this.compareName = compare;
		this.permissionDefault = def;
		this.permName = perm;
	}

	/**
	 * @return the compareName
	 */
	public String getCompareName() {
		return compareName;
	}

	/**
	 * Add a permission Child to the Permission Parent
	 * 
	 * @param perm
	 * @return the PermParent (this)
	 */
	public PermParent addChild(final PermChild perm)
			throws IllegalArgumentException {
		if (perm.equals(this)) {
			throw new IllegalArgumentException("The Child can't be the parent.");
		}
		children.put(perm.getPermName(), true);
		if (perm instanceof PermParent) {
			permParentChildren.add((PermParent) perm);
		}
		return this;
	}

	/**
	 * Register the permParent.
	 * 
	 * @return
	 */
	public Permission registerPermission() {
		DebugLog.beginInfo("Registering PermParent : " + this.permName);
		try {
			if (this.bukkitPerm != null) {
				return this.bukkitPerm;
			}
			for (final PermParent perm : permParentChildren) {
				perm.registerPermission();
			}
			this.bukkitPerm = new Permission(this.permName,
					this.permissionDefault, children);
			try {
				ACPluginManager.getServer().getPluginManager()
						.addPermission(bukkitPerm);
			} catch (final Exception e) {
				DebugLog.INSTANCE
						.warning("Trying to register an existing PermParent : "
								+ this.permName);
				this.bukkitPerm = Bukkit.getPluginManager().getPermission(
						permName);
				this.bukkitPerm.getChildren().putAll(children);
				this.bukkitPerm.recalculatePermissibles();
			}

			return this.bukkitPerm;
		} finally {
			DebugLog.endInfo();
		}
	}

	/**
	 * Add a permission Child to the Permission Parent
	 * 
	 * @param perm
	 * @return the PermParent (this)
	 */
	public PermParent addChild(final String perm) {
		this.addChild(new PermChild(perm));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((compareName == null) ? 0 : compareName.hashCode());
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
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof PermParent)) {
			return false;
		}
		final PermParent other = (PermParent) obj;
		if (compareName == null) {
			if (other.compareName != null) {
				return false;
			}
		} else if (!compareName.equals(other.compareName)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Permissions.PermChild#getPermDefault()
	 */
	@Override
	public PermissionDefault getPermDefault() {
		return this.permissionDefault;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Permissions.PermChild#getPermName()
	 */
	@Override
	public String getPermName() {
		return this.permName;
	}

}
