/*************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package be.Balor.Importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.Unicode.UnicodeReader;
import be.Balor.Tools.Files.Unicode.UnicodeUtil;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class ImportTools {

	public static String getPluginsFolder(final File file) {
		final String path = file.getAbsolutePath();
		return path.substring(0, path.lastIndexOf(File.separator));
	}

	public static void copyTextFile(final File sourcefile, final File targeFile)
			throws IOException {
		assert sourcefile != null && targeFile != null;
		final BufferedReader in = new BufferedReader(new UnicodeReader(
				new FileInputStream(sourcefile), "UTF-8"));
		final StringBuffer result = new StringBuffer();
		String temp;
		while ((temp = in.readLine()) != null) {
			result.append(temp + System.getProperty("line.separator"));
		}
		in.close();
		UnicodeUtil.saveUTF8File(targeFile, result.toString(), false);
	}

	public static boolean importESHomes(final ExtendedConfiguration userFile,
			final String playerName) {
		Set<String> nodeList;
		ConfigurationSection home = null, homes = null;
		Location homeLoc = null;
		if (userFile == null) {
			ACLogger.info("[ERROR] Could not import data of user: "
					+ playerName);
			return false;
		}
		homes = userFile.getConfigurationSection("homes");
		if (homes != null) {
			nodeList = homes.getKeys(false);
			for (final String name : nodeList) {
				home = homes.getConfigurationSection(name);
				final ACWorld w = ACWorld.getWorld(home.getString("world"));
				try {
					homeLoc = buildLocation(home, w);
				} catch (final Exception e) {
					ACLogger.info("[ERROR] Could not import homes of user: "
							+ playerName);
					DebugLog.INSTANCE.log(Level.WARNING,
							"[ERROR] Could not import homes of user: "
									+ playerName, e);
					return false;
				}
				ACPlayer.getPlayer(playerName).setHome(name, homeLoc);
			}
		} else {
			if (ConfigEnum.VERBOSE.getBoolean()) {
				ACLogger.info("User " + playerName + " did not have any homes.");
			}
			return false;
		}
		return false;
	}

	public static Location buildLocation(final ConfigurationSection section,
			final ACWorld world) {
		Location loc = null;
		assert section != null && world != null;
		double x = 0D, y = 0D, z = 0D;
		float pitch = 0F, yaw = 0F;
		x = section.getDouble("x");
		y = section.getDouble("y");
		z = section.getDouble("z");
		yaw = Float.parseFloat(section.getString("yaw"));
		pitch = Float.parseFloat(section.getString("pitch"));
		loc = new Location(world.getHandler(), x, y, z, yaw, pitch);
		return loc;
	}

	public static boolean importESLastLocation(
			final ExtendedConfiguration uerFile, final String playerName) {
		Location lastLoc = null;
		ConfigurationSection iLoc = null;
		iLoc = uerFile.getConfigurationSection("lastlocation");
		if (iLoc == null) {
			DebugLog.INSTANCE
					.info("Can find the lastLocation in the configuration file for "
							+ playerName + " in file " + uerFile);
			return false;
		}
		final ACWorld w = ACWorld.getWorld(iLoc.getString("world"));
		try {
			lastLoc = buildLocation(iLoc, w);
		} catch (final Exception e) {
			ACLogger.info("[ERROR] Could not import the last location of user: "
					+ playerName);
			DebugLog.INSTANCE.log(Level.WARNING,
					"[ERROR] Could not import last location of user: "
							+ playerName, e);
			return false;
		}
		ACPlayer.getPlayer(playerName).setLastLocation(lastLoc);
		return true;
	}

}
