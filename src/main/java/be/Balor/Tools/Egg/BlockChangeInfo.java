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
package be.Balor.Tools.Egg;

import java.io.Serializable;

import org.bukkit.Material;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class BlockChangeInfo implements Serializable {

	private static final long serialVersionUID = 5071733512565352011L;
	private int blockTypeId;
	private int radius;

	/**
	 * @param blockTypeId
	 * @param radius
	 */
	public BlockChangeInfo(final int blockTypeId, final int radius) {
		this.blockTypeId = blockTypeId;
		this.radius = radius;
	}

	/**
 * 
 */
	public BlockChangeInfo() {
	}

	/**
	 * @return the blockTypeId
	 */
	public int getBlockTypeId() {
		return blockTypeId;
	}

	/**
	 * @return the radius
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * @param blockTypeId
	 *            the blockTypeId to set
	 */
	public void setBlockTypeId(final int blockTypeId) {
		this.blockTypeId = blockTypeId;
	}

	/**
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(final int radius) {
		this.radius = radius;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BlockChangeInfo [b=" + Material.getMaterial(blockTypeId)
				+ ", r=" + radius + "]";
	}

}
