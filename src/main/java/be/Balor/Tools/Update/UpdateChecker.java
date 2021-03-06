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
package be.Balor.Tools.Update;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.AdminCmd;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Antoine
 * 
 */
public class UpdateChecker {
	public enum Channel {
		BETA("http://www.admincmd.com/category/beta/feed/") {
			@Override
			public String toString() {
				return "Beta Version";
			}
		},
		STABLE("http://www.admincmd.com/category/stable/feed/") {
			@Override
			public String toString() {
				return "Stable Version";
			}
		};
		/**
		 * 
		 */
		private final String url;

		private Channel(final String url) {
			this.url = url;
		}

		private Document getRss() throws ParserConfigurationException,
				SAXException, IOException {
			final DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			return builder.parse(this.url);

		}
	}

	private Document rss = null;

	private final AdminCmdVersion currentVersion;
	private final Channel channel;

	private AdminCmdVersion rssVersion;

	public UpdateChecker(final Channel channel, final AdminCmd plugin) {
		this.channel = channel;
		currentVersion = new AdminCmdVersion(plugin.getDescription()
				.getVersion());
		try {
			rss = this.channel.getRss();
			scheduleCheck();
		} catch (final Exception e) {
			DebugLog.INSTANCE.log(Level.SEVERE,
					"Can't get the RSS feed for the update checker", e);
		}

	}

	/**
	 * 
	 */
	private void scheduleCheck() {
		ACPluginManager.getScheduler().runTaskTimerAsynchronously(
				ACPluginManager.getCorePlugin(), new Runnable() {

					@Override
					public void run() {
						if (checkForUpdate()) {
							notifyNewVersion();
						}

					}
				}, 0, 48 * 24 * 3600 * Utils.secInTick);

	}

	/**
	 * Check if there is an update on the RSS flux of the plugin.
	 * 
	 * @return
	 */
	public boolean checkForUpdate() {
		try {
			final NodeList rssChannel = rss.getElementsByTagName("channel");
			final Element elementChannel = (Element) rssChannel.item(0);
			final Element rssItem = (Element) elementChannel
					.getElementsByTagName("item").item(0);
			final Node title = rssItem.getElementsByTagName("title").item(0);
			final Node node = title;
			rssVersion = new AdminCmdVersion(node.getTextContent());
			DebugLog.INSTANCE.fine("This version : " + currentVersion);
			DebugLog.INSTANCE.fine("Distant version found : " + rssVersion);
			return rssVersion.isNewerThan(currentVersion);

		} catch (final Exception e) {
			DebugLog.INSTANCE.log(Level.WARNING,
					"Problem while checking the RSS flux", e);
			return false;
		}
	}

	private void notifyNewVersion() {
		ACPluginManager.scheduleSyncTask(new Runnable() {

			@Override
			public void run() {
				final Map<String, String> replace = new HashMap<String, String>();
				replace.put("version", rssVersion.getVersion());
				final String message = LocaleHelper.NEW_VERSION
						.getLocale(replace);
				ACLogger.warning(message);
				Bukkit.getServer().broadcast(message,
						"admincmd.spec.versionbcast");
			}
		});

	}

}
