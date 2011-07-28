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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.PermissionManager;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.AdminCmd;


/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Utils {
	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 */
	public static MaterialContainer checkMaterial(String mat) {
		MaterialContainer mc = new MaterialContainer();
		String[] info = new String[2];
		if (mat.contains(":"))
			info = mat.split(":");
		else {
			info[0] = mat;
			info[1] = "0";
		}
		if ((mc = ACHelper.getInstance().getAlias(info[0])) == null) {
			mc = new MaterialContainer(info[0], info[1]);
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
			String result = null;
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
			return null;
		}
	}
	public static String colorParser(String toParse) {
		return colorParser(toParse, "&");
	}

	public static long getDistanceSquared(Player player1, Player player2) {
		if (!player1.getWorld().getName().equals(player2.getWorld().getName()))
			return Long.MAX_VALUE;
		Location loc1 = player1.getLocation();
		Location loc2 = player2.getLocation();
		return (loc1.getBlockX() - loc2.getBlockX()) ^ 2 + (loc1.getBlockZ() - loc2.getBlockZ())
				^ 2;
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
	 * Heal the selected player.
	 * 
	 * @param name
	 * @return
	 */
	public static boolean setPlayerHealth(CommandSender sender, String[] name, String toDo) {
		Player target = getUser(sender, name, "admincmd.player." + toDo + ".other");
		if (target == null)
			return false;
		if (toDo.equals("heal")) {
			target.setHealth(20);
			target.setFireTicks(0);
		} else
			target.setHealth(0);

		return true;
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
	public static Player getUser(CommandSender sender, String[] args, String permNode, int index,
			boolean errorMsg) {
		Player target = null;
		if (args.length >= index + 1) {
			if (PermissionManager.getInstance().hasPerm(sender, permNode + ".other"))
				target = sender.getServer().getPlayer(args[index]);
			else
				return target;
		} else if (isPlayer(sender, false))
			target = ((Player) sender);
		else if (errorMsg) {
			sender.sendMessage("You must type the player name");
			return target;
		}
		if (target == null && errorMsg) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", args[index]);
			Utils.sI18n(sender, "playerNotFound", replace);
			return target;
		}
		return target;

	}

	public static Player getUser(CommandSender sender, String[] args, String permNode) {
		return getUser(sender, args, permNode, 0, true);
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
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put(alias, toReplace);
		sI18n(sender, key, replace);
	}

	public static void sI18n(CommandSender sender, String key) {
		sI18n(sender, key, null);
	}

	public static String I18n(String key) {
		return I18n(key, null);
	}

	public static String I18n(String key, String alias, String toReplace) {
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put(alias, toReplace);
		return I18n(key, replace);
	}

	public static String I18n(String key, Map<String, String> replace) {
		return LocaleManager.getInstance().get(key, replace);
	}

	public static void addLocale(String key, String value) {
		LocaleManager.getInstance().addLocale(key, value);
	}

	private static void setTime(World w, String arg) {
		long curtime = w.getTime();
		long newtime = curtime - (curtime % 24000);
		if (arg.equalsIgnoreCase("day"))
			newtime += 0;
		else if (arg.equalsIgnoreCase("night"))
			newtime += 14000;
		else if (arg.equalsIgnoreCase("dusk"))
			newtime += 12500;
		else if (arg.equalsIgnoreCase("dawn"))
			newtime += 23000;
		else
			// if not a constant, use raw time
			try {
				newtime += Integer.parseInt(arg);
			} catch (Exception e) {
			}
		w.setTime(newtime);
	}

	// all functions return if they handled the command
	// false then means to show the default handle
	// ! make sure the player variable IS a player!
	// set world time to a new value
	public static boolean timeSet(CommandSender sender, String arg) {
		if (isPlayer(sender, false)) {
			Player p = (Player) sender;
			setTime(p.getWorld(), arg);
		} else {
			for (World w : sender.getServer().getWorlds())
				setTime(w, arg);
		}
		return true;

	}

	public static boolean tpP2P(CommandSender sender, String nFrom, String nTo) {
		boolean found = true;
		Player pFrom = AdminCmd.getBukkitServer().getPlayer(nFrom);
		Player pTo = AdminCmd.getBukkitServer().getPlayer(nTo);
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
			pFrom.teleport(pTo);
			replace.put("fromPlayer", pFrom.getName());
			replace.put("toPlayer", pTo.getName());
			Utils.sI18n(sender, "tp", replace);
		}
		return true;
	}

	private static void weatherChange(CommandSender sender, World w, String type, String[] duration) {
		if (type == "clear") {
			w.setThundering(false);
			w.setStorm(false);
			sender.sendMessage(ChatColor.GOLD + Utils.I18n("sClear") + " " + w.getName());
		} else {
			HashMap<String, String> replace = new HashMap<String, String>();
			if (duration == null || duration.length < 1) {
				w.setStorm(true);
				w.setWeatherDuration(12000);
				replace.put("duration", "10");
				sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
			} else {
				try {
					w.setStorm(true);
					int time = Integer.parseInt(duration[0]);
					w.setWeatherDuration(time * 1200);
					replace.put("duration", duration[0]);
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.BLUE + "Sorry, that (" + duration[0]
							+ ") isn't a number!");
					w.setStorm(true);
					w.setWeatherDuration(12000);
					replace.put("duration", "10");
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
				}
			}
		}
	}

	public static boolean weather(CommandSender sender, String type, String[] duration) {
		if (isPlayer(sender, false)) {
			weatherChange(sender, ((Player) sender).getWorld(), type, duration);
		} else
			for (World w : sender.getServer().getWorlds())
				weatherChange(sender, w, type, duration);

		return true;
	}
}
