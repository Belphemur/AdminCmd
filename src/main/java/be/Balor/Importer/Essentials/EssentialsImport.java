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
 **************************************************************************/

package be.Balor.Importer.Essentials;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import be.Balor.Importer.IImport;
import be.Balor.Importer.ImportTools;
import be.Balor.Importer.SubDirFileFilter;
import be.Balor.Importer.SubDirFileFilter.Type;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.FileManager;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class EssentialsImport implements IImport {

	private final String importPath;
	private final SubDirFileFilter filter = new SubDirFileFilter();

	public EssentialsImport(final String path) {
		importPath = path + File.separator + "Essentials";
	}

	@Override
	public void initImport() {
		int number = 0;
		final File file = new File(importPath);
		if (!file.exists()) {
			ConfigEnum.IMPORT_ESSENTIALS.setValue(false);
			try {
				ConfigEnum.save();
			} catch (final IOException e) {
			}
			ACLogger.info("Import deactivated. Did not find folder 'Essentials' in plugins.");
			return;
		}
		ACLogger.info("Starting import of Essentials data.\n Trying to import User-data.....");
		number = importUserData();
		ACLogger.info("Data of " + number
				+ " user(s) imported. Trying to import spawn point(s)....");
		number = importSpawnPoints();
		ACLogger.info(number
				+ " spawn point(s) imported. Trying to import warp point(s)....");
		number = importWarpPoints();
		ACLogger.info(number
				+ "Warp point(s) imported. Trying to import text-files....");
		try {
			importTextFiles();
		} catch (final IOException e) {
			ACLogger.info("[ERROR] Failed to import rules.txt and motd.txt. For more information refer to the debug.log!");
			DebugLog.INSTANCE.log(Level.INFO,
					"[ERROR] Failed to import rules.txt and motd.txt.", e);
			return;
		}
		ACLogger.info("Text files imported.\n Import finished successfully, deactivating"
				+ "import-option in the configuration file...");
		ConfigEnum.IMPORT_ESSENTIALS.setValue(false);
		try {
			ConfigEnum.save();
		} catch (final IOException e) {
		}
		ACLogger.info("Import deactivated, have fun with your old data in AdminCmd.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Importer.IImport#importUserData()
	 */
	@Override
	public int importUserData() {
		int counter = 0;
		final List<File> files = filter.getFiles(new File(importPath
				+ File.separator + "userdata"), filter.new PatternFilter(
				Type.FILE, ".yml"), false);
		ExtendedConfiguration esUserFile = null;
		String playerName;
		boolean homes = false, lastLoc = false, connect = false, newPlayer = false, ip = false, mute = false, god = false;
		for (final File f : files) {
			playerName = f.getName().substring(0, f.getName().length() - 4);
			ACPlayer p = ACPlayer.getPlayer(playerName);
			if (p == null) {
				playerName = playerName.substring(0, 1).toUpperCase()
						+ playerName.substring(1);
			}
			p = ACPlayer.getPlayer(playerName);
			esUserFile = ExtendedConfiguration.loadConfiguration(f);
			homes = ImportTools.importESHomes(esUserFile, playerName);
			lastLoc = ImportTools.importESLastLocation(esUserFile, playerName);
			if (p != null && esUserFile != null) {
				p.setInformation("lastConnection",
						esUserFile.getLong("timestamps.login"));
				p.setInformation("lastDisconnect",
						esUserFile.getLong("timestamps.logout"));
				connect = true;
				p.setInformation("firstTime",
						esUserFile.getBoolean("newplayer"));
				newPlayer = true;
				p.setInformation("last-ip", esUserFile.getString("ipAddress"));
				ip = true;
				if (esUserFile.getBoolean("muted")) {
					p.setPower(be.Balor.Tools.Type.MUTED,
							"Permanently muted by Server Admin");
					mute = true;
				}
				if (esUserFile.getBoolean("godmode")) {
					p.setPower(be.Balor.Tools.Type.GOD);
					god = true;
				}
			}
			if (homes || lastLoc || connect || ip || newPlayer || mute || god) {
				counter++;
			}
		}
		return counter;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Importer.IImport#importWarpPoints()
	 */
	@Override
	public int importWarpPoints() {
		List<File> files = filter.getFiles(new File(importPath + File.separator
				+ "warps"), filter.new PatternFilter(Type.FILE, ".yml"), false);
		ExtendedConfiguration esWarp = null;
		Location acWarp = null;
		ACWorld world = null;
		String name = "";
		int counter = 0;

		for (final File f : files) {
			esWarp = ExtendedConfiguration.loadConfiguration(f);
			if (esWarp == null) {
				ACLogger.info("[ERROR] Could not import WarpPoint: "
						+ f.getName().substring(0, f.getName().length() - 4));
				continue;
			}
			world = ACWorld.getWorld(esWarp.getString("world"));
			if (world == null) {
				continue;
			}
			try {
				name = esWarp.getString("name");
				acWarp = ImportTools.buildLocation(esWarp, world);
			} catch (final Exception e) {
				ACLogger.info("[ERROR] Could not import WarpPoint: "
						+ f.getName().substring(0, f.getName().length() - 4));
				continue;
			}
			world.addWarp(name, acWarp);
			counter++;
		}
		files = filter.getFiles(new File(importPath), filter.new PatternFilter(
				Type.FILE, ".yml"), false);
		ExtendedConfiguration esJails = null;
		for (final File f : files) {
			if (!f.getName().contains("jails")) {
				continue;
			}
			esJails = ExtendedConfiguration.loadConfiguration(f);
			if (esJails == null) {
				ACLogger.info("[ERROR] Could not import Jails.");
				continue;
			}
			ConfigurationSection jails = null;
			jails = esJails.getConfigurationSection("jails");
			if (jails == null) {
				ACLogger.info("[ERROR] Could not import Jails.");
				continue;
			}
			final Set<String> keys = jails.getKeys(false);
			if (keys == null) {
				ACLogger.info("[ERROR] Could not import Jails.");
				continue;
			}
			ConfigurationSection jail = null;
			for (final String jName : keys) {
				if (jName == null) {
					ACLogger.info("[ERROR] Could not import jail "
							+ String.valueOf(jName));
					continue;
				}
				jail = jails.getConfigurationSection(jName);
				world = ACWorld.getWorld(esWarp.getString("world"));
				if (world == null) {
					continue;
				}
				try {
					acWarp = ImportTools.buildLocation(jail, world);
				} catch (final Exception e) {
					ACLogger.info("[ERROR] Could not import jail: " + jName);
					continue;
				}
				world.addWarp("jail:" + jName, acWarp);
				counter++;
			}
		}
		return counter;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Importer.IImport#importSpawnPoints()
	 */
	@Override
	public int importSpawnPoints() {
		final List<File> files = filter.getFiles(new File(importPath),
				filter.new PatternFilter(Type.FILE, ".yml"), false);
		ExtendedConfiguration esSpawns = null;
		ConfigurationSection esSpawn = null;
		Location acSpawn = null;
		ACWorld world = null;
		int counter = 0;
		for (final File f : files) {
			if (!f.getName().contains("spawn")) {
				continue;
			}
			esSpawns = ExtendedConfiguration.loadConfiguration(f);
			esSpawn = esSpawns.getConfigurationSection("spawns");
			if (esSpawn == null) {
				ACLogger.info("[ERROR] Could not import Spawns.");
				continue;
			}
			final Set<String> keys = esSpawn.getKeys(false);
			if (keys == null) {
				ACLogger.info("[ERROR] Could not import Spawns.");
				continue;
			}
			ConfigurationSection spawn = null;
			for (final String sName : keys) {
				if (sName == null) {
					ACLogger.info("[ERROR] Could not import spawn "
							+ String.valueOf(sName));
					continue;
				}
				spawn = esSpawn.getConfigurationSection(sName);
				world = ACWorld.getWorld(spawn.getString("world"));
				if (world == null) {
					continue;
				}
				try {
					acSpawn = ImportTools.buildLocation(spawn, world);
				} catch (final Exception e) {
					ACLogger.info("[ERROR] Could not import spawn: " + sName);
					continue;
				}
				if (sName.equalsIgnoreCase("default")) {
					world.setSpawn(acSpawn);
				} else {
					world.setGroupSpawn(sName, acSpawn);
				}
				counter++;
			}
		}
		return counter;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Importer.IImport#importTextFiles()
	 */
	@Override
	public void importTextFiles() throws IOException {
		final List<File> files = filter.getFiles(new File(importPath),
				filter.new PatternFilter(Type.FILE, ".txt"), false);
		for (final File f : files) {
			if (f.getName().contains("rules")) {
				ImportTools.copyTextFile(
						f, FileManager.getInstance().getFile(ACHelper.getInstance()
								.getCoreInstance().getDataFolder().getAbsolutePath(),
								"rules.txt"));
			} else if (f.getName().contains("motd")) {
				ImportTools.copyTextFile(
						f, FileManager.getInstance().getFile(ACHelper.getInstance()
								.getCoreInstance().getDataFolder().getAbsolutePath(),
								"motd.txt"));
			} else {
				continue;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Importer.IImport#importOtherFiles()
	 */
	@Override
	public void importOtherFiles() {
	}
}
