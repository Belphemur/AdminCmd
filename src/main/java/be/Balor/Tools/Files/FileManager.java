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
package be.Balor.Tools.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import au.com.bytecode.opencsv.CSVReader;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.BannedPlayer;
import be.Balor.Player.TempBannedPlayer;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class FileManager implements DataManager {
	protected File pathFile;
	private static FileManager instance = null;
	private String lastDirectory = "";
	private String lastFilename = "";
	private File lastFile = null;
	private ExtendedConfiguration lastLoadedConf = null;
	static {
		ExtendedConfiguration.registerClass(BannedPlayer.class);
		ExtendedConfiguration.registerClass(TempBannedPlayer.class);
	}

	/**
	 * @return the instance
	 */
	public static FileManager getInstance() {
		if (instance == null)
			instance = new FileManager();
		return instance;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		pathFile = new File(path);
		if (!pathFile.exists()) {
			pathFile.mkdir();
		}
		File spawn = getFile(null, "spawnLocations.yml", false);
		File homeDir = new File(this.pathFile, "home");
		if (spawn.exists()) {
			File dir = new File(this.pathFile, "spawn");
			dir.mkdir();
			spawn.renameTo(new File(dir, "spawnLocations.yml.old"));
		}
		if (homeDir.exists())
			homeDir.renameTo(new File(this.pathFile, "userData"));
	}

	/**
	 * Open the file and return the ExtendedConfiguration object
	 * 
	 * @param directory
	 * @param filename
	 * @return the configuration file
	 */
	public ExtendedConfiguration getYml(String filename, String directory) {
		if (lastLoadedConf != null && lastDirectory.equals(directory == null ? "" : directory)
				&& lastFilename.equals(filename))
			return lastLoadedConf;
		ExtendedConfiguration config = ExtendedConfiguration.loadConfiguration(getFile(directory,
				filename + ".yml"));
		lastLoadedConf = config;
		return config;
	}

	public ExtendedConfiguration getYml(String filename) {
		return getYml(filename, null);
	}

	/**
	 * Open the file and return the File object
	 * 
	 * @param directory
	 * @param filename
	 * @return the configuration file
	 */
	public File getFile(String directory, String filename) {
		return getFile(directory, filename, true);
	}

	public File getFile(String directory, String filename, boolean create) {
		if (lastFile != null && lastDirectory.equals(directory == null ? "" : directory)
				&& lastFilename.equals(filename))
			return lastFile;
		File file = null;
		if (directory != null) {
			File directoryFile = new File(this.pathFile, directory);
			if (!directoryFile.exists() && create) {
				directoryFile.mkdir();
			}
			file = new File(directoryFile, filename);
		} else
			file = new File(pathFile, filename);

		if (!file.exists() && create) {

			try {
				file.createNewFile();
			} catch (IOException ex) {
				System.out.println("cannot create file " + file.getPath());
			}
		}
		lastFile = file;
		lastDirectory = directory == null ? "" : directory;
		lastFilename = filename;
		return file;
	}

	/**
	 * Write the alias in the yml file
	 * 
	 * @param alias
	 * @param mc
	 */
	@SuppressWarnings("unchecked")
	public void addAlias(String alias, MaterialContainer mc) {
		ExtendedConfiguration conf = getYml("Alias");

		ArrayList<String> aliasList = (ArrayList<String>) conf.getList("alias",
				new ArrayList<String>());
		ArrayList<String> idList = (ArrayList<String>) conf.getList("ids", new ArrayList<String>());
		if (aliasList.contains(alias)) {
			int index = aliasList.indexOf(alias);
			aliasList.remove(index);
			idList.remove(index);
		}
		aliasList.add(alias);
		idList.add(mc.toString());
		conf.set("alias", aliasList);
		conf.set("ids", idList);
		try {
			conf.save();
		} catch (IOException e) {
		}
	}

	/**
	 * Remove the alias from the yml fileF
	 * 
	 * @param alias
	 */
	@SuppressWarnings("unchecked")
	public void removeAlias(String alias) {
		ExtendedConfiguration conf = getYml("Alias");
		ArrayList<String> aliasList = (ArrayList<String>) conf.getList("alias",
				new ArrayList<String>());
		ArrayList<String> idList = (ArrayList<String>) conf.getList("ids", new ArrayList<String>());
		int index = aliasList.indexOf(alias);
		aliasList.remove(index);
		idList.remove(index);
		conf.set("alias", aliasList);
		conf.set("ids", idList);
		try {
			conf.save();
		} catch (IOException e) {
		}
	}

	/**
	 * Get a file in the jar, copy it in the choose directory inside the plugin
	 * folder, open it and return it
	 * 
	 * @param filename
	 * @return
	 */
	public File getInnerFile(String filename) {
		return getInnerFile(filename, null, false);
	}

	public File getInnerFile(String filename, String directory, boolean replace) {
		final File file;
		if (directory != null) {
			File directoryFile = new File(this.pathFile, directory);
			if (!directoryFile.exists()) {
				directoryFile.mkdirs();
			}
			file = new File(directoryFile, filename);
		} else
			file = new File(pathFile, filename);
		if (file.exists() && replace)
			file.delete();
		if (!file.exists()) {
			final InputStream res = this.getClass().getResourceAsStream("/" + filename);
			FileWriter tx = null;
			try {
				tx = new FileWriter(file);
				for (int i = 0; (i = res.read()) > 0;) {
					tx.write(i);
				}
				tx.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
				return file;
			} finally {
				try {
					res.close();
				} catch (Exception ex) {
				}
				try {
					if (tx != null) {
						tx.close();
					}
				} catch (Exception ex) {
				}
			}
		}
		return file;
	}

	public HashMap<String, MaterialContainer> getAlias() {
		HashMap<String, MaterialContainer> result = new HashMap<String, MaterialContainer>();
		ExtendedConfiguration conf = getYml("Alias");
		List<String> aliasList = conf.getStringList("alias",
				new ArrayList<String>());
		List<String> idList =  conf.getStringList("ids",
				new ArrayList<String>());
		int i = 0;
		try {
			CSVReader csv = new CSVReader(new FileReader(getInnerFile("items.csv")));
			String[] alias;
			while ((alias = csv.readNext()) != null) {
				try {
					result.put(alias[0], new MaterialContainer(alias[1], alias[2]));
				} catch (ArrayIndexOutOfBoundsException e) {
					result.put(alias[0], new MaterialContainer(alias[1]));
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String alias : aliasList) {
			result.put(alias, new MaterialContainer(idList.get(i)));
			i++;
		}
		return result;
	}

	/**
	 * Create a flat file with the location informations
	 * 
	 * @param loc
	 * @param filename
	 * @param directory
	 */
	@Override
	public void writeLocation(Location loc, String name, String filename, String directory) {
		ExtendedConfiguration conf = getYml(filename, directory);
		conf.set(name + ".world", loc.getWorld().getName());
		conf.set(name + ".x", loc.getX());
		conf.set(name + ".y", loc.getY());
		conf.set(name + ".z", loc.getZ());
		conf.set(name + ".yaw", loc.getYaw());
		conf.set(name + ".pitch", loc.getPitch());
		try {
			conf.save();
		} catch (IOException e) {
		}
	}

	/**
	 * Return the location after parsing the flat file
	 * 
	 * @param property
	 * @param filename
	 * @param directory
	 * @return
	 */
	@Override
	public Location getLocation(String property, String filename, String directory)
			throws WorldNotLoaded {
		ExtendedConfiguration conf = getYml(filename, directory);
		if (conf.get(property + ".world") == null) {
			Location loc = parseLocation(property, conf);
			if (loc != null)
				writeLocation(loc, property, filename, directory);
			return loc;
		} else {
			World w = ACPluginManager.getServer().getWorld(conf.getString(property + ".world"));
			if (w != null)
				return new Location(w, conf.getDouble(property + ".x", 0), conf.getDouble(property
						+ ".y", 0), conf.getDouble(property + ".z", 0), Float.parseFloat(conf
						.getString(property + ".yaw")), Float.parseFloat(conf.getString(property
						+ ".pitch")));
			else
				throw new WorldNotLoaded(conf.getString(property + ".world"));

		}
	}

	/**
	 * Remove the given location from the file
	 * 
	 * @param property
	 * @param filename
	 * @param directory
	 */
	@Override
	public void removeKey(String property, String filename, String directory) {
		ExtendedConfiguration conf = getYml(filename, directory);
		conf.set(property, null);
		try {
			conf.save();
		} catch (IOException e) {
		}
	}

	/**
	 * Return a string Set containing all locations names
	 * 
	 * @param filename
	 * @param directory
	 * @return
	 */
	@Override
	public Set<String> getKeys(String info, String filename, String directory) {
		Set<String> keys = getYml(filename, directory).getConfigurationSection(info).getKeys(false);
		if (keys == null)
			return new HashSet<String>();
		else
			return keys;
	}

	/**
	 * Parse String to create a location
	 * 
	 * @param property
	 * @param conf
	 * @return
	 */
	private Location parseLocation(String property, ExtendedConfiguration conf) {
		String toParse = conf.getString(property, null);
		if (toParse == null)
			return null;
		if (toParse.isEmpty())
			return null;
		String infos[] = new String[5];
		Double coords[] = new Double[3];
		Float direction[] = new Float[2];
		infos = toParse.split(";");
		for (int i = 0; i < coords.length; i++)
			try {
				coords[i] = Double.parseDouble(infos[i]);
			} catch (NumberFormatException e) {
				return null;
			}
		for (int i = 3; i < infos.length - 1; i++)
			try {
				direction[i - 3] = Float.parseFloat(infos[i]);
			} catch (NumberFormatException e) {
				return null;
			}
		return new Location(ACPluginManager.getServer().getWorld(infos[5]), coords[0], coords[1],
				coords[2], direction[0], direction[1]);
	}

	/**
	 * Load the map
	 * 
	 * @param type
	 * @param directory
	 * @param filename
	 * @return
	 */
	public Map<String, Object> loadMap(Type type, String directory, String filename) {
		Map<String, Object> result = new HashMap<String, Object>();
		ExtendedConfiguration conf = getYml(filename, directory);
		ConfigurationSection confSection = conf.getConfigurationSection(type.toString());
		if (confSection != null) {
			for (String key : confSection.getKeys(false))
				result.put(key, confSection.get(key));
		}
		return result;
	}

	@Override
	public Map<String, BannedPlayer> loadBan() {
		Map<String, BannedPlayer> result = new HashMap<String, BannedPlayer>();
		ExtendedConfiguration conf = getYml("banned");
		if (conf.get("bans") != null) {
			ConfigurationSection node = conf.getConfigurationSection("bans");
			for (String key : node.getKeys(false))
				result.put(key, (BannedPlayer) node.get(key));

		}
		return result;
	}

	/**
	 * Load all the kits
	 * 
	 * @return
	 */
	public Map<String, KitInstance> loadKits() {
		Map<String, KitInstance> result = new HashMap<String, KitInstance>();
		List<MaterialContainer> items = new ArrayList<MaterialContainer>();
		ExtendedConfiguration kits = getYml("kits");
		boolean convert = false;

		ConfigurationSection kitNodes = kits.getConfigurationSection("kits");
		for (String kitName : kitNodes.getKeys(false)) {
			int delay = 0;
			ConfigurationSection kitNode = kitNodes.getConfigurationSection(kitName);
			ConfigurationSection kitItems = null;
			try {
				kitItems = kitNode.getConfigurationSection("items");
			} catch (NullPointerException e) {
				continue;
			}

			if (kitItems != null) {
				for (String item : kitItems.getKeys(false)) {
					MaterialContainer m = Utils.checkMaterial(item);
					m.setAmount(kitItems.getInt(item, 1));
					if (!m.isNull())
						items.add(m);
				}
				delay = kitNode.getInt("delay", 0);
			} else {
				kitNode.addDefault("items", new HashMap<String, Object>());
				kitItems = kitNode.getConfigurationSection("items");
				for (String item : kitNode.getKeys(false)) {
					if (item.equals("items"))
						continue;
					MaterialContainer m = Utils.checkMaterial(item);
					int amount = kitNode.getInt(item, 1);
					m.setAmount(amount);
					if (!m.isNull()) {
						items.add(m);
						kitItems.set(item, amount);
						kitNode.set(item, null);
					}
				}
				kitNode.set("delay", 0);
				convert = true;
			}

			result.put(kitName, new KitInstance(kitName, delay, new ArrayList<MaterialContainer>(
					items)));
			items.clear();
		}

		if (convert)
			try {
				kits.save();
			} catch (IOException e) {
			}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Files.DataManager#addBannedPlayer(be.Balor.Player.BannedPlayer
	 * )
	 */
	@Override
	public void addBannedPlayer(BannedPlayer player) {
		ExtendedConfiguration banFile = getYml("banned");
		ConfigurationSection bans = banFile.addSection("bans");
		bans.set(player.getPlayer(), player);
		try {
			banFile.save();
		} catch (IOException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Files.DataManager#unbanPlayer(java.lang.String)
	 */
	@Override
	public void unBanPlayer(String player) {
		ExtendedConfiguration banFile = getYml("banned");
		ConfigurationSection bans = banFile.addSection("bans");
		bans.set(player, null);
		try {
			banFile.save();
		} catch (IOException e) {
		}

	}

}
