package be.Balor.bukkit.AdminCmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import be.Balor.Manager.CommandManager;
import be.Balor.Manager.ExtendedConfiguration;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.BlockRemanence;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Files.FilesManager;
import be.Balor.Tools.Help.HelpLoader;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.Tools.Type.Category;
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
	EnumMap<Type, ConcurrentMap<String, Object>> storedTypeValues = new EnumMap<Type, ConcurrentMap<String, Object>>(
			Type.class);
	private ConcurrentMap<String, MaterialContainer> alias = new MapMaker().makeMap();
	private HashMap<String, List<MaterialContainer>> kits = new HashMap<String, List<MaterialContainer>>();
	private ConcurrentMap<String, ConcurrentMap<String, Location>> locations = new MapMaker()
			.makeMap();
	private Set<String> warpList = new HashSet<String>();
	private ConcurrentMap<String, Set<String>> homeList = new MapMaker().softValues()
			.expiration(15, TimeUnit.MINUTES).makeMap();
	private static ACHelper instance = null;
	private ConcurrentMap<String, Stack<Stack<BlockRemanence>>> undoQueue = new MapMaker()
			.makeMap();
	private static long pluginStarted;
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
		listOfPossibleRepair.add(359);
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
	 * Return the elapsed time.
	 * 
	 * @return
	 */
	public static Long[] getElapsedTime() {
		return Utils.getElapsedTime(pluginStarted);
	}

	/**
	 * Add modified block in the undoQueue
	 * 
	 * @param blocks
	 */
	public void addInUndoQueue(String player, Stack<BlockRemanence> blocks) {
		if (undoQueue.containsKey(player))
			undoQueue.get(player).push(blocks);
		else {
			Stack<Stack<BlockRemanence>> blockQueue = new Stack<Stack<BlockRemanence>>();
			blockQueue.push(blocks);
			undoQueue.put(player, blockQueue);
		}

	}

	public int undoLastModification(String player) throws EmptyStackException {
		if (!undoQueue.containsKey(player))
			throw new EmptyStackException();
		Stack<Stack<BlockRemanence>> blockQueue = undoQueue.get(player);
		if (blockQueue.isEmpty())
			throw new EmptyStackException();
		Stack<BlockRemanence> undo = blockQueue.pop();
		int i = 0;
		try {
			if (Utils.logBlock == null)
				while (!undo.isEmpty()) {
					undo.pop().returnToThePast();
					i++;
				}
			else
				while (!undo.isEmpty()) {
					BlockRemanence br = undo.pop();
					Utils.logBlock.queueBlockPlace(player, br.returnToThePast().getState());
					i++;
				}
		} catch (Exception e) {
			e.printStackTrace();
			return i;
		}
		return i;
	}

	/**
	 * Get ItemStacks for given kit
	 * 
	 * @param kit
	 * @return
	 */
	public ArrayList<ItemStack> getKit(String kit) {
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		try {
			if (Utils.oddItem != null) {
				result.addAll(Utils.oddItem.getItemGroup(kit, -1));
				return result;
			}
		} catch (Throwable e) {
		}
		List<MaterialContainer> list = kits.get(kit);
		if (list != null)
			for (MaterialContainer mc : list)
				result.add(mc.getItemStack());
		return result;
	}

	/**
	 * Get the list of kit.
	 * 
	 * @return
	 */
	public String getKitList() {
		String kitList = "";
		HashSet<String> list = new HashSet<String>();
		try {
			list.addAll(kits.keySet());
			if (Utils.oddItem != null) {
				list.addAll(Utils.oddItem.getGroups());
			}

		} catch (Throwable e) {
		}

		for (String kit : list) {
			kitList += kit + ", ";
		}
		if (!kitList.equals("")) {
			if (kitList.endsWith(", "))
				kitList = kitList.substring(0, kitList.lastIndexOf(","));
		}
		return kitList.trim();
	}

	/**
	 * Reload the "plugin"
	 */
	public synchronized void reload() {
		CommandManager.getInstance().stopAllExecutorThreads();
		pluginInstance.getServer().getScheduler().cancelTasks(pluginInstance);
		storedTypeValues.clear();
		alias.clear();
		blacklist.clear();
		undoQueue.clear();
		pluginConfig = new ExtendedConfiguration("config.yml", null);
		loadInfos();
		for (Player p : InvisibleWorker.getInstance().getAllInvisiblePlayers())
			InvisibleWorker.getInstance().reappear(p);
		InvisibleWorker.killInstance();
		AFKWorker.killInstance();
		CommandManager.killInstance();
		HelpLister.killInstance();
		System.gc();
		init();
		if (ACHelper.getInstance().getConfBoolean("help.getHelpForAllPlugins"))
			for (Plugin plugin : pluginInstance.getServer().getPluginManager().getPlugins())
				HelpLister.getInstance().addPlugin(plugin);
		if (pluginConfig.getBoolean("autoAfk", true)) {
			for (Player p : pluginInstance.getServer().getOnlinePlayers())
				AFKWorker.getInstance().updateTimeStamp(p);
		}
	}

	/**
	 * Same code used when reload and onEnable
	 */
	private void init() {
		if (pluginConfig.getBoolean("autoAfk", true)) {
			AFKWorker.getInstance().setExpiration(pluginConfig.getInt("afkKickInMinutes", 3) + 1);
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
		LocaleManager.getInstance().setLocaleFile(
				pluginConfig.getString("locale", "en_US") + ".yml");
		LocaleManager.getInstance().setNoMsg(pluginConfig.getBoolean("noMessage", false));
		CommandManager.getInstance().setPlugin(pluginInstance);
		AdminCmd.registerCmds();
		CommandManager.getInstance().checkAlias();
		HelpLoader.load(pluginInstance.getDataFolder());
		if (pluginConfig.getProperty("pluginStarted") != null) {
			pluginStarted = Long.parseLong(pluginConfig.getString("pluginStarted"));
			pluginConfig.removeProperty("pluginStarted");
			pluginConfig.save();
		} else
			pluginStarted = System.currentTimeMillis();

		if (pluginConfig.getBoolean("tpRequestActivatedByDefault", false)) {
			for (Player p : pluginInstance.getServer().getOnlinePlayers())
				addValue(Type.TP_REQUEST, p.getName());
		}
	}

	/**
	 * @param pluginInstance
	 *            the pluginInstance to set
	 */
	public void setPluginInstance(AdminCmd pluginInstance) {
		this.pluginInstance = pluginInstance;
		fManager = FilesManager.getInstance();
		fManager.setPath(pluginInstance.getDataFolder().getPath());
		fManager.getInnerFile("de_DE.yml", "locales", false);
		fManager.getInnerFile("kits.yml");
		fManager.getInnerFile("ReadMe.txt", null, true);
		fManager.getInnerFile("AdminCmd.yml", "HelpFiles" + File.separator + "AdminCmd", true);
		fManager.getInnerFile("acmotd.yml", "HelpFiles" + File.separator + "AdminCmd", true);
		pluginConfig = new ExtendedConfiguration("config.yml", null);
		pluginConfig.addProperty("resetPowerWhenTpAnotherWorld", true);
		pluginConfig.addProperty("noMessage", false);
		pluginConfig.addProperty("locale", "en_US");
		pluginConfig.addProperty("statutCheckInSec", 20);
		pluginConfig.addProperty("invisibleRangeInBlock", 320);
		pluginConfig.addProperty("autoAfk", true);
		pluginConfig.addProperty("afkTimeInSecond", 60);
		pluginConfig.addProperty("autoKickAfkPlayer", false);
		pluginConfig.addProperty("afkKickInMinutes", 3);
		pluginConfig.addProperty("glideWhenFallingInFlyMode", true);
		pluginConfig.addProperty("maxHomeByUser", 0);
		pluginConfig.addProperty("fakeQuitWhenInvisible", true);
		pluginConfig.addProperty("forceOfficialBukkitPerm", false);
		pluginConfig.addProperty("MessageOfTheDay", false);
		pluginConfig.addProperty("ColoredSign", true);
		pluginConfig.addProperty("DefaultFlyPower", 1.75F);
		pluginConfig.addProperty("DefaultFireBallPower", 1.0F);
		pluginConfig.addProperty("DefaultVulcanPower", 4.0F);
		pluginConfig.addProperty("glinding.multiplicator", 0.1F);
		pluginConfig.addProperty("glinding.YvelocityCheckToGlide", -0.2F);
		pluginConfig.addProperty("glinding.newYvelocity", -0.5F);
		pluginConfig.addProperty("firstConnectionToSpawnPoint", false);
		pluginConfig.addProperty("mutedPlayerCantPm", false);
		pluginConfig.addProperty("maxRangeForTpAtSee", 400);
		pluginConfig.addProperty("tpRequestTimeOutInMinutes", 5);
		pluginConfig.addProperty("verboseLog", true);
		pluginConfig.addProperty("tpRequestActivatedByDefault", false);
		pluginConfig.addProperty("logPrivateMessages", false);
		pluginConfig.addProperty("broadcastServerReload", true);
		pluginConfig.addProperty("help.entryPerPage", 9);
		pluginConfig.addProperty("help.shortenEntries", false);
		pluginConfig.addProperty("help.useWordWrap", false);
		pluginConfig.addProperty("help.wordWrapRight", false);
		pluginConfig.addProperty("help.getHelpForAllPlugins", true);

		List<String> disabled = new ArrayList<String>();
		List<String> priority = new ArrayList<String>();
		if (pluginConfig.getProperty("disabledCommands") != null) {
			disabled = pluginConfig.getStringList("disabledCommands", disabled);
			pluginConfig.removeProperty("disabledCommands");
		}
		if (pluginConfig.getProperty("prioritizedCommands") != null) {
			priority = pluginConfig.getStringList("prioritizedCommands", priority);
			pluginConfig.removeProperty("prioritizedCommands");
		}
		pluginConfig.save();
		ExtendedConfiguration commands = new ExtendedConfiguration("commands.yml", null);
		commands.addProperty("disabledCommands", disabled);
		commands.addProperty("prioritizedCommands",
				priority.isEmpty() ? Arrays.asList("reload", "/") : priority);
		commands.addProperty("alias.god", Arrays.asList("gg", "gd"));
		commands.save();
		init();
	}

	/**
	 * Get boolean from config
	 * 
	 * @param path
	 * @return
	 */
	public boolean getConfBoolean(String path) {
		return pluginConfig.getBoolean(path, false);
	}

	/**
	 * Get float parameter of config file.
	 * 
	 * @param path
	 * @return
	 */
	public Float getConfFloat(String path) {
		return Float.parseFloat(pluginConfig.getString(path));
	}

	/**
	 * Get Integer parameter from config.
	 * 
	 * @param path
	 * @return
	 */
	public Integer getConfInt(String path) {
		return pluginConfig.getInt(path, 0);
	}

	/**
	 * Save elapsed time when reload
	 */
	public void saveElapsedTime() {
		pluginConfig.load();
		pluginConfig.setProperty("pluginStarted", pluginStarted);
		pluginConfig.save();
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
			list.add(m.getMaterial().getId());
			config.setProperty("BlackListed", list);
			config.save();
			if (blacklist == null)
				blacklist = new ArrayList<Integer>();
			blacklist.add(m.getMaterial().getId());
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", m.getMaterial().toString());
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
		addLocation(type, name, type + '.' + name, filename, loc);

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
		return getLocation(type, name, type + "." + name, filename);
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
		if (!m.isNull()) {
			Configuration config = fManager.getYml("blacklist");
			List<Integer> list = config.getIntList("BlackListed", new ArrayList<Integer>());
			if (!list.isEmpty() && list.contains(m.getMaterial().getId())) {
				list.remove((Integer) m.getMaterial().getId());
				config.setProperty("BlackListed", list);
				config.save();
			}
			if (blacklist != null && !blacklist.isEmpty()
					&& blacklist.contains(m.getMaterial().getId()))
				blacklist.remove((Integer) m.getMaterial().getId());
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("getMaterial()", m.getMaterial().toString());
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
		addValue(Type.VULCAN, playerName, power);
	}

	public void removeVulcan(String playerName) {
		removeValue(Type.VULCAN, playerName);
	}

	public void addValue(Type powerName, String user, Object power) {
		if (storedTypeValues.containsKey(powerName))
			storedTypeValues.get(powerName).put(user, power);
		else {
			ConcurrentMap<String, Object> tmp = new MapMaker().makeMap();
			tmp.put(user, power);
			storedTypeValues.put(powerName, tmp);
		}

	}

	public void addValue(Type powerName, Player user, Object power) {
		addValue(powerName, user.getName(), power);
	}

	public void addValue(Type powerName, Player user) {
		addValue(powerName, user.getName(), 0);
	}

	public void addValue(Type powerName, String user) {
		addValue(powerName, user, 0);
	}

	public void removeValue(Type powerName, String user) {
		if (storedTypeValues.containsKey(powerName))
			storedTypeValues.get(powerName).remove(user);
	}

	public void removeValue(Type powerName, Player user) {
		removeValue(powerName, user.getName());
	}

	public boolean isValueSet(Type powerName, String user) {
		return storedTypeValues.containsKey(powerName)
				&& storedTypeValues.get(powerName).containsKey(user);
	}

	public Object getValue(Type powerName, String user) {
		if (user != null && isValueSet(powerName, user))
			return storedTypeValues.get(powerName).get(user);
		return null;
	}

	public Object getValue(Type powerName, Player user) {
		return getValue(powerName, user.getName());
	}

	/**
	 * Get all the Player having the "Power"
	 * 
	 * @param power
	 * @return
	 */
	public List<Player> getAllUserOf(Type power) {
		List<Player> players = new ArrayList<Player>();
		if (storedTypeValues.containsKey(power))
			for (String player : storedTypeValues.get(power).keySet())
				players.add(pluginInstance.getServer().getPlayer(player));
		return players;
	}

	/**
	 * Get a list of the StoredValue using the wanted type.
	 * 
	 * @param type
	 *            String
	 * @return
	 */
	public Set<String> getUserList(String type) {
		Type toList = Type.matchType(type);
		if (toList == null)
			return null;
		else
			return getUserList(toList);
	}

	/**
	 * Get a list of the StoredValue using the wanted type.
	 * 
	 * @param type
	 *            Type
	 * @return
	 */
	public Set<String> getUserList(Type type) {
		if (storedTypeValues.containsKey(type))
			return new HashSet<String>(storedTypeValues.get(type).keySet());
		return null;
	}

	/**
	 * Remove all user Power.
	 * 
	 * @param player
	 * @return
	 */
	public boolean removeKeyFromValues(Player player) {
		boolean found = false;
		for (Type type : storedTypeValues.keySet()) {
			if (!type.getCategory().equals(Category.PLAYER))
				continue;
			if (type.equals(Type.TP_REQUEST)
					&& PermissionManager.hasPerm(player, "admincmd.tp.toggle"))
				continue;
			if (storedTypeValues.get(type).remove(player.getName()) != null)
				found = true;
		}
		return found;
	}

	public boolean isValueSet(Type powerName, Player user) {
		return isValueSet(powerName, user.getName());
	}

	public void addThor(String playerName) {
		addValue(Type.THOR, playerName);
	}

	public void removeThor(String playerName) {
		removeValue(Type.THOR, playerName);
	}

	public boolean hasThorPowers(String player) {
		return isValueSet(Type.THOR, player);
	}

	public boolean hasGodPowers(String player) {
		return isValueSet(Type.GOD, player);
	}

	public Float getVulcainExplosionPower(String player) {
		return (Float) getValue(Type.VULCAN, player);
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
				&& blacklist.contains(mat.getMaterial().getId())) {
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

	public void addValueWithFile(Type power, String user, Object value) {
		addValue(power, user, value);
		Configuration ban = fManager.getYml(power.toString());
		ban.setProperty(power + "." + user, value);
		ban.save();
	}

	public void removeValueWithFile(Type power, String user) {
		removeValue(power, user);
		Configuration ban = fManager.getYml(power.toString());
		ban.removeProperty(power + "." + user);
		ban.save();
	}

	public Set<String> getHomeList(String player) {
		if (homeList.containsKey(player)) {
			return homeList.get(player);
		} else {
			List<String> tmp = fManager.getYmlKeyFromFile(player, "home");
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
		List<String> tmp = fManager.getYmlKeyFromFile("warpPoints", "warp");
		if (tmp != null)
			warpList.addAll(tmp);

		Map<String, Object> map = fManager.loadMap(Type.BANNED, null, Type.BANNED.toString());
		for (String key : map.keySet())
			addValue(Type.BANNED, key, map.get(key));
		Map<String, Object> map2 = fManager.loadMap(Type.MUTED, null, Type.MUTED.toString());
		for (String key : map2.keySet())
			addValue(Type.MUTED, key, map2.get(key));
		kits.putAll(fManager.loadKits());
		if (pluginConfig.getBoolean("verboseLog", true)) {
			Logger.getLogger("Minecraft").info(
					"[AdminCmd] " + blacklist.size() + " blacklisted items loaded.");
			Logger.getLogger("Minecraft").info("[AdminCmd] " + alias.size() + " alias loaded.");
			Logger.getLogger("Minecraft").info(
					"[AdminCmd] " + map.size() + " Banned players loaded.");
			Logger.getLogger("Minecraft").info(
					"[AdminCmd] " + map2.size() + " Muted players loaded.");
			Logger.getLogger("Minecraft").info("[AdminCmd] " + kits.size() + " kits loaded.");
		}
	}

	public int getLimit(Player player, String type) {
		Integer limit = null;
		String toParse = PermissionManager.getPermissionLimit(player, "maxHomeByUser");
		limit = toParse != null && !toParse.isEmpty() ? Integer.parseInt(toParse) : null;
		if (limit == null || limit == -1)
			limit = pluginConfig.getInt(type, 0);
		if (limit == 0)
			limit = Integer.MAX_VALUE;
		return limit;
	}
	// ----- / item coloring section -----
}
