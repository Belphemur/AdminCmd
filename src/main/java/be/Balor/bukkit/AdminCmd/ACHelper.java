package be.Balor.bukkit.AdminCmd;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import be.Balor.Manager.ExtendedConfiguration;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.PermissionManager;
import be.Balor.Tools.FilesManager;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Powers;
import be.Balor.Tools.Utils;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

import com.google.common.collect.MapMaker;

/**
 * Handle commands
 * 
 * @authors Plague, Balor
 */
public class ACHelper {

	private HashMap<Material, String[]> materialsColors;
	private List<Integer> listOfPossibleRepair;
	private FilesManager fManager;
	private List<Integer> blacklist;
	private AdminCmd pluginInstance;
	EnumMap<Powers, ConcurrentMap<String, Object>> usersWithPowers = new EnumMap<Powers, ConcurrentMap<String, Object>>(
			Powers.class);
	private ConcurrentMap<String, MaterialContainer> alias = new MapMaker().makeMap();
	private ConcurrentMap<String, ConcurrentMap<String, Location>> locations = new MapMaker()
			.makeMap();
	private Set<String> warpList = new HashSet<String>();
	private ConcurrentMap<String, Set<String>> homeList = new MapMaker().softValues()
			.expiration(15, TimeUnit.MINUTES).makeMap();
	private static ACHelper instance = null;
	private ExtendedConfiguration pluginConfig;

	private ACHelper() {
		materialsColors = new HashMap<Material, String[]>();
		materialsColors.put(Material.WOOL, new String[] { "White", "Orange", "Magenta",
				"LightBlue", "Yellow", "LimeGreen", "Pink", "Gray", "LightGray", "Cyan", "Purple",
				"Blue", "Brown", "Green", "Red", "Black" });
		materialsColors.put(Material.INK_SACK, new String[] { "Black", "Red", "Green", "Brown",
				"Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "LimeGreen", "Yellow",
				"LightBlue", "Magenta", "Orange", "White" });
		materialsColors.put(Material.LOG, new String[] { "Oak", "Pine", "Birch" });
		materialsColors.put(Material.STEP, new String[] { "Stone", "Sandstone", "Wooden",
				"Cobblestone" });
		materialsColors.put(Material.DOUBLE_STEP, materialsColors.get(Material.STEP));
		listOfPossibleRepair = new LinkedList<Integer>();
		for (int i = 256; i <= 259; i++)
			listOfPossibleRepair.add(i);
		for (int i = 267; i <= 279; i++)
			listOfPossibleRepair.add(i);
		for (int i = 283; i <= 286; i++)
			listOfPossibleRepair.add(i);
		for (int i = 290; i <= 294; i++)
			listOfPossibleRepair.add(i);
		for (int i = 298; i <= 317; i++)
			listOfPossibleRepair.add(i);
	}

	public static ACHelper getInstance() {
		if (instance == null)
			instance = new ACHelper();
		return instance;
	}

	public static void killInstance() {
		instance = null;
	}

	/**
	 * @param pluginInstance
	 *            the pluginInstance to set
	 */
	public void setPluginInstance(AdminCmd pluginInstance) {
		this.pluginInstance = pluginInstance;
		fManager = FilesManager.getInstance();
		fManager.setPath(pluginInstance.getDataFolder().getPath());
		pluginConfig = new ExtendedConfiguration(new File(pluginInstance.getDataFolder().getPath(),
				"config.yml"));
		pluginConfig.addProperty("resetPowerWhenTpAnotherWorld", true);
		pluginConfig.addProperty("noMessage", false);
		pluginConfig.addProperty("locale", "en_US");
		pluginConfig.addProperty("statutCheckInSec", 20);
		pluginConfig.addProperty("invisibleRangeInBlock", 512);
		pluginConfig.addProperty("autoAfk", true);
		pluginConfig.addProperty("afkTimeInSecond", 60);
		pluginConfig.addProperty("autoKickAfkPlayer", false);
		pluginConfig.addProperty("afkKickInMinutes", 3);
		pluginConfig.addProperty("glideWhenFallingInFlyMode", true);
		pluginConfig.addProperty("maxHomeByUser", 0);
		pluginConfig.addProperty("fakeQuitWhenInvisible", true);
		pluginConfig.save();
		if (pluginConfig.getBoolean("autoAfk", true)) {
			AFKWorker.getInstance().setAfkTime(pluginConfig.getInt("afkTimeInSecond", 60));
			AFKWorker.getInstance().setKickTime(pluginConfig.getInt("afkKickInMinutes", 3));
			this.pluginInstance
					.getServer()
					.getScheduler()
					.scheduleAsyncRepeatingTask(this.pluginInstance,
							AFKWorker.getInstance().getAfkChecker(), 0,
							pluginConfig.getInt("statutCheckInSec", 20) * 20);
			if (pluginConfig.getBoolean("autoKickAfkPlayer", false))
				this.pluginInstance
						.getServer()
						.getScheduler()
						.scheduleAsyncRepeatingTask(this.pluginInstance,
								AFKWorker.getInstance().getKickChecker(), 0,
								pluginConfig.getInt("statutCheckInSec", 20) * 20);
		}
		InvisibleWorker.getInstance()
				.setMaxRange(pluginConfig.getInt("invisibleRangeInBlock", 512));
		InvisibleWorker.getInstance().setTickCheck(pluginConfig.getInt("statutCheckInSec", 20));
		LocaleManager.getInstance().setLocaleFile(pluginConfig.getString("locale", "en_US"));
		LocaleManager.getInstance().setNoMsg(pluginConfig.getBoolean("noMessage", false));
	}

	/**
	 * get the value of the path in the config
	 * 
	 * @param path
	 * @return
	 */
	public Object getConfValue(String path) {
		return pluginConfig.getProperty(path);
	}

	/**
	 * @return the pluginInstance
	 */
	public AdminCmd getPluginInstance() {
		return pluginInstance;
	}

	// teleports chosen player to another player

	/**
	 * Add an item to the BlackList
	 * 
	 * @param name
	 * @return
	 */
	public boolean setBlackListedItem(CommandSender sender, String name) {
		MaterialContainer m = checkMaterial(sender, name);
		if (!m.isNull()) {
			Configuration config = fManager.getYml("blacklist");
			List<Integer> list = config.getIntList("BlackListed", null);
			if (list == null)
				list = new ArrayList<Integer>();
			list.add(m.material.getId());
			config.setProperty("BlackListed", list);
			config.save();
			if (blacklist == null)
				blacklist = new ArrayList<Integer>();
			blacklist.add(m.material.getId());
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", m.material.toString());
			Utils.sI18n(sender, "addBlacklist", replace);
			return true;
		}
		return false;
	}

	/**
	 * @return the warpList
	 */
	public Set<String> getWarpList() {
		return warpList;
	}

	/**
	 * Add a location in the location cache
	 * 
	 * @param type
	 * @param name
	 * @param loc
	 */
	private void addLocationInMemory(String type, String name, Location loc) {
		if (locations.containsKey(type))
			locations.get(type).put(name, loc);
		else {
			ConcurrentMap<String, Location> tmp = new MapMaker().softValues()
					.expiration(15, TimeUnit.MINUTES).makeMap();
			tmp.put(name, loc);
			locations.put(type, tmp);
		}
	}

	/**
	 * Get a location that is cached in memory.
	 * 
	 * @param type
	 * @param name
	 * @return
	 */
	private Location getLocationFromMemory(String type, String name) {
		Location loc = null;
		if (locations.containsKey(type))
			loc = locations.get(type).get(name);
		return loc;
	}

	/**
	 * Remove the location from the memory
	 * 
	 * @param type
	 * @param name
	 */
	private void removeLocationFromMemory(String type, String name) {
		if (locations.containsKey(type)) {
			locations.get(type).remove(name);
			if (locations.get(type).isEmpty())
				locations.remove(type);
		}
	}

	/**
	 * Remove the location from memory and from disk
	 * 
	 * @param type
	 * @param name
	 * @param filename
	 */
	public void removeLocation(String type, String nameMemory, String property, String filename) {
		removeLocationFromMemory(type, nameMemory);
		fManager.removeLocationFromFile(property, filename, type);
	}

	public void removeLocation(String type, String name, String filename) {
		removeLocation(type, name, name, filename);
	}

	/**
	 * Add a location in memory and on the disk.
	 * 
	 * @param type
	 * @param property
	 * @param filename
	 * @param loc
	 */

	public void addLocation(String type, String nameMemory, String property, String filename,
			Location loc) {
		addLocationInMemory(type, nameMemory, loc);
		fManager.writeLocationFile(loc, property, filename, type);
	}

	public void addLocation(String type, String name, String filename, Location loc) {
		addLocation(type, name, name, filename, loc);

	}

	/**
	 * Get a location, if not in memory check on the disk, if found, add it in
	 * memory and return it.
	 * 
	 * @param type
	 * @param name
	 * @param property
	 * @return
	 */
	public Location getLocation(String type, String nameMemory, String property, String filename) {
		Location loc = null;
		loc = getLocationFromMemory(type, nameMemory);
		if (loc == null) {
			loc = fManager.getLocationFile(property, filename, type);
			if (loc != null)
				addLocationInMemory(type, nameMemory, loc);
		}

		return loc;
	}

	public Location getLocation(String type, String name, String filename) {
		return getLocation(type, name, name, filename);
	}

	/**
	 * Set the spawn point.
	 */
	public void setSpawn(CommandSender sender) {
		if (Utils.isPlayer(sender)) {
			Location loc = ((Player) sender).getLocation();
			((Player) sender).getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(),
					loc.getBlockZ());
			addLocation("spawn", loc.getWorld().getName(), "spawnLocations", loc);
			Utils.sI18n(sender, "setSpawn");
		}
	}

	public void spawn(CommandSender sender) {
		if (Utils.isPlayer(sender)) {
			Player player = ((Player) sender);
			Location loc = null;
			String worldName = player.getWorld().getName();
			loc = getLocation("spawn", worldName, "spawnLocations");
			if (loc == null)
				loc = player.getWorld().getSpawnLocation();
			player.teleport(loc);
			Utils.sI18n(sender, "spawn");
		}
	}

	/**
	 * remove a black listed item
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeBlackListedItem(CommandSender sender, String name) {
		MaterialContainer m = checkMaterial(sender, name);
		if (m.material != null) {
			Configuration config = fManager.getYml("blacklist");
			List<Integer> list = config.getIntList("BlackListed", null);
			if (list == null)
				list = new ArrayList<Integer>();
			if (!list.isEmpty() && list.contains(m.material.getId())) {
				list.remove((Integer) m.material.getId());
				config.setProperty("BlackListed", list);
				config.save();
			}
			if (blacklist != null && !blacklist.isEmpty() && blacklist.contains(m.material.getId()))
				blacklist.remove((Integer) m.material.getId());
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", m.material.toString());
			Utils.sI18n(sender, "rmBlacklist", replace);
			return true;
		}
		return false;
	}

	/**
	 * Get the blacklisted items
	 * 
	 * @return
	 */
	private List<Integer> getBlackListedItems() {
		return fManager.getYml("blacklist").getIntList("BlackListed", new ArrayList<Integer>());
	}

	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 */
	public MaterialContainer checkMaterial(CommandSender sender, String mat) {
		MaterialContainer m = Utils.checkMaterial(mat);
		if (m.isNull()) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat);
			Utils.sI18n(sender, "unknownMat", replace);
		}
		return m;

	}

	public MaterialContainer getAlias(String name) {
		return alias.get(name);
	}

	// ----- / item coloring section -----

	// translates a given color value/name into a real color value
	// also does some checking (error = -1)
	private short getColor(String name, Material mat) {
		short value = -1;
		// first try numbered colors
		try {
			value = Short.parseShort(name);
		} catch (Exception e) {
			// try to find the name then
			for (short i = 0; i < materialsColors.get(mat).length; ++i)
				if (materialsColors.get(mat)[i].equalsIgnoreCase(name)) {
					value = i;
					break;
				}
		}
		// is the value OK?
		if (value < 0 || value >= materialsColors.get(mat).length)
			return -1;
		return value;
	}

	// returns all members of the color name array concatenated with commas
	private String printColors(Material mat) {
		String output = "";
		for (int i = 0; i < materialsColors.get(mat).length; ++i)
			output += materialsColors.get(mat)[i] + ", ";
		return output;
	}

	public void addVulcain(String playerName, float power) {
		addPowerUser(Powers.VULCAN, playerName, power);
	}

	public void removeVulcan(String playerName) {
		removePowerUser(Powers.VULCAN, playerName);
	}

	public void addPowerUser(Powers powerName, String user, Object power) {
		if (usersWithPowers.containsKey(powerName))
			usersWithPowers.get(powerName).put(user, power);
		else {
			ConcurrentMap<String, Object> tmp = new MapMaker().makeMap();
			tmp.put(user, power);
			usersWithPowers.put(powerName, tmp);
		}

	}

	public void addPowerUser(Powers powerName, Player user, Object power) {
		addPowerUser(powerName, user.getName(), power);
	}

	public void addPowerUser(Powers powerName, Player user) {
		addPowerUser(powerName, user.getName(), 0);
	}

	public void addPowerUser(Powers powerName, String user) {
		addPowerUser(powerName, user, 0);
	}

	public void removePowerUser(Powers powerName, String user) {
		if (usersWithPowers.containsKey(powerName)) {
			usersWithPowers.get(powerName).remove(user);
			if (usersWithPowers.get(powerName).isEmpty())
				usersWithPowers.remove(powerName);
		}
	}

	public void removePowerUser(Powers powerName, Player user) {
		removePowerUser(powerName, user.getName());
	}

	public boolean isPowerUser(Powers powerName, String user) {
		return usersWithPowers.containsKey(powerName)
				&& usersWithPowers.get(powerName).containsKey(user);
	}

	public Object getPowerOfPowerUser(Powers powerName, String user) {
		if (user != null && isPowerUser(powerName, user))
			return usersWithPowers.get(powerName).get(user);
		return null;
	}

	public Object getPowerOfPowerUser(Powers powerName, Player user) {
		return getPowerOfPowerUser(powerName, user.getName());
	}

	public List<Player> getAllPowerUserOf(String power) {
		List<Player> players = new ArrayList<Player>();
		if (usersWithPowers.containsKey(power))
			for (String player : usersWithPowers.get(power).keySet())
				players.add(pluginInstance.getServer().getPlayer(player));
		return players;
	}

	/**
	 * Remove all user Power.
	 * 
	 * @param player
	 * @return
	 */
	public boolean removePlayerFromAllPowerUser(String player) {
		boolean found = false;
		for (Powers type : usersWithPowers.keySet()) {
			if (usersWithPowers.get(type).remove(player) != null)
				found = true;
		}
		return found;
	}

	public boolean isPowerUser(Powers powerName, Player user) {
		return isPowerUser(powerName, user.getName());
	}

	public void addThor(String playerName) {
		addPowerUser(Powers.THOR, playerName);
	}

	public void removeThor(String playerName) {
		removePowerUser(Powers.THOR, playerName);
	}

	public boolean hasThorPowers(String player) {
		return isPowerUser(Powers.THOR, player);
	}

	public boolean hasGodPowers(String player) {
		return isPowerUser(Powers.GOD, player);
	}

	public Float getVulcainExplosionPower(String player) {
		return (Float) getPowerOfPowerUser(Powers.VULCAN, player);
	}

	public boolean alias(CommandSender sender, String[] args) {
		MaterialContainer m = checkMaterial(sender, args[1]);
		if (m.isNull())
			return true;
		String alias = args[0];
		this.alias.put(alias, m);
		this.fManager.addAlias(alias, m);
		sender.sendMessage(ChatColor.BLUE + "You can now use " + ChatColor.GOLD + alias
				+ ChatColor.BLUE + " for the item " + ChatColor.GOLD + m.display());
		return true;
	}

	public boolean rmAlias(CommandSender sender, String alias) {
		this.fManager.removeAlias(alias);
		this.alias.remove(alias);
		sender.sendMessage(ChatColor.GOLD + alias + ChatColor.RED + " removed");
		return true;
	}

	public boolean reparable(int id) {
		return listOfPossibleRepair.contains(id);
	}

	// changes the color of a colorable item in hand
	public boolean itemColor(CommandSender sender, String color) {
		if (Utils.isPlayer(sender)) {
			// help?
			if (color.equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.RED + "Wool: " + ChatColor.WHITE
						+ printColors(Material.WOOL));
				sender.sendMessage(ChatColor.RED + "Dyes: " + ChatColor.WHITE
						+ printColors(Material.INK_SACK));
				sender.sendMessage(ChatColor.RED + "Logs: " + ChatColor.WHITE
						+ printColors(Material.LOG));
				sender.sendMessage(ChatColor.RED + "Slab: " + ChatColor.WHITE
						+ printColors(Material.STEP));
				return true;
			}
			// determine the value based on what you're holding
			short value = -1;
			Material m = ((Player) sender).getItemInHand().getType();

			if (materialsColors.containsKey(m))
				value = getColor(color, m);
			else {
				sender.sendMessage(ChatColor.RED + "You must hold a colorable material!");
				return true;
			}
			// error?
			if (value < 0) {
				sender.sendMessage(ChatColor.RED + "Color " + ChatColor.WHITE + color
						+ ChatColor.RED + " is not usable for what you're holding!");
				return true;
			}

			((Player) sender).getItemInHand().setDurability(value);
		}
		return true;
	}

	public boolean inBlackList(CommandSender sender, MaterialContainer mat) {
		if (!PermissionManager.hasPerm(sender, "admincmd.item.noblacklist", false)
				&& blacklist.contains(mat.material.getId())) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat.display());
			Utils.sI18n(sender, "inBlacklist", replace);
			return true;
		}
		return false;
	}

	public boolean inBlackList(CommandSender sender, ItemStack mat) {
		if (!PermissionManager.hasPerm(sender, "admincmd.item.noblacklist", false)
				&& blacklist.contains(mat.getTypeId())) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat.getType().toString());
			Utils.sI18n(sender, "inBlacklist", replace);
			return true;
		}
		return false;
	}

	public void addPowerUserWithFile(Powers power, String user, String reason) {
		addPowerUser(power, user, reason);
		Configuration ban = fManager.getYml(power.toString());
		ban.setProperty(power + "." + user, reason);
		ban.save();
	}

	public void removePowerUserWithFile(Powers power, String user) {
		removePowerUser(power, user);
		Configuration ban = fManager.getYml(power.toString());
		ban.removeProperty(power + "." + user);
		ban.save();
	}

	public Set<String> getHomeList(String player) {
		if (homeList.containsKey(player)) {
			return homeList.get(player);
		} else {
			List<String> tmp = fManager.getAllLocationsNameFromFile(player, "home");
			if (tmp != null)
				homeList.put(player, new HashSet<String>(tmp));
			else
				homeList.put(player, new HashSet<String>());
			return homeList.get(player);
		}
	}

	public synchronized void loadInfos() {
		blacklist = getBlackListedItems();
		alias.putAll(fManager.getAlias());
		List<String> tmp = fManager.getAllLocationsNameFromFile("warpPoints", "warp");
		if (tmp != null)
			warpList.addAll(tmp);

		Map<String, Object> map = fManager.loadMap("banned", null, "banned");
		for (String key : map.keySet())
			addPowerUser(Powers.BANNED, key, map.get(key));
	}

	@SuppressWarnings("deprecation")
	public int getLimit(Player player, String type) {
		Integer limit = null;
		if (PermissionManager.getPermission() != null) {
			try {
				limit = PermissionManager.getPermission().getInfoInteger(
						player.getWorld().getName(), player.getName(), "admincmd." + type, false);
			} catch (NoSuchMethodError e) {
				limit = PermissionManager.getPermission().getPermissionInteger(
						player.getWorld().getName(), player.getName(), "admincmd." + type);
			}
		} else {
			String toParse = PermissionManager.getPermissionLimit(player, "maxHomeByUser");
			limit = toParse != null ? Integer.parseInt(toParse) : null;
		}
		if (limit == null || limit == -1)
			limit = pluginConfig.getInt(type, 0);
		if (limit == 0)
			limit = Integer.MAX_VALUE;
		return limit;
	}
	// ----- / item coloring section -----
}
