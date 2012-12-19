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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.regex.Matcher;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import au.com.bytecode.opencsv.CSVReader;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Player.Ban;
import be.Balor.Player.BannedIP;
import be.Balor.Player.BannedPlayer;
import be.Balor.Player.IBan;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Type.ArmorPart;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Exceptions.InvalidInputException;
import be.Balor.Tools.Files.Unicode.UnicodeReader;
import be.Balor.Tools.Files.Unicode.UnicodeUtil;
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
			final Properties gitVersion = new Properties();
			gitVersion.load(FileManager.class
					.getResourceAsStream("/git.properties"));
			fileVersion = (String) gitVersion.get("git.commit.id");
			DebugLog.INSTANCE.info("Git Version : " + fileVersion);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the instance
	 */
	public static FileManager getInstance() {
		if (instance == null) {
			instance = new FileManager();
		}
		return instance;
	}

	/**
	 * Get a txt-file and return its content in a String
	 * 
	 * @param fileName
	 *            - The name of the file to be loaded
	 * @return The content of the file
	 */
	public String getTextFile(final String fileName) {
		final StringBuffer result = new StringBuffer();
		try {
			final File fileDir = getInnerFile(fileName);
			final BufferedReader in = new BufferedReader(new UnicodeReader(
					new FileInputStream(fileDir), "UTF-8"));
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
		if (result.length() == 0) {
			return null;
		} else {
			return result.toString().trim();
		}
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(final String path) {
		pathFile = new File(path);
		if (!pathFile.exists()) {
			try {
				Files.createParentDirs(pathFile);
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		final File spawn = getFile(null, "spawnLocations.yml", false);
		final File homeDir = new File(this.pathFile, "home");
		if (spawn.exists()) {
			final File dir = new File(this.pathFile, "spawn");
			dir.mkdir();
			spawn.renameTo(new File(dir, "spawnLocations.yml.old"));
		}
		if (homeDir.exists()) {
			homeDir.renameTo(new File(this.pathFile, "userData"));
		}
	}

	/**
	 * Open the file and return the ExtendedConfiguration object
	 * 
	 * @param directory
	 * @param filename
	 * @return the configuration file
	 */
	public ExtendedConfiguration getYml(final String filename,
			final String directory) {
		if (lastLoadedConf != null
				&& lastDirectory.equals(directory == null ? "" : directory)
				&& lastFilename.equals(filename)) {
			return lastLoadedConf;
		}
		final ExtendedConfiguration config = ExtendedConfiguration
				.loadConfiguration(getFile(directory, filename + ".yml"));
		lastLoadedConf = config;
		return config;
	}

	public ExtendedConfiguration getYml(final String filename) {
		return getYml(filename, null);
	}

	/**
	 * Open the file and return the File object
	 * 
	 * @param directory
	 * @param filename
	 * @return the configuration file
	 */
	public File getFile(final String directory, final String filename) {
		return getFile(directory, filename, true);
	}

	public File getFile(final String directory, final String filename,
			final boolean create) {
		if (lastFile != null
				&& lastDirectory.equals(directory == null ? "" : directory)
				&& lastFilename.equals(filename)) {
			return lastFile;
		}
		File file = null;
		if (directory != null) {
			final File directoryFile = new File(this.pathFile, directory);
			if (!directoryFile.exists() && create) {
				directoryFile.mkdir();
			}
			file = new File(directoryFile, filename);
		} else {
			file = new File(pathFile, filename);
		}

		if (!file.exists() && create) {

			try {
				file.createNewFile();
			} catch (final IOException ex) {
				System.out.println("cannot create file " + file.getPath());
			}
		}
		lastFile = file;
		lastDirectory = directory == null ? "" : directory;
		lastFilename = filename;
		return file;
	}

	/**
	 * To write a text file on the AdminCmd folder.
	 * 
	 * @param filename
	 * @param toSet
	 */
	public void setTxtFile(final String filename, final String toSet) {
		final File txt = getFile(null, filename + ".txt");
		try {
			UnicodeUtil.saveUTF8File(txt, toSet, false);
		} catch (final IOException e) {
			ACLogger.severe("Can't write the file " + filename, e);
		}
	}

	/**
	 * Write the alias in the yml file
	 * 
	 * @param alias
	 * @param mc
	 */
	public void addAlias(final String alias, final MaterialContainer mc) {
		final ExtendedConfiguration conf = getYml("Alias");

		final List<String> aliasList = conf.getStringList("alias",
				new ArrayList<String>());
		final List<String> idList = conf.getStringList("ids",
				new ArrayList<String>());
		if (aliasList.contains(alias)) {
			final int index = aliasList.indexOf(alias);
			aliasList.remove(index);
			idList.remove(index);
		}
		aliasList.add(alias);
		idList.add(mc.toString());
		conf.set("alias", aliasList);
		conf.set("ids", idList);
		try {
			conf.save();
		} catch (final IOException e) {}
	}

	/**
	 * Remove the alias from the yml fileF
	 * 
	 * @param alias
	 */
	public void removeAlias(final String alias) {
		final ExtendedConfiguration conf = getYml("Alias");
		final List<String> aliasList = conf.getStringList("alias",
				new ArrayList<String>());
		final List<String> idList = conf.getStringList("ids",
				new ArrayList<String>());
		final int index = aliasList.indexOf(alias);
		aliasList.remove(index);
		idList.remove(index);
		conf.set("alias", aliasList);
		conf.set("ids", idList);
		try {
			conf.save();
		} catch (final IOException e) {}
	}

	/**
	 * Get a file in the jar, copy it in the choose directory inside the plugin
	 * folder, open it and return it
	 * 
	 * @param filename
	 * @return
	 */
	public File getInnerFile(final String filename) {
		return getInnerFile(filename, null, false);
	}

	public File getInnerFile(final String filename, final String directory,
			final boolean replace) {
		final File file;
		if (directory != null) {
			final File directoryFile = new File(this.pathFile, directory);
			if (!directoryFile.exists()) {
				directoryFile.mkdirs();
			}
			file = new File(directoryFile, filename);
		} else {
			file = new File(pathFile, filename);
		}
		if (file.exists() && replace) {
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new UnicodeReader(
						new FileInputStream(file), "UTF-8"));
			} catch (final FileNotFoundException e) {}
			try {
				final String version = reader.readLine();
				final String versioncheck = version.substring(10);
				if (!versioncheck.equals(fileVersion)) {
					reader.close();
					file.delete();
					DebugLog.INSTANCE.info("Delete file : " + file);
				} else {
					return file;
				}
			} catch (final IOException e) {
				file.delete();
			}

			try {
				reader.close();
			} catch (final IOException e) {}

		}
		if (!file.exists()) {
			try {
				UnicodeUtil.saveUTF8File(file, this.getClass()
						.getResourceAsStream("/" + filename), false);
			} catch (final IOException e) {
				ACLogger.severe("Can't copy the inner file " + filename
						+ " to " + file, e);
			}
		}
		return file;
	}

	public HashMap<String, MaterialContainer> getAlias() {
		final HashMap<String, MaterialContainer> result = new HashMap<String, MaterialContainer>();
		final ExtendedConfiguration conf = getYml("Alias");
		final List<String> aliasList = conf.getStringList("alias",
				new ArrayList<String>());
		final List<String> idList = conf.getStringList("ids",
				new ArrayList<String>());
		int i = 0;
		try {
			final CSVReader csv = new CSVReader(new FileReader(getInnerFile(
					"items.csv", null, true)));
			String[] alias;
			while ((alias = csv.readNext()) != null) {
				try {
					result.put(alias[0], new MaterialContainer(alias[1],
							alias[2]));
				} catch (final ArrayIndexOutOfBoundsException e) {
					try {
						result.put(alias[0], new MaterialContainer(alias[1]));
					} catch (final ArrayIndexOutOfBoundsException e2) {}

				}

			}

		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		for (final String alias : aliasList) {
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
	public void writeLocation(final Location loc, final String name,
			final String filename, final String directory) {
		final ExtendedConfiguration conf = getYml(filename, directory);
		conf.set(name + ".world", loc.getWorld().getName());
		conf.set(name + ".x", loc.getX());
		conf.set(name + ".y", loc.getY());
		conf.set(name + ".z", loc.getZ());
		conf.set(name + ".yaw", loc.getYaw());
		conf.set(name + ".pitch", loc.getPitch());
		try {
			conf.save();
		} catch (final IOException e) {}
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
	public Location getLocation(final String property, final String filename,
			final String directory) throws WorldNotLoaded {
		final ExtendedConfiguration conf = getYml(filename, directory);
		if (conf.get(property + ".world") == null) {
			final Location loc = parseLocation(property, conf);
			if (loc != null) {
				writeLocation(loc, property, filename, directory);
			}
			return loc;
		} else {
			final World w = ACPluginManager.getServer().getWorld(
					conf.getString(property + ".world"));
			if (w != null) {
				return new Location(w, conf.getDouble(property + ".x", 0),
						conf.getDouble(property + ".y", 0), conf.getDouble(
								property + ".z", 0), Float.parseFloat(conf
								.getString(property + ".yaw")),
						Float.parseFloat(conf.getString(property + ".pitch")));
			} else {
				throw new WorldNotLoaded(conf.getString(property + ".world"));
			}

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
	public void removeKey(final String property, final String filename,
			final String directory) {
		final ExtendedConfiguration conf = getYml(filename, directory);
		conf.set(property, null);
		try {
			conf.save();
		} catch (final IOException e) {}
	}

	/**
	 * Return a string Set containing all locations names
	 * 
	 * @param filename
	 * @param directory
	 * @return
	 */
	@Override
	public Set<String> getKeys(final String info, final String filename,
			final String directory) {
		final Set<String> keys = getYml(filename, directory)
				.getConfigurationSection(info).getKeys(false);
		if (keys == null) {
			return new HashSet<String>();
		} else {
			return keys;
		}
	}

	/**
	 * Parse String to create a location
	 * 
	 * @param property
	 * @param conf
	 * @return
	 */
	private Location parseLocation(final String property,
			final ExtendedConfiguration conf) {
		final String toParse = conf.getString(property, null);
		if (toParse == null) {
			return null;
		}
		if (toParse.isEmpty()) {
			return null;
		}
		final Double coords[] = new Double[3];
		final Float direction[] = new Float[2];
		final String[] infos = toParse.split(";");
		for (int i = 0; i < coords.length; i++) {
			try {
				coords[i] = Double.parseDouble(infos[i]);
			} catch (final NumberFormatException e) {
				return null;
			}
		}
		for (int i = 3; i < infos.length - 1; i++) {
			try {
				direction[i - 3] = Float.parseFloat(infos[i]);
			} catch (final NumberFormatException e) {
				return null;
			}
		}
		return new Location(ACPluginManager.getServer().getWorld(infos[5]),
				coords[0], coords[1], coords[2], direction[0], direction[1]);
	}

	/**
	 * Load the map
	 * 
	 * @param type
	 * @param directory
	 * @param filename
	 * @return
	 */
	public Map<String, Object> loadMap(final Type type, final String directory,
			final String filename) {
		final Map<String, Object> result = new HashMap<String, Object>();
		final ExtendedConfiguration conf = getYml(filename, directory);
		final ConfigurationSection confSection = conf
				.getConfigurationSection(type.toString());
		if (confSection != null) {
			for (final String key : confSection.getKeys(false)) {
				result.put(key, confSection.get(key));
			}
		}
		return result;
	}

	@Override
	public Map<String, Ban> loadBan() {
		final Map<String, Ban> result = new HashMap<String, Ban>();
		final ExtendedConfiguration conf = getYml("banned");
		if (conf.get("bans") != null) {
			final ConfigurationSection node = conf
					.getConfigurationSection("bans");
			for (final String key : node.getKeys(false)) {
				result.put(key, (BannedPlayer) node.get(key));
			}

		}
		if (conf.get("IPs") != null) {
			final ConfigurationSection node = conf
					.getConfigurationSection("IPs");
			for (final String key : node.getKeys(false)) {
				final BannedIP ban = (BannedIP) node.get(key);
				result.put(ban.getPlayer(), ban);
			}

		}
		if (ConfigEnum.IMPORT_BAN_TXT.getBoolean()) {
			importBannedPlayerTXT(result);
			ConfigEnum.IMPORT_BAN_TXT.setValue(false);
			try {
				ConfigEnum.save();
			} catch (IOException e) {
			}
		}
		return result;
	}

	/**
	 * Loads the messages from the deathMessages.yml into a Map and returns it.
	 * 
	 * @return A {@code Map< String, String >} object with all death reasons and
	 *         their message</br> defined in the deathMessages.yml
	 */
	public Map<String, String> loadDeathMessages() {
		final Map<String, String> result = new HashMap<String, String>();
		final ExtendedConfiguration conf = getYml("deathMessages");
		for (final String reason : conf.getKeys(false)) {
			result.put(reason, conf.getString(reason));
		}
		return result;
	}

	private void importBannedPlayerTXT(final Map<String, Ban> result) {
		final Set<OfflinePlayer> banned = ACPluginManager.getServer()
				.getBannedPlayers();
		final Set<String> ipBanned = ACPluginManager.getServer().getIPBans();
		final Iterator<OfflinePlayer> it = banned.iterator();
		while (it.hasNext()) {
			final OfflinePlayer op = it.next();
			final String name = op.getName();
			if (!result.containsKey(name)) {
				final BannedPlayer bp = new BannedPlayer(name,
						"Import from banned-players.txt");
				result.put(name, bp);
			}
		}
		for (final String ip : ipBanned) {
			if (result.containsKey(ip)) {
				continue;
			}
			result.put(ip, new BannedIP(ip, "Import from banned-ip.txt"));
		}
	}

	/**
	 * Load all the kits
	 * 
	 * @return
	 */
	public Map<String, KitInstance> loadKits() {
		final Map<String, KitInstance> result = new LinkedHashMap<String, KitInstance>();
		final List<MaterialContainer> items = new ArrayList<MaterialContainer>();
		final ExtendedConfiguration kits = getYml("kits");
		final Map<String, List<String>> kitParents = new HashMap<String, List<String>>();
		final Map<ArmorPart, MaterialContainer> armor = new EnumMap<Type.ArmorPart, MaterialContainer>(
				ArmorPart.class);

		final ConfigurationSection kitNodes = kits
				.getConfigurationSection("kits");
		if (kitNodes == null) {
			ACLogger.severe("A problem happen when wanting to load the kits. Please check your kits.yml file.");
			return result;
		}
		for (final String kitName : kitNodes.getKeys(false)) {
			int delay = 0;
			final ConfigurationSection kitNode = kitNodes
					.getConfigurationSection(kitName);
			ConfigurationSection kitItems = null;
			ConfigurationSection armorItems = null;
			List<String> parents = null;
			try {
				kitItems = kitNode.getConfigurationSection("items");
				armorItems = kitNode.getConfigurationSection("armor");
				parents = kitNode.getStringList("parents");
			} catch (final NullPointerException e) {
				DebugLog.INSTANCE.warning("Problem with kit " + kitName);
				continue;
			}

			if (kitItems != null) {
				for (final String item : kitItems.getKeys(false)) {
					try {
						final MaterialContainer m = Utils.checkMaterial(item);
						m.setAmount(kitItems.getInt(item, 1));
						if (!m.isNull()) {
							items.add(m);
						}
					} catch (final InvalidInputException e) {
						DebugLog.INSTANCE.log(Level.WARNING,
								"Problem with kit : " + item, e);
					}
				}
			}
			delay = kitNode.getInt("delay", 0);

			if (armorItems != null) {
				for (final ArmorPart part : ArmorPart.values()) {
					final String partId = armorItems.getString(part.toString());
					if (partId == null) {
						continue;
					}
					try {
						final MaterialContainer m = Utils.checkMaterial(partId);
						if (!m.isNull()) {
							armor.put(part, m);
						}
					} catch (final InvalidInputException e) {
						DebugLog.INSTANCE.log(Level.WARNING,
								"Problem with kit : " + partId, e);
					}
				}
				result.put(kitName, new ArmoredKitInstance(kitName, delay,
						new ArrayList<MaterialContainer>(items),
						new EnumMap<Type.ArmorPart, MaterialContainer>(armor)));
			} else {
				result.put(kitName, new KitInstance(kitName, delay,
						new ArrayList<MaterialContainer>(items)));
			}

			if (parents != null) {
				kitParents.put(kitName, parents);
			} else {
				ACLogger.info(kitName + " has no parents");
			}

			items.clear();
			armor.clear();
		}
		for (final Entry<String, List<String>> entry : kitParents.entrySet()) {
			KitInstance kit = result.get(entry.getKey());
			for (final String parent : entry.getValue()) {
				final KitInstance parentKit = result.get(parent);
				if (parentKit == null) {
					continue;
				}
				if (parentKit instanceof ArmoredKitInstance
						&& !(kit instanceof ArmoredKitInstance)) {
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
	public void addBan(final IBan player) {
		final ExtendedConfiguration banFile = getYml("banned");
		ConfigurationSection bans;
		if (player instanceof BannedPlayer) {
			bans = banFile.addSection("bans");
			bans.set(player.getPlayer(), player);
		} else {
			bans = banFile.addSection("IPs");
			bans.set(String.valueOf(player.getPlayer().hashCode()), player);
		}

		try {
			banFile.save();
		} catch (final IOException e) {}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Files.DataManager#unbanPlayer(java.lang.String)
	 */
	@Override
	public void unBanPlayer(final IBan ban) {
		final String player = ban.getPlayer();
		final Matcher ipv4 = Utils.REGEX_IP_V4.matcher(player);
		final ExtendedConfiguration banFile = getYml("banned");
		ConfigurationSection bans;
		if (ipv4.find()) {
			bans = banFile.addSection("IPs");
			bans.set(String.valueOf(player.hashCode()), null);
		} else {
			bans = banFile.addSection("bans");
			bans.set(player, null);
		}
		try {
			banFile.save();
		} catch (final IOException e) {}

	}
}
