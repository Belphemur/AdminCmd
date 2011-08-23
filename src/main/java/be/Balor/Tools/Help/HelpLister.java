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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.plugin.Plugin;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class HelpLister {
	private static HelpLister instance = null;
	private HashMap<String, HelpList> plugins = new HashMap<String, HelpList>();
	private List<String> noCmds = new ArrayList<String>();

	private HelpLister() {
	}

	/**
	 * @return the instance
	 */
	public static HelpLister getInstance() {
		if (instance == null)
			instance = new HelpLister();
		return instance;
	}

	public static void killInstance() {
		instance = null;
	}

	/**
	 * Add a plugin to the lister
	 * 
	 * @param plugin
	 */
	public void addPlugin(Plugin plugin) {
		String pName = plugin.getDescription().getName();
		if (!plugins.containsKey(pName) || !noCmds.contains(pName))
			try {
				plugins.put(pName, new HelpList(plugin));
			} catch (IllegalArgumentException e) {
				noCmds.add(pName);
			}
			
	}

	/**
	 * Add a new helpEntry for the wanted plugin. If the plugin is not found,
	 * add in the database
	 * 
	 * @param command
	 * @param description
	 * @param plugin
	 * @param permissions
	 */
	public void addHelpEntry(String command, String description, String plugin,
			List<String> permissions) {
		HelpList help = plugins.get(plugin);
		if (help == null) {
			help = new HelpList(plugin);
			plugins.put(plugin, help);
		}
		help.addEntry(new HelpEntry(command, description, permissions));
	}
}
