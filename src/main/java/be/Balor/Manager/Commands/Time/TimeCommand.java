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
package be.Balor.Manager.Commands.Time;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.Type;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.Tools.Threads.SetTimeTask;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class TimeCommand extends CoreCommand {
	/**
 * 
 */
	public TimeCommand() {
		super();
		this.permParent = plugin.getPermissionLinker().getPermParent(
				"admincmd.time.*");
	}

	/**
	 * @param string
	 * @param string2
	 */
	public TimeCommand(final String cmd, final String permNode) {
		super(cmd, permNode);
		this.permParent = plugin.getPermissionLinker().getPermParent(
				"admincmd.time.*");
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
		if (Users.isPlayer(sender, false) && world == null) {
			final Player p = (Player) sender;
			setTime(sender, p.getWorld(), time);
		} else if (world != null) {
			final World w = sender.getServer().getWorld(world);
			if (w == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("world", world);
				LocaleManager.sI18n(sender, "worldNotFound", replace);
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

	private static void setTime(final CommandSender sender, final World w,
			final String arg) {
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("type", arg);
		replace.put("world", w.getName());
		if (ACWorld.getWorld(w).getInformation(Type.TIME_FROZEN.toString())
				.isNull()) {
			if (arg.equalsIgnoreCase("pause")) {
				pauseTime(w);
			} else {
				final long newtime = calculNewTime(w, arg);
				ACPluginManager.scheduleSyncTask(new SetTimeTask(w, newtime));
			}
			LocaleManager.sI18n(sender, "timeSet", replace);
		} else if (arg.equalsIgnoreCase("unpause")) {
			unPauseTime(w);
			LocaleManager.sI18n(sender, "timeSet", replace);
		} else {
			LocaleManager.sI18n(sender, "timePaused", "world", w.getName());
		}

	}

	/**
	 * @param w
	 */
	private static void pauseTime(final World w) {
		ACWorld.getWorld(w).setInformation(Type.TIME_FROZEN.toString(),
				w.getTime());
		ACPluginManager.scheduleSyncTask(new Runnable() {
			@Override
			public void run() {
				for (final Player p : Bukkit.getOnlinePlayers()) {
					p.setPlayerTime(w.getTime(), false);
				}

			}
		});
	}

	/**
	 * @param w
	 */
	private static void unPauseTime(final World w) {
		ACWorld.getWorld(w).removeInformation(Type.TIME_FROZEN.toString());
		ACPluginManager.scheduleSyncTask(new Runnable() {

			@Override
			public void run() {
				for (final Player p : Bukkit.getOnlinePlayers()) {
					p.resetPlayerTime();
				}

			}
		});
	}

	/**
	 * Calcul the new time to set for the wanted world.
	 * 
	 * @param w
	 *            word
	 * @param arg
	 *            keyword or time to set
	 * @return the newtime
	 */
	public static long calculNewTime(final World w, final String arg) {
		final long curtime = w.getTime();
		if (arg.equalsIgnoreCase("normal")) {
			return curtime;
		}
		long newtime = curtime - (curtime % 24000);
		if (arg.equalsIgnoreCase("day")) {
			newtime += 0;
		} else if (arg.equalsIgnoreCase("night")) {
			newtime += 14000;
		} else if (arg.equalsIgnoreCase("dusk")) {
			newtime += 12500;
		} else if (arg.equalsIgnoreCase("dawn")) {
			newtime += 23000;
		} else {
			newtime = Long.parseLong(arg);

		}
		return newtime;
	}
}
