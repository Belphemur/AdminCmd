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
package be.Balor.bukkit.AdminCmd;

import java.util.HashMap;

import org.bukkit.Server;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PluginInstance {
	private static PluginInstance instance;
	private HashMap<String, AbstractAdminCmdPlugin> pluginInstances = new HashMap<String, AbstractAdminCmdPlugin>();
	private static Server server = null;

	private PluginInstance() {

	}

	/**
	 * @return the instance
	 */
	public static PluginInstance getInstance() {
		if (instance == null)
			instance = new PluginInstance();
		return instance;
	}

	/**
	 * @return the server
	 */
	public static Server getServer() {
		return server;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public static void setServer(Server server) {
		PluginInstance.server = server;
	}

	/**
	 * Get registered plugin
	 * 
	 * @param name
	 *            name of the addon
	 * @return the addon or null if not registered
	 */
	public AbstractAdminCmdPlugin getPluginInstance(String name) {
		return pluginInstances.get(name);
	}

	/**
	 * Register a AdminCmd addon
	 * 
	 * @param addon
	 */
	public void registerACPlugin(AbstractAdminCmdPlugin addon) {
		pluginInstances.put(addon.getName(), addon);
	}

}
