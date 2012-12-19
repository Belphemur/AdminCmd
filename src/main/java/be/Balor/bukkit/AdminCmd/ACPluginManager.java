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

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import be.Balor.Manager.CommandManager;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Metrics.Metrics;
import be.Balor.Tools.Metrics.Metrics.Graph;
import be.Balor.Tools.Metrics.Metrics.Plotter;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPluginManager {
	private final static ACPluginManager instance = new ACPluginManager();
	private final Map<String, AbstractAdminCmdPlugin> pluginInstances = Collections
			.synchronizedMap(new HashMap<String, AbstractAdminCmdPlugin>());
	private final static Server server = Bukkit.getServer();
	private static AbstractAdminCmdPlugin corePlugin;
	private static Graph graph = null;

	/**
	 * @return the instance
	 */
	protected static ACPluginManager getInstance() {
		return instance;
	}

	public static AbstractAdminCmdPlugin getPluginInstance(final String name) {
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

	public static void registerACPlugin(final AbstractAdminCmdPlugin addon)
			throws IllegalArgumentException {
		getInstance().registerPlugin(addon);
	}

	/**
	 * Register a Plugin Command
	 * 
	 * @param clazz
	 */
	public static void registerCommand(final Class<? extends CoreCommand> clazz)
			throws IllegalArgumentException {
		CommandManager.getInstance().registerCommand(clazz);
	}

	/**
	 * Schedule a SyncTask
	 * 
	 * @param task
	 * @return
	 */
	public static int scheduleSyncTask(final Runnable task) {
		return server.getScheduler().scheduleSyncDelayedTask(corePlugin, task);
	}

	/**
	 * Schedule a AsyncDelayedTask
	 * 
	 * @param task
	 * @return
	 */
	public static int scheduleAsyncDelayedTask(final Runnable task) {
		return server.getScheduler().scheduleAsyncDelayedTask(corePlugin, task);
	}

	/**
	 * @param corePlugin
	 *            the corePlugin to set
	 */
	static void setCorePlugin(final AbstractAdminCmdPlugin corePlugin) {
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
	static void setMetrics(final Metrics metrics) {
		ACPluginManager.graph = metrics.createGraph("Plugins");
	}

	public static void unRegisterACPlugin(final Plugin addon) {
		if (addon instanceof AbstractAdminCmdPlugin) {
			getInstance().unRegisterPlugin((AbstractAdminCmdPlugin) addon);
		}
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
	protected AbstractAdminCmdPlugin getPlugin(final String name) {
		return pluginInstances.get(name);
	}

	/**
	 * Register a AdminCmd addon
	 * 
	 * @param addon
	 */
	protected void registerPlugin(final AbstractAdminCmdPlugin addon)
			throws IllegalArgumentException {
		if (!pluginInstances.containsKey(addon.getAddonName())) {
			pluginInstances.put(addon.getAddonName(), addon);
			DebugLog.INSTANCE.info("Registering : " + addon);
			if (corePlugin == null || addon.equals(corePlugin)) {
				return;
			}
			graph.addPlotter(new Plotter() {

				@Override
				public int getValue() {
					return 1;
				}

				@Override
				public String getColumnName() {
					return "Addon " + addon.getAddonName();
				}
			});
		} else {
			throw new IllegalArgumentException("Plugin " + addon.getAddonName()
					+ " Already registered.");
		}
	}

	void stopChildrenPlugins() {
		ACLogger.info("Disabling all AdminCmd's plugins");
		for (final Entry<String, AbstractAdminCmdPlugin> plugin : pluginInstances
				.entrySet()) {
			if (plugin.getValue().isEnabled()) {
				server.getPluginManager().disablePlugin(plugin.getValue());
			}
		}
	}

	/**
	 * Unregister an AdminCmd addon
	 * 
	 * @param addon
	 */
	protected void unRegisterPlugin(final AbstractAdminCmdPlugin addon) {
		pluginInstances.remove(addon.getAddonName());
		if (!addon.equals(corePlugin)) {
			graph.removePlotter(new Plotter() {

				@Override
				public int getValue() {
					return 1;
				}

				@Override
				public String getColumnName() {
					return "Addon " + addon.getAddonName();
				}
			});
		}
	}

}
