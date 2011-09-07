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
package be.Balor.Tools.Configuration;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.util.config.ConfigurationNode;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ExtendedNode extends ConfigurationNode {

	/**
	 * @param root
	 */
	protected ExtendedNode(Map<String, Object> root) {
		super(root);
	}

	/**
	 * Add a Property to the configuration file
	 * 
	 * @param path
	 * @param toAdd
	 * @param override
	 * @return if the property was correctly set.
	 */
	public boolean addProperty(String path, Object toAdd, boolean override) {
		Object property = this.getProperty(path);
		if (property == null || override) {
			this.setProperty(path, toAdd);
			return true;
		}
		return false;

	}

	public boolean addProperty(String path, Object toAdd) {
		return addProperty(path, toAdd, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.util.config.ConfigurationNode#getNode(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ExtendedNode getNode(String path) {
		Object raw = getProperty(path);

		if (raw instanceof Map) {
			return new ExtendedNode((Map<String, Object>) raw);
		}

		return null;
	}

	/**
	 * Create the configuration node in the chosen location if the node don't
	 * exists.
	 * 
	 * @param path
	 *            location of the node
	 * @return the created node or the already existing node.
	 */
	public ExtendedNode createNode(String path) {
		addProperty(path, new HashMap<String, Object>());
		return getNode(path);
	}
}
