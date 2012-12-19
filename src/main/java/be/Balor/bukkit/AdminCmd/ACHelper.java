package be.Balor.bukkit.AdminCmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import lib.SQL.PatPeter.SQLibrary.Database;
import lib.SQL.PatPeter.SQLibrary.DatabaseConfig.DatabaseType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import be.Balor.Importer.IImport;
import be.Balor.Importer.ImportTools;
import be.Balor.Importer.Essentials.EssentialsImport;
import be.Balor.Manager.CommandManager;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.Ban;
import be.Balor.Player.BannedIP;
import be.Balor.Player.FilePlayer;
import be.Balor.Player.FilePlayerFactory;
import be.Balor.Player.IBan;
import be.Balor.Player.ITempBan;
import be.Balor.Player.PlayerManager;
import be.Balor.Player.sql.SQLPlayer;
import be.Balor.Player.sql.SQLPlayerFactory;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Blocks.BlockRemanence;
import be.Balor.Tools.Configuration.ExConfigurationSection;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Exceptions.InvalidInputException;
import be.Balor.Tools.Files.DataManager;
import be.Balor.Tools.Files.FileManager;
import be.Balor.Tools.Files.KitInstance;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.Tools.Help.HelpLoader;
import be.Balor.Tools.Lister.Lister;
import be.Balor.Tools.Threads.SetTimeTask;
import be.Balor.Tools.Threads.UnBanTask;
import be.Balor.Tools.Threads.UndoBlockTask;
import be.Balor.World.ACWorld;
import be.Balor.World.FileWorldFactory;
import be.Balor.World.WorldManager;
import be.Balor.World.sql.SQLWorld;
import be.Balor.World.sql.SQLWorldFactory;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

import com.google.common.collect.MapMaker;

/**
 * Handle commands
 * 
 * @authors Plague, Balor, Lathanael
 */
public class ACHelper {

	private final static HashMap<Material, String[]> materialsColors;
	private final static List<Integer> listOfPossibleRepair;
	private static long mainThreadID;

	/**
	 * Return the elapsed time.
	 * 
	 * @return
	 */
	public static Long[] getElapsedTime() {
		return Utils.getElapsedTime(pluginStarted);
	}

	public static ACHelper getInstance() {
		return instance;
	}

	static void killInstance() {
		final String dbWrap = ConfigEnum.DATA_WRAPPER.getString();
		if (dbWrap.equalsIgnoreCase("mysql")
				|| dbWrap.equalsIgnoreCase("sqlite")) {
			Database.DATABASE.close();
		}
		instance = null;
	}

	public static boolean isMainThread() {
		return mainThreadID == Thread.currentThread().getId();
	}

	private FileManager fManager;
	private final Set<MaterialContainer> itemBlacklist = new TreeSet<MaterialContainer>();
	private List<Integer> blockBlacklist;
	private List<String> groups;
	private AdminCmd coreInstance;
	private final ConcurrentMap<String, MaterialContainer> alias = new MapMaker()
			.makeMap();
	private Map<String, KitInstance> kits = new HashMap<String, KitInstance>();
	private Map<String, String> deathMessages = new HashMap<String, String>();
	private final ConcurrentMap<String, IBan> bannedPlayers = new MapMaker()
			.makeMap();
	private final ConcurrentMap<Player, Object> fakeQuitPlayers = new MapMaker()
			.makeMap();
	private final ConcurrentMap<Player, Object> spyPlayers = new MapMaker()
			.makeMap();
	private static ACHelper instance = new ACHelper();
	private final ConcurrentMap<String, Stack<Stack<BlockRemanence>>> undoQueue = new MapMaker()
			.makeMap();

	private static long pluginStarted;

	static {
		materialsColors = new HashMap<Material, String[]>();
		materialsColors.put(Material.WOOL, new String[] { "White", "Orange",
				"Magenta", "LightBlue", "Yellow", "LimeGreen", "Pink", "Gray",
				"LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red",
				"Black" });
		materialsColors.put(Material.INK_SACK, new String[] { "Black", "Red",
				"Green", "Brown", "Blue", "Purple", "Cyan", "LightGray",
				"Gray", "Pink", "LimeGreen", "Yellow", "LightBlue", "Magenta",
				"Orange", "White" });
		materialsColors.put(Material.LOG,
				new String[] { "Oak", "Pine", "Birch" });
		materialsColors.put(Material.STEP, new String[] { "Stone", "Sandstone",
				"Wooden", "Cobblestone" });
		materialsColors.put(Material.DOUBLE_STEP,
				materialsColors.get(Material.STEP));
		listOfPossibleRepair = new LinkedList<Integer>();
		for (int i = 256; i <= 259; i++) {
			listOfPossibleRepair.add(i);
		}
		for (int i = 267; i <= 279; i++) {
			listOfPossibleRepair.add(i);
		}
		for (int i = 283; i <= 286; i++) {
			listOfPossibleRepair.add(i);
		}
		for (int i = 290; i <= 294; i++) {
			listOfPossibleRepair.add(i);
		}
		for (int i = 298; i <= 317; i++) {
			listOfPossibleRepair.add(i);
		}
		listOfPossibleRepair.add(359);
		listOfPossibleRepair.add(Material.BOW.getId());
		listOfPossibleRepair.add(Material.FISHING_ROD.getId());
	}

	private ExtendedConfiguration pluginConfig;

	private DataManager dataManager;

	private boolean serverLocked = false;

	private final ConcurrentMap<Player, Player> playersForReplyMessage = new MapMaker()
			.makeMap();

	/**
	 * Ban a new player
	 * 
	 * @param ban
	 */
	public void banPlayer(final IBan ban) {
		bannedPlayers.put(ban.getPlayer(), ban);
		dataManager.addBan(ban);
		if (ban instanceof BannedIP) {
			ACPluginManager.getServer().banIP(ban.getPlayer());
		}
		final Lister list = Lister.getLister(Lister.List.BAN, false);
		if (list != null) {
			list.update();
		}
	}

	/**
	 * Add an item to the Command BlackList
	 * 
	 * @param name
	 * @return
	 */
	public boolean addBlackListedBlock(final CommandSender sender,
			final String name) {
		final MaterialContainer m = checkMaterial(sender, name);
		if (!m.isNull()) {
			final ExtendedConfiguration config = fManager.getYml("blacklist");
			List<Integer> list = config.getIntList("BlackListedBlocks", null);
			if (list == null) {
				list = new ArrayList<Integer>();
			}
			list.add(m.getMaterial().getId());
			config.set("BlackListedBlocks", list);
			try {
				config.save();
			} catch (final IOException e) {
			}
			if (blockBlacklist == null) {
				blockBlacklist = new ArrayList<Integer>();
			}
			blockBlacklist.add(m.getMaterial().getId());
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", m.getMaterial().toString());
			Utils.sI18n(sender, "addBlacklistBlock", replace);
			return true;
		}
		return false;
	}

	/**
	 * Add an item to the BlackList
	 * 
	 * @param name
	 *            string representing the item to blacklist
	 * @return
	 */
	public boolean addBlackListedItem(final CommandSender sender,
			final String name) {
		final MaterialContainer m = checkMaterial(sender, name);
		return addBlackListedItem(sender, m);

	}

	/**
	 * Add an item to the BlackList
	 * 
	 * @param sender
	 *            sender of the command
	 * @param item
	 *            itemstack to blacklist
	 * @return
	 */
	public boolean addBlackListedItem(final CommandSender sender,
			final ItemStack item) {
		final MaterialContainer m = new MaterialContainer(item);
		return addBlackListedItem(sender, m);

	}

	private boolean addBlackListedItem(final CommandSender sender,
			final MaterialContainer mat) {
		if (mat.isNull()) {
			return false;
		}

		if (!itemBlacklist.add(mat)) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("item", mat.display());
			LocaleHelper.BL_ITEM_ALREADY.sendLocale(sender, replace);
			return false;
		}
		final ExtendedConfiguration config = fManager.getYml("blacklist");
		config.set("BlackListedMaterial", itemBlacklist);
		try {
			config.save();
		} catch (final IOException e) {
			DebugLog.INSTANCE.log(Level.WARNING, "Can't save the blacklist", e);
			LocaleHelper.BL_ITEM_PROBLEM.sendLocale(sender);
			return false;
		}
		Utils.sI18n(sender, "addBlacklistItem", "material", mat.display());
		return true;
	}

	public void addFakeQuit(final Player p) {
		fakeQuitPlayers.put(p, new Object());
	}

	/**
	 * Add modified block in the undoQueue
	 * 
	 * @param blocks
	 */
	public void addInUndoQueue(final String player,
			final Stack<BlockRemanence> blocks) {
		if (undoQueue.containsKey(player)) {
			undoQueue.get(player).push(blocks);
		} else {
			final Stack<Stack<BlockRemanence>> blockQueue = new Stack<Stack<BlockRemanence>>();
			blockQueue.push(blocks);
			undoQueue.put(player, blockQueue);
		}

	}

	private void addLocaleFromFile() {
		String locale = fManager.getTextFile("motd.txt");
		if (locale == null) {
			ACLogger.info("Could not read motd.txt. Using default values for the MotD!");
			Utils.addLocale("MOTD", ChatColor.GOLD + "Welcome "
					+ ChatColor.WHITE + "%player" + ChatColor.GOLD
					+ ", there is currently " + ChatColor.DARK_RED
					+ "%nb players connected : //n" + ChatColor.GOLD
					+ "%connected //n" + ChatColor.DARK_GREEN
					+ "You've played so far : " + ChatColor.AQUA
					+ "#elapsedTotalTime# //n" + ChatColor.DARK_GREEN
					+ "Your last login was: " + ChatColor.AQUA + "%lastlogin",
					true);
		} else {
			ACLogger.info("motd.txt loaded");
			Utils.addLocale("MOTD", Utils.colorParser(locale), true);
		}
		locale = fManager.getTextFile("motdNewUser.txt");
		if (locale == null) {
			ACLogger.info("Could not read motdNewUser.txt. Using default values for the MotDNewUser!");
			Utils.addLocale("MOTDNewUser", ChatColor.GOLD + "Welcome "
					+ ChatColor.WHITE + "%player" + ChatColor.GOLD
					+ ", there is currently " + ChatColor.DARK_RED
					+ "%nb players connected : //n" + ChatColor.GOLD
					+ "%connected //n" + ChatColor.DARK_GREEN
					+ "You've played so far : " + ChatColor.AQUA
					+ "#elapsedTotalTime#", true);
		} else {
			ACLogger.info("motdNewUser.txt loaded");
			Utils.addLocale("MOTDNewUser", Utils.colorParser(locale), true);
		}
		locale = fManager.getTextFile("news.txt");
		if (locale == null) {
			ACLogger.info("Could not read news.txt. Using default values for the MotD!");
			Utils.addLocale("NEWS", ChatColor.DARK_GREEN
					+ "News : AdminCmd Plugin has been installed", true);
		} else {
			ACLogger.info("news.txt loaded");
			Utils.addLocale("NEWS", Utils.colorParser(locale), true);
		}
		locale = fManager.getTextFile("rules.txt");
		if (locale == null) {
			ACLogger.info("Could not read motdNewUser.txt. Using default values for the MotD!");
			Utils.addLocale("Rules", "1. Do not grief! //n"
					+ "2. Do not use strong language! //n"
					+ "3. Be friendly to other players!", true);
		} else {
			ACLogger.info("rules.txt loaded");
			Utils.addLocale("Rules", Utils.colorParser(locale), true);
		}
		LocaleManager.getInstance().save();
	}

	public void addSpy(final Player p) {
		spyPlayers.put(p, new Object());
	}

	public boolean alias(final CommandSender sender, final CommandArgs args) {
		final MaterialContainer m = checkMaterial(sender, args.getString(1));
		if (m.isNull()) {
			return true;
		}
		final String alias = args.getString(0);
		this.alias.put(alias, m);
		this.fManager.addAlias(alias, m);
		sender.sendMessage(ChatColor.BLUE + "You can now use " + ChatColor.GOLD
				+ alias + ChatColor.BLUE + " for the item " + ChatColor.GOLD
				+ m.display());
		return true;
	}

	/**
	 * Used to check if the Ban is a Temporary ban, to relaunch the task to
	 * unBan the player or unban him if his time out.
	 * 
	 * @param player
	 * @return true if the ban is valid, false if invalid (expired)
	 */
	private boolean checkBan(final IBan player) {
		if (player instanceof ITempBan) {
			final ITempBan tempBan = (ITempBan) player;
			final Long timeLeft = tempBan.timeLeft();
			if (timeLeft <= 0) {
				unBanPlayer(player);
				return false;
			} else {
				ACPluginManager.getScheduler().runTaskLaterAsynchronously(
						coreInstance, new UnBanTask(tempBan, true),
						timeLeft / Utils.secondInMillis * Utils.secInTick);
				return true;
			}
		} else {
			return true;
		}
	}

	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 */
	public MaterialContainer checkMaterial(final CommandSender sender,
			final String mat) {
		MaterialContainer m = null;
		try {
			m = Utils.checkMaterial(mat);
		} catch (final InvalidInputException e) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat);
			Utils.sI18n(sender, "unknownMat", replace);
			return new MaterialContainer();
		}
		if (m.isNull()) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat);
			Utils.sI18n(sender, "unknownMat", replace);
		}
		return m;

	}

	private void convertSpawnWarp() {
		final File spawnFile = fManager.getFile("spawn", "spawnLocations.yml",
				false);
		if (spawnFile.exists()) {
			final ExtendedConfiguration spawn = ExtendedConfiguration
					.loadConfiguration(spawnFile);
			final ConfigurationSection spawnPoints = spawn
					.getConfigurationSection("spawn");
			if (spawnPoints != null) {
				for (final String key : spawnPoints.getKeys(false)) {
					try {
						ACWorld.getWorld(key).setSpawn(
								fManager.getLocation("spawn." + key,
										"spawnLocations", "spawn"));
					} catch (final WorldNotLoaded e) {
					}
				}
			}

			spawnFile.delete();
			spawnFile.getParentFile().delete();
		}
		final File warpFile = fManager.getFile("warp", "warpPoints.yml", false);
		if (warpFile.exists()) {
			for (final String key : fManager.getKeys("warp", "warpPoints",
					"warp")) {
				try {
					final Location loc = fManager.getLocation("warp." + key,
							"warpPoints", "warp");
					ACWorld.getWorld(loc.getWorld().getName())
							.addWarp(key, loc);
				} catch (final WorldNotLoaded e) {
				}
			}
			warpFile.delete();
			warpFile.getParentFile().delete();
		}
	}

	public int countBannedPlayers() {
		return bannedPlayers.size();
	}

	public int countBlackListedItems() {
		return itemBlacklist.size();
	}

	public Collection<IBan> getBannedPlayers() {
		return bannedPlayers.values();
	}

	public MaterialContainer getAlias(final String name) {
		return alias.get(name);
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
	 * Get the blacklisted items
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Set<MaterialContainer> getBlackListedItems() {
		return (Set<MaterialContainer>) fManager.getYml("blacklist").get(
				"BlackListedMaterial", new TreeSet<MaterialContainer>());
	}

	/*
	 * private void convertBannedMuted() { Map<String, Object> map =
	 * fManager.loadMap(Type.BANNED, null, Type.BANNED.toString()); if
	 * (!map.isEmpty()) { fManager.getFile(null, "banned.yml", false).delete();
	 * for (String key : map.keySet()) dataManager.addBannedPlayer(new
	 * BannedPlayer(key, String.valueOf(map.get(key)))); } File muted =
	 * fManager.getFile(null, "muted.yml", false); if (muted.exists()) { map =
	 * fManager.loadMap(Type.MUTED, null, Type.MUTED.toString()); for (String
	 * key : map.keySet()) { PlayerManager.getInstance().setOnline(key);
	 * ACPlayer.getPlayer(key).setPower(Type.MUTED, map.get(key)); }
	 * muted.delete(); } }
	 */

	// translates a given color value/name into a real color value
	// also does some checking (error = -1)
	private short getColor(final String name, final Material mat) {
		short value = -1;
		// first try numbered colors
		try {
			value = Short.parseShort(name);
		} catch (final Exception e) {
			// try to find the name then
			for (short i = 0; i < materialsColors.get(mat).length; ++i) {
				if (materialsColors.get(mat)[i].equalsIgnoreCase(name)) {
					value = i;
					break;
				}
			}
		}
		// is the value OK?
		if (value < 0 || value >= materialsColors.get(mat).length) {
			return -1;
		}
		return value;
	}

	/**
	 * @return the pluginInstance
	 */
	public AbstractAdminCmdPlugin getCoreInstance() {
		return coreInstance;
	}

	public Set<Player> getFakeQuitPlayers() {
		return fakeQuitPlayers.keySet();
	}

	/**
	 * Gets the List< String> of groups defined in the config.
	 * 
	 * @return
	 */
	public List<String> getGroupList() {
		return Collections.unmodifiableList(groups);
	}

	/**
	 * Get the Permission group names
	 * 
	 * @return
	 */
	private List<String> getGroupNames() {
		final List<String> gFileList = fManager.getYml("config").getStringList(
				"groupNames", new ArrayList<String>());
		final List<String> gNewList = new ArrayList<String>();
		for (final String item : gFileList) {
			gNewList.add(item.toLowerCase());
		}
		return gNewList;
	}

	/**
	 * Get KitInstance for given kit
	 * 
	 * @param kit
	 * @return
	 */
	public KitInstance getKit(final String kit) {
		return kits.get(kit);
	}

	/**
	 * Get the list of kit.
	 * 
	 * @return
	 */
	public String getKitList(final CommandSender sender) {
		String kitList = "";
		final HashSet<String> list = new HashSet<String>();
		try {
			list.addAll(kits.keySet());
			if (Utils.oddItem != null) {
			}

		} catch (final Throwable e) {
		}

		for (final String kit : list) {
			if (PermissionManager.hasPerm(sender, "admincmd.kit." + kit, false)) {
				kitList += kit + ", ";
			}
		}
		if (!kitList.equals("")) {
			if (kitList.endsWith(", ")) {
				kitList = kitList.substring(0, kitList.lastIndexOf(","));
			}
		}
		return kitList.trim();
	}

	public int getLimit(final CommandSender sender, final Type.Limit type) {
		if (sender instanceof ConsoleCommandSender) {
			return Integer.MAX_VALUE;
		}
		return getLimit((Player) sender, type, type.toString());
	}

	public int getLimit(final Player player, final Type.Limit type) {
		return getLimit(player, type, type.toString());
	}

	// teleports chosen player to another player

	public int getLimit(final Player player, final Type.Limit type,
			final String defaultLvl) {
		Integer limit = null;
		final String toParse = PermissionManager.getPermissionLimit(player,
				type.toString());
		limit = toParse != null && !toParse.isEmpty() ? Integer
				.parseInt(toParse) : null;
		if (limit == null || limit == -1) {
			limit = pluginConfig.getInt(defaultLvl, 0);
		}
		if (limit == 0 && type != Type.Limit.IMMUNITY) {
			limit = Integer.MAX_VALUE;
		}
		return limit;
	}

	// ----- / item coloring section -----

	/**
	 * Get the number of kit in the system.
	 * 
	 * @return
	 */
	public int getNbKit() {
		return kits.size();
	}

	/**
	 * Get the player to whom the reply message is sent to.
	 * 
	 * @param key
	 *            The player who wants to reply to a message.
	 * @return
	 */
	public Player getReplyPlayer(final Player key) {
		return playersForReplyMessage.get(key);
	}

	public Set<Player> getSpyPlayers() {
		return spyPlayers.keySet();
	}

	/**
	 * Teleports a player to the GroupSpawn location belonging to his group
	 * defined in his permission node.
	 * 
	 * @param player
	 *            - The player who should be teleported
	 * @author Lathanael
	 */
	public void groupSpawn(final Player player) {
		final Location loc = getGroupSpawnLocation(player);
		player.teleport(loc);
		Utils.sI18n(player, "spawn");
	}

	/**
	 * Gets the spawn location of a group.
	 * 
	 * @param player
	 *            - The player whose group is to be checked
	 * @return The {@code Location} of the Spawn or the worlds spawn if none is
	 *         found.
	 * @author Lathanael
	 */
	public Location getGroupSpawnLocation(final Player player) {
		Location loc = null;
		final ACWorld acWorld = ACWorld.getWorld(player.getWorld());
		if (groups.isEmpty()) {
			loc = acWorld.getSpawn();
			if (loc == null) {
				loc = player.getWorld().getSpawnLocation();
			}
			return loc;
		}
		for (final String groupName : groups) {
			if (PermissionManager.hasPerm(player, "admincmd.respawn."
					+ groupName)) {
				loc = acWorld.getGroupSpawn(groupName);
				break;
			}
		}
		if (loc == null) {
			loc = player.getWorld().getSpawnLocation();
		}
		return loc;
	}

	public boolean inBlackListBlock(final CommandSender sender,
			final ItemStack mat) {
		if (!PermissionManager.hasPerm(sender, "admincmd.spec.noblacklist",
				false) && blockBlacklist.contains(mat.getTypeId())) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat.getType().toString());
			Utils.sI18n(sender, "inBlacklistBlock", replace);
			return true;
		}
		return false;
	}

	public boolean inBlackListBlock(final CommandSender sender,
			final MaterialContainer mat) {
		if (!PermissionManager.hasPerm(sender, "admincmd.spec.noblacklist",
				false) && blockBlacklist.contains(mat.getMaterial().getId())) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", mat.display());
			Utils.sI18n(sender, "inBlacklistBlock", replace);
			return true;
		}
		return false;
	}

	public boolean inBlackListItem(final CommandSender sender,
			final ItemStack mat) {
		return inBlackListItem(sender, new MaterialContainer(mat));
	}

	public boolean inBlackListItem(final CommandSender sender,
			final MaterialContainer mat) {
		if (PermissionManager.hasPerm(sender, "admincmd.spec.noblacklist",
				false)) {
			return false;
		}
		if (!itemBlacklist.contains(mat)) {
			return false;
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("material", mat.display());
		Utils.sI18n(sender, "inBlacklistItem", replace);

		return true;
	}

	/**
	 * Same code used when reload and onEnable
	 */
	private void init() {
		AFKWorker.createInstance();
		if (pluginConfig.getBoolean("autoAfk", true)) {
			AFKWorker.getInstance().setAfkTime(
					pluginConfig.getInt("afkTimeInSecond", 60));
			AFKWorker.getInstance().setKickTime(
					pluginConfig.getInt("afkKickInMinutes", 3));

			this.coreInstance
					.getServer()
					.getScheduler()
					.runTaskTimerAsynchronously(this.coreInstance,
							AFKWorker.getInstance().getAfkChecker(), 0,
							pluginConfig.getInt("statutCheckInSec", 20) * 20);
			if (pluginConfig.getBoolean("autoKickAfkPlayer", false)) {
				this.coreInstance
						.getServer()
						.getScheduler()
						.runTaskTimerAsynchronously(
								this.coreInstance,
								AFKWorker.getInstance().getKickChecker(),
								0,
								pluginConfig.getInt("statutCheckInSec", 20) * 20);
			}
		}
		final ExtendedConfiguration localeFile = ExtendedConfiguration
				.loadConfiguration(new File(coreInstance.getDataFolder(),
						"locales" + File.separator
								+ pluginConfig.getString("locale", "en_US")
								+ ".yml"));
		final ExtendedConfiguration defaultLocale = ExtendedConfiguration
				.loadConfiguration(fManager.getInnerFile("default_locale.yml",
						"locales", true));
		final Properties props = new Properties();
		try {
			props.load(this.getClass().getResourceAsStream("/git.properties"));
		} catch (final IOException e1) {
		}
		defaultLocale.options()
				.header("version: " + props.getProperty("git.commit.id"))
				.copyHeader(true);
		localeFile.setDefaults(defaultLocale);
		localeFile.options().copyHeader(false);
		LocaleManager.getInstance().addLocaleFile(LocaleManager.DEFAULT_LOCALE,
				defaultLocale);
		LocaleManager.getInstance().addLocaleFile(LocaleManager.PRIMARY_LOCALE,
				localeFile);
		LocaleManager.getInstance().addLocaleFile(
				"kickMessages",
				ExtendedConfiguration.loadConfiguration(fManager.getInnerFile(
						"kickMessages.yml", "locales", false)));
		LocaleManager.getInstance().setNoMsg(
				pluginConfig.getBoolean("noMessage", false));
		HelpLoader.load(coreInstance.getDataFolder());
		CommandManager.createInstance().setCorePlugin(coreInstance);
		if (pluginConfig.get("pluginStarted") != null) {
			pluginStarted = Long.parseLong(pluginConfig
					.getString("pluginStarted"));
			pluginConfig.remove("pluginStarted");
			try {
				pluginConfig.save();
			} catch (final IOException e) {
			}
		} else {
			pluginStarted = System.currentTimeMillis();
		}
		for (final Player p : coreInstance.getServer().getOnlinePlayers()) {
			PlayerManager.getInstance().setOnline(p);
		}
		// TODO : Don't forget to check if the admin use a MySQL database or the
		// file system
		if (!isSqlWrapper()) {
			FilePlayer.scheduleAsyncSave();
		}
		if (pluginConfig.getBoolean("tpRequestActivatedByDefault", false)) {
			for (final Player p : coreInstance.getServer().getOnlinePlayers()) {
				ACPlayer.getPlayer(p).setPower(Type.TP_REQUEST);
			}
		}
		for (final World w : coreInstance.getServer().getWorlds()) {
			final ACWorld world = ACWorld.getWorld(w);
			int task = world.getInformation(Type.TIME_FREEZED.toString())
					.getInt(-1);
			if (task != -1) {
				task = ACPluginManager.getScheduler()
						.scheduleSyncRepeatingTask(
								ACHelper.getInstance().getCoreInstance(),
								new SetTimeTask(w), 0, 5L);
				world.setInformation(Type.TIME_FREEZED.toString(), task);
			}
		}
	}

	/**
	 * Return the ban of the player
	 * 
	 * @param player
	 *            player's name
	 * @return the ban if the player have one, else return null
	 */
	public IBan getBan(final String player) {
		return bannedPlayers.get(player);
	}

	/**
	 * @return the serverLocked
	 */
	public boolean isServerLocked() {
		return serverLocked;
	}

	// changes the color of a colorable item in hand
	public boolean itemColor(final CommandSender sender, final String color) {
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
			final Material m = ((Player) sender).getItemInHand().getType();

			if (materialsColors.containsKey(m)) {
				value = getColor(color, m);
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You must hold a colorable material!");
				return true;
			}
			// error?
			if (value < 0) {
				sender.sendMessage(ChatColor.RED + "Color " + ChatColor.WHITE
						+ color + ChatColor.RED
						+ " is not usable for what you're holding!");
				return true;
			}

			final Player player = (Player) sender;
			final short colorVal = value;
			ACPluginManager.scheduleSyncTask(new Runnable() {
				@Override
				public void run() {
					player.getItemInHand().setDurability(colorVal);

				}
			});
		}
		return true;
	}

	public synchronized void loadInfos() {
		itemBlacklist.addAll(getBlackListedItems());
		blockBlacklist = getBlackListedBlocks();
		groups = getGroupNames();

		alias.putAll(fManager.getAlias());

		addLocaleFromFile();

		kits = fManager.loadKits();
		deathMessages = fManager.loadDeathMessages();
		final Map<String, Ban> bans = dataManager.loadBan();
		for (final Entry<String, Ban> ban : bans.entrySet()) {
			if (checkBan(ban.getValue())) {
				bannedPlayers.put(ban.getKey(), ban.getValue());
			}
		}

		if (pluginConfig.getBoolean("verboseLog", true)) {
			final Logger logger = coreInstance.getLogger();
			logger.info(itemBlacklist.size() + " blacklisted items loaded.");
			logger.info(blockBlacklist.size() + " blacklisted blocks loaded.");
			logger.info(alias.size() + " alias loaded.");
			logger.info(kits.size() + " kits loaded.");
			logger.info(bannedPlayers.size() + " banned players loaded.");
			logger.info(deathMessages.size() + " death messages loaded.");
		}
	}

	// returns all members of the color name array concatenated with commas
	private String printColors(final Material mat) {
		String output = "";
		for (int i = 0; i < materialsColors.get(mat).length; ++i) {
			output += materialsColors.get(mat)[i] + ", ";
		}
		return output;
	}

	private boolean isSqlWrapper() {
		final String dWrapper = ConfigEnum.DATA_WRAPPER.getString();
		return dWrapper.equalsIgnoreCase("mysql")
				|| dWrapper.equalsIgnoreCase("sqlite");
	}

	/**
	 * Reload the "plugin"
	 */
	public synchronized void reload() {
		coreInstance.getServer().getScheduler().cancelTasks(coreInstance);
		// TODO: check datawrapper
		if (!isSqlWrapper()) {
			FilePlayer.forceSaveList();
		}
		alias.clear();
		itemBlacklist.clear();
		blockBlacklist.clear();
		groups.clear();
		undoQueue.clear();
		try {
			pluginConfig.reload();
		} catch (final FileNotFoundException e) {
			ACLogger.severe("Config Reload Problem :", e);
		} catch (final IOException e) {
			ACLogger.severe("Config Reload Problem :", e);
		} catch (final InvalidConfigurationException e) {
			ACLogger.severe("Config Reload Problem :", e);
		}
		bannedPlayers.clear();

		loadInfos();
		for (final Player p : InvisibleWorker.getInstance()
				.getAllInvisiblePlayers()) {
			InvisibleWorker.getInstance().reappear(p);
		}
		InvisibleWorker.killInstance();
		AFKWorker.killInstance();
		CommandManager.killInstance();
		HelpLister.killInstance();
		System.gc();
		init();
		CommandManager.getInstance().registerACPlugin(coreInstance);
		coreInstance.registerCmds();
		CommandManager.getInstance().checkAlias(coreInstance);
		if (ConfigEnum.H_ALLPLUGIN.getBoolean()) {
			for (final Plugin plugin : coreInstance.getServer()
					.getPluginManager().getPlugins()) {
				HelpLister.getInstance().addPlugin(plugin);
			}
		}
		if (pluginConfig.getBoolean("autoAfk", true)) {
			for (final Player p : Utils.getOnlinePlayers()) {
				AFKWorker.getInstance().updateTimeStamp(p);
			}
		}
	}

	/**
	 * remove a black listed block
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeBlackListedBlock(final CommandSender sender,
			final String name) {
		final MaterialContainer m = checkMaterial(sender, name);
		if (!m.isNull()) {
			final ExtendedConfiguration config = fManager.getYml("blacklist");
			final List<Integer> list = config.getIntList("BlackListedBlocks",
					new ArrayList<Integer>());
			if (!list.isEmpty() && list.contains(m.getMaterial().getId())) {
				list.remove((Integer) m.getMaterial().getId());
				config.set("BlackListedBlocks", list);
				try {
					config.save();
				} catch (final IOException e) {
				}
			}
			if (itemBlacklist != null && !itemBlacklist.isEmpty()
					&& itemBlacklist.contains(m.getMaterial().getId())) {
				itemBlacklist.remove(m.getMaterial().getId());
			}
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("material", m.getMaterial().toString());
			Utils.sI18n(sender, "rmBlacklistBlock", replace);
			return true;
		}
		return false;
	}

	// ----- / item coloring section -----

	/**
	 * remove a black listed item
	 * 
	 * @param sender
	 *            sender of the command
	 * @param name
	 *            string used to determine the material to blacklist
	 * @return
	 */
	public boolean removeBlackListedItem(final CommandSender sender,
			final String name) {
		final MaterialContainer m = checkMaterial(sender, name);
		return removeBlackListedItem(sender, m);
	}

	/**
	 * remove a black listed item
	 * 
	 * @param sender
	 *            sender of the command
	 * @param item
	 *            itemstack to remove from the blackList
	 * @return
	 */
	public boolean removeBlackListedItem(final CommandSender sender,
			final ItemStack item) {
		final MaterialContainer m = new MaterialContainer(item);
		return removeBlackListedItem(sender, m);
	}

	private boolean removeBlackListedItem(final CommandSender sender,
			final MaterialContainer mat) {
		if (mat.isNull()) {
			return false;
		}
		if (!itemBlacklist.remove(mat)) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("item", mat.display());
			LocaleHelper.BL_ITEM_NOT_BLISTED.sendLocale(sender, replace);
			return false;
		}
		final ExtendedConfiguration config = fManager.getYml("blacklist");
		config.set("BlackListedMaterial", itemBlacklist);
		try {
			config.save();
		} catch (final IOException e) {
			DebugLog.INSTANCE.log(Level.WARNING, "Can't save the blacklist", e);
			LocaleHelper.BL_ITEM_PROBLEM.sendLocale(sender);
			return false;
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("material", mat.getMaterial().toString());
		Utils.sI18n(sender, "rmBlacklistItem", replace);
		return true;
	}

	public void removeDisconnectedPlayer(final Player player) {
		AFKWorker.getInstance().removePlayer(player);
		fakeQuitPlayers.remove(player);
		playersForReplyMessage.remove(player);
		spyPlayers.remove(player);
		InvisibleWorker.getInstance().onQuitEvent(player);
	}

	public void removeFakeQuit(final Player p) {
		fakeQuitPlayers.remove(p);
	}

	/**
	 * Remove the Key-Value pair from the Map
	 * 
	 * @param key
	 */
	public void removeReplyPlayer(final Player key) {
		if (key == null) {
			return;
		}
		playersForReplyMessage.remove(key);
	}

	public void removeSpy(final Player p) {
		if (p == null) {
			return;
		}
		spyPlayers.remove(p);
	}

	public boolean repairable(final int id) {
		return listOfPossibleRepair.contains(id);
	}

	public boolean rmAlias(final CommandSender sender, final String alias) {
		this.fManager.removeAlias(alias);
		this.alias.remove(alias);
		sender.sendMessage(ChatColor.GOLD + alias + ChatColor.RED + " removed");
		return true;
	}

	/**
	 * Save elapsed time when reload
	 */
	public void saveElapsedTime() {
		pluginConfig.set("pluginStarted", pluginStarted);
		try {
			pluginConfig.save();
		} catch (final IOException e) {
		}
	}

	/**
	 * @param pluginInstance
	 *            the pluginInstance to set
	 */
	public void setCoreInstance(final AdminCmd pluginInstance) {
		mainThreadID = Thread.currentThread().getId();
		ACPluginManager.setCorePlugin(pluginInstance);
		this.coreInstance = pluginInstance;
		fManager = FileManager.getInstance();
		fManager.setPath(pluginInstance.getDataFolder().getPath());
		dataManager = fManager;

		// convertBannedMuted();
		convertSpawnWarp();
		fManager.getInnerFile("kits.yml");
		fManager.getInnerFile("deathMessages.yml");
		fManager.getInnerFile("ReadMe.txt", null, true);
		fManager.getInnerFile("LiesMich.txt", null, true);
		fManager.getInnerFile("AdminCmd.yml", "HelpFiles" + File.separator
				+ "AdminCmd", true);
		pluginConfig = ExtendedConfiguration.loadConfiguration(new File(
				coreInstance.getDataFolder(), "config.yml"));
		TextLocale.setVersion(ExtendedConfiguration.loadConfiguration(new File(
				new File(coreInstance.getDataFolder(), "locales"),
				"textFile.yml")));
		ConfigEnum.setPluginInfos(pluginInstance.getDescription());
		ConfigEnum.setConfig(pluginConfig);

		dataWrapperInit();

		if (ConfigEnum.IMPORT_ESSENTIALS.getBoolean()) {
			final IImport importer = new EssentialsImport(
					ImportTools.getPluginsFolder(coreInstance.getDataFolder()));
			importer.initImport();
		}
		List<String> disabled = new ArrayList<String>();
		List<String> priority = new ArrayList<String>();
		if (pluginConfig.get("disabledCommands") != null) {
			disabled = pluginConfig.getStringList("disabledCommands", disabled);
			pluginConfig.remove("disabledCommands");
		}
		if (pluginConfig.get("prioritizedCommands") != null) {
			priority = pluginConfig.getStringList("prioritizedCommands",
					priority);
			pluginConfig.remove("prioritizedCommands");
		}
		if (pluginConfig.get("respawnAtSpawnPoint") != null) {
			pluginConfig.remove("respawnAtSpawnPoint");
		}
		if (!pluginConfig.getBoolean("debug")) {
			DebugLog.stopLogging();
		}
		final ExtendedConfiguration commands = ExtendedConfiguration
				.loadConfiguration(new File(coreInstance.getDataFolder(),
						"commands.yml"));
		commands.add("disabledCommands", disabled);
		commands.add("prioritizedCommands",
				priority.isEmpty() ? Arrays.asList("reload", "/", "stop")
						: priority);
		final ExConfigurationSection aliases = commands.addSection("aliases");
		final ExConfigurationSection god = aliases.addSection("god");
		god.add("gg", "");
		god.add("gd", "");
		final ExConfigurationSection fly = aliases.addSection("fly");
		fly.add("ofly", "-o");
		final ExConfigurationSection egg = aliases.addSection("egg");
		egg.add("grenade", "-E ExplosionEgg");
		commands.add("onNewJoin", Arrays.asList("/kit dirt", "/motd"));
		try {
			commands.save();
		} catch (final IOException e) {
		}
		init();
	}

	/**
	 * Put a player into the Map, so that the message reciever can use /reply
	 * 
	 * @param key
	 *            The Player to whom the message is send.
	 * @param value
	 *            The Player who sent the message.
	 */
	public void setReplyPlayer(final Player key, final Player value) {
		playersForReplyMessage.put(key, value);
	}

	/**
	 * @param serverLocked
	 *            the serverLocked to set
	 */
	public void setServerLocked(final boolean serverLocked) {
		this.serverLocked = serverLocked;
	}

	/**
	 * Set the spawn point.
	 */
	public void setSpawn(final CommandSender sender) {
		if (Utils.isPlayer(sender)) {
			final Location loc = ((Player) sender).getLocation();
			final World w = loc.getWorld();
			ACPluginManager.scheduleSyncTask(new Runnable() {
				@Override
				public void run() {
					w.setSpawnLocation(loc.getBlockX(), loc.getBlockY(),
							loc.getBlockZ());
				}
			});

			ACWorld.getWorld(w.getName()).setSpawn(loc);
			Utils.sI18n(sender, "setSpawn");
		}
	}

	public void spawn(final Player player) {
		spawn(player, null);
	}

	public void spawn(final Player player, final ACWorld world) {
		Location loc = null;
		if (world == null) {
			final String worldName = player.getWorld().getName();
			loc = ACWorld.getWorld(worldName).getSpawn();
		} else {
			loc = world.getSpawn();
		}
		if (loc == null) {
			loc = player.getWorld().getSpawnLocation();
		}
		Utils.teleportWithChunkCheck(player, loc);
	}

	/**
	 * Unban the player
	 * 
	 * @param ban
	 */
	public void unBanPlayer(final IBan ban) {
		bannedPlayers.remove(ban.getPlayer());
		dataManager.unBanPlayer(ban);
		if (ban instanceof BannedIP) {
			ACPluginManager.getServer().unbanIP(ban.getPlayer());
		}
		final Lister list = Lister.getLister(Lister.List.BAN, false);
		if (list != null) {
			list.update();
		}
	}

	public int undoLastModification(final String player)
			throws EmptyStackException {
		if (!undoQueue.containsKey(player)) {
			throw new EmptyStackException();
		}
		final Stack<Stack<BlockRemanence>> blockQueue = undoQueue.get(player);
		if (blockQueue.isEmpty()) {
			throw new EmptyStackException();
		}
		final Stack<BlockRemanence> undo = blockQueue.pop();
		final Stack<BlockRemanence> undoCache = new Stack<BlockRemanence>();
		int i = 0;
		try {
			while (!undo.isEmpty()) {
				undoCache.push(undo.pop());
				if (undoCache.size() == Utils.MAX_BLOCKS) {
					ACPluginManager.getScheduler().scheduleSyncDelayedTask(
							coreInstance, new UndoBlockTask(undoCache), 1);
				}
				i++;
			}

		} catch (final Exception e) {
			ACLogger.severe(e.getMessage(), e);
			return i;
		} finally {
			ACPluginManager.getScheduler().scheduleSyncDelayedTask(
					coreInstance, new UndoBlockTask(undoCache), 1);
		}
		return i;
	}

	public String getDeathMessage(final String reason) {
		return deathMessages.get(reason);
	}

	private void dataWrapperInit() {
		if (isSqlWrapper()) {
			try {
				createTable();
				PlayerManager.getInstance().setPlayerFactory(
						new SQLPlayerFactory());
				WorldManager.getInstance().setWorldFactory(
						new SQLWorldFactory());
			} catch (final SQLException e) {
				PlayerManager.getInstance().setPlayerFactory(
						new FilePlayerFactory(coreInstance.getDataFolder()
								.getPath() + File.separator + "userData"));
				WorldManager.getInstance().setWorldFactory(
						new FileWorldFactory(coreInstance.getDataFolder()
								.getPath() + File.separator + "worldData"));
				return;
			}

		} else {
			PlayerManager.getInstance().setPlayerFactory(
					new FilePlayerFactory(coreInstance.getDataFolder()
							.getPath() + File.separator + "userData"));
			WorldManager.getInstance().setWorldFactory(
					new FileWorldFactory(coreInstance.getDataFolder().getPath()
							+ File.separator + "worldData"));
		}
		convertFactory();
	}

	private void createTable() throws SQLException {
		try {
			final Database db = Database.DATABASE;
			db.open();
			if (!db.checkTable("ac_players")) {
				// Mysql
				if (db.getType() == DatabaseType.MYSQL) {
					// Players
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_homes` ("
							+ "  `name` varchar(64) NOT NULL,"
							+ "  `player_id` int(10) unsigned NOT NULL,"
							+ "  `world` varchar(64) NOT NULL,"
							+ "  `x` double NOT NULL,"
							+ "  `y` double NOT NULL,"
							+ "  `z` double NOT NULL,"
							+ "  `yaw` double NOT NULL,"
							+ "  `pitch` double NOT NULL,"
							+ "  PRIMARY KEY (`name`,`player_id`),"
							+ "  KEY `player_id` (`player_id`)"
							+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_informations` ("
							+ "  `key` varchar(128) NOT NULL,"
							+ "  `player_id` int(10) unsigned NOT NULL,"
							+ "  `info` text NOT NULL,"
							+ "  PRIMARY KEY (`key`,`player_id`),"
							+ "  KEY `player_id` (`player_id`)"
							+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_kit_uses` ("
							+ "  `kit` varchar(64) NOT NULL,"
							+ "  `player_id` int(10) unsigned NOT NULL,"
							+ "  `use` BIGINT unsigned NOT NULL,"
							+ "  PRIMARY KEY (`kit`,`player_id`),"
							+ "  KEY `player_id` (`player_id`)"
							+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_players` ("
							+ "  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
							+ "  `name` varchar(64) NOT NULL,"
							+ "  `world` varchar(64) DEFAULT NULL,"
							+ "  `x` double DEFAULT NULL,"
							+ "  `y` double DEFAULT NULL,"
							+ "  `z` double DEFAULT NULL,"
							+ "  `yaw` double DEFAULT NULL,"
							+ "  `pitch` double DEFAULT NULL,"
							+ "  PRIMARY KEY (`id`),"
							+ "  UNIQUE KEY `name` (`name`)"
							+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_powers` ("
							+ "  `key` varchar(128) NOT NULL,"
							+ "  `player_id` int(10) unsigned NOT NULL,"
							+ "  `info` text NOT NULL,"
							+ "  `category` varchar(64) NOT NULL,"
							+ "  PRIMARY KEY (`key`,`player_id`),"
							+ "  KEY `player_id` (`player_id`),"
							+ "  KEY `category` (`category`)"
							+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
					db.createTable("ALTER TABLE `ac_homes`"
							+ "  ADD CONSTRAINT `ac_homes_ibfk_1` FOREIGN KEY (`player_id`) "
							+ "REFERENCES `ac_players` (`id`) "
							+ "ON DELETE CASCADE ON UPDATE CASCADE;");
					db.createTable("ALTER TABLE `ac_informations`"
							+ "  ADD CONSTRAINT `ac_informations_ibfk_1`"
							+ "  FOREIGN KEY (`player_id`) "
							+ "  REFERENCES `ac_players` (`id`)"
							+ "  ON DELETE CASCADE ON UPDATE CASCADE;");
					db.createTable("ALTER TABLE `ac_kit_uses`"
							+ "  ADD CONSTRAINT `ac_kit_uses_ibfk_1`"
							+ "  FOREIGN KEY (`player_id`)"
							+ "  REFERENCES `ac_players` (`id`)"
							+ "  ON DELETE CASCADE ON UPDATE CASCADE;");
					db.createTable("ALTER TABLE `ac_powers`"
							+ "  ADD CONSTRAINT `ac_powers_ibfk_1`"
							+ "  FOREIGN KEY (`player_id`)"
							+ "  REFERENCES `ac_players` (`id`)"
							+ "  ON DELETE CASCADE ON UPDATE CASCADE;");

					// Worlds
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_warps` ("
							+ "  `name` varchar(64) NOT NULL,"
							+ "  `world_id` int(10) unsigned NOT NULL,"
							+ "  `x` double NOT NULL,"
							+ "  `y` double NOT NULL,"
							+ "  `z` double NOT NULL,"
							+ "  `pitch` double NOT NULL,"
							+ "  `yaw` double NOT NULL,"
							+ "  PRIMARY KEY (`name`,`world_id`),"
							+ "  KEY `world_id` (`world_id`)"
							+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_worlds` ("
							+ "  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
							+ "  `name` varchar(64) NOT NULL,"
							+ "  PRIMARY KEY (`id`),"
							+ "  UNIQUE KEY `name` (`name`)"
							+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_w_infos` ("
							+ "  `key` varchar(64) NOT NULL,"
							+ "  `world_id` int(10) unsigned NOT NULL,"
							+ "  `info` text NOT NULL,"
							+ "  PRIMARY KEY (`key`,`world_id`),"
							+ "  KEY `world_id` (`world_id`)"
							+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_spawns` ("
							+ " `name` varchar(64) NOT NULL,"
							+ "  `world_id` int(10) unsigned NOT NULL,"
							+ "  `x` double NOT NULL,"
							+ "  `y` double NOT NULL,"
							+ "  `z` double NOT NULL,"
							+ "  `pitch` double NOT NULL,"
							+ "  `yaw` double NOT NULL,"
							+ "  PRIMARY KEY (`name`,`world_id`),"
							+ "  KEY `world_id` (`world_id`)"
							+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

					db.createTable("ALTER TABLE `ac_warps`"
							+ "  ADD CONSTRAINT `ac_warps_ibfk_1` "
							+ "  FOREIGN KEY (`world_id`) "
							+ "  REFERENCES `ac_worlds` (`id`) "
							+ "  ON DELETE CASCADE ON UPDATE CASCADE;");
					db.createTable("ALTER TABLE `ac_w_infos`"
							+ "  ADD CONSTRAINT `ac_w_infos_ibfk_1`"
							+ "  FOREIGN KEY (`world_id`)"
							+ "  REFERENCES `ac_worlds` (`id`) "
							+ "  ON DELETE CASCADE ON UPDATE CASCADE;");
					db.createTable("ALTER TABLE `ac_spawns`"
							+ "  ADD CONSTRAINT `ac_spawns_ibfk_1`"
							+ "  FOREIGN KEY (`world_id`)"
							+ "  REFERENCES `ac_worlds` (`id`) "
							+ "  ON DELETE CASCADE ON UPDATE CASCADE;");
					// SQLITE
				} else if (db.getType() == DatabaseType.SQLITE) {
					// Players
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_homes` ("
							+ "  `name` varchar(64) NOT NULL,"
							+ "  `player_id` INTEGER  NOT NULL,"
							+ "  `world` varchar(64) NOT NULL,"
							+ "  `x` double  NOT NULL,"
							+ "  `y` double  NOT NULL,"
							+ "  `z` double  NOT NULL,"
							+ "  `yaw` double  NOT NULL,"
							+ "  `pitch` double  NOT NULL,"
							+ "  PRIMARY KEY (`name`,`player_id`)" + ");");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_informations` ("
							+ "  `key` varchar(128) NOT NULL,"
							+ "  `player_id` INTEGER NOT NULL,"
							+ "  `info` text NOT NULL,"
							+ "  PRIMARY KEY (`key`,`player_id`)" + " ) ;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_kit_uses` ("
							+ "  `kit` varchar(64) NOT NULL,"
							+ "  `player_id` INTEGER  NOT NULL,"
							+ "  `use` INTEGER  NOT NULL,"
							+ "  PRIMARY KEY (`kit`,`player_id`)" + " );");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_powers` ("
							+ "  `key` varchar(128) NOT NULL,"
							+ "  `player_id` INTEGER NOT NULL,"
							+ "  `info` text NOT NULL,"
							+ "  `category` varchar(64) NOT NULL,"
							+ "  PRIMARY KEY (`key`,`player_id`)" + ");");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_players` ("
							+ "  `id` INTEGER PRIMARY KEY AUTOINCREMENT ,"
							+ "  `name` varchar(64) NOT NULL,"
							+ "  `world` varchar(64) DEFAULT NULL,"
							+ "  `x` double DEFAULT NULL,"
							+ "  `y` double  DEFAULT NULL,"
							+ "  `z` double DEFAULT NULL,"
							+ "  `yaw` double  DEFAULT NULL,"
							+ "  `pitch` double  DEFAULT NULL,"
							+ "  UNIQUE (`name`)" + ") ;");

					db.createTable("CREATE INDEX home_pid ON ac_homes (player_id);");
					db.createTable("CREATE INDEX info_pid ON ac_informations (player_id);");
					db.createTable("CREATE INDEX kit_pid ON ac_kit_uses (player_id);");
					db.createTable("CREATE INDEX power_pid ON ac_powers (player_id);");
					db.createTable("CREATE INDEX power_cat ON ac_powers (category);");

					// Worlds
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_warps` ("
							+ "  `name` varchar(64) NOT NULL,"
							+ "  `world_id` INTEGER  NOT NULL,"
							+ "  `x` double NOT NULL,"
							+ "  `y` double NOT NULL,"
							+ "  `z` double NOT NULL,"
							+ "  `pitch` double NOT NULL,"
							+ "  `yaw` double NOT NULL,"
							+ "  PRIMARY KEY (`name`,`world_id`)" + ") ;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_worlds` ("
							+ "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,"
							+ "  `name` varchar(64) NOT NULL,"
							+ "  UNIQUE (`name`)" + ") ;");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_w_infos` ("
							+ "  `key` varchar(64) NOT NULL,"
							+ "  `world_id` INTEGER NOT NULL,"
							+ "  `info` text NOT NULL,"
							+ "  PRIMARY KEY (`key`,`world_id`)" + ");");
					db.createTable("CREATE TABLE IF NOT EXISTS `ac_spawns` ("
							+ " `name` varchar(64) NOT NULL,"
							+ "  `world_id` INTEGER NOT NULL,"
							+ "  `x` double NOT NULL,"
							+ "  `y` double NOT NULL,"
							+ "  `z` double NOT NULL,"
							+ "  `pitch` double NOT NULL,"
							+ "  `yaw` double NOT NULL,"
							+ "  PRIMARY KEY (`name`,`world_id`)" + ") ");

					db.createTable("CREATE INDEX warp_wid ON ac_warps (world_id);");
					db.createTable("CREATE INDEX info_wid ON ac_w_infos (world_id);");
					db.createTable("CREATE INDEX spawn_wid ON ac_spawns (world_id);");
				}
			}
		} catch (final SQLException e) {
			ACLogger.severe("There is a problem in your SQL configuration : ",
					e);
			ACLogger.warning("The plugin is falling back to YML data managment");
			throw e;
		}
	}

	private void convertFactory() {
		final String currentWrapper = ConfigEnum.DATA_WRAPPER.getString();
		final String convertTo = ConfigEnum.CONVERT_INTO.getString();
		if (convertTo.equalsIgnoreCase(currentWrapper)) {
			return;
		}
		if (currentWrapper.equalsIgnoreCase("yml")
				&& (convertTo.equalsIgnoreCase("sqlite") || convertTo
						.equalsIgnoreCase("mysql"))) {
			ConfigEnum.DATA_WRAPPER.setValue(convertTo);
			try {
				ConfigEnum.save();
			} catch (final IOException e) {
			}
			try {
				createTable();
			} catch (final SQLException e) {
				ACLogger.severe(
						"Can't Convert to the Database. There is a problem in your configuration",
						e);
				return;
			}
			WorldManager.getInstance().convertFactory(new SQLWorldFactory());
			PlayerManager.getInstance().convertFactory(new SQLPlayerFactory());
			FilePlayer.stopSavingTask();
		} else if (isSqlWrapper() && (convertTo.equalsIgnoreCase("yml"))) {
			ConfigEnum.DATA_WRAPPER.setValue(convertTo);
			try {
				ConfigEnum.save();
			} catch (final IOException e) {
			}
			WorldManager.getInstance().convertFactory(
					new FileWorldFactory(coreInstance.getDataFolder().getPath()
							+ File.separator + "worldData"));
			PlayerManager.getInstance().convertFactory(
					new FilePlayerFactory(coreInstance.getDataFolder()
							.getPath() + File.separator + "userData"));
			FilePlayer.scheduleAsyncSave();
		} else if (isSqlWrapper()
				&& (convertTo.equalsIgnoreCase("sqlite") || convertTo
						.equalsIgnoreCase("mysql"))) {
			WorldManager.getInstance().convertFactory(
					new FileWorldFactory(coreInstance.getDataFolder().getPath()
							+ File.separator + "worldData"));
			PlayerManager.getInstance().convertFactory(
					new FilePlayerFactory(coreInstance.getDataFolder()
							.getPath() + File.separator + "userData"));
			ConfigEnum.DATA_WRAPPER.setValue(convertTo);
			try {
				ConfigEnum.save();
			} catch (final IOException e) {
			}
			Database.initDb();
			try {
				createTable();
			} catch (final SQLException e) {
				ACLogger.severe(
						"Can't Convert to the Database. There is a problem in your configuration",
						e);
				return;
			}
			SQLPlayer.initPrepStmt();
			SQLWorld.initPrepStmt();
			WorldManager.getInstance().convertFactory(new SQLWorldFactory());
			PlayerManager.getInstance().convertFactory(new SQLPlayerFactory());
		}

	}
}
