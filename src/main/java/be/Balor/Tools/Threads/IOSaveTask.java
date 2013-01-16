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
package be.Balor.Tools.Threads;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class IOSaveTask implements Runnable {
	private final Queue<ExtendedConfiguration> configurations = new LinkedBlockingQueue<ExtendedConfiguration>();

	/**
	 * Add an ExtendedConfiguration to save
	 * 
	 * @param ex
	 */
	public void addConfigurationToSave(final ExtendedConfiguration ex) {

		if (!configurations.contains(ex)) {
			configurations.add(ex);
			DebugLog.INSTANCE.fine("Added ExtendedConfiguration : " + ex);
		} else {
			DebugLog.INSTANCE.info("ALREADY IN ExtendedConfiguration : " + ex);
		}

	}

	/**
	 * Remove an ExtendedConfiguration to save
	 * 
	 * @param ex
	 * @return
	 */
	public boolean removeConfiguration(final ExtendedConfiguration ex) {

		return configurations.remove(ex);

	}

	@Override
	public void run() {
		DebugLog.INSTANCE.info("Begin Configuration save with "
				+ configurations.size() + " file(s)");
		while (!configurations.isEmpty()) {
			try {
				configurations.poll().save();
			} catch (final Exception e) {
				ACLogger.severe(
						"Problem while saving ExtendedConfiguration file", e);
				DebugLog.INSTANCE.log(Level.SEVERE,
						"Problem while saving config files", e);
			}
		}
		DebugLog.INSTANCE.info("All Configuration File saved.");

	}
}
