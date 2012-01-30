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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PermParent extends PermChild {
	protected final String compareName;
	protected final Set<PermChild> children = new HashSet<PermChild>();
	public final static PermParent ROOT = new PermParent(null);
	public final static PermParent ALONE = new PermParent(null);

	public PermParent(String perm) {
		this(perm, perm == null ? null : perm.substring(0, perm.length() - 1),
				PermissionDefault.OP);
	}

	public PermParent(String perm, String compare, PermissionDefault def) {
		super(perm, true, def);
		this.compareName = compare;
	}

	/**
	 * @return the compareName
	 */
	public String getCompareName() {
		return compareName;
	}

	/**
	 * @return the permName
	 */
	public String getPermName() {
		return permName;
	}

	/**
	 * Add a permission Child to the Permission Parent
	 * 
	 * @param perm
	 * @return the PermParent (this)
	 */
	public PermParent addChild(PermChild perm) throws IllegalArgumentException {
		if (perm.equals(this))
			throw new IllegalArgumentException("The Child can't be the parent.");
		children.add(perm);
		perm.parent = this;
		if (!(perm instanceof PermParent))
			perm.registerPermission();
		return this;
	}

	/**
	 * Add a permission Child to the Permission Parent
	 * 
	 * @param perm
	 * @return the PermParent (this)
	 */
	public PermParent addChild(String perm) {
		PermChild child = new PermChild(perm);
		child.registerPermission();
		child.parent = this;
		children.add(child);
		return this;
	}

	/**
	 * @return the children to be registered by the bukkit API.
	 */
	private Map<String, Boolean> getChildren() {
		Map<String, Boolean> childrenMap = new LinkedHashMap<String, Boolean>();
		for (PermChild child : children)
			childrenMap.put(child.getPermName(), child.isSet());
		return childrenMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Permissions.PermChild#registerPermission()
	 */
	@Override
	void registerPermission() {
		if (registered)
			return;
		if (permName == null && !this.equals(ROOT) && !this.equals(ALONE))
			return;
		for (PermChild child : children)
			child.registerPermission();
		if (permName == null)
			return;
		final Permission perm = ACPluginManager.getServer().getPluginManager()
				.getPermission(permName);
		if (perm == null)
			ACPluginManager.getServer().getPluginManager()
					.addPermission(new Permission(permName, permDefault, getChildren()));
		else
			perm.getChildren().putAll(getChildren());
		registered = true;
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
		result = prime * result + ((compareName == null) ? 0 : compareName.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof PermParent))
			return false;
		PermParent other = (PermParent) obj;
		if (compareName == null) {
			if (other.compareName != null)
				return false;
		} else if (!compareName.equals(other.compareName))
			return false;
		return true;
	}

}
