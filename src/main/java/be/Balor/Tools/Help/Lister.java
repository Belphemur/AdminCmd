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
package be.Balor.Tools.Help;

import java.io.File;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import be.Balor.Tools.Files.FilesManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Lister {
	private static Lister instance = null;
	private HashMap<String, HelpList> plugins = new HashMap<String, HelpList>();

	private Lister() {
	}

	/**
	 * @return the instance
	 */
	public static Lister getInstance() {
		if (instance == null)
			instance = new Lister();
		return instance;
	}

	/**
	 * Add a plugin to the lister
	 * 
	 * @param plugin
	 */
	public void addPlugin(Plugin plugin) {
		HelpList toAdd = new HelpList(plugin);
		plugins.put(toAdd.getPluginName(), toAdd);
	}
}
