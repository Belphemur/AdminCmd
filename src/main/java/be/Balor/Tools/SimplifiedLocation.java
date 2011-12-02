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
package be.Balor.Tools;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SimplifiedLocation extends Location {
	boolean visited = false;

	/**
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public SimplifiedLocation(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public SimplifiedLocation(Location loc) {
		super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
	}

	/**
	 * @param visited
	 *            the visited to set
	 */
	public void setVisited() {
		this.visited = true;
	}

	/**
	 * @return the visited
	 */
	public boolean isVisited() {
		return visited;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.Location#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Location)) {
			return false;
		}
		Location other = (Location) obj;
		return other.getX() == this.getX() && other.getY() == this.getY()
				&& other.getZ() == this.getZ();
	}

}
