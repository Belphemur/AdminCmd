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

	/**
	 * Gets a long at a location. This will either return an long or the default
	 * value. If the object at the particular location is not actually a long,
	 * the default value will be returned.
	 * 
	 * @param def
	 *            default value
	 * @return boolean or default
	 */
	public long getLong(String path, long def) {
		Long o = castLong(getProperty(path));
		if (o == null) {
			setProperty(path, def);
			return def;
		}
		return o;
	}

	/**
	 * Casts a value to a long. May return null.
	 * 
	 * @param o
	 * @return
	 */
	private static Long castLong(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Long) {
			return (Long) o;
		} else if (o instanceof Float) {
			return Long.parseLong(((Float) o).toString());
		} else if (o instanceof Double) {
			return Long.parseLong(((Double) o).toString());
		} else if (o instanceof Byte) {
			return Long.parseLong(((Byte) o).toString());
		} else if (o instanceof Integer) {
			return Long.parseLong(((Integer) o).toString());
		} else {
			return null;
		}
	}

}
