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

import info.somethingodd.bukkit.OddItem.OddItem;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.D3GN.MiracleM4n.mChat.mChatAPI;
import net.minecraft.server.Packet4UpdateTime;
import net.minecraft.server.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;

import de.diddiz.LogBlock.Consumer;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class Utils {
	public static OddItem oddItem = null;
	public static Consumer logBlock = null;
	public static Heroes heroes = null;
	public static mChatAPI mChatApi = null;
	public static boolean signExtention = false;
	private final static long secondInMillis = 1000;
	private final static long minuteInMillis = secondInMillis * 60;
	private final static long hourInMillis = minuteInMillis * 60;
	private final static long dayInMillis = hourInMillis * 24;

	/**
	 * @author Balor (aka Antoine Aflalo)
	 *
	 */

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
				ItemStack is = oddItem.getItemStack(mat);
				if (is != null) {
					return new MaterialContainer(is);
				}
			}
		} catch (Exception e) {
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
			Pattern regex = Pattern.compile(delimiter + "[A-Fa-f]|" + delimiter + "1[0-5]|"
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
				result = regexMatcher.replaceFirst(ChatColor.getByCode(colorint).toString());
				regexMatcher = regex.matcher(result);
			}
			return result;
		} catch (Exception ex) {
			return toParse;
		}
	}

	public static String colorParser(String toParse) {
		return colorParser(toParse, "&");
	}

	public static double getDistanceSquared(Player player1, Player player2) {
		if (!player1.getWorld().getName().equals(player2.getWorld().getName()))
			return Double.MAX_VALUE;
		Location loc1 = player1.getLocation();
		Location loc2 = player2.getLocation();
		return Math.pow((loc1.getX() - loc2.getX()), 2) + Math.pow((loc1.getZ() - loc2.getZ()), 2);
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
				sender.sendMessage("You must be a player to use this command.");
			return false;
		}
	}

	/**
	 * Heal or refill the FoodBar of the selected player.
	 *
	 * @param name
	 * @return
	 */
	public static boolean setPlayerHealth(CommandSender sender, CommandArgs name, String toDo) {
		Player target = getUser(sender, name, "admincmd.player." + toDo);
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

	/**
	 * Get the complete player name with all prefix
	 *
	 * @param player
	 *            player to get the name
	 * @param sender
	 *            sender that want the name
	 * @return the complete player name with prefix
	 */
	public static String getPlayerName(Player player, CommandSender sender) {
		String prefix = colorParser(getPrefix(player, sender));
		if (ACHelper.getInstance().getConfBoolean("useDisplayName"))
			return prefix + player.getDisplayName();

		return prefix + player.getName();
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
			target = sender.getServer().getPlayer(args.getString(index));
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
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", args.getString(index));
			Utils.sI18n(sender, "playerNotFound", replace);
			return target;
		}
		return target;

	}

	public static Player getUser(CommandSender sender, CommandArgs args, String permNode) {
		return getUser(sender, args, permNode, 0, true);
	}

	/**
	 * Get the user and check who launched the command.
	 *
	 * @author Balor, Lathanael
	 *
	 * @param sender
	 * @param name
	 * @param permNode
	 * @param errorMsg
	 * @return
	 */
	public static Player getUser(CommandSender sender, String name, String permNode, boolean errorMsg) {
		Player target = null;
		target = sender.getServer().getPlayer(name);
		if (target != null) {
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
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", name);
			Utils.sI18n(sender, "playerNotFound", replace);
			return target;
		}
		return target;

	}

	public static void sendMessage(CommandSender sender, CommandSender player, String key) {
		sendMessage(sender, player, key, null);
	}

	public static void sendMessage(CommandSender sender, CommandSender player, String key,
			Map<String, String> replace) {
		String msg = I18n(key, replace);
		if (msg != null && !msg.isEmpty()) {
			if (!sender.equals(player))
				player.sendMessage(msg);
			sender.sendMessage(msg);
		}

	}

	public static void sI18n(CommandSender sender, String key, Map<String, String> replace) {
		String locale = I18n(key, replace);
		if (locale != null && !locale.isEmpty())
			sender.sendMessage(locale);
	}

	public static void sI18n(CommandSender sender, String key, String alias, String toReplace) {
		String locale = I18n(key, alias, toReplace);
		if (locale != null && !locale.isEmpty())
			sender.sendMessage(locale);
	}

	public static void sI18n(CommandSender sender, String key) {
		sI18n(sender, key, null);
	}

	public static String I18n(String key) {
		return I18n(key, null);
	}

	public static String I18n(String key, String alias, String toReplace) {
		return LocaleManager.getInstance().get(key, alias, toReplace);
	}

	public static String I18n(String key, Map<String, String> replace) {
		return LocaleManager.getInstance().get(key, replace);
	}

	public static void addLocale(String key, String value) {
		LocaleManager.getInstance().addLocale(key, value);
	}

	private static void setTime(CommandSender sender, World w, String arg) {
		long curtime = w.getTime();
		long newtime = curtime - (curtime % 24000);
		HashMap<String, String> replace = new HashMap<String, String>();
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
				int taskId = ACPluginManager.getScheduler().scheduleAsyncRepeatingTask(
						ACHelper.getInstance().getCoreInstance(), new SetTime(w), 0, 10);
				ACWorld.getWorld(w.getName()).setInformation(Type.TIME_FREEZED.toString(), taskId);
			} else {
				// if not a constant, use raw time
				try {
					newtime += Integer.parseInt(arg);
				} catch (Exception e) {
					sI18n(sender, "timeNotSet", replace);
					return;
				}
			}
			sI18n(sender, "timeSet", replace);
		} else if (arg.equalsIgnoreCase("unpause")) {
			int removeTask = ACWorld.getWorld(w.getName())
					.getInformation(Type.TIME_FREEZED.toString()).getInt(-1);
			ACPluginManager.getScheduler().cancelTask(removeTask);
			ACWorld.getWorld(w.getName()).removeInformation(Type.TIME_FREEZED.toString());
			sI18n(sender, "timeSet", replace);
		} else
			sI18n(sender, "timePaused", "world", w.getName());

		ACPluginManager.getScheduler().scheduleAsyncDelayedTask(
				ACPluginManager.getPluginInstance("Core"), new SetTime(w, newtime));
	}

	// all functions return if they handled the command
	// false then means to show the default handle
	// ! make sure the player variable IS a player!
	// set world time to a new value
	public static boolean timeSet(CommandSender sender, String arg) {
		if (isPlayer(sender, false)) {
			Player p = (Player) sender;
			setTime(sender, p.getWorld(), arg);
		} else {
			for (World w : sender.getServer().getWorlds())
				setTime(sender, w, arg);
		}
		return true;

	}

	public static void tpP2P(CommandSender sender, String nFrom, String nTo, Type.Tp type) {
		boolean found = true;
		Player pFrom = ACPluginManager.getServer().getPlayer(nFrom);
		Player pTo = ACPluginManager.getServer().getPlayer(nTo);
		HashMap<String, String> replace = new HashMap<String, String>();
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
				ACPlayer.getPlayer(nFrom).setLastLocation(pFrom.getLocation());
				pFrom.teleport(pTo);
				replace.put("fromPlayer", pFrom.getName());
				replace.put("toPlayer", pTo.getName());
				Utils.sI18n(sender, "tp", replace);
			} else if ((type.equals(Type.Tp.TO) || type.equals(Type.Tp.PLAYERS))
					&& ACPlayer.getPlayer(pTo.getName()).hasPower(Type.TP_REQUEST)) {
				ACPlayer.getPlayer(pTo.getName()).setPower(Type.TP_REQUEST,
						new TpRequest(pFrom, pTo));
				Utils.sI18n(pTo, "tpRequestTo", "player", pFrom.getName());
				HashMap<String, String> replace2 = new HashMap<String, String>();
				replace2.put("player", pTo.getName());
				replace2.put("tp_type", type.toString());
				Utils.sI18n(pFrom, "tpRequestSend", replace2);

			} else if ((type.equals(Type.Tp.HERE) || type.equals(Type.Tp.PLAYERS))
					&& ACPlayer.getPlayer(pFrom.getName()).hasPower(Type.TP_REQUEST)) {
				ACPlayer.getPlayer(pFrom.getName()).setPower(Type.TP_REQUEST,
						new TpRequest(pFrom, pTo));
				Utils.sI18n(pFrom, "tpRequestFrom", "player", pTo.getName());
				HashMap<String, String> replace2 = new HashMap<String, String>();
				replace2.put("player", pFrom.getName());
				replace2.put("tp_type", type.toString());
				Utils.sI18n(pTo, "tpRequestSend", replace2);

			} else {
				ACPlayer.getPlayer(nFrom).setLastLocation(pFrom.getLocation());
				pFrom.teleport(pTo);
				replace.put("fromPlayer", pFrom.getName());
				replace.put("toPlayer", pTo.getName());
				Utils.sI18n(sender, "tp", replace);
			}
		}
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
			HashMap<String, String> replace = new HashMap<String, String>();
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
					int time = duration.getInt(0);
					w.setWeatherDuration(time * 1200);
					replace.put("duration", String.valueOf(time));
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
				} catch (NumberFormatException e) {
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
			HashMap<String, String> replaceRain = new HashMap<String, String>();
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
					int time = duration.getInt(0);
					w.setWeatherDuration(time * 1200);
					replaceRain.put("duration", String.valueOf(time));
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sRain", replaceRain)
							+ w.getName());
				} catch (NumberFormatException e) {
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

	public static boolean weather(CommandSender sender, Type.Weather type, CommandArgs duration) {
		if (isPlayer(sender, false)) {
			weatherChange(sender, ((Player) sender).getWorld(), type, duration);
		} else
			for (World w : sender.getServer().getWorlds())
				weatherChange(sender, w, type, duration);

		return true;
	}

	/**
	 * Broadcast message to every user since the bukkit one is bugged
	 *
	 * @param message
	 */
	public static void broadcastMessage(String message) {
		for (Player p : getOnlinePlayers())
			p.sendMessage(message);
		// new ColouredConsoleSender((CraftServer)
		// ACPluginManager.getServer()).sendMessage(message);
	}

	public static void sParsedLocale(Player p, String locale) {
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", p.getName());
		long total = ACPlayer.getPlayer(p.getName()).getCurrentPlayedTime();
		Long[] time = Utils.transformToElapsedTime(total);
		replace.put("d", time[0].toString());
		replace.put("h", time[1].toString());
		replace.put("m", time[2].toString());
		replace.put("s", time[3].toString());
		replace.put(
				"nb",
				String.valueOf(p.getServer().getOnlinePlayers().length
						- InvisibleWorker.getInstance().nbInvisibles()));
		String connected = "";
		for (Player player : p.getServer().getOnlinePlayers())
			if (!InvisibleWorker.getInstance().hasInvisiblePowers(player.getName()))
				connected += getPrefix(player, p) + player.getName() + ", ";
		if (!connected.equals("")) {
			if (connected.endsWith(", "))
				connected = connected.substring(0, connected.lastIndexOf(","));
		}
		replace.put("connected", connected);
		String serverTime = replaceDateAndTimeFormat();
		replace.put("time", serverTime);
		String date = replaceDateAndTimeFormat(p);
		if (date == null)
			replace.put("lastlogin", I18n("noLoginInformation"));
		else
			replace.put("lastlogin", date);
		String motd = I18n(locale, replace);
		if (motd != null)
			for (String toSend : motd.split("//n"))
				p.sendMessage(toSend);

	}

	public static Integer replaceBlockByAir(CommandSender sender, CommandArgs args,
			List<Material> mat, int defaultRadius) {
		if (Utils.isPlayer(sender)) {
			int radius = defaultRadius;
			if (args.length >= 1) {
				try {
					radius = args.getInt(0);
				} catch (NumberFormatException e) {
					if (args.length >= 2)
						try {
							radius = args.getInt(1);
						} catch (NumberFormatException e2) {

						}
				}

			}
			String playername = ((Player) sender).getName();
			Stack<BlockRemanence> blocks;
			Block block = ((Player) sender).getLocation().getBlock();
			if (mat.contains(Material.LAVA) || mat.contains(Material.WATER))
				blocks = drainFluid(playername, block, radius);
			else
				blocks = replaceInCuboid(playername, mat, block, radius);
			if (!blocks.isEmpty())
				ACHelper.getInstance().addInUndoQueue(((Player) sender).getName(), blocks);
			return blocks.size();
		}
		return null;
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
		Stack<BlockRemanence> blocks = new Stack<BlockRemanence>();
		int limitX = block.getX() + radius;
		int limitY = block.getY() + radius;
		int limitZ = block.getZ() + radius;
		Block current;
		BlockRemanence br = null;
		if (logBlock == null)
			for (int y = block.getY() - radius; y <= limitY; y++) {
				for (int x = block.getX() - radius; x <= limitX; x++)
					for (int z = block.getZ() - radius; z <= limitZ; z++) {
						current = block.getWorld().getBlockAt(x, y, z);
						if (mat.contains(current.getType())) {
							br = new BlockRemanence(current.getLocation());
							blocks.push(br);
							br.setBlockType(0);
						}
					}
			}
		else
			for (int y = block.getY() - radius; y <= limitY; y++) {
				for (int x = block.getX() - radius; x <= limitX; x++)
					for (int z = block.getZ() - radius; z <= limitZ; z++) {
						current = block.getWorld().getBlockAt(x, y, z);
						if (mat.contains(current.getType())) {
							br = new BlockRemanence(current.getLocation());
							logBlock.queueBlockBreak(playername, current.getState());
							blocks.push(br);
							br.setBlockType(0);
						}
					}
			}
		return blocks;
	}

	/**
	 * Broadcast a fakeQuit message for the selected player
	 *
	 * @param player
	 *            that fake quit.
	 */
	public static void broadcastFakeQuit(Player player) {
		String name = player.getName();
		if (mChatApi != null)
			Utils.broadcastMessage(mChatApi.ParseJoinName(player) + ChatColor.YELLOW
					+ " has left the game.");
		else
			Utils.broadcastMessage(ChatColor.YELLOW + name + " left the game.");
	}

	/**
	 * Broadcast a fakeJoin message for the selected player
	 *
	 * @param player
	 *            that fake join.
	 */
	public static void broadcastFakeJoin(Player player) {
		String name = player.getName();
		if (mChatApi != null)
			Utils.broadcastMessage(mChatApi.ParseJoinName(player) + ChatColor.YELLOW
					+ " has joined the game.");
		else
			Utils.broadcastMessage(ChatColor.YELLOW + name + " joined the game.");
	}

	/**
	 * Because water and lava are fluid, using another algo to "delete"
	 *
	 * @param block
	 * @param radius
	 * @return
	 */
	private static Stack<BlockRemanence> drainFluid(String playername, Block block, int radius) {
		Stack<BlockRemanence> blocks = new Stack<BlockRemanence>();
		Stack<SimplifiedLocation> processQueue = new Stack<SimplifiedLocation>();
		BlockRemanence current = null;
		World w = block.getWorld();
		Location start = block.getLocation();
		if (logBlock == null) {
			for (int x = block.getX() - 2; x <= block.getX() + 2; x++) {
				for (int z = block.getZ() - 2; z <= block.getZ() + 2; z++) {
					for (int y = block.getY() - 2; y <= block.getY() + 2; y++) {
						SimplifiedLocation newPos = new SimplifiedLocation(w, x, y, z);
						if (isFluid(newPos) && !newPos.isVisited()) {
							newPos.setVisited();
							processQueue.push(newPos);
							current = new BlockRemanence(newPos);
							blocks.push(current);
							current.setBlockType(0);
						}

					}
				}
			}
			while (!processQueue.isEmpty()) {
				SimplifiedLocation loc = processQueue.pop();
				for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
					for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
						for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
							SimplifiedLocation newPos = new SimplifiedLocation(w, x, y, z);
							if (!newPos.isVisited() && isFluid(newPos)
									&& start.distance(newPos) < radius) {
								processQueue.push(newPos);
								current = new BlockRemanence(newPos);
								blocks.push(current);
								current.setBlockType(0);
								newPos.setVisited();
							}
						}

					}
				}
			}
		} else {
			for (int x = block.getX() - 2; x <= block.getX() + 2; x++) {
				for (int z = block.getZ() - 2; z <= block.getZ() + 2; z++) {
					for (int y = block.getY() - 2; y <= block.getY() + 2; y++) {
						SimplifiedLocation newPos = new SimplifiedLocation(w, x, y, z);
						if (isFluid(newPos) && !newPos.isVisited()) {
							newPos.setVisited();
							processQueue.push(newPos);
							current = new BlockRemanence(newPos);
							logBlock.queueBlockBreak(playername, newPos, current.getOldType(),
									current.getData());
							blocks.push(current);
							current.setBlockType(0);
						}

					}
				}
			}
			while (!processQueue.isEmpty()) {
				SimplifiedLocation loc = processQueue.pop();
				for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
					for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
						for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
							SimplifiedLocation newPos = new SimplifiedLocation(w, x, y, z);
							if (!newPos.isVisited() && isFluid(newPos)
									&& start.distance(newPos) < radius) {
								processQueue.push(newPos);
								current = new BlockRemanence(newPos);
								logBlock.queueBlockBreak(playername, newPos, current.getOldType(),
										current.getData());
								blocks.push(current);
								current.setBlockType(0);
								newPos.setVisited();
							}
						}

					}
				}
			}
		}

		return blocks;
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
	 * Transform a given time to an elapsed time.
	 *
	 * @param time
	 *            in milisec
	 * @return Long[] containing days, hours, mins and sec.
	 */
	public static Long[] transformToElapsedTime(final long time) {
		long diff = time;

		long elapsedDays = diff / dayInMillis;
		diff = diff % dayInMillis;
		long elapsedHours = diff / hourInMillis;
		diff = diff % hourInMillis;
		long elapsedMinutes = diff / minuteInMillis;
		diff = diff % minuteInMillis;
		long elapsedSeconds = diff / secondInMillis;

		return new Long[] { elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds };
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
		String format = ACHelper.getInstance().getConfString("DateAndTime.Format");
		SimpleDateFormat formater = new SimpleDateFormat(format);
		Date serverTime = getServerRealTime("GMT"
				+ ACHelper.getInstance().getConfString("DateAndTime.GMToffSet"));
		timeFormatted = formater.format(serverTime);
		return timeFormatted;
	}

	public static String replaceDateAndTimeFormat(String player) {
		String format = ACHelper.getInstance().getConfString("DateAndTime.Format");
		SimpleDateFormat formater = new SimpleDateFormat(format);
		String lastlogin = "";
		lastlogin = formater.format(new Date(ACPlayer.getPlayer(player)
				.getInformation("lastConnection").getLong(1)));
		if (lastlogin == formater.format(new Date(1)))
			return null;
		return lastlogin;
	}

	public static String replaceDateAndTimeFormat(Player player) {
		return replaceDateAndTimeFormat(player.getName());
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
		TimeZone tz = TimeZone.getTimeZone(gmt);
		Calendar cal = GregorianCalendar.getInstance(tz);
		cal.setTime(new Date());
		serverTime = cal.getTime();
		return serverTime;
	}

	/**
	 * Check if the block is a fluid.
	 *
	 * @param loc
	 * @return
	 */
	private static boolean isFluid(Location loc) {
		Block b = loc.getWorld().getBlockAt(loc);
		if (b == null)
			return false;
		return b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER
				|| b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA;
	}

	/**
	 * Shortcut to online players.
	 *
	 * @return
	 */
	public static List<Player> getOnlinePlayers() {
		return PlayerManager.getInstance().getOnlinePlayers();
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] Arrays_copyOfRange(T[] original, int start, int end) {
		if (original.length >= start && 0 <= start) {
			if (start <= end) {
				int length = end - start;
				int copyLength = Math.min(length, original.length - start);
				T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType(), length);

				System.arraycopy(original, start, copy, 0, copyLength);
				return copy;
			}
			throw new IllegalArgumentException();
		}
		throw new ArrayIndexOutOfBoundsException();
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
		Home result = new Home();
		if (toParse != null && toParse.contains(":")) {
			try {
				String[] split = toParse.split(":");
				result.player = split[0];
				result.home = split[1];
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			if (isPlayer(sender, false)) {
				Player p = (Player) sender;
				if (!p.getName().equals(result.player)
						&& !PermissionManager.hasPerm(p, "admincmd.admin.home"))
					return null;
			}
			return result;
		}
		if (!isPlayer(sender))
			return null;
		Player p = ((Player) sender);
		result.player = p.getName();
		if (toParse != null)
			result.home = toParse;
		else
			result.home = p.getWorld().getName();

		return result;
	}

	/**
	 * Get the prefix of the player, by checking the right the sender have
	 *
	 * @param player
	 * @return
	 */
	private static String getPrefix(Player player, CommandSender sender) {
		boolean isInv = false;
		String prefixstring;
		String statusPrefix = "";
		if (sender != null)
			isInv = InvisibleWorker.getInstance().hasInvisiblePowers(player.getName())
					&& PermissionManager.hasPerm(sender, "admincmd.invisible.cansee", false);
		if (isInv)
			statusPrefix = Utils.I18n("invTitle");
		if (AFKWorker.getInstance().isAfk(player))
			statusPrefix = Utils.I18n("afkTitle") + statusPrefix;
		if (mChatApi != null)
			prefixstring = mChatApi.getPrefix(player);
		else
			prefixstring = PermissionManager.getPrefix(player);
		String result = statusPrefix;
		if (prefixstring != null && prefixstring.length() > 1)
			result += prefixstring;
		return colorParser(result);

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
		Player player = (Player) sender;
		int pLvl = ACHelper.getInstance().getLimit(player, "immunityLvl", "defaultImmunityLvl");
		int tLvl = ACHelper.getInstance().getLimit(target, "immunityLvl", "defaultImmunityLvl");
		if (PermissionManager.hasPerm(player, "admincmd.immunityLvl.samelvl", false)
				&& pLvl != tLvl)
			return false;
		if (pLvl >= tLvl)
			return true;
		else
			return false;
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
		Player target = sender.getServer().getPlayer(args.getString(index));
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
			Player player = (Player) sender;
			int pLvl = ACHelper.getInstance().getLimit(player, "immunityLvl", "defaultImmunityLvl");
			int tLvl = ACPlayer.getPlayer(args.getString(index)).getInformation("immunityLvl")
					.getInt(0);
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

	public static class SetTime implements Runnable {
		private World w;
		private Long time;

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
			long newTime = w.getFullTime() + margin;
			WorldServer world = ((CraftWorld) w).getHandle();
			world.setTime(newTime);
			for (Player p : getOnlinePlayers()) {
				CraftPlayer cp = (CraftPlayer) p;
				cp.getHandle().netServerHandler.sendPacket(new Packet4UpdateTime(cp.getHandle()
						.getPlayerTime()));
			}

		}

	}
}
