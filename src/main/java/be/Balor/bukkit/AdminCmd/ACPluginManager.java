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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import be.Balor.Manager.CommandManager;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.Metrics;
import be.Balor.Tools.Metrics.Plotter;
import be.Balor.Tools.Debug.ACLogger;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPluginManager {
	private final static ACPluginManager instance = new ACPluginManager();
	private final Map<String, AbstractAdminCmdPlugin> pluginInstances = Collections
			.synchronizedMap(new HashMap<String, AbstractAdminCmdPlugin>());
	private static Server server = null;
	public static Metrics metrics = null;
	public static AbstractAdminCmdPlugin corePlugin;

	/**
	 * @return the instance
	 */
	protected static ACPluginManager getInstance() {
		return instance;
	}

	public static AbstractAdminCmdPlugin getPluginInstance(String name) {
		return getInstance().getPlugin(name);
	}

	/**
	 * Get Bukkit Scheduler
	 * 
	 * @return
	 */
	public static BukkitScheduler getScheduler() {
		return server.getScheduler();
	}

	/**
	 * @return the server
	 */
	public static Server getServer() {
		return server;
	}

	public static void registerACPlugin(AbstractAdminCmdPlugin addon)
			throws IllegalArgumentException {
		getInstance().registerPlugin(addon);
	}

	/**
	 * Register a Plugin Command
	 * 
	 * @param clazz
	 */
	public static void registerCommand(Class<? extends CoreCommand> clazz)
			throws IllegalArgumentException {
		CommandManager.getInstance().registerCommand(clazz);
	}

	/**
	 * Schedule a SyncTask
	 * 
	 * @param task
	 * @return
	 */
	public static int scheduleSyncTask(Runnable task) {
		return server.getScheduler().scheduleSyncDelayedTask(corePlugin, task);
	}

	/**
	 * @param server
	 *            the server to set
	 */
	static void setServer(Server server) {
		ACPluginManager.server = server;
	}

	/**
	 * @param corePlugin
	 *            the corePlugin to set
	 */
	static void setCorePlugin(AbstractAdminCmdPlugin corePlugin) {
		ACPluginManager.corePlugin = corePlugin;
	}

	/**
	 * @return the corePlugin
	 */
	public static AbstractAdminCmdPlugin getCorePlugin() {
		return corePlugin;
	}

	/**
	 * @param metrics
	 *            the metrics to set
	 */
	static void setMetrics(Metrics metrics) {
		ACPluginManager.metrics = metrics;
	}

	public static void unRegisterACPlugin(Plugin addon) {
		if (addon instanceof AbstractAdminCmdPlugin)
			getInstance().unRegisterPlugin((AbstractAdminCmdPlugin) addon);
	}

	private ACPluginManager() {
	}

	/**
	 * Get registered plugin
	 * 
	 * @param name
	 *            name of the addon
	 * @return the addon or null if not registered
	 */
	protected AbstractAdminCmdPlugin getPlugin(String name) {
		return pluginInstances.get(name);
	}

	/**
	 * Register a AdminCmd addon
	 * 
	 * @param addon
	 */
	protected void registerPlugin(final AbstractAdminCmdPlugin addon)
			throws IllegalArgumentException {
		if (!pluginInstances.containsKey(addon.getName())) {
			pluginInstances.put(addon.getName(), addon);
			metrics.addCustomData(corePlugin, new Plotter() {

				@Override
				public int getValue() {
					return 1;
				}

				@Override
				public String getColumnName() {
					return "Addon " + addon.getName();
				}
			});
		} else
			throw new IllegalArgumentException("Plugin " + addon.getName() + " Already registered.");
	}

	void stopChildrenPlugins() {
		ACLogger.info("Disabling all AdminCmd's plugins");
		for (final Entry<String, AbstractAdminCmdPlugin> plugin : pluginInstances.entrySet())
			if (plugin.getValue().isEnabled())
				server.getPluginManager().disablePlugin(plugin.getValue());
	}

	/**
	 * Unregister an AdminCmd addon
	 * 
	 * @param addon
	 */
	protected void unRegisterPlugin(final AbstractAdminCmdPlugin addon) {
		pluginInstances.remove(addon.getName());
		metrics.removeCustomData(corePlugin, new Plotter() {

			@Override
			public int getValue() {
				return 1;
			}

			@Override
			public String getColumnName() {
				return "Addon " + addon.getName();
			}
		});
	}

}
