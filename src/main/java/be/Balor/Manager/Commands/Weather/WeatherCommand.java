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
package be.Balor.Manager.Commands.Weather;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.Type;
import be.Balor.Tools.Type.Weather;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class WeatherCommand extends CoreCommand {
	/**
 * 
 */
	public WeatherCommand() {
		super();
		this.permParent = plugin.getPermissionLinker().getPermParent(
				"admincmd.weather.*");
	}

	public static boolean weather(final CommandSender sender,
			final Type.Weather type, final CommandArgs duration) {
		if (Users.isPlayer(sender, false)) {
			if (duration.length >= 2) {
				final World w = sender.getServer().getWorld(
						duration.getString(1));
				if (w == null) {
					final HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("world", duration.getString(2));
					LocaleManager.sI18n(sender, "worldNotFound", replace);
					return true;
				}
				WeatherCommand.weatherChange(sender, w, type, duration);
			} else if ((type.equals(Type.Weather.FREEZE) || type
					.equals(Type.Weather.CLEAR))
					&& duration.getString(0) != null) {
				final World w = sender.getServer().getWorld(
						duration.getString(0));
				if (w == null) {
					final HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("world", duration.getString(0));
					LocaleManager.sI18n(sender, "worldNotFound", replace);
					return true;
				}
				WeatherCommand.weatherChange(sender, w, type, duration);
			} else {
				WeatherCommand.weatherChange(sender, ((Player) sender).getWorld(), type,
						duration);
			}
		} else if (duration.length >= 2) {
			final World w = sender.getServer().getWorld(duration.getString(1));
			if (w == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("world", duration.getString(0));
				LocaleManager.sI18n(sender, "worldNotFound", replace);
				return true;
			}
			WeatherCommand.weatherChange(sender, w, type, duration);
		} else if ((type.equals(Type.Weather.FREEZE) || type
				.equals(Type.Weather.CLEAR)) && duration.getString(0) != null) {
			final World w = sender.getServer().getWorld(duration.getString(0));
			if (w == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("world", duration.getString(0));
				LocaleManager.sI18n(sender, "worldNotFound", replace);
				return true;
			}
			WeatherCommand.weatherChange(sender, w, type, duration);
		} else {
			for (final World w : sender.getServer().getWorlds()) {
				WeatherCommand.weatherChange(sender, w, type, duration);
			}
		}
	
		return true;
	}

	public static void weatherChange(final CommandSender sender,
			final World w, final Type.Weather type, final CommandArgs duration) {
		if (!type.equals(Type.Weather.FREEZE)
				&& !ACWorld.getWorld(w.getName())
						.getInformation(Type.WEATHER_FROZEN.toString())
						.isNull()) {
			sender.sendMessage(ChatColor.GOLD + LocaleManager.I18n("wFrozen") + " "
					+ w.getName());
			return;
		}
		switch (type) {
		case CLEAR:
			w.setThundering(false);
			w.setStorm(false);
			sender.sendMessage(ChatColor.GOLD + LocaleManager.I18n("sClear") + " "
					+ w.getName());
			break;
		case STORM:
			final HashMap<String, String> replace = new HashMap<String, String>();
			if (duration == null || duration.length < 1) {
				w.setStorm(true);
				w.setThundering(true);
				w.setWeatherDuration(12000);
				replace.put("duration", "10");
				sender.sendMessage(ChatColor.GOLD
						+ LocaleManager.I18n("sStorm", replace) + w.getName());
			} else {
				try {
					w.setStorm(true);
					w.setThundering(true);
					final int time = duration.getInt(0);
					w.setWeatherDuration(time * 1200);
					replace.put("duration", String.valueOf(time));
					sender.sendMessage(ChatColor.GOLD
							+ LocaleManager.I18n("sStorm", replace) + w.getName());
				} catch (final NumberFormatException e) {
					sender.sendMessage(ChatColor.BLUE + "Sorry, that ("
							+ duration.getString(0) + ") isn't a number!");
					w.setStorm(true);
					w.setWeatherDuration(12000);
					replace.put("duration", "10");
					sender.sendMessage(ChatColor.GOLD
							+ LocaleManager.I18n("sStorm", replace) + w.getName());
				}
			}
			break;
		case FREEZE:
			if (!ACWorld.getWorld(w.getName())
					.getInformation(Type.WEATHER_FROZEN.toString()).isNull()) {
				ACWorld.getWorld(w.getName()).removeInformation(
						Type.WEATHER_FROZEN.toString());
				sender.sendMessage(ChatColor.GREEN + LocaleManager.I18n("wUnFrozen")
						+ " " + ChatColor.WHITE + w.getName());
			} else {
				ACWorld.getWorld(w.getName()).setInformation(
						Type.WEATHER_FROZEN.toString(), true);
				sender.sendMessage(ChatColor.RED + LocaleManager.I18n("wFrozen") + " "
						+ ChatColor.WHITE + w.getName());
			}
			break;
		case RAIN:
			final HashMap<String, String> replaceRain = new HashMap<String, String>();
			if (duration == null || duration.length < 1) {
				w.setStorm(true);
				w.setThundering(false);
				w.setWeatherDuration(12000);
				replaceRain.put("duration", "10");
				sender.sendMessage(ChatColor.GOLD
						+ LocaleManager.I18n("sRain", replaceRain) + w.getName());
			} else {
				try {
					w.setStorm(true);
					w.setThundering(false);
					final int time = duration.getInt(0);
					w.setWeatherDuration(time * 1200);
					replaceRain.put("duration", String.valueOf(time));
					sender.sendMessage(ChatColor.GOLD
							+ LocaleManager.I18n("sRain", replaceRain) + w.getName());
				} catch (final NumberFormatException e) {
					sender.sendMessage(ChatColor.BLUE + "Sorry, that ("
							+ duration.getString(0) + ") isn't a number!");
					w.setStorm(true);
					w.setWeatherDuration(12000);
					replaceRain.put("duration", "10");
					sender.sendMessage(ChatColor.GOLD
							+ LocaleManager.I18n("sRain", replaceRain) + w.getName());
				}
			}
			break;
		default:
			break;
		}
	}
}
