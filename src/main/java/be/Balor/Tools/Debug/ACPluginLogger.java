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
package be.Balor.Tools.Debug;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import be.Balor.bukkit.AdminCmd.AbstractAdminCmdPlugin;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPluginLogger {
	private final static Map<AbstractAdminCmdPlugin, WeakReference<ACPluginLogger>> loggers = new HashMap<AbstractAdminCmdPlugin, WeakReference<ACPluginLogger>>();
	private final String prefix;
	protected static final Logger logger = Logger.getLogger("Minecraft");

	/**
	 * @param name
	 */
	public ACPluginLogger(String name) {
		prefix = '[' + name + ']' + ' ';
	}

	public static ACPluginLogger getLogger(AbstractAdminCmdPlugin plugin) {
		final String name = plugin.getName();
		WeakReference<ACPluginLogger> ref = loggers.get(plugin);
		ACPluginLogger log;
		if (ref == null) {
			log = new ACPluginLogger(name);
			loggers.put(plugin, new WeakReference<ACPluginLogger>(log));
			return log;
		}
		log = ref.get();
		if (log == null) {
			// Hashtable holds stale weak reference
			// to a logger which has been GC-ed.
			loggers.remove(plugin);
			log = new ACPluginLogger(name);
			loggers.put(plugin, new WeakReference<ACPluginLogger>(log));
		}
		return log;
	}

	public void severe(String string, Throwable ex) {
		logger.log(Level.SEVERE, prefix + ChatColor.stripColor(ChatColor.stripColor(string)), ex);
	}

	public void severe(String string) {
		logger.log(Level.SEVERE, prefix.concat(ChatColor.stripColor(string)));
	}

	public void info(String string) {
		logger.log(Level.INFO, prefix.concat(ChatColor.stripColor(string)));
	}

	public void warning(String string) {
		logger.log(Level.WARNING, prefix.concat(ChatColor.stripColor(string)));
	}

	public void Log(String txt) {
		logger.log(Level.INFO, String.format(prefix + "%s", ChatColor.stripColor(txt)));
	}

	public void Log(Level loglevel, String txt) {
		Log(loglevel, txt, true);
	}

	public void Log(Level loglevel, String txt, boolean sendReport) {
		logger.log(loglevel,
				String.format(prefix + "%s", txt == null ? "" : ChatColor.stripColor(txt)));
	}

	public void Log(Level loglevel, String txt, Throwable params) {
		if (txt == null) {
			Log(loglevel, params);
		} else {
			logger.log(loglevel,
					String.format(prefix + "%s", txt == null ? "" : ChatColor.stripColor(txt)),
					params);
		}
	}

	public void Log(Level loglevel, Throwable err) {
		logger.log(
				loglevel,
				String.format(prefix + "%s",
						err == null ? "? unknown exception ?" : err.getMessage()), err);
	}

}
