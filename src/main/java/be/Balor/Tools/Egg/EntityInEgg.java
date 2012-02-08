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


/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class EntityInEgg {
	private int entity;
	private int nb;
	/**
	 * @param entity
	 * @param nb
	 */
	public EntityInEgg(int entity, int nb) {
		super();
		this.entity = entity;
		this.nb = nb;
	}
	/**
	 * 
	 */
	public EntityInEgg() {
		super();
	}
	/**
	 * @return the entity
	 */
	public int getEntity() {
		return entity;
	}
	/**
	 * @return the nb
	 */
	public int getNb() {
		return nb;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(int entity) {
		this.entity = entity;
	}
	/**
	 * @param nb the nb to set
	 */
	public void setNb(int nb) {
		this.nb = nb;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EntityInEgg [entity=" + entity + ", nb=" + nb + "]";
	}
	
	

}
