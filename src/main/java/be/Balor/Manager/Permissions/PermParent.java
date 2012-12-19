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

import org.bukkit.permissions.PermissionDefault;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PermParent extends PermChild {
	protected final String compareName;

	public PermParent(final String perm) {
		this(perm, perm == null ? null : perm.substring(0, perm.length() - 1),
				PermissionDefault.OP);
	}

	public PermParent(final String perm, final String compare,
			final PermissionDefault def) {
		super(perm, def);
		this.compareName = compare;
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
		perm.bukkitPerm.addParent(bukkitPerm, true);
		return this;
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

}
