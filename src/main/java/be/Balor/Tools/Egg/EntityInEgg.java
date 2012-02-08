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

import org.bukkit.entity.Chicken;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class EntityInEgg {
	private String entityClass;
	private int nb;
	private String entityName;

	/**
	 * @param entityClass
	 * @param nb
	 * @param entityName
	 */
	public EntityInEgg(String entityClass, int nb, String entityName) {
		super();
		this.entityClass = entityClass;
		this.nb = nb;
		this.entityName = entityName;
	}

	/**
	 * 
	 */
	public EntityInEgg() {
		super();
	}

	/**
	 * @return the entityClass
	 */
	public String getEntityClassName() {
		return entityClass;
	}

	/**
	 * @return the nb
	 */
	public int getNb() {
		return nb;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityClass
	 *            the entityClass to set
	 */
	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * @param nb
	 *            the nb to set
	 */
	public void setNb(int nb) {
		this.nb = nb;
	}

	/**
	 * @param entityName
	 *            the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	@SuppressWarnings("rawtypes")
	public Class getEntityClass() {
		try {
			return Class.forName(entityClass);
		} catch (ClassNotFoundException e) {
			return Chicken.class;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EntityInEgg [nb=" + nb + ", entity=" + entityName + "]";
	}

}
