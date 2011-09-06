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
package be.Balor.Tools.Files;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import be.Balor.Tools.Type;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public interface DataManager {
	/**
	 * Load the map
	 * 
	 * @param type
	 * @param directory
	 * @param filename
	 * @return
	 */
	public Map<String, Object> loadMap(Type type, String directory, String filename);

	/**
	 * Return a string List containing all locations names
	 * 
	 * @param user
	 * @param table
	 * @return
	 */
	public List<String> getKeys(String info, String user, String table);

	/**
	 * Remove the given location
	 * 
	 * @param property
	 * @param filename
	 * @param directory
	 */
	public void removeKey(String property, String filename, String directory);

	/**
	 * Return the location
	 * 
	 * @param property
	 * @param filename
	 * @param directory
	 * @return
	 */
	public Location getLocation(String property, String filename, String directory)
			throws WorldNotLoaded;

	/**
	 * Store the location informations
	 * 
	 * @param loc
	 * @param filename
	 * @param directory
	 */
	public void writeLocation(Location loc, String name, String filename, String directory);

}
