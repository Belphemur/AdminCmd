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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACLogger {

	protected static final Logger logger = Logger.getLogger("Minecraft");
	protected static final String prefix = "[AdminCmd] ";

	public static void severe(final String string, final Throwable ex) {
		logger.log(Level.SEVERE, prefix.concat(ChatColor.stripColor(string)),
				ex);
	}

	public static void severe(final String string) {
		logger.log(Level.SEVERE, prefix.concat(ChatColor.stripColor(string)));
	}

	public static void info(final String string) {
		logger.log(Level.INFO, prefix.concat(ChatColor.stripColor(string)));
	}

	public static void warning(final String string) {
		logger.log(Level.WARNING, prefix.concat(ChatColor.stripColor(string)));
	}

	public static void Log(final String txt) {
		logger.log(Level.INFO,
				String.format(prefix + "%s", ChatColor.stripColor(txt)));
	}

	public static void Log(final Level loglevel, final String txt) {
		Log(loglevel, txt, true);
	}

	public static void Log(final Level loglevel, final String txt,
			final boolean sendReport) {
		logger.log(
				loglevel,
				String.format(prefix + "%s",
						txt == null ? "" : ChatColor.stripColor(txt)));
	}

	public static void Log(final Level loglevel, final String txt,
			final Throwable params) {
		if (txt == null) {
			Log(loglevel, params);
		} else {
			logger.log(
					loglevel,
					String.format(prefix + "%s",
							txt == null ? "" : ChatColor.stripColor(txt)),
					params);
		}
	}

	public static void Log(final Level loglevel, final Throwable err) {
		logger.log(loglevel, String.format(prefix + "%s", err == null
				? "? unknown exception ?"
				: err.getMessage()), err);
	}
}
