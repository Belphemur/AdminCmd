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

import org.apache.commons.io.FilenameUtils;
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
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class EssentialsImport implements IImport {

	private String importPath;
	private SubDirFileFilter filter = new SubDirFileFilter();

	public EssentialsImport(String path) {
		importPath = path;
	}

	@Override
	public void initImport() {
		int number = 0;
		ACLogger.info("Starting import of Essentials data.\n Trying to import User-data.....");
		number = importUserData();
		ACLogger.info("Data of " + number + " user(s) imported. Trying to import spawn point(s)....");
		number = importSpawnPoints();
		ACLogger.info(number + " spawn point(s) imported. Trying to import warp point(s)....");
		number = importWarpPoints();
		ACLogger.info(number + "Warp point(s) imported. Trying to import text-files....");
		try {
			importTextFiles();
		} catch (IOException e) {
			ACLogger.info("[ERROR] Failed to import rules.txt and motd.txt. For more information refer to the debug.log!");
			DebugLog.INSTANCE.log(Level.INFO, "[ERROR] Failed to import rules.txt and motd.txt.", e);
			return;
		}
		ACLogger.info("Text files imported.\n Import finished successfully, deactivating" +
				"import-option in the configuration file...");
		ConfigEnum.IMPORT_ESSENTIALS.setValue(false);
		ACLogger.info("Import deactivated, have fun with your old data in AdminCmd.");
	}

	/* (non-Javadoc)
	 * @see be.Balor.Importer.IImport#importUserData()
	 */
	@Override
	public int importUserData() {
		int counter = 0;
		List<File> files = filter.getFiles(new File(importPath + File.separator + "userdata"), filter.new PatternFilter(Type.FILE, ".yml"), false);
		ExtendedConfiguration esUserFile = null;
		String playerName;
		boolean homes = false, lastLoc = false, connect = false, newPlayer = false, ip = false, mute = false, god = false;
		for (File f : files) {
			playerName = FilenameUtils.getBaseName(f.getAbsolutePath());
			ACPlayer p = ACPlayer.getPlayer(playerName);
			esUserFile = ExtendedConfiguration.loadConfiguration(f);
			homes = ImportTools.importESHomes(esUserFile, playerName);
			lastLoc = ImportTools.importESLastLocation(esUserFile, playerName);
			if (p != null && esUserFile != null) {
				p.setInformation("lastConnection", esUserFile.getLong("timestamps.login"));
				p.setInformation("lastDisconnect", esUserFile.getLong("timestamps.logout"));
				connect = true;
				p.setInformation("firstTime", esUserFile.getBoolean("newplayer"));
				newPlayer = true;
				p.setInformation("last-ip", esUserFile.getString("ipAddress"));
				ip = true;
				if (esUserFile.getBoolean("muted")) {
					p.setPower(be.Balor.Tools.Type.MUTED, "Permanently muted by Server Admin");
					mute = true;
				}
				if (esUserFile.getBoolean("godmode")) {
					p.setPower(be.Balor.Tools.Type.GOD);
					god = true;
				}
			}
			if (homes || lastLoc || connect || ip || newPlayer || mute || god)
				counter++;
		}
		return counter;
	}

	/* (non-Javadoc)
	 * @see be.Balor.Importer.IImport#importWarpPoints()
	 */
	@Override
	public int importWarpPoints() {
		List<File> files = filter.getFiles(new File(importPath + File.separator + "warps"), filter.new PatternFilter(Type.FILE, ".yml"), false);
		ExtendedConfiguration esWarp = null;
		Location acWarp = null;
		ACWorld world = null;
		String name = "";
		int counter = 0;

		for (File f : files) {
			esWarp = ExtendedConfiguration.loadConfiguration(f);
			if (esWarp == null) {
				ACLogger.info("[ERROR] Could not import WarpPoint: " + FilenameUtils.getBaseName(f.getAbsolutePath()));
				continue;
			}
			world = ACWorld.getWorld(esWarp.getString("world"));
			if (world == null)
				continue;
			try {
				name = esWarp.getString("name");
				acWarp = ImportTools.buildLocation(esWarp, world);
			} catch (Exception e) {
				ACLogger.info("[ERROR] Could not import WarpPoint: " + FilenameUtils.getBaseName(f.getAbsolutePath()));
				continue;
			}
			world.addWarp(name, acWarp);
			counter++;
		}
		files = filter.getFiles(new File(importPath), filter.new PatternFilter(Type.FILE, ".yml"), false);
		ExtendedConfiguration esJails = null;
		for (File f : files) {
			if (!f.getName().contains("jails"))
				continue;
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
			Set<String> keys = jails.getKeys(false);
			if (keys == null) {
				ACLogger.info("[ERROR] Could not import Jails.");
				continue;
			}
			ConfigurationSection jail = null;
			for (String jName : keys) {
				if (jName == null) {
					ACLogger.info("[ERROR] Could not import jail " + String.valueOf(jName));
					continue;
				}
				jail = jails.getConfigurationSection(jName);
				world = ACWorld.getWorld(esWarp.getString("world"));
				if (world == null)
					continue;
				try {
					acWarp = ImportTools.buildLocation(jail, world);
				} catch (Exception e) {
					ACLogger.info("[ERROR] Could not import jail: " + jName);
					continue;
				}
				world.addWarp("jail:" + jName, acWarp);
				counter++;
			}
		}
		return counter;
	}

	/* (non-Javadoc)
	 * @see be.Balor.Importer.IImport#importSpawnPoints()
	 */
	@Override
	public int importSpawnPoints() {
		final List<File>files = filter.getFiles(new File(importPath), filter.new PatternFilter(Type.FILE, ".yml"), false);
		ExtendedConfiguration esSpawns = null;
		ConfigurationSection esSpawn = null;
		Location acSpawn = null;
		ACWorld world = null;
		int counter = 0;
		for (File f : files) {
			if (!f.getName().contains("spawn"))
				continue;
			esSpawns = ExtendedConfiguration.loadConfiguration(f);
			esSpawn = esSpawns.getConfigurationSection("spawns");
			if (esSpawn == null) {
				ACLogger.info("[ERROR] Could not import Spawns.");
				continue;
			}
			Set<String> keys = esSpawn.getKeys(false);
			if (keys == null) {
				ACLogger.info("[ERROR] Could not import Spawns.");
				continue;
			}
			ConfigurationSection spawn = null;
			for (String sName : keys) {
				if (sName == null) {
					ACLogger.info("[ERROR] Could not import jail " + String.valueOf(sName));
					continue;
				}
				spawn = esSpawn.getConfigurationSection(sName);
				world = ACWorld.getWorld(spawn.getString("world"));
				if (world == null)
					continue;
				try {
					acSpawn = ImportTools.buildLocation(spawn, world);
				} catch (Exception e) {
					ACLogger.info("[ERROR] Could not import jail: " + sName);
					continue;
				}
				world.addWarp("jail:" + sName, acSpawn);
				counter++;
			}
		}
		return counter;
	}

	/* (non-Javadoc)
	 * @see be.Balor.Importer.IImport#importTextFiles()
	 */
	@Override
	public void importTextFiles() throws IOException {
		List<File> files = filter.getFiles(new File(importPath), filter.new PatternFilter(Type.FILE, ".txt"), false);
		for (File f : files) {
			if (f.getName().contains("rules")) {
				ImportTools.copyTextFile(f, FileManager.getInstance().getFile(importPath, "rules.txt"));
			} else if (f.getName().contains("motd")) {
				ImportTools.copyTextFile(f, FileManager.getInstance().getFile(importPath, "motd.txt"));
			} else
				continue;
		}
	}

	/* (non-Javadoc)
	 * @see be.Balor.Importer.IImport#importOtherFiles()
	 */
	@Override
	public void importOtherFiles() {
	}
}

