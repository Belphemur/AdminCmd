/************************************************************************
Copyright (C) 2011 jblaske

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

NOTE: The modification of this code could result in improper tracking of
statistics, stability of the server running your plugins or the statistics
website. Please do not modify this file without verification of the author.
 ************************************************************************/
package be.Balor.Tools;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import be.Balor.Tools.Debug.LogFormatter;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ping {

	private static final File configFile = new File("plugins/PluginStats/config.yml");
	private static final String logFile = "plugins/PluginStats/log.txt";
	private static final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
	private static Logger logger = null;
	private static Pinger pinger;

	public static void init(Plugin plugin) {	
		if (configExists() && logExists() && !config.getBoolean("opt-out")) {
			pinger = new Pinger(plugin, config.getString("guid"), logger);
			plugin.getServer().getScheduler()
					.scheduleAsyncRepeatingTask(plugin, pinger, 10L, 20L * 60L * 60 * 24);
			System.out
					.println("["
							+ plugin.getDescription().getName()
							+ "] Stats are being kept for this plugin. To opt-out for any reason, check plugins/PluginStats/config.yml");
		}
	}

	private static Boolean configExists() {
		config.addDefault("opt-out", false);
		config.addDefault("guid", UUID.randomUUID().toString());
		if (!configFile.exists() || config.get("guid", null) == null) {
			System.out
					.println("PluginStats is initializing for the first time. To opt-out for any reason check plugins/PluginStats/config.yml");
			try {
				config.options().copyDefaults(true);
				config.save(configFile);
			} catch (Exception ex) {
				System.out.println("Error creating PluginStats configuration file.");
				ex.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private static Boolean logExists() {
		try {
			FileHandler handler = new FileHandler(logFile, true);
			logger = Logger.getLogger("com.randomappdev");
			logger.setUseParentHandlers(false);
			handler.setFormatter(new LogFormatter());
			logger.addHandler(handler);
		} catch (Exception ex) {
			System.out.println("Error creating PluginStats log file.");
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}

class Pinger implements Runnable {
	private final Plugin plugin;
	private String guid, serverName, version, name, main, author, website, pversion;
	private final Logger logger;

	public Pinger(Plugin plugin, String guid, Logger theLogger) {
		this.plugin = plugin;
		this.guid = guid;
		this.logger = theLogger;
		try {
			serverName = URLEncoder.encode(plugin.getServer().getServerName(), "UTF-8");
			version = URLEncoder.encode(plugin.getServer().getVersion(), "UTF-8");
			name = URLEncoder.encode(plugin.getDescription().getName(), "UTF-8");
			main = URLEncoder.encode(plugin.getDescription().getMain(), "UTF-8");
			website = URLEncoder.encode(plugin.getDescription().getWebsite() == null ? "" : plugin
					.getDescription().getWebsite(), "UTF-8");
			pversion = URLEncoder.encode(plugin.getDescription().getVersion(), "UTF-8");
			String authors = "";
			for (String auth : plugin.getDescription().getAuthors()) {
				authors = authors + " " + auth;
			}
			authors = authors.trim();
			author = URLEncoder.encode(authors, "UTF-8");
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void run() {
		try {

			String url = String
					.format("http://pluginstats.randomappdev.com/ping.aspx?snam=%s&sprt=%s&shsh=%s&sver=%s&spcnt=%s&pnam=%s&pmcla=%s&paut=%s&pweb=%s&pver=%s",
							serverName, plugin.getServer().getPort(), guid, version, plugin
									.getServer().getOnlinePlayers().length, name, main, author,
							website, pversion);

			new URL(url).openConnection().getInputStream();
			logger.log(Level.INFO, "PluginStats pinged the central server.");

		} catch (Exception ex) {
			// Fail Silently to avoid console spam.
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

}