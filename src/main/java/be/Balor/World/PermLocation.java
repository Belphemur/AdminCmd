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
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/

package be.Balor.World;

import org.bukkit.Location;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class PermLocation extends SimpleLocation {
	private String perm;
	
	public PermLocation(final Location loc, final String perm) {
		super(loc);
		this.perm = perm;
	}
	
	public PermLocation() {
	}
	
	/**
	 * Returns the needed permission for this warp point as a String
	 */
	public String getPerm() {
		return perm;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hash = super.hashCode();
		final int prime = 31;
		hash = prime * hash + ((perm == null) ? 0 : perm.hashCode());
		return hash;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj) {
		final boolean equal = super.equals(obj);
		if (equal) {
			if (!(obj instanceof PermLocation)) {
				return false;
			}
			final PermLocation other = (PermLocation) obj;
			if (perm == null) {
				if (other.perm != null) {
					return false;
				}
			} else if (!other.perm.equals(perm)) {
				return false;
			}
			return true;
		}
		else {
			return false;
		}
	}
}
