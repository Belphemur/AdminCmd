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

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public interface ExConfigurationSection extends ConfigurationSection {
	/**
	 * Create an entry in the {@link ExConfigurationSection} if it not existing
	 * else return the existing one.
	 * 
	 * @param path
	 *            Path to create/get the entry at.
	 * @return false if the path already exists, true if it's not set.
	 */
	public boolean add(String path, Object value);

	/**
	 * Create a {@link ExConfigurationSection} if it not existing else return
	 * the existing one.
	 * 
	 * @param path
	 *            Path to create/get the section at.
	 * @return the ConfigurationSection
	 */
	public ExConfigurationSection addSection(String path);

	@Override
	public ExConfigurationSection createSection(String path);

	/**
	 * Gets a list of booleans. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual list and cannot be just a
	 * boolean,
	 * 
	 * @param path
	 *            path to node (dot notation)
	 * @param def
	 *            default value or null for an empty list as default
	 * @return list of integers
	 */
	public List<Boolean> getBooleanList(String path, List<Boolean> def);

	@Override
	public ExConfigurationSection getConfigurationSection(String path);

	/**
	 * Gets a list of doubles. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual list and cannot be just a
	 * double.
	 * 
	 * @param path
	 *            path to node (dot notation)
	 * @param def
	 *            default value or null for an empty list as default
	 * @return list of integers
	 */
	public List<Double> getDoubleList(String path, List<Double> def);

	/**
	 * Gets a list of integers. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual list and not just an
	 * integer.
	 * 
	 * @param path
	 *            path to node (dot notation)
	 * @param def
	 *            default value or null for an empty list as default
	 * @return list of integers
	 */
	public List<Integer> getIntList(String path, List<Integer> def);

	/**
	 * Gets a list of strings. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. If an item in the list is not a string, it will be
	 * converted to a string. The node must be an actual list and not just a
	 * string.
	 * 
	 * @param path
	 *            path to node (dot notation)
	 * @param def
	 *            default value or null for an empty list as default
	 * @return list of strings
	 */
	public List<String> getStringList(String path, List<String> def);

	/**
	 * Shortcut to remove an item by setting it null
	 * 
	 * @param path
	 *            Path to remove the entry at.
	 */
	public void remove(String path);

}
