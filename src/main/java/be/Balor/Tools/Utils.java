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
package be.Balor.Tools;

import in.mDev.MiracleM4n.mChatSuite.MInfoReader;
import info.somethingodd.bukkit.OddItem.OddItem;
import info.somethingodd.bukkit.OddItem.OddItemBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.Packet201PlayerInfo;
import net.minecraft.server.Packet4UpdateTime;
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
import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.EmptyPlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.Tools.Blocks.BlockRemanence;
import be.Balor.Tools.Blocks.IBlockRemanenceFactory;
import be.Balor.Tools.Blocks.LogBlockRemanenceFactory;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Files.FileManager;
import be.Balor.Tools.Threads.ReplaceBlockTask;
import be.Balor.Tools.Threads.TeleportTask;
import be.Balor.Tools.Type.Whois;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;

import de.diddiz.LogBlock.Consumer;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Utils {
	public static class SetTime implements Runnable {
		private final World w;
		private final Long time;

		/**
		 *
		 */
		public SetTime(World w) {
			this.w = w;
			this.time = w.getTime();
		}

		/**
		 * @param w
		 * @param time
		 */
		public SetTime(World w, Long time) {
			this.w = w;
			this.time = time;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			long margin = (time - w.getFullTime()) % 24000;
			if (margin < 0)
				margin += 24000;
			final long newTime = w.getFullTime() + margin;
			final WorldServer world = ((CraftWorld) w).getHandle();
			world.setTime(newTime);
			for (final Player p : getOnlinePlayers()) {
				final CraftPlayer cp = (CraftPlayer) p;
				cp.getHandle().netServerHandler.sendPacket(new Packet4UpdateTime(cp.getHandle()
						.getPlayerTime()));
			}

		}

	}

	public static OddItemBase oddItem = null;
	public static Consumer logBlock = null;
	public static Heroes heroes = null;
	public static MInfoReader mChatApi = null;
	public static boolean signExtention = false;
	public final static long secondInMillis = 1000;
	public final static long minuteInMillis = secondInMillis * 60;
	public final static long hourInMillis = minuteInMillis * 60;
	public final static long dayInMillis = hourInMillis * 24;

	/**
	 * @author Balor (aka Antoine Aflalo)
	 * 
	 */

	public final static int MAX_BLOCKS = 512;

	public static void addLocale(String key, String value) {
		LocaleManager.getInstance().addLocale(key, value);
	}

	public static void addLocale(String key, String value, boolean override) {
		LocaleManager.getInstance().addLocale(key, value, true);
	}

	/**
	 * Add the player in the online list (TAB key)
	 * 
	 * @param player
	 *            player to remove
	 */
	public static void addPlayerInOnlineList(Player player) {
		((CraftServer) player.getServer()).getHandle().sendAll(
				new Packet201PlayerInfo(((CraftPlayer) player).getHandle().listName, true, 1000));
	}

	public static void addPlayerInOnlineList(Player toAdd, Player fromPlayer) {
		((CraftPlayer) fromPlayer).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(
				((CraftPlayer) toAdd).getHandle().listName, true, 1000));
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] Arrays_copyOfRange(T[] original, int start, int end) {
		if (original.length >= start && 0 <= start) {
			if (start <= end) {
				final int length = end - start;
				final int copyLength = Math.min(length, original.length - start);
				final T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType(),
						length);

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
	public static void broadcastFakeJoin(Player player) {
		if (mChatApi != null)
			Utils.broadcastMessage(getPlayerName(player, null, true) + " "
					+ mChatApi.getEventMessage("Join"));
		else
			Utils.broadcastMessage(I18n("joinMessage", "name", getPlayerName(player, null, true)));

	}

	/**
	 * Broadcast a fakeQuit message for the selected player
	 * 
	 * @param player
	 *            that fake quit.
	 */
	public static void broadcastFakeQuit(Player player) {
		if (mChatApi != null)
			Utils.broadcastMessage(getPlayerName(player, null, true) + " "
					+ mChatApi.getEventMessage("Quit"));
		else
			Utils.broadcastMessage(I18n("quitMessage", "name", getPlayerName(player, null, true)));

	}

	/**
	 * Broadcast message to every user since the bukkit one is bugged
	 * 
	 * @param message
	 */
	public static void broadcastMessage(String message) {
		for (final Player p : getOnlinePlayers())
			p.sendMessage(message);
		// new ColouredConsoleSender((CraftServer)
		// ACPluginManager.getServer()).sendMessage(message);
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
	public static boolean checkImmunity(CommandSender sender, CommandArgs args, int index) {
		final Player target = sender.getServer().getPlayer(args.getString(index));
		if (target != null)
			if (checkImmunity(sender, target))
				return true;
			else {
				sI18n(sender, "insufficientLvl");
				return false;
			}
		else {
			if (!ACHelper.getInstance().getConfBoolean("useImmunityLvl"))
				return true;
			if (!isPlayer(sender, false))
				return true;
			final Player player = (Player) sender;
			final int pLvl = ACHelper.getInstance().getLimit(player, "immunityLvl",
					"defaultImmunityLvl");
			final int tLvl = ACPlayer.getPlayer(args.getString(index))
					.getInformation("immunityLvl").getInt(0);
			if (PermissionManager.hasPerm(player, "admincmd.immunityLvl.samelvl", false)
					&& pLvl != tLvl) {
				sI18n(sender, "insufficientLvl");
				return false;
			}
			if (pLvl >= tLvl)
				return true;
			else {
				sI18n(sender, "insufficientLvl");
				return false;
			}

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
	public static boolean checkImmunity(CommandSender sender, Player target) {
		if (!ACHelper.getInstance().getConfBoolean("useImmunityLvl"))
			return true;
		if (!isPlayer(sender, false))
			return true;
		if (target == null)
			return true;
		final Player player = (Player) sender;
		final int pLvl = ACHelper.getInstance().getLimit(player, "immunityLvl",
				"defaultImmunityLvl");
		final int tLvl = ACHelper.getInstance().getLimit(target, "immunityLvl",
				"defaultImmunityLvl");
		if (PermissionManager.hasPerm(player, "admincmd.immunityLvl.samelvl", false)
				&& pLvl != tLvl)
			return false;
		if (pLvl >= tLvl)
			return true;
		else
			return false;
	}

	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 */
	public static MaterialContainer checkMaterial(String mat) {
		MaterialContainer mc = new MaterialContainer();
		try {
			if (oddItem != null) {
				final ItemStack is = OddItem.getItemStack(mat);
				if (is != null) {
					return new MaterialContainer(is);
				}
			}
		} catch (final Exception e) {
		}
		String[] info = new String[2];
		if (mat.contains(":")) {
			info = mat.split(":");
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

	public static String colorParser(String toParse) {
		return colorParser(toParse, "&");
	}

	/**
	 * Parse a string and replace the color in it
	 * 
	 * @author Speedy64
	 * @param toParse
	 * @return
	 */
	public static String colorParser(String toParse, String delimiter) {
		String ResultString = null;
		try {
			final Pattern regex = Pattern.compile(delimiter + "[A-Fa-f]|" + delimiter + "1[0-5]|"
					+ delimiter + "[0-9]");
			Matcher regexMatcher = regex.matcher(toParse);
			String result = toParse;
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group();
				int colorint = Integer.parseInt(ResultString.substring(1, 2), 16);
				if (ResultString.length() > 1) {
					if (colorint == 1 && ResultString.substring(2).matches("[012345]")) {
						colorint = colorint * 10 + Integer.parseInt(ResultString.substring(2));
					}
				}
				result = regexMatcher.replaceFirst(ChatColor.getByChar(
						Integer.toHexString(colorint)).toString());
				regexMatcher = regex.matcher(result);
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
	private static Stack<BlockRemanence> drainFluid(String playername, Block block, int radius) {
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
					final SimplifiedLocation newPos = new SimplifiedLocation(w, x, y, z);
					if (isFluid(newPos) && !visited.contains(newPos)) {
						visited.add(newPos);
						processQueue.push(newPos);
						current = IBlockRemanenceFactory.FACTORY.createBlockRemanence(newPos);
						blocks.push(current);
						blocksCache.push(current);
						if (blocksCache.size() == MAX_BLOCKS)
							ACPluginManager.getScheduler().scheduleSyncDelayedTask(
									ACHelper.getInstance().getCoreInstance(),
									new ReplaceBlockTask(blocksCache));
					}

				}
			}
		}
		while (!processQueue.isEmpty()) {
			final SimplifiedLocation loc = processQueue.pop();
			for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
				for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
					for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
						final SimplifiedLocation newPos = new SimplifiedLocation(w, x, y, z);
						if (!visited.contains(newPos) && isFluid(newPos)
								&& start.distance(newPos) < radius) {
							processQueue.push(newPos);
							current = IBlockRemanenceFactory.FACTORY.createBlockRemanence(newPos);
							blocks.push(current);
							blocksCache.push(current);
							if (blocksCache.size() == MAX_BLOCKS)
								ACPluginManager.getScheduler().scheduleSyncDelayedTask(
										ACHelper.getInstance().getCoreInstance(),
										new ReplaceBlockTask(blocksCache));
							visited.add(newPos);
						}
					}

				}
			}
		}
		if (blocksCache.size() == MAX_BLOCKS)
			ACPluginManager.getScheduler().scheduleSyncDelayedTask(
					ACHelper.getInstance().getCoreInstance(), new ReplaceBlockTask(blocksCache));
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
	 */
	public static ACPlayer getACPlayer(CommandSender sender, CommandArgs args, String permNode) {
		final Player target = Utils.getUser(sender, args, permNode, 0,
				!Utils.isPlayer(sender, false));
		ACPlayer actarget;
		if (target == null) {
			if (args.length == 0) {
				sender.sendMessage("You must type the player name");
				return null;
			}
			actarget = ACPlayer.getPlayer(args.getString(0));
			if (actarget instanceof EmptyPlayer) {
				Utils.sI18n(sender, "playerNotFound", "player", actarget.getName());
				return null;
			}
			if (!Utils.checkImmunity(sender, args, 0))
				return null;
		} else
			actarget = ACPlayer.getPlayer(target);
		return actarget;
	}

	public static double getDistanceSquared(Player player1, Player player2) {
		if (!player1.getWorld().getName().equals(player2.getWorld().getName()))
			return Double.MAX_VALUE;
		final Location loc1 = player1.getLocation();
		final Location loc2 = player2.getLocation();
		return Math.pow((loc1.getX() - loc2.getX()), 2) + Math.pow((loc1.getZ() - loc2.getZ()), 2);
	}

	/**
	 * Get the elapsed time since the start.
	 * 
	 * @param start
	 * @return
	 */
	public static Long[] getElapsedTime(long start) {
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
	public static Home getHome(CommandSender sender, String toParse) {
		final Home result = new Home();
		if (toParse != null && toParse.contains(":")) {
			try {
				final String[] split = toParse.split(":");
				result.player = split[0];
				result.home = split[1];
			} catch (final ArrayIndexOutOfBoundsException e) {
			}
			if (isPlayer(sender, false)) {
				final Player p = (Player) sender;
				if (!p.getName().equals(result.player)
						&& !PermissionManager.hasPerm(p, "admincmd.admin.home"))
					return null;
			}
			return result;
		}
		if (!isPlayer(sender))
			return null;
		final Player p = ((Player) sender);
		result.player = p.getName();
		if (toParse != null)
			result.home = toParse;
		else
			result.home = p.getWorld().getName();

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

	public static Player getPlayer(String name) {
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
					curDelta = player.getDisplayName().length() - lowerName.length();
					if (curDelta < delta) {
						found = player;
						delta = curDelta;
					}
				}
				if (curDelta == 0)
					break;
			}
		}
		return found;

	}

	public static String getPlayerName(Player player) {
		return getPlayerName(player, null, false);
	}

	public static String getPlayerName(Player player, CommandSender sender) {
		return getPlayerName(player, sender, true);
	}

	/**
	 * Get the complete player name with all prefix
	 * 
	 * @param player
	 *            player to get the name
	 * @param sender
	 *            sender that want the name
	 * @param withPrefix
	 *            return the name with or without prefixes/suffix (e.g [INV])
	 * @return the complete player name with prefix
	 */
	public static String getPlayerName(Player player, CommandSender sender, boolean withPrefix) {
		if (withPrefix) {
			String prefix = colorParser(getPrefix(player, sender));
			final String suffix = colorParser(PermissionManager.getSuffix(player));
			if (prefix.isEmpty())
				prefix = ChatColor.WHITE.toString();
			if (ACHelper.getInstance().getConfBoolean("useDisplayName"))
				return prefix + player.getDisplayName() + suffix + ChatColor.YELLOW;

			return prefix + player.getName() + suffix + ChatColor.YELLOW;
		}

		if (ACHelper.getInstance().getConfBoolean("useDisplayName"))
			return ChatColor.WHITE + player.getDisplayName();

		return ChatColor.WHITE + player.getName();
	}

	/**
	 * Get the prefix of the player, by checking the right the sender have
	 * 
	 * @param player
	 * @return
	 */
	private static String getPrefix(Player player, CommandSender sender) {
		boolean isInv = false;
		String prefixstring = "";
		String statusPrefix = "";
		if (sender != null)
			isInv = InvisibleWorker.getInstance().hasInvisiblePowers(player.getName())
					&& PermissionManager.hasPerm(sender, "admincmd.invisible.cansee", false);
		if (isInv)
			statusPrefix = Utils.I18n("invTitle");
		if (AFKWorker.getInstance().isAfk(player))
			statusPrefix = Utils.I18n("afkTitle") + statusPrefix;
		prefixstring = PermissionManager.getPrefix(player);
		String result = statusPrefix;
		if (prefixstring.length() > 1)
			result += prefixstring;
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
	public static Date getServerRealTime(String gmt) {
		Date serverTime;
		final TimeZone tz = TimeZone.getTimeZone(gmt);
		final Calendar cal = Calendar.getInstance(tz);
		cal.setTime(new Date());
		serverTime = cal.getTime();
		return serverTime;
	}

	/**
	 * Get a txt-file and return its content in a String
	 * 
	 * @param fileName
	 *            - The name of the file to be loaded
	 * @return The content of the file
	 */
	public static String getTextFile(String fileName) {
		final StringBuffer result = new StringBuffer();
		try {
			final File fileDir = FileManager.getInstance().getInnerFile(fileName);
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

	public static Player getUser(CommandSender sender, CommandArgs args, String permNode) {
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
	 */
	public static Player getUser(CommandSender sender, CommandArgs args, String permNode,
			int index, boolean errorMsg) {
		Player target = null;
		if (args.length >= index + 1) {
			target = getPlayer(args.getString(index));
			if (target != null)
				if (target.equals(sender))
					return target;
				else if (PermissionManager.hasPerm(sender, permNode + ".other")) {
					if (checkImmunity(sender, target))
						return target;
					else {
						Utils.sI18n(sender, "insufficientLvl");
						return null;
					}
				} else
					return null;
		} else if (isPlayer(sender, false))
			target = ((Player) sender);
		else if (errorMsg) {
			sender.sendMessage("You must type the player name");
			return target;
		}
		if (target == null && errorMsg) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", args.getString(index));
			Utils.sI18n(sender, "playerNotFound", replace);
			return target;
		}
		return target;

	}

	public static String I18n(String key) {
		return I18n(key, null);
	}

	public static String I18n(String key, Map<String, String> replace) {
		return LocaleManager.getInstance().get(key, replace);
	}

	public static String I18n(String key, String alias, String toReplace) {
		return LocaleManager.getInstance().get(key, alias, toReplace);
	}

	/**
	 * Check if the block is a fluid.
	 * 
	 * @param loc
	 * @return
	 */
	private static boolean isFluid(Location loc) {
		final Block b = loc.getWorld().getBlockAt(loc);
		if (b == null)
			return false;
		return b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER
				|| b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA;
	}

	/**
	 * Check if the command sender is a Player
	 * 
	 * @return
	 */
	public static boolean isPlayer(CommandSender sender) {
		return isPlayer(sender, true);
	}

	public static boolean isPlayer(CommandSender sender, boolean msg) {
		if (sender instanceof Player)
			return true;
		else {
			if (msg)
				Utils.sI18n(sender, "mustBePlayer");
			return false;
		}
	}

	/**
	 * Remove the player from the online list (TAB key)
	 * 
	 * @param player
	 *            player to remove
	 */
	public static void removePlayerFromOnlineList(Player player) {
		((CraftServer) player.getServer()).getHandle().sendAll(
				new Packet201PlayerInfo(((CraftPlayer) player).getHandle().listName, false, 9999));
	}

	public static void removePlayerFromOnlineList(Player toRemove, Player fromPlayer) {
		if (toRemove == null || fromPlayer == null)
			return;
		((CraftPlayer) fromPlayer).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(
				((CraftPlayer) toRemove).getHandle().listName, false, 9999));
	}

	public static Integer replaceBlockByAir(CommandSender sender, CommandArgs args,
			List<Material> mat, int defaultRadius) {
		if (Utils.isPlayer(sender)) {
			int radius = defaultRadius;
			if (args.length >= 1) {
				try {
					radius = args.getInt(0);
				} catch (final NumberFormatException e) {
					if (args.length >= 2)
						try {
							radius = args.getInt(1);
						} catch (final NumberFormatException e2) {

						}
				}

			}
			final String playername = ((Player) sender).getName();
			IBlockRemanenceFactory.FACTORY.setPlayerName(playername);
			Stack<BlockRemanence> blocks;
			final Block block = ((Player) sender).getLocation().getBlock();
			if (mat.contains(Material.LAVA) || mat.contains(Material.WATER))
				blocks = drainFluid(playername, block, radius);
			else
				blocks = replaceInCuboid(playername, mat, block, radius);
			if (!blocks.isEmpty())
				ACHelper.getInstance().addInUndoQueue(playername, blocks);
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
	public static String replaceDateAndTimeFormat() {
		String timeFormatted = "";
		final String format = ACHelper.getInstance().getConfString("DateAndTime.Format");
		final SimpleDateFormat formater = new SimpleDateFormat(format);
		final Date serverTime = getServerRealTime("GMT"
				+ ACHelper.getInstance().getConfString("DateAndTime.GMToffSet"));
		timeFormatted = formater.format(serverTime);
		return timeFormatted;
	}

	public static String replaceDateAndTimeFormat(ACPlayer player, Type.Whois type) {
		final String format = ACHelper.getInstance().getConfString("DateAndTime.Format");
		final SimpleDateFormat formater = new SimpleDateFormat(format);
		String lastlogin = "";
		lastlogin = formater.format(new Date(player.getInformation(type.getVal()).getLong(1)));
		if (lastlogin == formater.format(new Date(1)))
			return null;
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
	private static Stack<BlockRemanence> replaceInCuboid(String playername, List<Material> mat,
			Block block, int radius) {
		final Stack<BlockRemanence> blocks = new Stack<BlockRemanence>();
		final Stack<BlockRemanence> blocksCache = new Stack<BlockRemanence>();
		final int limitX = block.getX() + radius;
		final int limitY = block.getY() + radius;
		final int limitZ = block.getZ() + radius;
		BlockRemanence br = null;
		for (int y = block.getY() - radius; y <= limitY; y++) {
			for (int x = block.getX() - radius; x <= limitX; x++)
				for (int z = block.getZ() - radius; z <= limitZ; z++) {
					if (mat.contains(Material.getMaterial(block.getWorld()
							.getBlockTypeIdAt(x, y, z)))) {
						br = IBlockRemanenceFactory.FACTORY
								.createBlockRemanence(new SimplifiedLocation(block.getWorld(), x,
										y, z));
						blocks.push(br);
						blocksCache.push(br);
						if (blocksCache.size() == MAX_BLOCKS)
							ACPluginManager.getScheduler().scheduleSyncDelayedTask(
									ACHelper.getInstance().getCoreInstance(),
									new ReplaceBlockTask(blocksCache), 1);

					}
				}
		}
		ACPluginManager.getScheduler().scheduleSyncDelayedTask(
				ACHelper.getInstance().getCoreInstance(), new ReplaceBlockTask(blocksCache), 1);
		return blocks;
	}

	public static void sendMessage(CommandSender sender, CommandSender player, String key) {
		sendMessage(sender, player, key, null);
	}

	public static void sendMessage(CommandSender sender, CommandSender player, String key,
			Map<String, String> replace) {
		final String msg = I18n(key, replace);
		if (msg != null && !msg.isEmpty()) {
			if (!sender.equals(player))
				player.sendMessage(msg);
			sender.sendMessage(msg);
		}

	}

	/**
	 * @param logBlock
	 *            the logBlock to set
	 */
	public static void setLogBlock(Consumer logBlock) {
		Utils.logBlock = logBlock;
		IBlockRemanenceFactory.FACTORY = new LogBlockRemanenceFactory();
	}

	/**
	 * Heal or refill the FoodBar of the selected player.
	 * 
	 * @param name
	 * @return
	 */
	public static boolean setPlayerHealth(CommandSender sender, CommandArgs name, String toDo) {
		final Player target = getUser(sender, name, "admincmd.player." + toDo);
		Hero hero = null;
		if (target == null)
			return false;
		if (heroes != null) {
			hero = heroes.getHeroManager().getHero(target);
		}
		if (toDo.equals("heal") && hero == null) {
			target.setHealth(20);
			target.setFireTicks(0);
		} else if (toDo.equals("heal") && hero != null) {
			hero.setHealth(hero.getMaxHealth());
			target.setFireTicks(0);
		} else if (toDo.equals("feed")) {
			target.setFoodLevel(20);
		} else {
			target.setHealth(0);
			if (logBlock != null)
				logBlock.queueKill(isPlayer(sender, false) ? (Player) sender : null, target);
		}

		return true;
	}

	private static void setTime(CommandSender sender, World w, String arg) {
		final long curtime = w.getTime();
		long newtime = curtime - (curtime % 24000);
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("type", arg);
		replace.put("world", w.getName());
		if (ACWorld.getWorld(w.getName()).getInformation(Type.TIME_FREEZED.toString()).isNull()) {
			if (arg.equalsIgnoreCase("day"))
				newtime += 0;
			else if (arg.equalsIgnoreCase("night"))
				newtime += 14000;
			else if (arg.equalsIgnoreCase("dusk"))
				newtime += 12500;
			else if (arg.equalsIgnoreCase("dawn"))
				newtime += 23000;
			else if (arg.equalsIgnoreCase("pause")) {
				final int taskId = ACPluginManager.getScheduler().scheduleAsyncRepeatingTask(
						ACHelper.getInstance().getCoreInstance(), new SetTime(w), 0, 10);
				ACWorld.getWorld(w.getName()).setInformation(Type.TIME_FREEZED.toString(), taskId);
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
			ACWorld.getWorld(w.getName()).removeInformation(Type.TIME_FREEZED.toString());
			sI18n(sender, "timeSet", replace);
		} else
			sI18n(sender, "timePaused", "world", w.getName());

		ACPluginManager.getScheduler().scheduleAsyncDelayedTask(
				ACPluginManager.getPluginInstance("Core"), new SetTime(w, newtime));
	}

	public static void sI18n(CommandSender sender, String key) {
		sI18n(sender, key, null);
	}

	public static void sI18n(CommandSender sender, String key, Map<String, String> replace) {
		final String locale = I18n(key, replace);
		if (locale != null && !locale.isEmpty())
			sender.sendMessage(locale);
	}

	public static void sI18n(CommandSender sender, String key, String alias, String toReplace) {
		final String locale = I18n(key, alias, toReplace);
		if (locale != null && !locale.isEmpty())
			sender.sendMessage(locale);
	}

	public static void sParsedLocale(Player p, String locale) {
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", p.getName());
		ACPlayer acPlayer = ACPlayer.getPlayer(p);
		final long total = acPlayer.getCurrentPlayedTime();
		final Long[] time = Utils.transformToElapsedTime(total);
		replace.put("d", time[0].toString());
		replace.put("h", time[1].toString());
		replace.put("m", time[2].toString());
		replace.put("s", time[3].toString());
		replace.put(
				"nb",
				String.valueOf(p.getServer().getOnlinePlayers().length
						- InvisibleWorker.getInstance().nbInvisibles()));
		String connected = "";
		for (final Player player : p.getServer().getOnlinePlayers())
			if (!InvisibleWorker.getInstance().hasInvisiblePowers(player.getName()))
				connected += getPrefix(player, p) + player.getName() + ", ";
		if (!connected.equals("")) {
			if (connected.endsWith(", "))
				connected = connected.substring(0, connected.lastIndexOf(","));
		}
		replace.put("connected", connected);
		final String serverTime = replaceDateAndTimeFormat();
		replace.put("time", serverTime);
		final String date = replaceDateAndTimeFormat(acPlayer,Whois.LOGIN);
		if (date == null)
			replace.put("lastlogin", I18n("noLoginInformation"));
		else
			replace.put("lastlogin", date);
		final String motd = I18n(locale, replace);
		if (motd != null)
			for (final String toSend : motd.split("//n"))
				p.sendMessage(toSend);

	}

	public static boolean timeSet(CommandSender sender, String time) {
		return timeSet(sender, time, null);
	}

	// all functions return if they handled the command
	// false then means to show the default handle
	// ! make sure the player variable IS a player!
	// set world time to a new value
	public static boolean timeSet(CommandSender sender, String time, String world) {
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
			for (final World w : sender.getServer().getWorlds())
				setTime(sender, w, time);
		}
		return true;

	}

	public static void tpP2P(CommandSender sender, String nFrom, String nTo, Type.Tp type) {
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

		if (found) {
			if ((type.equals(Type.Tp.TO) || type.equals(Type.Tp.PLAYERS))
					&& InvisibleWorker.getInstance().hasInvisiblePowers(pTo.getName())
					&& !PermissionManager.hasPerm(pFrom, "admincmd.invisible.cansee", false)) {
				replace.put("player", nTo);
				Utils.sI18n(sender, "playerNotFound", replace);
				return;
			}
			if ((type.equals(Type.Tp.HERE) || type.equals(Type.Tp.PLAYERS))
					&& (InvisibleWorker.getInstance().hasInvisiblePowers(pFrom.getName()) && !PermissionManager
							.hasPerm(pTo, "admincmd.invisible.cansee", false))) {
				replace.put("player", nFrom);
				Utils.sI18n(sender, "playerNotFound", replace);
				return;
			}
			if (PermissionManager.hasPerm(sender, "admincmd.spec.notprequest", false)) {
				ACPlayer.getPlayer(pFrom).setLastLocation(pFrom.getLocation());
				ACPluginManager.scheduleSyncTask(new TeleportTask(pFrom, pTo.getLocation()));
				replace.put("fromPlayer", pFrom.getName());
				replace.put("toPlayer", pTo.getName());
				Utils.sI18n(sender, "tp", replace);
			} else if ((type.equals(Type.Tp.TO) || type.equals(Type.Tp.PLAYERS))
					&& ACPlayer.getPlayer(pTo.getName()).hasPower(Type.TP_REQUEST)) {
				ACPlayer.getPlayer(pTo).setTpRequest(new TpRequest(pFrom, pTo));
				Utils.sI18n(pTo, "tpRequestTo", "player", pFrom.getName());
				final HashMap<String, String> replace2 = new HashMap<String, String>();
				replace2.put("player", pTo.getName());
				if (type.toString().equalsIgnoreCase("to"))
					replace2.put("tp_type", Utils.I18n("tpTO"));
				else if (type.toString().equalsIgnoreCase("players")) {
					replace2.put("tp_type", Utils.I18n("tpPLAYERSTO"));
					replace2.put("target", pTo.getName());
				} else
					replace2.put("tp_type", type.toString());
				Utils.sI18n(pFrom, "tpRequestSend", replace2);

			} else if ((type.equals(Type.Tp.HERE) || type.equals(Type.Tp.PLAYERS))
					&& ACPlayer.getPlayer(pFrom.getName()).hasPower(Type.TP_REQUEST)) {
				ACPlayer.getPlayer(pFrom).setTpRequest(new TpRequest(pFrom, pTo));
				Utils.sI18n(pFrom, "tpRequestFrom", "player", pTo.getName());
				final HashMap<String, String> replace2 = new HashMap<String, String>();
				replace2.put("player", pFrom.getName());
				if (type.toString().equalsIgnoreCase("here"))
					replace2.put("tp_type", Utils.I18n("tpHERE"));
				else if (type.toString().equalsIgnoreCase("players")) {
					replace2.put("tp_type", Utils.I18n("tpPLAYERSFROM"));
					replace2.put("target", pFrom.getName());
				} else
					replace2.put("tp_type", type.toString());
				Utils.sI18n(pTo, "tpRequestSend", replace2);

			} else {
				ACPlayer.getPlayer(pFrom).setLastLocation(pFrom.getLocation());
				ACPluginManager.scheduleSyncTask(new TeleportTask(pFrom, pTo.getLocation()));
				replace.put("fromPlayer", pFrom.getName());
				replace.put("toPlayer", pTo.getName());
				Utils.sI18n(sender, "tp", replace);
			}
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

		return new Long[] { elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds };
	}

	public static boolean weather(CommandSender sender, Type.Weather type, CommandArgs duration) {
		if (isPlayer(sender, false)) {
			if (duration.length >= 2) {
				final World w = sender.getServer().getWorld(duration.getString(1));
				if (w == null) {
					final HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("world", duration.getString(2));
					Utils.sI18n(sender, "worldNotFound", replace);
					return true;
				}
				weatherChange(sender, w, type, duration);
			} else if ((type.equals(Type.Weather.FREEZE) || type.equals(Type.Weather.CLEAR))
					&& duration.getString(0) != null) {
				final World w = sender.getServer().getWorld(duration.getString(0));
				if (w == null) {
					final HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("world", duration.getString(0));
					Utils.sI18n(sender, "worldNotFound", replace);
					return true;
				}
				weatherChange(sender, w, type, duration);
			} else
				weatherChange(sender, ((Player) sender).getWorld(), type, duration);
		} else if (duration.length >= 2) {
			final World w = sender.getServer().getWorld(duration.getString(1));
			if (w == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("world", duration.getString(0));
				Utils.sI18n(sender, "worldNotFound", replace);
				return true;
			}
			weatherChange(sender, w, type, duration);
		} else if ((type.equals(Type.Weather.FREEZE) || type.equals(Type.Weather.CLEAR))
				&& duration.getString(0) != null) {
			final World w = sender.getServer().getWorld(duration.getString(0));
			if (w == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("world", duration.getString(0));
				Utils.sI18n(sender, "worldNotFound", replace);
				return true;
			}
			weatherChange(sender, w, type, duration);
		} else
			for (final World w : sender.getServer().getWorlds())
				weatherChange(sender, w, type, duration);

		return true;
	}

	private static void weatherChange(CommandSender sender, World w, Type.Weather type,
			CommandArgs duration) {
		if (!type.equals(Type.Weather.FREEZE)
				&& !ACWorld.getWorld(w.getName()).getInformation(Type.WEATHER_FROZEN.toString())
						.isNull()) {
			sender.sendMessage(ChatColor.GOLD + Utils.I18n("wFrozen") + " " + w.getName());
			return;
		}
		switch (type) {
		case CLEAR:
			w.setThundering(false);
			w.setStorm(false);
			sender.sendMessage(ChatColor.GOLD + Utils.I18n("sClear") + " " + w.getName());
			break;
		case STORM:
			final HashMap<String, String> replace = new HashMap<String, String>();
			if (duration == null || duration.length < 1) {
				w.setStorm(true);
				w.setThundering(true);
				w.setWeatherDuration(12000);
				replace.put("duration", "10");
				sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
			} else {
				try {
					w.setStorm(true);
					w.setThundering(true);
					final int time = duration.getInt(0);
					w.setWeatherDuration(time * 1200);
					replace.put("duration", String.valueOf(time));
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
				} catch (final NumberFormatException e) {
					sender.sendMessage(ChatColor.BLUE + "Sorry, that (" + duration.getString(0)
							+ ") isn't a number!");
					w.setStorm(true);
					w.setWeatherDuration(12000);
					replace.put("duration", "10");
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
				}
			}
			break;
		case FREEZE:
			if (!ACWorld.getWorld(w.getName()).getInformation(Type.WEATHER_FROZEN.toString())
					.isNull()) {
				ACWorld.getWorld(w.getName()).removeInformation(Type.WEATHER_FROZEN.toString());
				sender.sendMessage(ChatColor.GREEN + Utils.I18n("wUnFrozen") + " "
						+ ChatColor.WHITE + w.getName());
			} else {
				ACWorld.getWorld(w.getName()).setInformation(Type.WEATHER_FROZEN.toString(), true);
				sender.sendMessage(ChatColor.RED + Utils.I18n("wFrozen") + " " + ChatColor.WHITE
						+ w.getName());
			}
			break;
		case RAIN:
			final HashMap<String, String> replaceRain = new HashMap<String, String>();
			if (duration == null || duration.length < 1) {
				w.setStorm(true);
				w.setThundering(false);
				w.setWeatherDuration(12000);
				replaceRain.put("duration", "10");
				sender.sendMessage(ChatColor.GOLD + Utils.I18n("sRain", replaceRain) + w.getName());
			} else {
				try {
					w.setStorm(true);
					w.setThundering(false);
					final int time = duration.getInt(0);
					w.setWeatherDuration(time * 1200);
					replaceRain.put("duration", String.valueOf(time));
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sRain", replaceRain)
							+ w.getName());
				} catch (final NumberFormatException e) {
					sender.sendMessage(ChatColor.BLUE + "Sorry, that (" + duration.getString(0)
							+ ") isn't a number!");
					w.setStorm(true);
					w.setWeatherDuration(12000);
					replaceRain.put("duration", "10");
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sRain", replaceRain)
							+ w.getName());
				}
			}
			break;
		default:
			break;
		}
	}
}
