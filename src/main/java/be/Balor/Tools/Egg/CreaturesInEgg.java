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

import org.bukkit.entity.EntityType;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CreaturesInEgg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3337151993591119472L;
	private EntityType type;
	private byte nb;

	/**
	 * @param type
	 * @param nb
	 */
	public CreaturesInEgg(final EntityType type, final byte nb) {
		super();
		this.type = type;
		this.nb = nb;
	}

	/**
	 * 
	 */
	public CreaturesInEgg() {
		super();
	}

	/**
	 * @return the type
	 */
	public EntityType getType() {
		return type;
	}

	/**
	 * @return the nb
	 */
	public byte getNb() {
		return nb;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final EntityType type) {
		this.type = type;
	}

	/**
	 * @param nb
	 *            the nb to set
	 */
	public void setNb(final byte nb) {
		this.nb = nb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CreaturesInEgg [type=" + type.getName() + ", nb=" + nb + "]";
	}

}
