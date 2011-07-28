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
package be.Balor.Manager;

import org.bukkit.util.config.Configuration;

import be.Balor.Tools.FilesManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ConfigurationManager {
	private Configuration conf;

	/**
	 * 
	 */
	public ConfigurationManager(Configuration conf) {
		this.conf = conf;
		conf.load();
	}

	public ConfigurationManager(String path) {
		this.conf = FilesManager.getInstance().getYml(path);
		conf.load();
	}
	public ConfigurationManager(String fileName, String directory) {
		this.conf = FilesManager.getInstance().getYml(fileName, directory);
		conf.load();
	}
	/**
	 * Load the config
	 */
	public void load() {
		conf.load();
	}

	/**
	 * Save the config
	 */
	public void save() {
		conf.save();
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
		Object property = conf.getProperty(path);
		if (property == null || override) {
			conf.setProperty(path, toAdd);
			return true;
		}
		return false;

	}

	public boolean addProperty(String path, Object toAdd) {
		return addProperty(path, toAdd, false);
	}

	/**
	 * @return the conf
	 */
	public final Configuration getConf() {
		return conf;
	}
}
