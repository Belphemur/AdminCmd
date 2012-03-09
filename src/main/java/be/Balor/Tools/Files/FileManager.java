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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import au.com.bytecode.opencsv.CSVReader;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.BannedPlayer;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Type.ArmorPart;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

import com.google.common.io.Files;

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
	private static String fileVersion = null;
	static {
		try {
			Properties gitVersion = new Properties();
			gitVersion.load(FileManager.class.getResourceAsStream("/git.properties"));
			fileVersion = (String) gitVersion.get("git.commit.id");
			DebugLog.INSTANCE.info("Git Version : " + fileVersion);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * Get a txt-file and return its content in a String
	 *
	 * @param fileName
	 *            - The name of the file to be loaded
	 * @return The content of the file
	 */
	public String getTextFile(String fileName) {
		final StringBuffer result = new StringBuffer();
		try {
			final File fileDir = getInnerFile(fileName);
			final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileDir), "UTF8"));
			String temp;
			while ((temp = in.readLine()) != null) {
				result.append(temp + "\n");
			}
			in.close();
		} catch (final UnsupportedEncodingException e) {
			// TODO: Better debug code here
			ACLogger.Log(Level.SEVERE, e.getMessage(), e);
		} catch (final IOException e) {
			// TODO: Better debug code here
			ACLogger.Log(Level.SEVERE, e.getMessage(), e);
		} catch (final Exception e) {
			// TODO: Better debug code here
			ACLogger.Log(Level.SEVERE, e.getMessage(), e);
		}
		if (result.length() == 0)
			return null;
		else
			return result.toString().trim();
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		pathFile = new File(path);
		if (!pathFile.exists()) {
			try {
				Files.createParentDirs(pathFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		if (filename.contains("yml"))
			preParseYamlFile(file);
		lastFile = file;
		lastDirectory = directory == null ? "" : directory;
		lastFilename = filename;
		return file;
	}

	/**
	 * Parses a YAML file before it is loaded by the yaml parser
	 * to catch common errors like tabs instead of spaces
	 *
	 *
	 * @param file
	 */
	public void preParseYamlFile(File file) {
		List<String> input = new ArrayList<String>();
		try {
			final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF8"));
			String line;
			while ((line = in.readLine()) != null) {
				line = line.replaceAll("\uFFFD", "?");
				if (line.contains("\t"))
						while(line.contains("\t"))
							line = line.replace("\t", "  ");
				input.add(line);
			}
			in.close();
			for (int i=0; i<input.size(); i++) {
				line = input.get(i);
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(
					file), "UTF8"));
			for (int i=0; i<input.size(); i++) {
				out.write(input.get(i));
			}
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * To write a text file on the AdminCmd folder.
	 *
	 * @param filename
	 * @param toSet
	 */
	public void setTxtFile(String filename, String toSet) {
		File txt = getFile(null, filename + ".txt");
		FileWriter fstream = null;
		try {
			fstream = new FileWriter(txt);
		} catch (IOException e) {
			ACLogger.severe("Can't write the txt file : " + filename, e);
			return;
		}
		BufferedWriter out = new BufferedWriter(fstream);
		try {
			out.write(toSet);
			out.close();
		} catch (IOException e) {
			ACLogger.severe("Can't write the txt file : " + filename, e);
			return;
		}

	}

	/**
	 * Write the alias in the yml file
	 *
	 * @param alias
	 * @param mc
	 */
	public void addAlias(String alias, MaterialContainer mc) {
		ExtendedConfiguration conf = getYml("Alias");

		List<String> aliasList = conf.getStringList("alias", new ArrayList<String>());
		List<String> idList = conf.getStringList("ids", new ArrayList<String>());
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
	public void removeAlias(String alias) {
		ExtendedConfiguration conf = getYml("Alias");
		List<String> aliasList = conf.getStringList("alias", new ArrayList<String>());
		List<String> idList = conf.getStringList("ids", new ArrayList<String>());
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
		if (file.exists() && replace) {
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
			}
			try {
				String version = reader.readLine();
				final String versioncheck = version.substring(10);
				if (!versioncheck.equals(fileVersion)) {
					reader.close();
					file.delete();
					DebugLog.INSTANCE.info("Delete file : " + file);
				} else
					return file;
			} catch (IOException e) {
				file.delete();
			}

			try {
				reader.close();
			} catch (IOException e) {
			}

		}
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
		List<String> aliasList = conf.getStringList("alias", new ArrayList<String>());
		List<String> idList = conf.getStringList("ids", new ArrayList<String>());
		int i = 0;
		try {
			CSVReader csv = new CSVReader(new FileReader(getInnerFile("items.csv", null, true)));
			String[] alias;
			while ((alias = csv.readNext()) != null) {
				try {
					result.put(alias[0], new MaterialContainer(alias[1], alias[2]));
				} catch (ArrayIndexOutOfBoundsException e) {
					try {
						result.put(alias[0], new MaterialContainer(alias[1]));
					} catch (ArrayIndexOutOfBoundsException e2) {
					}

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
		Double coords[] = new Double[3];
		Float direction[] = new Float[2];
		String[] infos = toParse.split(";");
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
		if (ConfigEnum.IMPORT_BAN_TXT.getBoolean()) {
			result = importBannedPlayerTXT(result);
			ConfigEnum.IMPORT_BAN_TXT.setValue(false);
		}
		return result;
	}

	private Map<String, BannedPlayer> importBannedPlayerTXT(Map<String, BannedPlayer> result) {
		Set<OfflinePlayer> banned = ACPluginManager.getServer().getBannedPlayers();
		Iterator<OfflinePlayer> it = banned.iterator();
		while (it.hasNext()) {
			OfflinePlayer op = it.next();
			String name = op.getName();
			BannedPlayer bp = new BannedPlayer(name, "Import from banned-players.txt");
			if (!result.containsKey(name))
				result.put(name, bp);
		}
		return result;
	}

	/**
	 * Load all the kits
	 *
	 * @return
	 */
	public Map<String, KitInstance> loadKits() {
		Map<String, KitInstance> result = new LinkedHashMap<String, KitInstance>();
		List<MaterialContainer> items = new ArrayList<MaterialContainer>();
		ExtendedConfiguration kits = getYml("kits");
		Map<String, List<String>> kitParents = new HashMap<String, List<String>>();
		Map<ArmorPart, MaterialContainer> armor = new EnumMap<Type.ArmorPart, MaterialContainer>(
				ArmorPart.class);

		ConfigurationSection kitNodes = kits.getConfigurationSection("kits");
		if (kitNodes == null) {
			ACLogger.severe("A problem happen when wanting to load the kits. Please check your kits.yml file.");
			return result;
		}
		for (String kitName : kitNodes.getKeys(false)) {
			int delay = 0;
			ConfigurationSection kitNode = kitNodes.getConfigurationSection(kitName);
			ConfigurationSection kitItems = null;
			ConfigurationSection armorItems = null;
			List<String> parents = null;
			try {
				kitItems = kitNode.getConfigurationSection("items");
				armorItems = kitNode.getConfigurationSection("armor");
				parents = kitNode.getStringList("parents");
			} catch (NullPointerException e) {
				DebugLog.INSTANCE.warning("Problem with kit " + kitName);
				continue;
			}

			if (kitItems != null)
				for (String item : kitItems.getKeys(false)) {
					MaterialContainer m = Utils.checkMaterial(item);
					m.setAmount(kitItems.getInt(item, 1));
					if (!m.isNull())
						items.add(m);
				}
			delay = kitNode.getInt("delay", 0);
			/*
			 * Old convertor code, not used anymore TODO: CLEAN IT. } else {
			 * kitNode.addDefault("items", new HashMap<String, Object>());
			 * kitItems = kitNode.getConfigurationSection("items"); for (String
			 * item : kitNode.getKeys(false)) { if (item.equals("items"))
			 * continue; MaterialContainer m = Utils.checkMaterial(item); int
			 * amount = kitNode.getInt(item, 1); m.setAmount(amount); if
			 * (!m.isNull()) { items.add(m); kitItems.set(item, amount);
			 * kitNode.set(item, null); } } kitNode.set("delay", 0); convert =
			 * true; }
			 */
			if (armorItems != null) {
				for (ArmorPart part : ArmorPart.values()) {
					String partId = armorItems.getString(part.toString());
					if (partId == null)
						continue;
					MaterialContainer m = Utils.checkMaterial(partId);
					if (!m.isNull())
						armor.put(part, m);
				}
				result.put(kitName, new ArmoredKitInstance(kitName, delay,
						new ArrayList<MaterialContainer>(items),
						new EnumMap<Type.ArmorPart, MaterialContainer>(armor)));
			} else
				result.put(kitName, new KitInstance(kitName, delay,
						new ArrayList<MaterialContainer>(items)));

			if (parents != null)
				kitParents.put(kitName, parents);
			else
				ACLogger.info(kitName + " has no parents");

			items.clear();
			armor.clear();
		}
		for (Entry<String, List<String>> entry : kitParents.entrySet()) {
			KitInstance kit = result.get(entry.getKey());
			for (String parent : entry.getValue()) {
				KitInstance parentKit = result.get(parent);
				if (parentKit == null)
					continue;
				if (parentKit instanceof ArmoredKitInstance && !(kit instanceof ArmoredKitInstance)) {
					kit = new ArmoredKitInstance(kit);
					result.put(kit.getName(), kit);
				}
				kit.addParent(parentKit);
			}
		}

		/*
		 * if (convert) try { kits.save(); } catch (IOException e) { }
		 */
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
