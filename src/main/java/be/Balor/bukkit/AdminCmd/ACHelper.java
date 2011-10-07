package be.Balor.bukkit.AdminCmd;

import info.somethingodd.bukkit.OddItem.OddItem;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import be.Balor.Manager.CommandManager;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.ACPlayerFactory;
import be.Balor.Player.BannedPlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.Player.TempBannedPlayer;
import be.Balor.Tools.BlockRemanence;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Configuration.ExtendedConfiguration;
import be.Balor.Tools.Configuration.ExtendedNode;
import be.Balor.Tools.Files.DataManager;
import be.Balor.Tools.Files.FileManager;
import be.Balor.Tools.Files.KitInstance;
import be.Balor.Tools.Help.HelpLoader;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;
import be.Balor.World.ACWorldFactory;
import be.Balor.World.WorldManager;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

import com.google.common.collect.MapMaker;

/**
 * Handle commands
 * 
 * @authors Plague, Balor, Lathanael
 */
public class ACHelper {

	private HashMap<Material, String[]> materialsColors;
	private List<Integer> listOfPossibleRepair;
	private FileManager fManager;
	private List<Integer> itemBlacklist;
	private List<Integer> blockBlacklist;
	private List<String> groups;
	private AdminCmd coreInstance;
	private ConcurrentMap<String, MaterialContainer> alias = new MapMaker().makeMap();
	private HashMap<String, KitInstance> kits = new HashMap<String, KitInstance>();
	private ConcurrentMap<String, BannedPlayer> bannedPlayers = new MapMaker().makeMap();
	private ConcurrentMap<Player, Object> fakeQuitPlayers = new MapMaker().weakKeys().weakValues()
			.makeMap();
	private static ACHelper instance = null;
	private ConcurrentMap<String, Stack<Stack<BlockRemanence>>> undoQueue = new MapMaker()
			.makeMap();
	private static long pluginStarted;
	private ExtendedConfiguration pluginConfig;
	private DataManager dataManager;
	private boolean serverLocked = false;
	private ConcurrentMap<Player, Player> playersForReplyMessage = new MapMaker().makeMap();

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

	static void killInstance() {
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
	 * @return the serverLocked
	 */
	public boolean isServerLocked() {
		return serverLocked;
	}

	/**
	 * @param serverLocked
	 *            the serverLocked to set
	 */
	public void setServerLocked(boolean serverLocked) {
		this.serverLocked = serverLocked;
	}

	/**
	 * Ban a new player
	 * 
	 * @param ban
	 */
	public void addBannedPlayer(BannedPlayer ban) {
		bannedPlayers.put(ban.getPlayer(), ban);
		dataManager.addBannedPlayer(ban);
	}

	/**
	 * Is the player banned.
	 * 
	 * @param player
	 * @return
	 */
	public BannedPlayer isBanned(String player) {
		return bannedPlayers.get(player);
	}

	/**
	 * Unban the player
	 * 
	 * @param player
	 */
	public void unBanPlayer(String player) {
		bannedPlayers.remove(player);
		dataManager.unBanPlayer(player);
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
	 * Get KitInstance for given kit
	 * 
	 * @param kit
	 * @return
	 */
	public KitInstance getKit(String kit) {
		return kits.get(kit);
	}

	/**
	 * Get the list of kit.
	 * 
	 * @return
	 */
	public String getKitList(CommandSender sender) {
		String kitList = "";
		HashSet<String> list = new HashSet<String>();
		try {
			list.addAll(kits.keySet());
			if (Utils.oddItem != null) {
				list.addAll(OddItem.getGroups());
			}

		} catch (Throwable e) {
		}

		for (String kit : list) {
			if (PermissionManager.hasPerm(sender, "admincmd.kit." + kit, false))
				kitList += kit + ", ";
		}
		if (!kitList.equals("")) {
			if (kitList.endsWith(", "))
				kitList = kitList.substring(0, kitList.lastIndexOf(","));
		}
		return kitList.trim();
	}

	public void addFakeQuit(Player p) {
		fakeQuitPlayers.put(p, new Object());
	}

	public void removeFakeQuit(Player p) {
		fakeQuitPlayers.remove(p);
	}

	public Set<Player> getFakeQuitPlayers() {
		return fakeQuitPlayers.keySet();
	}

	/**
	 * Reload the "plugin"
	 */
	public synchronized void reload() {
		CommandManager.getInstance().stopAllExecutorThreads();
		coreInstance.getServer().getScheduler().cancelTasks(coreInstance);
		alias.clear();
		itemBlacklist.clear();
		blockBlacklist.clear();
		groups.clear();
		undoQueue.clear();
		pluginConfig = new ExtendedConfiguration("config.yml", null);
		pluginConfig.load();
		bannedPlayers.clear();
		loadInfos();
		for (Player p : InvisibleWorker.getInstance().getAllInvisiblePlayers())
			InvisibleWorker.getInstance().reappear(p);
		InvisibleWorker.killInstance();
		AFKWorker.killInstance();
		CommandManager.killInstance();
		HelpLister.killInstance();
		System.gc();
		init();
		CommandManager.getInstance().registerACPlugin(coreInstance);
		coreInstance.registerCmds();
		CommandManager.getInstance().checkAlias(coreInstance);
		if (ACHelper.getInstance().getConfBoolean("help.getHelpForAllPlugins"))
			for (Plugin plugin : coreInstance.getServer().getPluginManager().getPlugins())
				HelpLister.getInstance().addPlugin(plugin);
		if (pluginConfig.getBoolean("autoAfk", true)) {
			for (Player p : Utils.getOnlinePlayers())
				AFKWorker.getInstance().updateTimeStamp(p);
		}
	}

	/**
	 * Same code used when reload and onEnable
	 */
	private void init() {
		if (pluginConfig.getBoolean("autoAfk", true)) {
			AFKWorker.getInstance().setAfkTime(pluginConfig.getInt("afkTimeInSecond", 60));
			AFKWorker.getInstance().setKickTime(pluginConfig.getInt("afkKickInMinutes", 3));

			this.coreInstance
					.getServer()
					.getScheduler()
					.scheduleAsyncRepeatingTask(this.coreInstance,
							AFKWorker.getInstance().getAfkChecker(), 0,
							pluginConfig.getInt("statutCheckInSec", 20) * 20);
			if (pluginConfig.getBoolean("autoKickAfkPlayer", false))
				this.coreInstance
						.getServer()
						.getScheduler()
						.scheduleAsyncRepeatingTask(this.coreInstance,
								AFKWorker.getInstance().getKickChecker(), 0,
								pluginConfig.getInt("statutCheckInSec", 20) * 20);
		}
		InvisibleWorker.getInstance()
				.setMaxRange(pluginConfig.getInt("invisibleRangeInBlock", 512));
		InvisibleWorker.getInstance().setTickCheck(pluginConfig.getInt("statutCheckInSec", 20));
		LocaleManager.getInstance().setLocaleFile(
				pluginConfig.getString("locale", "en_US") + ".yml");
		LocaleManager.getInstance().setNoMsg(pluginConfig.getBoolean("noMessage", false));
		CommandManager.getInstance().setCorePlugin(coreInstance);
		HelpLoader.load(coreInstance.getDataFolder());
		if (pluginConfig.getProperty("pluginStarted") != null) {
			pluginStarted = Long.parseLong(pluginConfig.getString("pluginStarted"));
			pluginConfig.removeProperty("pluginStarted");
			pluginConfig.save();
		} else
			pluginStarted = System.currentTimeMillis();

		if (pluginConfig.getBoolean("tpRequestActivatedByDefault", false)) {
			for (Player p : coreInstance.getServer().getOnlinePlayers())
				ACPlayer.getPlayer(p.getName()).setPower(Type.TP_REQUEST);
		}
		for (World w : coreInstance.getServer().getWorlds()) {
			ACWorld world = ACWorld.getWorld(w.getName());
			int task = world.getInformation(Type.TIME_FREEZED.toString()).getInt(-1);
			if (task != -1) {
				task = ACPluginManager.getScheduler().scheduleAsyncRepeatingTask(
						ACHelper.getInstance().getCoreInstance(), new Utils.SetTime(w), 0, 10);
				world.setInformation(Type.TIME_FREEZED.toString(), task);
			}
		}
	}

	private void convertBannedMuted() {
		Map<String, Object> map = fManager.loadMap(Type.BANNED, null, Type.BANNED.toString());
		if (!map.isEmpty()) {
			fManager.getFile(null, "banned.yml", false).delete();
			for (String key : map.keySet())
				dataManager.addBannedPlayer(new BannedPlayer(key, String.valueOf(map.get(key))));
		}
		File muted = fManager.getFile(null, "muted.yml", false);
		if (muted.exists()) {
			map = fManager.loadMap(Type.MUTED, null, Type.MUTED.toString());
			for (String key : map.keySet()) {
				PlayerManager.getInstance().setOnline(key);
				ACPlayer.getPlayer(key).setPower(Type.MUTED, map.get(key));
			}
			muted.delete();
		}
	}

	private void convertSpawnWarp() {
		File spawnFile = fManager.getFile("spawn", "spawnLocations.yml", false);
		if (spawnFile.exists()) {
			ExtendedConfiguration spawn = new ExtendedConfiguration(spawnFile);
			spawn.load();
			ExtendedNode spawnPoints = spawn.getNode("spawn");
			if (spawnPoints != null)
				for (String key : spawnPoints.getKeys())
					try {
						ACWorld.getWorld(key).setSpawn(
								fManager.getLocation("spawn." + key, "spawnLocations", "spawn"));
					} catch (WorldNotLoaded e) {
					}

			spawnFile.delete();
			spawnFile.getParentFile().delete();
		}
		File warpFile = fManager.getFile("warp", "warpPoints.yml", false);
		if (warpFile.exists()) {
			for (String key : fManager.getKeys("warp", "warpPoints", "warp")) {
				try {
					Location loc = fManager.getLocation("warp." + key, "warpPoints", "warp");
					ACWorld.getWorld(loc.getWorld().getName()).addWarp(key, loc);
				} catch (WorldNotLoaded e) {
				}
			}
			warpFile.delete();
			warpFile.getParentFile().delete();
		}
	}

	/**
	 * @param pluginInstance
	 *            the pluginInstance to set
	 */
	public void setCoreInstance(AdminCmd pluginInstance) {
		this.coreInstance = pluginInstance;
		fManager = FileManager.getInstance();
		fManager.setPath(pluginInstance.getDataFolder().getPath());
		dataManager = fManager;
		PlayerManager.getInstance().setPlayerFactory(
				new ACPlayerFactory(coreInstance.getDataFolder().getPath() + File.separator
						+ "userData"));
		WorldManager.getInstance().setWorldFactory(
				new ACWorldFactory(coreInstance.getDataFolder().getPath() + File.separator
						+ "worldData"));
		convertBannedMuted();
		convertSpawnWarp();
		fManager.getInnerFile("kits.yml");
		fManager.getInnerFile("ReadMe.txt", null, true);
		fManager.getInnerFile("AdminCmd.yml", "HelpFiles" + File.separator + "AdminCmd", true);
		fManager.getInnerFile("acmotd.yml", "HelpFiles" + File.separator + "AdminCmd", true);
		pluginConfig = new ExtendedConfiguration("config.yml", null);
		pluginConfig.load();
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
		pluginConfig.addProperty("superBreakerItem", Material.DIAMOND_PICKAXE.getId());
		pluginConfig.addProperty("DisplayNewsOnJoin", true);
		pluginConfig.addProperty("DisplayRulesOnJoin", true);
		pluginConfig.addProperty("DisplayRulesOnlyOnFirstJoin", false);
		pluginConfig.addProperty("DateAndTime.Format", "E, dd/MM/yy '-' HH:mm:ss");
		pluginConfig.addProperty("DateAndTime.GMToffset", "+00:00");
		pluginConfig.addProperty("useImmunityLvl", false);
		pluginConfig.addProperty("defaultImmunityLvl", 0);
		pluginConfig.addProperty("maxItemAmount", 0);
		pluginConfig.addProperty("useDisplayName", true);
		pluginConfig.addProperty("globalRespawnSetting", "globalSpawn");
		pluginConfig.addProperty("groupNames", Arrays.asList("default", "mod", "admin"));
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
		if (pluginConfig.getProperty("glinding") != null) {
			pluginConfig.addProperty("gliding.multiplicator",
					getConfFloat("glinding.multiplicator"));
			pluginConfig.addProperty("gliding.YvelocityCheckToGlide",
					getConfFloat("glinding.YvelocityCheckToGlide"));
			pluginConfig.addProperty("gliding.newYvelocity", getConfFloat("glinding.newYvelocity"));
			pluginConfig.removeProperty("glinding");

		} else {
			pluginConfig.addProperty("gliding.multiplicator", 0.1F);
			pluginConfig.addProperty("gliding.YvelocityCheckToGlide", -0.2F);
			pluginConfig.addProperty("gliding.newYvelocity", -0.5F);
		}
		if (pluginConfig.getProperty("respawnAtSpawnPoint") != null)
			pluginConfig.removeProperty("respawnAtSpawnPoint");
		pluginConfig.save();
		ExtendedConfiguration commands = new ExtendedConfiguration("commands.yml", null);
		commands.load();
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
	 * Get String parameter from config.
	 * 
	 * @param path
	 * @return
	 */
	public String getConfString(String path) {
		return pluginConfig.getString(path, "");
	}

	/**
	 * Get List<String> groups.
	 * 
	 * @return
	 */
	public List<String> getGroupList() {
		return groups;
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
	public AbstractAdminCmdPlugin getCoreInstance() {
		return coreInstance;
	}

	// teleports chosen player to another player

	/**
	 * Add an item to the Command BlackList
	 * 
	 * @param name
	 * @return
	 */
	public boolean addBlackListedItem(CommandSender sender, String name) {
		MaterialContainer m = checkMaterial(sender, name);
		if (!m.isNull()) {
			ExtendedConfiguration config = fManager.getYml("blacklist");
			List<Integer> list = config.getIntList("BlackListedItems", null);
			if (list == null)
				list = new ArrayList<Integer>();
			list.add(m.getMaterial().getId());
			config.setProperty("BlackListedItems", list);
			config.save();
			if (itemBlacklist == null)
				itemBlacklist = new ArrayList<Integer>();
			itemBlacklist.add(m.getMaterial().getId());
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", m.getMaterial().toString());
			Utils.sI18n(sender, "addBlacklistItem", replace);
			return true;
		}
		return false;
	}

	/**
	 * Add an item to the Command BlackList
	 * 
	 * @param name
	 * @return
	 */
	public boolean addBlackListedBlock(CommandSender sender, String name) {
		MaterialContainer m = checkMaterial(sender, name);
		if (!m.isNull()) {
			ExtendedConfiguration config = fManager.getYml("blacklist");
			List<Integer> list = config.getIntList("BlackListedBlocks", null);
			if (list == null)
				list = new ArrayList<Integer>();
			list.add(m.getMaterial().getId());
			config.setProperty("BlackListedBlocks", list);
			config.save();
			if (blockBlacklist == null)
				blockBlacklist = new ArrayList<Integer>();
			blockBlacklist.add(m.getMaterial().getId());
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", m.getMaterial().toString());
			Utils.sI18n(sender, "addBlacklistBlock", replace);
			return true;
		}
		return false;
	}

	/**
	 * Set the spawn point.
	 */
	public void setSpawn(CommandSender sender) {
		if (Utils.isPlayer(sender)) {
			Location loc = ((Player) sender).getLocation();
			World w = ((Player) sender).getWorld();
			w.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			ACWorld.getWorld(w.getName()).setSpawn(loc);
			Utils.sI18n(sender, "setSpawn");
		}
	}

	public void spawn(CommandSender sender) {
		if (Utils.isPlayer(sender)) {
			Player player = ((Player) sender);
			Location loc = null;
			String worldName = player.getWorld().getName();
			loc = ACWorld.getWorld(worldName).getSpawn();
			if (loc == null)
				loc = player.getWorld().getSpawnLocation();
			player.teleport(loc);
			Utils.sI18n(sender, "spawn");
		}
	}

	public void groupSpawn(CommandSender sender) {
		if (Utils.isPlayer(sender)) {
			Player player = ((Player) sender);
			Location loc = null;
			String worldName = player.getWorld().getName();
			if (groups.isEmpty()) {
				loc = ACWorld.getWorld(worldName).getSpawn();
				if (loc == null)
					loc = player.getWorld().getSpawnLocation();
				player.teleport(loc);
				Utils.sI18n(sender, "spawn");
				return;
			}
			for (String groupName : groups) {
				try {
					if (PermissionManager.isInGroup(groupName, player))
						loc = ACWorld.getWorld(worldName)
								.getWarp("spawn" + groupName.toLowerCase());
					break;
				} catch (NoPermissionsPlugin e) {
					loc = ACWorld.getWorld(worldName).getSpawn();
					break;
				}
			}

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
			ExtendedConfiguration config = fManager.getYml("blacklist");
			List<Integer> list = config.getIntList("BlackListedItems", new ArrayList<Integer>());
			if (!list.isEmpty() && list.contains(m.getMaterial().getId())) {
				list.remove((Integer) m.getMaterial().getId());
				config.setProperty("BlackListedItems", list);
				config.save();
			}
			if (itemBlacklist != null && !itemBlacklist.isEmpty()
					&& itemBlacklist.contains(m.getMaterial().getId()))
				itemBlacklist.remove((Integer) m.getMaterial().getId());
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("getMaterial()", m.getMaterial().toString());
			Utils.sI18n(sender, "rmBlacklist", replace);
			return true;
		}
		return false;
	}

	/**
	 * remove a black listed block
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeBlackListedBlock(CommandSender sender, String name) {
		MaterialContainer m = checkMaterial(sender, name);
		if (!m.isNull()) {
			ExtendedConfiguration config = fManager.getYml("blacklist");
			List<Integer> list = config.getIntList("BlackListedBlocks", new ArrayList<Integer>());
			if (!list.isEmpty() && list.contains(m.getMaterial().getId())) {
				list.remove((Integer) m.getMaterial().getId());
				config.setProperty("BlackListedBlocks", list);
				config.save();
			}
			if (itemBlacklist != null && !itemBlacklist.isEmpty()
					&& itemBlacklist.contains(m.getMaterial().getId()))
				itemBlacklist.remove((Integer) m.getMaterial().getId());
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
		return fManager.getYml("blacklist")
				.getIntList("BlackListedItems", new ArrayList<Integer>());
	}

	/**
	 * Get the blacklisted blocks
	 * 
	 * @return
	 */
	private List<Integer> getBlackListedBlocks() {
		return fManager.getYml("blacklist").getIntList("BlackListedBlocks",
				new ArrayList<Integer>());
	}

	/**
	 * Get the Permission group names
	 * 
	 * @return
	 */
	private List<String> getGroupNames() {
		return fManager.getYml("config").getStringList("groupNames", new ArrayList<String>());
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

	/**
	 * Put a player into the Map, so that the message reciever can use /reply
	 * 
	 * @param key
	 *            The Player to whom the message is send.
	 * @param value
	 *            The Player who sent the message.
	 */
	public void setReplyPlayer(Player key, Player value) {
		playersForReplyMessage.put(key, value);
	}

	/**
	 * Get the player to whom the reply message is sent to.
	 * 
	 * @param key
	 *            The player who wants to reply to a message.
	 * @return
	 */
	public Player getReplyPlayer(Player key) {
		return playersForReplyMessage.get(key);
	}

	/**
	 * Remove the Key-Value pair from the Map
	 * 
	 * @param key
	 */
	public void removeReplyPlayer(Player key) {
		playersForReplyMessage.remove(key);
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

	public boolean alias(CommandSender sender, CommandArgs args) {
		MaterialContainer m = checkMaterial(sender, args.getString(1));
		if (m.isNull())
			return true;
		String alias = args.getString(0);
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

	public boolean inBlackListItem(CommandSender sender, MaterialContainer mat) {
		if (!PermissionManager.hasPerm(sender, "admincmd.item.noblacklist", false)
				&& itemBlacklist.contains(mat.getMaterial().getId())) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat.display());
			Utils.sI18n(sender, "inBlacklistItem", replace);
			return true;
		}
		return false;
	}

	public boolean inBlackListItem(CommandSender sender, ItemStack mat) {
		if (!PermissionManager.hasPerm(sender, "admincmd.item.noblacklist", false)
				&& itemBlacklist.contains(mat.getTypeId())) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat.getType().toString());
			Utils.sI18n(sender, "inBlacklistItem", replace);
			return true;
		}
		return false;
	}

	public boolean inBlackListBlock(CommandSender sender, MaterialContainer mat) {
		if (!PermissionManager.hasPerm(sender, "admincmd.item.noblacklist", false)
				&& blockBlacklist.contains(mat.getMaterial().getId())) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat.display());
			Utils.sI18n(sender, "inBlacklistBlock", replace);
			return true;
		}
		return false;
	}

	public boolean inBlackListBlock(CommandSender sender, ItemStack mat) {
		if (!PermissionManager.hasPerm(sender, "admincmd.item.noblacklist", false)
				&& blockBlacklist.contains(mat.getTypeId())) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat.getType().toString());
			Utils.sI18n(sender, "inBlacklistBlock", replace);
			return true;
		}
		return false;
	}

	public synchronized void loadInfos() {
		itemBlacklist = getBlackListedItems();
		blockBlacklist = getBlackListedBlocks();
		groups = getGroupNames();

		alias.putAll(fManager.getAlias());

		Map<String, KitInstance> kitsLoaded = fManager.loadKits();
		for (String kit : kitsLoaded.keySet()) {
			kits.put(kit, kitsLoaded.get(kit));
			coreInstance.getPermissionLinker().addPermChild("admincmd.kit." + kit);
		}
		Map<String, BannedPlayer> bans = dataManager.loadBan();
		Date current = new Date(System.currentTimeMillis());
		for (final String key : bans.keySet()) {
			BannedPlayer player = bans.get(key);
			if (player instanceof TempBannedPlayer) {
				TempBannedPlayer temp = (TempBannedPlayer) player;
				if (temp.getEndBan().after(current)) {
					bannedPlayers.put(key, bans.get(key));
					int tickLeft = (int) ((temp.getEndBan().getTime() - System.currentTimeMillis()) / 1000) * 20;
					ACPluginManager.getScheduler().scheduleAsyncDelayedTask(coreInstance,
							new Runnable() {

								@Override
								public void run() {
									unBanPlayer(key);
								}
							}, tickLeft);

				} else {
					dataManager.unBanPlayer(key);
				}
			} else
				bannedPlayers.put(key, bans.get(key));
		}

		if (pluginConfig.getBoolean("verboseLog", true)) {
			Logger.getLogger("Minecraft").info(
					"[AdminCmd] " + itemBlacklist.size() + " blacklisted items loaded.");
			Logger.getLogger("Minecraft").info(
					"[AdminCmd] " + blockBlacklist.size() + " blacklisted blocks loaded.");
			Logger.getLogger("Minecraft").info("[AdminCmd] " + alias.size() + " alias loaded.");
			Logger.getLogger("Minecraft").info("[AdminCmd] " + kits.size() + " kits loaded.");
			Logger.getLogger("Minecraft").info(
					"[AdminCmd] " + bannedPlayers.size() + " banned players loaded.");
		}
	}

	public int getLimit(Player player, String type) {
		return getLimit(player, type, type);
	}

	public int getLimit(CommandSender sender, String type) {
		if (sender instanceof ConsoleCommandSender)
			return Integer.MAX_VALUE;
		return getLimit((Player) sender, type, type);
	}

	public int getLimit(Player player, String type, String defaultLvl) {
		Integer limit = null;
		String toParse = PermissionManager.getPermissionLimit(player, type);
		limit = toParse != null && !toParse.isEmpty() ? Integer.parseInt(toParse) : null;
		if (limit == null || limit == -1)
			limit = pluginConfig.getInt(defaultLvl, 0);
		if (limit == 0)
			limit = Integer.MAX_VALUE;
		return limit;
	}
	// ----- / item coloring section -----
}
