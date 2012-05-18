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

import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

import be.Balor.Tools.Utils;
import be.Balor.Tools.WebBrowser;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class WebBrowsingTask implements Runnable {
	private final WebBrowser browser;
	private final static Random random = new Random(System.nanoTime());

	/**
	 * @param browser
	 */
	public WebBrowsingTask(final WebBrowser browser) {
		super();
		this.browser = browser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {

			DebugLog.INSTANCE.info("WebPage opened");
			for (final Entry<String, String> entry : browser.openPage().entrySet())
				DebugLog.INSTANCE.info(entry.getKey() + " -> " + entry.getValue());
		} catch (final Exception e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Problem with the web browser", e);
		}
		ACPluginManager.getScheduler().scheduleAsyncDelayedTask(ACPluginManager.getCorePlugin(),
				this, (long) ((random.nextDouble() + 1) * 1800L * Utils.secInTick));
	}
}
