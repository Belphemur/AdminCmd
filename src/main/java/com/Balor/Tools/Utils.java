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
package com.Balor.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.PermissionManager;

import com.Balor.bukkit.AdminCmd.ACHelper;

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
	 * @param toParse
	 * @return
	 */
	public static String colorParser(String toParse, String delimiter) {
		String ResultString = null;
		try {
			Pattern regex = Pattern.compile(delimiter + "[0-9]+");
			Matcher regexMatcher = regex.matcher(toParse);
			String result = null;
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group();
				result = regexMatcher.replaceFirst(ChatColor.getByCode(
						Integer.parseInt(ResultString.substring(1))).toString());
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
	public static boolean setPlayerHealth(CommandSender sender,String[] name, String toDo) {
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
	public static Player getUser(CommandSender sender, String[] args, String permNode)
	{
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
}
