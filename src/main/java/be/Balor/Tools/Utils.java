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
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Tools;

import info.somethingodd.OddItem.OddItem;
import info.somethingodd.OddItem.OddItemBase;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet201PlayerInfo;
import net.minecraft.server.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import be.Balor.Listeners.Events.ACTeleportEvent;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.NotANumberException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.EmptyPlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.Tools.Type.Whois;
import be.Balor.Tools.Blocks.BlockRemanence;
import be.Balor.Tools.Blocks.IBlockRemanenceFactory;
import be.Balor.Tools.Blocks.LogBlockRemanenceFactory;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Exceptions.InvalidInputException;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.Tools.Threads.CheckingBlockTask;
import be.Balor.Tools.Threads.ReplaceBlockTask;
import be.Balor.Tools.Threads.SetTimeTask;
import be.Balor.Tools.Threads.TeleportTask;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

import com.google.common.base.Joiner;
import com.miraclem4n.mchat.api.Reader;
import com.miraclem4n.mchat.types.EventType;

import de.diddiz.LogBlock.Consumer;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public final class Utils {
	public static OddItemBase oddItem = null;
	public static Consumer logBlock = null;
	public static boolean mChatPresent = false;
	public static boolean signExtention = false;
	public final static long secondInMillis = 1000;
	public final static long minuteInMillis = secondInMillis * 60;
	public final static long hourInMillis = minuteInMillis * 60;
	public final static long dayInMillis = hourInMillis * 24;
	public final static int secInTick = 20;
	private static final Character CHATCOLOR_DELIMITER = '&';
	public static final Pattern REGEX_COLOR_PERSER = Pattern
			.compile(CHATCOLOR_DELIMITER + "[A-Fa-f]|" + CHATCOLOR_DELIMITER
					+ "1[0-5]|" + CHATCOLOR_DELIMITER + "[0-9]|"
					+ CHATCOLOR_DELIMITER + "[L-Ol-o]");
	public static final Pattern REGEX_IP_V4 = Pattern
			.compile("\\b(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b");
	public static final Pattern REGEX_INACCURATE_IP_V4 = Pattern
			.compile("\\b([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\b");
	public static final Pattern NUMBERS = Pattern.compile("(\\d*[.|\\.]?\\d+)"
			+ "|(\\d+)");
	public static final Pattern TIMES1 = Pattern
			.compile("month(s?)|day(s?)|hour(s?)|week(s?)|year(s?)");
	public static final Pattern TIMES2 = Pattern
			.compile("m|h|d|w|y");

	/**
	 * @author Balor (aka Antoine Aflalo)
	 * 
	 */
	/**
	 *
	 */
	private Utils() {
	}

	public final static int MAX_BLOCKS = 512;

	public static void addLocale(final String key, final String value) {
		LocaleManager.getInstance().addLocale(key, value);
	}

	public static void addLocale(final String key, final String value,
			final boolean override) {
		LocaleManager.getInstance().addLocale(key, value, true);
	}

	/**
	 * Add the player in the online list (TAB key)
	 * 
	 * @param player
	 *            player to remove
	 */
	public static void addPlayerInOnlineList(final Player player) {
		((CraftServer) player.getServer()).getHandle()
				.sendAll(
						new Packet201PlayerInfo(((CraftPlayer) player)
								.getHandle().listName, true, 1000));
	}

	public static void addPlayerInOnlineList(final Player toAdd,
			final Player fromPlayer) {
		((CraftPlayer) fromPlayer).getHandle().netServerHandler
				.sendPacket(new Packet201PlayerInfo(((CraftPlayer) toAdd)
						.getHandle().listName, true, 1000));
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] Arrays_copyOfRange(final T[] original,
			final int start, final int end) {
		if (original.length >= start && 0 <= start) {
			if (start <= end) {
				final int length = end - start;
				final int copyLength = Math
						.min(length, original.length - start);
				final T[] copy = (T[]) Array.newInstance(original.getClass()
						.getComponentType(), length);

				System.arraycopy(original, start, copy, 0, copyLength);
				return copy;
			}
			throw new IllegalArgumentException();
		}
		throw new ArrayIndexOutOfBoundsException();
	}

	/**
	 * Broadcast a fakeJoin message for the selected player
	 * 
	 * @param player
	 *            that fake join.
	 */
	public static void broadcastFakeJoin(final Player player) {
		if (mChatPresent) {
			Utils.broadcastMessage(getPlayerName(player, null, true) + " "
					+ Reader.getEventMessage(EventType.JOIN));
		} else {
			Utils.broadcastMessage(I18n("joinMessage", "name",
					getPlayerName(player, null, true)));
		}

	}

	/**
	 * Broadcast a fakeQuit message for the selected player
	 * 
	 * @param player
	 *            that fake quit.
	 */
	public static void broadcastFakeQuit(final Player player) {
		if (mChatPresent) {
			Utils.broadcastMessage(getPlayerName(player, null, true) + " "
					+ Reader.getEventMessage(EventType.QUIT));
		} else {
			Utils.broadcastMessage(I18n("quitMessage", "name",
					getPlayerName(player, null, true)));
		}

	}

	/**
	 * Broadcast message to every user since the bukkit one is bugged
	 * 
	 * @param message
	 */
	public static void broadcastMessage(final String message) {
		if (message == null) {
			return;
		}
		for (final Player p : getOnlinePlayers()) {
			p.sendMessage(message);
			// new ColouredConsoleSender((CraftServer)
			// ACPluginManager.getServer()).sendMessage(message);
		}
	}

	/**
	 * Check the if the player have the right to execute the command on the
	 * other player
	 * 
	 * @param sender
	 *            the one who want to do the command
	 * @param args
	 *            containing the name of the target
	 * @param index
	 *            index of the target's name in the argument
	 * @return true if the sender have the right to execute the command, else
	 *         false with displaying an error message to the sender.
	 */
	public static boolean checkImmunity(final CommandSender sender,
			final CommandArgs args, final int index) {
		final Player target = sender.getServer().getPlayer(
				args.getString(index));
		if (target != null) {
			if (checkImmunity(sender, target)) {
				return true;
			} else {
				sI18n(sender, "insufficientLvl");
				return false;
			}
		} else {
			if (preImmunityCheck(sender, target)) {
				return true;
			}
			final Player player = (Player) sender;
			final int pLvl = ACHelper.getInstance().getLimit(player,
					Type.Limit.IMMUNITY, "defaultImmunityLvl");
			final int tLvl = ACPlayer.getPlayer(args.getString(index))
					.getInformation("immunityLvl").getInt(0);
			return checkLvl(player, pLvl, tLvl);

		}
	}

	/**
	 * Check the if the player have the right to execute the command on the
	 * other player
	 * 
	 * @param sender
	 *            the one who want to do the command
	 * @param target
	 *            the target of the command
	 * @return true if the sender have the right to execute the command, else
	 *         false.
	 */
	public static boolean checkImmunity(final CommandSender sender,
			final Player target) {
		if (preImmunityCheck(sender, target)) {
			return true;
		}
		final Player player = (Player) sender;
		final int pLvl = ACHelper.getInstance().getLimit(player,
				Type.Limit.IMMUNITY, "defaultImmunityLvl");
		final int tLvl = ACHelper.getInstance().getLimit(target,
				Type.Limit.IMMUNITY, "defaultImmunityLvl");

		return checkLvl(player, pLvl, tLvl);
	}

	public static boolean checkImmunity(final CommandSender sender,
			final ACPlayer target) {
		if (preImmunityCheck(sender, target)) {
			return true;
		}
		final Player player = (Player) sender;
		final int pLvl = ACHelper.getInstance().getLimit(player,
				Type.Limit.IMMUNITY, "defaultImmunityLvl");
		int tLvl = 0;
		if (target.isOnline()) {
			tLvl = ACHelper.getInstance().getLimit(target.getHandler(),
					Type.Limit.IMMUNITY, "defaultImmunityLvl");
		} else {
			tLvl = target.getInformation("immunityLvl").getInt(0);
		}
		return checkLvl(player, pLvl, tLvl);

	}

	private static boolean checkLvl(final Player player, final int pLvl,
			final int tLvl) {
		if (PermissionManager.hasPerm(player, "admincmd.immunityLvl.samelvl",
				false) && pLvl != tLvl) {
			return false;
		}
		if (pLvl >= tLvl) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean preImmunityCheck(final CommandSender sender,
			final Object target) {
		if (!ConfigEnum.IMMUNITY.getBoolean()) {
			return true;
		}
		if (!isPlayer(sender, false)) {
			return true;
		}
		if (target == null) {
			return true;
		}
		return false;
	}

	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 * @throws InvalidInputException
	 *             if the input is invalid
	 */
	public static MaterialContainer checkMaterial(final String mat)
			throws InvalidInputException {
		MaterialContainer mc = new MaterialContainer();
		try {
			if (oddItem != null) {
				final ItemStack is = OddItem.getItemStack(mat);
				if (is != null) {
					return new MaterialContainer(is);
				}
			}
		} catch (final Exception e) {}
		String[] info = new String[2];
		if (mat.contains(":")) {
			info = mat.split(":");
			if (info.length < 2) {
				throw new InvalidInputException(mat);
			}
			mc = new MaterialContainer(info[0], info[1]);
		} else {
			info[0] = mat;
			info[1] = "0";
			if ((mc = ACHelper.getInstance().getAlias(info[0])) == null) {
				mc = new MaterialContainer(info[0], info[1]);
			}
		}
		return mc;

	}

	/**
	 * Parse a string and replace the color in it
	 * 
	 * @author Speedy64
	 * @param toParse
	 * @return
	 */
	public static String colorParser(final String toParse) {
		try {
			return ChatColor.translateAlternateColorCodes(CHATCOLOR_DELIMITER,
					toParse);
		} catch (final NoSuchMethodError e) {
			return oldColorParser(toParse);
		}
	}
	private static String oldColorParser(final String toParse) {
		String ResultString = null;
		try {
			Matcher regexMatcher = REGEX_COLOR_PERSER.matcher(toParse);
			String result = toParse;
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group();
				int colorint = Integer.parseInt(ResultString.substring(1, 2),
						16);
				if (ResultString.length() > 1) {
					if (colorint == 1
							&& ResultString.substring(2).matches("[012345]")) {
						colorint = colorint * 10
								+ Integer.parseInt(ResultString.substring(2));
					}
				}
				result = regexMatcher.replaceFirst(ChatColor.getByChar(
						Integer.toHexString(colorint)).toString());
				regexMatcher = REGEX_COLOR_PERSER.matcher(result);
			}
			return result;
		} catch (final Exception ex) {
			return toParse;
		}
	}

	/**
	 * Because water and lava are fluid, using another algo to "delete"
	 * 
	 * @param block
	 * @param radius
	 * @return
	 */
	private static Stack<BlockRemanence> drainFluid(final String playername,
			final Block block, final int radius) {
		final Stack<BlockRemanence> blocks = new Stack<BlockRemanence>();
		final Stack<SimplifiedLocation> processQueue = new Stack<SimplifiedLocation>();
		BlockRemanence current = null;
		final World w = block.getWorld();
		final Location start = block.getLocation();
		final HashSet<SimplifiedLocation> visited = new HashSet<SimplifiedLocation>();
		final Stack<BlockRemanence> blocksCache = new Stack<BlockRemanence>();
		for (int x = block.getX() - 2; x <= block.getX() + 2; x++) {
			for (int z = block.getZ() - 2; z <= block.getZ() + 2; z++) {
				for (int y = block.getY() - 2; y <= block.getY() + 2; y++) {
					final SimplifiedLocation newPos = new SimplifiedLocation(w,
							x, y, z);
					if (isFluid(newPos) && !visited.contains(newPos)) {
						visited.add(newPos);
						processQueue.push(newPos);
						current = IBlockRemanenceFactory.FACTORY
								.createBlockRemanence(newPos);
						blocks.push(current);
						blocksCache.push(current);
						if (blocksCache.size() == MAX_BLOCKS) {
							ACPluginManager.getScheduler()
									.scheduleSyncDelayedTask(
											ACHelper.getInstance()
													.getCoreInstance(),
											new ReplaceBlockTask(blocksCache));
						}
					}

				}
			}
		}
		while (!processQueue.isEmpty()) {
			final SimplifiedLocation loc = processQueue.pop();
			for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
				for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
					for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
						final SimplifiedLocation newPos = new SimplifiedLocation(
								w, x, y, z);
						if (!visited.contains(newPos) && isFluid(newPos)
								&& start.distance(newPos) < radius) {
							processQueue.push(newPos);
							current = IBlockRemanenceFactory.FACTORY
									.createBlockRemanence(newPos);
							blocks.push(current);
							blocksCache.push(current);
							if (blocksCache.size() == MAX_BLOCKS) {
								ACPluginManager.getScheduler()
										.scheduleSyncDelayedTask(
												ACHelper.getInstance()
														.getCoreInstance(),
												new ReplaceBlockTask(
														blocksCache));
							}
							visited.add(newPos);
						}
					}

				}
			}
		}
		ACPluginManager.getScheduler().scheduleSyncDelayedTask(
				ACHelper.getInstance().getCoreInstance(),
				new ReplaceBlockTask(blocksCache));
		return blocks;
	}

	/**
	 * Get the ACPlayer, useful when working with only the AC user informations
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            args in the command
	 * @param permNode
	 *            permission node to execute the command
	 * @return null if the ACPlayer can't be get else the ACPlayer
	 * @throws ActionNotPermitedException
	 * @throws PlayerNotFound
	 */
	public static ACPlayer getACPlayer(final CommandSender sender,
			final CommandArgs args, final String permNode)
			throws PlayerNotFound, ActionNotPermitedException {
		final Player target = Utils.getUser(sender, args, permNode, 0, false);
		ACPlayer actarget;
		if (target == null) {
			if (args.length == 0) {
				sender.sendMessage("You must type the player name");
				return null;
			}
			actarget = ACPlayer.getPlayer(args.getString(0));
			if (actarget instanceof EmptyPlayer) {
				Utils.sI18n(sender, "playerNotFound", "player",
						actarget.getName());
				return null;
			}
			if (!Utils.checkImmunity(sender, args, 0)) {
				sI18n(sender, "insufficientLvl");
				return null;
			}
		} else {
			if (!checkImmunity(sender, target)) {
				return null;
			}
			actarget = ACPlayer.getPlayer(target);
		}
		return actarget;
	}

	public static double getDistanceSquared(final Player player1,
			final Player player2) {
		if (!player1.getWorld().getName().equals(player2.getWorld().getName())) {
			return Double.MAX_VALUE;
		}
		final Location loc1 = player1.getLocation();
		final Location loc2 = player2.getLocation();
		return Math.pow((loc1.getX() - loc2.getX()), 2)
				+ Math.pow((loc1.getZ() - loc2.getZ()), 2);
	}

	/**
	 * Get the elapsed time since the start.
	 * 
	 * @param start
	 * @return
	 */
	public static Long[] getElapsedTime(final long start) {
		return transformToElapsedTime(System.currentTimeMillis() - start);
	}

	/**
	 * Get the home by checking the colon
	 * 
	 * @param sender
	 *            who send the command
	 * @param toParse
	 *            what args was send
	 * @return the home containing the player and the home name
	 */
	public static Home getHome(final CommandSender sender, final String toParse) {
		final Home result = new Home();
		if (toParse != null && toParse.contains(":")) {
			try {
				final String[] split = toParse.split(":");
				result.player = split[0];
				if (split[1] == null) {
					final HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("arg", "home name after the ':'");
					replace.put("cmdName", "/home");
					LocaleHelper.MISSING_ARG.sendLocale(sender, replace);
					return null;
				}
				result.home = split[1];
			} catch (final ArrayIndexOutOfBoundsException e) {}
			if (isPlayer(sender, false)) {
				final Player p = (Player) sender;
				if (!p.getName().equals(result.player)
						&& !PermissionManager.hasPerm(p, "admincmd.admin.home")) {
					return null;
				}
			}
			return result;
		}
		if (!isPlayer(sender)) {
			return null;
		}
		final Player p = ((Player) sender);
		result.player = p.getName();
		if (toParse != null) {
			result.home = toParse;
		} else {
			result.home = p.getWorld().getName();
		}

		return result;
	}

	
	/**
	 * Shortcut to online players.
	 * 
	 * @return
	 */
	public static List<Player> getOnlinePlayers() {
		return PlayerManager.getInstance().getOnlinePlayers();
	}

	public static Player getPlayer(final String name) {
		final Player[] players = ACPluginManager.getServer().getOnlinePlayers();

		Player found = null;
		final String lowerName = name.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for (final Player player : players) {
			if (player.getName().toLowerCase().startsWith(lowerName)) {
				int curDelta = player.getName().length() - lowerName.length();
				if (curDelta < delta) {
					found = player;
					delta = curDelta;
				} else {
					curDelta = player.getDisplayName().length()
							- lowerName.length();
					if (curDelta < delta) {
						found = player;
						delta = curDelta;
					}
				}
				if (curDelta == 0) {
					break;
				}
			}
		}
		return found;

	}

	public static String getPlayerName(final Player player) {
		return getPlayerName(player, null);
	}

	/**
	 * For compatibility
	 * 
	 * @param player
	 * @param sender
	 * @param withPrefix
	 * @return
	 */
	public static String getPlayerName(final Player player,
			final CommandSender sender, final boolean withPrefix) {
		return getPlayerName(player, sender);
	}

	/**
	 * Get the complete player name with all prefix
	 * 
	 * @param player
	 *            player to get the name
	 * @param sender
	 *            sender that want the name
	 * @return the complete player name with prefix
	 */
	public static String getPlayerName(final Player player,
			final CommandSender sender) {
		assert (player != null);
		if (ConfigEnum.USE_PREFIX.getBoolean()) {
			final String prefix = colorParser(getPrefix(player, sender));
			final String suffix = colorParser(PermissionManager
					.getSuffix(player));
			if (ConfigEnum.DNAME.getBoolean()) {
				return prefix + player.getDisplayName() + suffix
						+ ChatColor.YELLOW;
			}

			return prefix + player.getName() + suffix + ChatColor.YELLOW;
		}

		if (ConfigEnum.DNAME.getBoolean()) {
			return player.getDisplayName();
		}

		return player.getName();
	}

	/**
	 * Get the prefix of the player, by checking the right the sender have
	 * 
	 * @param player
	 * @return
	 */
	private static String getPrefix(final Player player,
			final CommandSender sender) {
		boolean isInv = false;
		String prefixstring = "";
		String statusPrefix = "";
		if (sender != null) {
			isInv = InvisibleWorker.getInstance().hasInvisiblePowers(player)
					&& PermissionManager.hasPerm(sender,
							"admincmd.invisible.cansee", false);
		}
		if (isInv) {
			statusPrefix = Utils.I18n("invTitle");
		}
		if (AFKWorker.getInstance().isAfk(player)) {
			statusPrefix = Utils.I18n("afkTitle") + statusPrefix;
		}
		prefixstring = PermissionManager.getPrefix(player);
		String result = statusPrefix;
		if (prefixstring != null && prefixstring.length() > 1) {
			result += prefixstring;
		}
		return colorParser(result);

	}

	/**
	 * Get the real time from the server
	 * 
	 * @author Lathanael
	 * @param gmt
	 *            The wanted GMT offset
	 * @return serverTime Represents the time read from the server
	 */
	public static Date getServerRealTime(final String gmt) {
		Date serverTime;
		final TimeZone tz = TimeZone.getTimeZone(gmt);
		final Calendar cal = Calendar.getInstance(tz);
		cal.setTime(new Date());
		serverTime = cal.getTime();
		return serverTime;
	}

	public static Player getUser(final CommandSender sender,
			final CommandArgs args, final String permNode)
			throws PlayerNotFound, ActionNotPermitedException {
		return getUser(sender, args, permNode, 0, true);
	}

	/**
	 * Get the user and check who launched the command.
	 * 
	 * @param sender
	 * @param args
	 * @param permNode
	 * @param index
	 * @param errorMsg
	 * @return
	 * @throws PlayerNotFound
	 * @throws ActionNotPermitedException
	 */
	public static Player getUser(final CommandSender sender,
			final CommandArgs args, final String permNode, final int index,
			final boolean errorMsg) throws PlayerNotFound,
			ActionNotPermitedException {
		Player target = null;
		if (args.length >= index + 1) {
			target = getPlayer(args.getString(index));
			if (target != null) {
				if (target.equals(sender)) {
					return target;
				} else if (PermissionManager.hasPerm(sender, permNode
						+ ".other", false)) {
					if (checkImmunity(sender, target)) {
						return target;
					} else {
						throw new PlayerNotFound(Utils.I18n("insufficientLvl"),
								sender);
					}
				} else {
					throw new ActionNotPermitedException(sender, permNode
							+ ".other");
				}
			}
		} else if (isPlayer(sender, false)) {
			target = ((Player) sender);
		} else if (errorMsg) {
			sender.sendMessage("You must type the player name");
			return target;
		}
		if (target == null && errorMsg) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", args.getString(index));
			throw new PlayerNotFound(Utils.I18n("playerNotFound", replace),
					sender);
		}
		return target;

	}

	/**
	 * Get the user using the -P param as indicating the userName and check who
	 * launched the command.
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            argument of the command
	 * @param permNode
	 *            permission to run the command
	 * @return target player if found
	 * @throws PlayerNotFound
	 *             if the target player is not found
	 * @throws ActionNotPermitedException
	 *             if the player don't have the permission
	 */
	public static Player getUserParam(final CommandSender sender,
			final CommandArgs args, final String permNode)
			throws PlayerNotFound, ActionNotPermitedException {
		return getUserParam(sender, args, permNode, true);
	}

	/**
	 * Get the user using the -P param as indicating the userName and check who
	 * launched the command.
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            argument of the command
	 * @param permNode
	 *            permission to run the command
	 * @param errorMsg
	 *            send or not the exception about an unfound player
	 * @return target player if found
	 * @throws PlayerNotFound
	 *             if the target player is not found
	 * @throws ActionNotPermitedException
	 *             if the player don't have the permission
	 */
	public static Player getUserParam(final CommandSender sender,
			final CommandArgs args, final String permNode,
			final boolean errorMsg) throws PlayerNotFound,
			ActionNotPermitedException {
		Player target = null;
		final String playerName = args.getValueFlag('P');
		if (playerName != null) {
			target = getPlayer(playerName);
			if (target != null) {
				if (target.equals(sender)) {
					return target;
				} else if (PermissionManager.hasPerm(sender, permNode
						+ ".other", false)) {
					if (checkImmunity(sender, target)) {
						return target;
					} else {
						throw new PlayerNotFound(Utils.I18n("insufficientLvl"),
								sender);
					}
				} else {
					throw new ActionNotPermitedException(sender, permNode
							+ ".other");
				}
			}
		} else if (isPlayer(sender, false)) {
			target = ((Player) sender);
		} else if (errorMsg) {
			sender.sendMessage("You must type the player name");
			return target;
		}
		if (target == null && errorMsg) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", playerName);
			throw new PlayerNotFound(Utils.I18n("playerNotFound", replace),
					sender);
		}
		return target;
	}

	public static String I18n(final String key) {
		return I18n(key, null);
	}

	public static String I18n(final String key,
			final Map<String, String> replace) {
		return LocaleManager.getInstance().get(key, replace);
	}

	public static String I18n(final String key, final String alias,
			final String toReplace) {
		return LocaleManager.getInstance().get(key, alias, toReplace);
	}

	public static String I18n(final LocaleHelper key) {
		return I18n(key.getKey(), null);
	}

	public static String I18n(final LocaleHelper key,
			final Map<String, String> replace) {
		return LocaleManager.getInstance().get(key.getKey(), replace);
	}

	public static String I18n(final LocaleHelper key, final String alias,
			final String toReplace) {
		return LocaleManager.getInstance().get(key.getKey(), alias, toReplace);
	}

	/**
	 * Check if the block is a fluid.
	 * 
	 * @param loc
	 * @return
	 */
	private static boolean isFluid(final Location loc) {
		final Block b = loc.getWorld().getBlockAt(loc);
		if (b == null) {
			return false;
		}
		return b.getType() == Material.WATER
				|| b.getType() == Material.STATIONARY_WATER
				|| b.getType() == Material.LAVA
				|| b.getType() == Material.STATIONARY_LAVA;
	}

	/**
	 * Check if the command sender is a Player
	 * 
	 * @return
	 */
	public static boolean isPlayer(final CommandSender sender) {
		return isPlayer(sender, true);
	}

	/**
	 * Checks if the command sender is a Player. Sends the sender an error
	 * message if he is not a player.
	 * 
	 * @param sender
	 * @param msg
	 *            - If {@code true} an error message will be sent.
	 * @return
	 */
	public static boolean isPlayer(final CommandSender sender, final boolean msg) {
		if (sender instanceof Player) {
			return true;
		} else {
			if (msg) {
				Utils.sI18n(sender, "mustBePlayer");
			}
			return false;
		}
	}

	/**
	 * Remove the player from the online list (TAB key)
	 * 
	 * @param player
	 *            player to remove
	 */
	public static void removePlayerFromOnlineList(final Player player) {
		((CraftServer) player.getServer()).getHandle().sendAll(
				new Packet201PlayerInfo(
						((CraftPlayer) player).getHandle().listName, false,
						9999));
	}

	public static void removePlayerFromOnlineList(final Player toRemove,
			final Player fromPlayer) {
		if (toRemove == null || fromPlayer == null) {
			return;
		}
		((CraftPlayer) fromPlayer).getHandle().netServerHandler
				.sendPacket(new Packet201PlayerInfo(((CraftPlayer) toRemove)
						.getHandle().listName, false, 9999));
	}

	public static Integer replaceBlockByAir(final CommandSender sender,
			final CommandArgs args, final List<Material> mat,
			final int defaultRadius) {
		if (Utils.isPlayer(sender)) {
			int radius = defaultRadius;
			if (args.length >= 1) {
				try {
					radius = args.getInt(0);
				} catch (final NumberFormatException e) {
					if (args.length >= 2) {
						try {
							radius = args.getInt(1);
						} catch (final NumberFormatException e2) {

						}
					}
				}

			}
			final String playername = ((Player) sender).getName();
			IBlockRemanenceFactory.FACTORY.setPlayerName(playername);
			Stack<BlockRemanence> blocks;
			final Block block = ((Player) sender).getLocation().getBlock();
			if (mat.contains(Material.LAVA) || mat.contains(Material.WATER)) {
				blocks = drainFluid(playername, block, radius);
			} else {
				blocks = replaceInCuboid(playername, mat, block, radius);
			}
			if (!blocks.isEmpty()) {
				ACHelper.getInstance().addInUndoQueue(playername, blocks);
			}
			return blocks.size();
		}
		return null;
	}

	/**
	 * Replace the time and date to the format given in the config with the
	 * corresponding date and time
	 * 
	 * @author Lathanael
	 * @param
	 * @return timeFormatted
	 */
	public static String replaceDateAndTimeFormat(final Date date) {
		String timeFormatted = "";
		final String format = ConfigEnum.DT_FORMAT.getString();
		final SimpleDateFormat formater = new SimpleDateFormat(format);
		final Date serverTime = date;
		timeFormatted = formater.format(serverTime);
		return timeFormatted;
	}

	public static String replaceDateAndTimeFormat(final ACPlayer player,
			final Type.Whois type) {
		final String format = ConfigEnum.DT_FORMAT.getString();
		final SimpleDateFormat formater = new SimpleDateFormat(format);
		String lastlogin = "";
		lastlogin = formater.format(new Date(player.getInformation(
				type.getVal()).getLong(1)));
		if (lastlogin == formater.format(new Date(1))) {
			return null;
		}
		return lastlogin;
	}

	/**
	 * Replace all the chosen material in the cuboid region.
	 * 
	 * @param mat
	 * @param block
	 * @param radius
	 * @return
	 */
	private static Stack<BlockRemanence> replaceInCuboid(
			final String playername, final List<Material> mat,
			final Block block, final int radius) {
		final Stack<BlockRemanence> blocks = new SynchronizedStack<BlockRemanence>();
		final Stack<BlockRemanence> blocksCache = new SynchronizedStack<BlockRemanence>();
		final int limitX = block.getX() + radius;
		final int limitY = block.getY() + radius;
		final int limitZ = block.getZ() + radius;
		BlockRemanence br = null;
		final Semaphore sema = new Semaphore(0, true);
		final List<SimplifiedLocation> okBlocks = new ArrayList<SimplifiedLocation>(
				50);
		ACPluginManager.scheduleSyncTask(new CheckingBlockTask(sema, okBlocks,
				block, radius, limitY, limitX, limitZ, mat));
		try {
			sema.acquire();
		} catch (final InterruptedException e) {
			DebugLog.INSTANCE.log(Level.SEVERE,
					"Problem with acquiring the semaphore", e);
		}
		for (final SimplifiedLocation loc : okBlocks) {
			br = IBlockRemanenceFactory.FACTORY.createBlockRemanence(loc);
			blocks.push(br);
			blocksCache.push(br);
			if (blocksCache.size() == MAX_BLOCKS) {
				ACPluginManager.getScheduler().scheduleSyncDelayedTask(
						ACHelper.getInstance().getCoreInstance(),
						new ReplaceBlockTask(blocksCache), 1);
			}
		}
		ACPluginManager.getScheduler().scheduleSyncDelayedTask(
				ACHelper.getInstance().getCoreInstance(),
				new ReplaceBlockTask(blocksCache), 1);
		return blocks;
	}

	public static void sendMessage(final CommandSender sender,
			final CommandSender player, final String key) {
		sendMessage(sender, player, key, null);
	}

	public static void sendMessage(final CommandSender sender,
			final CommandSender player, final String key,
			final Map<String, String> replace) {
		final String msg = I18n(key, replace);
		if (msg != null && !msg.isEmpty()) {
			if (!sender.equals(player)) {
				player.sendMessage(msg);
			}
			sender.sendMessage(msg);
		}

	}

	/**
	 * @param logBlock
	 *            the logBlock to set
	 */
	public static void setLogBlock(final Consumer logBlock) {
		Utils.logBlock = logBlock;
		IBlockRemanenceFactory.FACTORY = new LogBlockRemanenceFactory();
	}

	/**
	 * Heal or refill the FoodBar of the selected player.
	 * 
	 * @param name
	 * @return
	 * @throws ActionNotPermitedException
	 * @throws PlayerNotFound
	 */
	public static boolean setPlayerHealth(final CommandSender sender,
			final CommandArgs name, final Type.Health toDo)
			throws PlayerNotFound, ActionNotPermitedException {
		final Player target = getUser(sender, name, "admincmd.player." + toDo);
		if (target == null) {
			return false;
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", getPlayerName(target));
		final PluginManager pluginManager = ACPluginManager.getServer()
				.getPluginManager();
		final String newStateLocale = LocaleHelper.NEW_STATE.getLocale();
		final String newStatePlayerLocale = LocaleHelper.NEW_STATE_PLAYER
				.getLocale(replace);
		switch (toDo) {
			case HEAL :
				final EntityRegainHealthEvent heal = new EntityRegainHealthEvent(
						target, 20, RegainReason.CUSTOM);
				pluginManager.callEvent(heal);
				if (!heal.isCancelled()) {
					target.setHealth(heal.getAmount());
					target.setFireTicks(0);
					final String msg = newStateLocale
							+ LocaleHelper.HEALED.getLocale();
					target.sendMessage(msg);
					if (!target.equals(sender)) {
						final String newStateMsg = newStatePlayerLocale
								+ LocaleHelper.HEALED.getLocale();
						sender.sendMessage(newStateMsg);
					}
				}
				break;
			case FEED :
				final FoodLevelChangeEvent foodEvent = new FoodLevelChangeEvent(
						target, 20);
				pluginManager.callEvent(foodEvent);
				if (!foodEvent.isCancelled()) {
					target.setFoodLevel(foodEvent.getFoodLevel());
					final String msg = newStateLocale
							+ LocaleHelper.FEEDED.getLocale();
					target.sendMessage(msg);
					if (!target.equals(sender)) {
						final String newStateMsg = newStatePlayerLocale
								+ LocaleHelper.FEEDED.getLocale();
						sender.sendMessage(newStateMsg);
					}
				}
				break;
			case KILL :
				if (target.equals(sender)) {
					final EntityDamageEvent dmgEvent = new EntityDamageEvent(
							target, EntityDamageEvent.DamageCause.SUICIDE,
							Short.MAX_VALUE);
					pluginManager.callEvent(dmgEvent);
					if (!dmgEvent.isCancelled()) {
						target.damage(Short.MAX_VALUE);
						LocaleHelper.SUICIDE.sendLocale(target);
					}
				} else {
					final EntityDamageEvent dmgEvent = new EntityDamageEvent(
							target, EntityDamageEvent.DamageCause.CUSTOM,
							Short.MAX_VALUE);
					pluginManager.callEvent(dmgEvent);
					if (!dmgEvent.isCancelled()) {
						if (isPlayer(sender, false)) {
							target.damage(dmgEvent.getDamage(), (Player) sender);
						} else {
							target.damage(dmgEvent.getDamage());
						}
						final String msg = newStateLocale
								+ LocaleHelper.KILLED.getLocale();
						target.sendMessage(msg);
						final String newStateMsg = newStatePlayerLocale
								+ LocaleHelper.KILLED.getLocale();
						sender.sendMessage(newStateMsg);
					}
				}
				if (logBlock != null) {
					logBlock.queueKill(isPlayer(sender, false)
							? (Player) sender
							: null, target);
				}
				break;
			default :
				return false;
		}
		return true;
	}

	private static void setTime(final CommandSender sender, final World w,
			final String arg) {
		final long curtime = w.getTime();
		long newtime = curtime - (curtime % 24000);
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("type", arg);
		replace.put("world", w.getName());
		if (ACWorld.getWorld(w.getName())
				.getInformation(Type.TIME_FREEZED.toString()).isNull()) {
			if (arg.equalsIgnoreCase("day")) {
				newtime += 0;
			} else if (arg.equalsIgnoreCase("night")) {
				newtime += 14000;
			} else if (arg.equalsIgnoreCase("dusk")) {
				newtime += 12500;
			} else if (arg.equalsIgnoreCase("dawn")) {
				newtime += 23000;
			} else if (arg.equalsIgnoreCase("pause")) {
				final int taskId = ACPluginManager.getScheduler()
						.scheduleSyncRepeatingTask(
								ACHelper.getInstance().getCoreInstance(),
								new SetTimeTask(w), 0, 5L);
				ACWorld.getWorld(w.getName()).setInformation(
						Type.TIME_FREEZED.toString(), taskId);
			} else {
				// if not a constant, use raw time
				try {
					newtime += Integer.parseInt(arg);
				} catch (final Exception e) {
					sI18n(sender, "timeNotSet", replace);
					return;
				}
			}
			sI18n(sender, "timeSet", replace);
		} else if (arg.equalsIgnoreCase("unpause")) {
			final int removeTask = ACWorld.getWorld(w.getName())
					.getInformation(Type.TIME_FREEZED.toString()).getInt(-1);
			ACPluginManager.getScheduler().cancelTask(removeTask);
			ACWorld.getWorld(w.getName()).removeInformation(
					Type.TIME_FREEZED.toString());
			sI18n(sender, "timeSet", replace);
		} else {
			sI18n(sender, "timePaused", "world", w.getName());
		}

		ACPluginManager.scheduleSyncTask(new SetTimeTask(w, newtime));
	}

	public static void sI18n(final CommandSender sender, final String key) {
		sI18n(sender, key, null);
	}

	public static void sI18n(final CommandSender sender, final String key,
			final Map<String, String> replace) {
		final String locale = I18n(key, replace);
		if (locale != null && !locale.isEmpty()) {
			sender.sendMessage(locale);
		}
	}

	public static void sI18n(final CommandSender sender, final String key,
			final String alias, final String toReplace) {
		final String locale = I18n(key, alias, toReplace);
		if (locale != null && !locale.isEmpty()) {
			sender.sendMessage(locale);
		}
	}

	public static void sI18n(final CommandSender sender, final LocaleHelper key) {
		sI18n(sender, key, null);
	}

	public static void sI18n(final CommandSender sender,
			final LocaleHelper key, final Map<String, String> replace) {
		final String locale = I18n(key, replace);
		if (locale != null && !locale.isEmpty()) {
			sender.sendMessage(locale);
		}
	}

	public static void sI18n(final CommandSender sender,
			final LocaleHelper key, final String alias, final String toReplace) {
		final String locale = I18n(key, alias, toReplace);
		if (locale != null && !locale.isEmpty()) {
			sender.sendMessage(locale);
		}
	}

	public static void sParsedLocale(final Player p, final String locale) {
		final HashMap<String, String> replace = new HashMap<String, String>();
		final ACPlayer acPlayer = ACPlayer.getPlayer(p);
		final long total = acPlayer.getCurrentPlayedTime();
		replace.putAll(playedTime(getPlayerName(p), total));
		replace.put(
				"nb",
				String.valueOf(p.getServer().getOnlinePlayers().length
						- InvisibleWorker.getInstance().nbInvisibles()));
		final Collection<String> list = Utils.getPlayerList(p);
		String connected = Joiner.on(", ").join(list);
		if (connected.length() >= ACMinecraftFontWidthCalculator.chatwidth) {
			final String tmp = connected.substring(0,
					ACMinecraftFontWidthCalculator.chatwidth);
			final String tmp2 = connected.substring(
					ACMinecraftFontWidthCalculator.chatwidth,
					connected.length());
			connected = tmp + "//n" + tmp2;
		}
		replace.put("connected", connected);
		final String serverTime = replaceDateAndTimeFormat(getServerRealTime("GMT"
				+ ConfigEnum.DT_GMT.getString()));
		replace.put("time", serverTime);
		final String date = replaceDateAndTimeFormat(acPlayer, Whois.LOGIN);
		if (date == null) {
			replace.put("lastlogin", I18n("noLoginInformation"));
		} else {
			replace.put("lastlogin", date);
		}
		final String messageToSend = I18n(locale, replace);
		if (messageToSend != null) {
			messageToSend.replace("\\n", "\n").replace("//n", "\n");
			for (final String toSend : messageToSend.split("\n")) {
				if (toSend.isEmpty()) {
					continue;
				}
				p.sendMessage(toSend);
			}
		}

	}

	public static boolean timeSet(final CommandSender sender, final String time) {
		return timeSet(sender, time, null);
	}

	// all functions return if they handled the command
	// false then means to show the default handle
	// ! make sure the player variable IS a player!
	// set world time to a new value
	public static boolean timeSet(final CommandSender sender,
			final String time, final String world) {
		if (isPlayer(sender, false) && world == null) {
			final Player p = (Player) sender;
			setTime(sender, p.getWorld(), time);
		} else if (world != null) {
			final World w = sender.getServer().getWorld(world);
			if (w == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("world", world);
				Utils.sI18n(sender, "worldNotFound", replace);
				return true;
			}
			setTime(sender, w, time);
		} else {
			for (final World w : sender.getServer().getWorlds()) {
				setTime(sender, w, time);
			}
		}
		return true;

	}

	public static void tpP2P(final CommandSender sender, final String nFrom,
			final String nTo, final Type.Tp type) {
		boolean found = true;
		final Player pFrom = ACPluginManager.getServer().getPlayer(nFrom);
		final Player pTo = ACPluginManager.getServer().getPlayer(nTo);
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", nFrom);
		if (pFrom == null) {
			replace.put("player", nFrom);
			Utils.sI18n(sender, "playerNotFound", replace);
			found = false;
		}
		if (pTo == null) {
			replace.put("player", nTo);
			Utils.sI18n(sender, "playerNotFound", replace);
			found = false;
		}

		if (!found) {
			return;
		}
		if (!ConfigEnum.TP_DIFF_WORLD.getBoolean()
				&& !pFrom.getWorld().equals(pTo.getWorld())
				&& !PermissionManager.hasPerm(sender, "admincmd.tp.world."
						+ pTo.getWorld().getName().replace(' ', '_'), false)) {
			replace.put("to", pTo.getName());
			sI18n(sender, "diffWorld", replace);
			return;
		}
		if (type.equals(Type.Tp.TO) && !checkImmunity(pFrom, pTo)) {
			sI18n(sender, "insufficientLvl");
			return;
		}
		if (type.equals(Type.Tp.HERE) && !checkImmunity(pTo, pFrom)) {
			sI18n(sender, "insufficientLvl");
			return;
		}
		if (type.equals(Type.Tp.PLAYERS) && !checkImmunity(sender, pFrom)
				&& !checkImmunity(sender, pTo)) {
			sI18n(sender, "insufficientLvl");
			return;
		}
		if ((type.equals(Type.Tp.TO) || type.equals(Type.Tp.PLAYERS))
				&& InvisibleWorker.getInstance().hasInvisiblePowers(pTo)
				&& !PermissionManager.hasPerm(pFrom,
						"admincmd.invisible.cansee", false)) {
			replace.put("player", nTo);
			Utils.sI18n(sender, "playerNotFound", replace);
			return;
		}
		if ((type.equals(Type.Tp.HERE) || type.equals(Type.Tp.PLAYERS))
				&& (InvisibleWorker.getInstance().hasInvisiblePowers(pFrom) && !PermissionManager
						.hasPerm(pTo, "admincmd.invisible.cansee", false))) {
			replace.put("player", nFrom);
			Utils.sI18n(sender, "playerNotFound", replace);
			return;
		}
		if (PermissionManager.hasPerm(sender, "admincmd.spec.notprequest",
				false)) {
			ACPluginManager.scheduleSyncTask(new TeleportTask(pFrom, pTo
					.getLocation()));
			replace.put("fromPlayer", pFrom.getName());
			replace.put("toPlayer", pTo.getName());
			Utils.sI18n(sender, "tp", replace);
		} else if ((type.equals(Type.Tp.TO) || type.equals(Type.Tp.PLAYERS))
				&& ACPlayer.getPlayer(pTo.getName()).hasPower(Type.TP_REQUEST)) {
			ACPlayer.getPlayer(pTo).setTpRequest(new TpRequest(pFrom, pTo));
			Utils.sI18n(pTo, "tpRequestTo", "player", pFrom.getName());
			final HashMap<String, String> replace2 = new HashMap<String, String>();
			replace2.put("player", pTo.getName());
			if (type.toString().equalsIgnoreCase("to")) {
				replace2.put("tp_type", Utils.I18n("tpTO"));
			} else if (type.toString().equalsIgnoreCase("players")) {
				replace2.put("tp_type", Utils.I18n("tpPLAYERSTO"));
				replace2.put("target", pTo.getName());
			} else {
				replace2.put("tp_type", type.toString());
			}
			Utils.sI18n(pFrom, "tpRequestSend", replace2);

		} else if ((type.equals(Type.Tp.HERE) || type.equals(Type.Tp.PLAYERS))
				&& ACPlayer.getPlayer(pFrom.getName())
						.hasPower(Type.TP_REQUEST)) {
			ACPlayer.getPlayer(pFrom).setTpRequest(new TpRequest(pFrom, pTo));
			Utils.sI18n(pFrom, "tpRequestFrom", "player", pTo.getName());
			final HashMap<String, String> replace2 = new HashMap<String, String>();
			replace2.put("player", pFrom.getName());
			if (type.toString().equalsIgnoreCase("here")) {
				replace2.put("tp_type", Utils.I18n("tpHERE"));
			} else if (type.toString().equalsIgnoreCase("players")) {
				replace2.put("tp_type", Utils.I18n("tpPLAYERSFROM"));
				replace2.put("target", pFrom.getName());
			} else {
				replace2.put("tp_type", type.toString());
			}
			Utils.sI18n(pTo, "tpRequestSend", replace2);

		} else {
			ACPluginManager.scheduleSyncTask(new TeleportTask(pFrom, pTo
					.getLocation()));
			replace.put("fromPlayer", pFrom.getName());
			replace.put("toPlayer", pTo.getName());
			Utils.sI18n(sender, "tp", replace);

		}
	}

	/**
	 * Transform a given time to an elapsed time.
	 * 
	 * @param time
	 *            in milisec
	 * @return Long[] containing days, hours, mins and sec.
	 */
	public static Long[] transformToElapsedTime(final long time) {
		long diff = time;

		final long elapsedDays = diff / dayInMillis;
		diff = diff % dayInMillis;
		final long elapsedHours = diff / hourInMillis;
		diff = diff % hourInMillis;
		final long elapsedMinutes = diff / minuteInMillis;
		diff = diff % minuteInMillis;
		final long elapsedSeconds = diff / secondInMillis;

		return new Long[]{elapsedDays, elapsedHours, elapsedMinutes,
				elapsedSeconds};
	}

	public static boolean weather(final CommandSender sender,
			final Type.Weather type, final CommandArgs duration) {
		if (isPlayer(sender, false)) {
			if (duration.length >= 2) {
				final World w = sender.getServer().getWorld(
						duration.getString(1));
				if (w == null) {
					final HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("world", duration.getString(2));
					Utils.sI18n(sender, "worldNotFound", replace);
					return true;
				}
				weatherChange(sender, w, type, duration);
			} else if ((type.equals(Type.Weather.FREEZE) || type
					.equals(Type.Weather.CLEAR))
					&& duration.getString(0) != null) {
				final World w = sender.getServer().getWorld(
						duration.getString(0));
				if (w == null) {
					final HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("world", duration.getString(0));
					Utils.sI18n(sender, "worldNotFound", replace);
					return true;
				}
				weatherChange(sender, w, type, duration);
			} else {
				weatherChange(sender, ((Player) sender).getWorld(), type,
						duration);
			}
		} else if (duration.length >= 2) {
			final World w = sender.getServer().getWorld(duration.getString(1));
			if (w == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("world", duration.getString(0));
				Utils.sI18n(sender, "worldNotFound", replace);
				return true;
			}
			weatherChange(sender, w, type, duration);
		} else if ((type.equals(Type.Weather.FREEZE) || type
				.equals(Type.Weather.CLEAR)) && duration.getString(0) != null) {
			final World w = sender.getServer().getWorld(duration.getString(0));
			if (w == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("world", duration.getString(0));
				Utils.sI18n(sender, "worldNotFound", replace);
				return true;
			}
			weatherChange(sender, w, type, duration);
		} else {
			for (final World w : sender.getServer().getWorlds()) {
				weatherChange(sender, w, type, duration);
			}
		}

		return true;
	}

	private static void weatherChange(final CommandSender sender,
			final World w, final Type.Weather type, final CommandArgs duration) {
		if (!type.equals(Type.Weather.FREEZE)
				&& !ACWorld.getWorld(w.getName())
						.getInformation(Type.WEATHER_FROZEN.toString())
						.isNull()) {
			sender.sendMessage(ChatColor.GOLD + Utils.I18n("wFrozen") + " "
					+ w.getName());
			return;
		}
		switch (type) {
			case CLEAR :
				w.setThundering(false);
				w.setStorm(false);
				sender.sendMessage(ChatColor.GOLD + Utils.I18n("sClear") + " "
						+ w.getName());
				break;
			case STORM :
				final HashMap<String, String> replace = new HashMap<String, String>();
				if (duration == null || duration.length < 1) {
					w.setStorm(true);
					w.setThundering(true);
					w.setWeatherDuration(12000);
					replace.put("duration", "10");
					sender.sendMessage(ChatColor.GOLD
							+ Utils.I18n("sStorm", replace) + w.getName());
				} else {
					try {
						w.setStorm(true);
						w.setThundering(true);
						final int time = duration.getInt(0);
						w.setWeatherDuration(time * 1200);
						replace.put("duration", String.valueOf(time));
						sender.sendMessage(ChatColor.GOLD
								+ Utils.I18n("sStorm", replace) + w.getName());
					} catch (final NumberFormatException e) {
						sender.sendMessage(ChatColor.BLUE + "Sorry, that ("
								+ duration.getString(0) + ") isn't a number!");
						w.setStorm(true);
						w.setWeatherDuration(12000);
						replace.put("duration", "10");
						sender.sendMessage(ChatColor.GOLD
								+ Utils.I18n("sStorm", replace) + w.getName());
					}
				}
				break;
			case FREEZE :
				if (!ACWorld.getWorld(w.getName())
						.getInformation(Type.WEATHER_FROZEN.toString())
						.isNull()) {
					ACWorld.getWorld(w.getName()).removeInformation(
							Type.WEATHER_FROZEN.toString());
					sender.sendMessage(ChatColor.GREEN
							+ Utils.I18n("wUnFrozen") + " " + ChatColor.WHITE
							+ w.getName());
				} else {
					ACWorld.getWorld(w.getName()).setInformation(
							Type.WEATHER_FROZEN.toString(), true);
					sender.sendMessage(ChatColor.RED + Utils.I18n("wFrozen")
							+ " " + ChatColor.WHITE + w.getName());
				}
				break;
			case RAIN :
				final HashMap<String, String> replaceRain = new HashMap<String, String>();
				if (duration == null || duration.length < 1) {
					w.setStorm(true);
					w.setThundering(false);
					w.setWeatherDuration(12000);
					replaceRain.put("duration", "10");
					sender.sendMessage(ChatColor.GOLD
							+ Utils.I18n("sRain", replaceRain) + w.getName());
				} else {
					try {
						w.setStorm(true);
						w.setThundering(false);
						final int time = duration.getInt(0);
						w.setWeatherDuration(time * 1200);
						replaceRain.put("duration", String.valueOf(time));
						sender.sendMessage(ChatColor.GOLD
								+ Utils.I18n("sRain", replaceRain)
								+ w.getName());
					} catch (final NumberFormatException e) {
						sender.sendMessage(ChatColor.BLUE + "Sorry, that ("
								+ duration.getString(0) + ") isn't a number!");
						w.setStorm(true);
						w.setWeatherDuration(12000);
						replaceRain.put("duration", "10");
						sender.sendMessage(ChatColor.GOLD
								+ Utils.I18n("sRain", replaceRain)
								+ w.getName());
					}
				}
				break;
			default :
				break;
		}
	}

	/**
	 * Check if the chunk is loaded before teleport the player to the location
	 * 
	 * @param player
	 *            player to be teleported
	 * @param loc
	 *            location where the player will be tp
	 */
	public static void teleportWithChunkCheck(final Player player,
			final Location loc) {
		final CraftServer server = ((CraftServer) player.getServer());
		final PlayerTeleportEvent event = new ACTeleportEvent(player,
				player.getLocation(), loc, TeleportCause.PLUGIN);
		server.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}
		final Location toLocation = event.getTo();
		final int x = toLocation.getBlockX() >> 4;
		final int z = toLocation.getBlockZ() >> 4;
		if (!toLocation.getWorld().isChunkLoaded(x, z)) {
			toLocation.getWorld().loadChunk(x, z);
		}
		ACPlayer.getPlayer(player).setLastLocation(player.getLocation());
		final WorldServer fromWorld = ((CraftWorld) player.getLocation()
				.getWorld()).getHandle();
		final WorldServer toWorld = ((CraftWorld) toLocation.getWorld())
				.getHandle();
		final EntityPlayer entity = ((CraftPlayer) player).getHandle();
		// Check if the fromWorld and toWorld are the same.
		if (fromWorld == toWorld) {
			entity.netServerHandler.teleport(toLocation);
		} else {
			// Close any foreign inventory
			if (entity.activeContainer != entity.defaultContainer) {
				entity.closeInventory();
			}
			server.getHandle().moveToWorld(entity, toWorld.dimension, true,
					toLocation);
		}
	}

	/**
	 * Get the player list ordered by group and alphabetically for the sender
	 * 
	 * @param sender
	 *            sender of the command
	 * @return a Collection containing what to display
	 */
	public static Collection<String> getPlayerList(final CommandSender sender) {
		final List<Player> online = Utils.getOnlinePlayers();
		final Map<Player, String> players = new TreeMap<Player, String>(
				new PlayerComparator());
		for (final Player p : online) {
			if ((InvisibleWorker.getInstance().hasInvisiblePowers(p) || ACPlayer
					.getPlayer(p).hasPower(Type.FAKEQUIT))
					&& !PermissionManager.hasPerm(sender,
							"admincmd.invisible.cansee", false)) {
				continue;
			}
			players.put(p, Utils.getPlayerName(p, sender));
		}
		return Collections.unmodifiableCollection(players.values());
	}

	private static String timeLongToSring(final Long time) {
		return time < 10 ? "0" + time : time.toString();
	}

	/**
	 * Send the played time of a player to a another one.
	 * 
	 * @param playername
	 *            name of the player that the time belong to
	 * @param total
	 *            total time played
	 */
	public static Map<String, String> playedTime(final String playername,
			final long total) {
		final Long[] time = Utils.transformToElapsedTime(total);
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("d", time[0].toString());
		replace.put("h", timeLongToSring(time[1]));
		replace.put("m", timeLongToSring(time[2]));
		replace.put("s", timeLongToSring(time[3]));
		replace.put("player", playername);
		return replace;
	}

	/**
	 * Cut in 2 part the given time if it's in the format : <br />
	 * <blockquote> <X day | X hour | X minute | X week | X month> </blockquote>
	 * 
	 * @param toParse
	 *            input to be parsed
	 * @return a 2 sized String array with the parsed time if successful, else
	 *         an empty one.
	 */
	public static String[] tempStringParser(final String toParse) {
		final String[] parsed = new String[2];
		final Matcher numberMatcher = NUMBERS.matcher(toParse);
		final Matcher time1Matcher = TIMES1.matcher(toParse);
		final Matcher time2Matcher = TIMES2.matcher(toParse);
		if (numberMatcher.find()) {
			parsed[0] = numberMatcher.group();
		}
		if (time1Matcher.find()) {
			parsed[1] = time1Matcher.group();
		} else if (time2Matcher.find()) {
			parsed[1] = time1Matcher.group();
		}
		return parsed;
	}

	/**
	 * Parse the given string to get the time in an integer it's in the format : <br />
	 * <blockquote> <X day | X hour | X minute | X week | X month> </blockquote>
	 * 
	 * @param toParse
	 *            time to parse
	 * @return time parsed, -1 if nothing to be parsed
	 * @throws NotANumberException
	 *             if the String to be parsed doesn't have the right format
	 */
	public static int timeParser(final String toParse)
			throws NotANumberException {
		int tmpBan;
		final String[] tmpTimeParsed = Utils.tempStringParser(toParse);
		if (tmpTimeParsed[0] == null) {
			return -1;
		}
		if (tmpTimeParsed[1] == null) {
			try {
				return Integer.parseInt(tmpTimeParsed[0]);
			} catch (final NumberFormatException e) {
				throw new NotANumberException("Time given : "
						+ tmpTimeParsed[0], e);
			}
		} else {
			try {
				tmpBan = Integer.parseInt(tmpTimeParsed[0]);
			} catch (final NumberFormatException e) {
				throw new NotANumberException("Time given : "
						+ tmpTimeParsed[0], e);
			}
			final String timeMulti = tmpTimeParsed[1];
			if (timeMulti.contains("month") || timeMulti.contains("m")) {
				return tmpBan * 43200;
			}
			if (timeMulti.contains("week") || timeMulti.contains("w")) {
				return tmpBan * 10080;
			}
			if (timeMulti.contains("day") || timeMulti.contains("d")) {
				return tmpBan * 1440;
			}
			if (timeMulti.contains("hour") || timeMulti.contains("h")) {
				return tmpBan * 60;
			}
			if (timeMulti.contains("year") || timeMulti.contains("y")) {
				return tmpBan * 525600;
			}
			throw new NotANumberException("Can't parse the time : "
					+ tmpTimeParsed[1]);
		}
	}
}
