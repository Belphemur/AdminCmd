/*************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package be.Balor.Manager.Commands.Tp;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Warp;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class TpAll extends TeleportCommand {

	public TpAll() {
		cmdName = "bal_tpall";
		permNode = "admincmd.tp.all";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@SuppressWarnings("unused")
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws PlayerNotFound, ActionNotPermitedException {
		final Player[] players = ACPluginManager.getServer().getOnlinePlayers();
		final HashMap<String, String> replace = new HashMap<String, String>();
		String teleporter;
		if (Utils.isPlayer(sender, false)) {
			teleporter = Utils.getPlayerName((Player) sender);
		} else {
			teleporter = "Admin";
		}
		ACWorld world;
		if (args.length == 2) {
			world = ACWorld.getWorld(args.getString(0));
		} else if (args.length >= 4) {
			world = ACWorld.getWorld(args.getString(0));
		} else {
			if (sender instanceof Player) {
				world = ACWorld
						.getWorld(((Player) sender).getWorld().getName());
			} else {
				replace.put("arg", "world");
				replace.put("cmdName", "/tpall");
				LocaleHelper.MISSING_ARG.sendLocale(sender, replace);
				return;
			}
		}
		if (world == null) {
			replace.put("world", args.getString(0));
			Utils.sI18n(sender, "worldNotFound", replace);
			return;
		}
		Location to = null;
		if (args.hasFlag('s')) {
			// TP to Spawn point
			to = world.getSpawn();
			replace.put("sender", teleporter);
			replace.put("loc", "the Spawn of " + world.getName());
			doTeleportAndMessage(players, replace, to);
		} else if (args.hasFlag('w')) {
			// TP to Warp point
			final String value = args.getValueFlag('w');
			if (value == null) {
				return;
			}
			final Warp w = world.getWarp(value);
			if (w == null) {
				replace.clear();
				replace.put("name", value);
				Utils.sI18n(sender, "errorWarp", replace);
				return;
			}
			to = w.loc;
			replace.put("sender", teleporter);
			replace.put("loc", w.name);
			doTeleportAndMessage(players, replace, to);
		} else if (args.hasFlag('p')) {
			// TP to Player
			final String value = args.getValueFlag('p');
			final ACPlayer target = ACPlayer.getPlayer(value);
			if (target == null) {
				replace.clear();
				replace.put("player", value);
				Utils.sI18n(sender, "playerNotFound", replace);
				return;
			}
			to = target.getHandler().getLocation();
			replace.put("sender", teleporter);
			replace.put("loc", Utils.getPlayerName(target.getHandler()));
			doTeleportAndMessage(players, replace, to);
		} else if (args.hasFlag('l')) {
			// TP to Location
			String x, y, z;
			if (args.length == 3) {
				x = args.getString(0);
				y = args.getString(1);
				z = args.getString(2);
			} else if (args.length > 3) {
				x = args.getString(1);
				y = args.getString(2);
				z = args.getString(3);
			} else {
				replace.clear();
				replace.put("cmdName", "/tpall");
				replace.put("arg", "Location in x,y,z coordinates");
				LocaleHelper.MISSING_ARG.sendLocale(sender, replace);
				return;
			}
			final Double locX = null, locY = null, locZ = null;
			parseNumber(sender, x, locX);
			parseNumber(sender, y, locY);
			parseNumber(sender, z, locZ);
			if (locX == null || locY == null || locZ == null) {
				return;
			}
			to = new Location(world.getHandle(), locX, locY, locZ);
			replace.put("sender", teleporter);
			replace.put("loc", "Location: x=" + x + ", y=" + y + ", z=" + z);
			doTeleportAndMessage(players, replace, to);
		} else {
			// TP to sender if he is a player
			if (Utils.isPlayer(sender, false)) {
				final Player p = (Player) sender;
				to = p.getLocation();
				replace.put("sender", teleporter);
				replace.put("loc", Utils.getPlayerName((Player) sender));
				doTeleportAndMessage(p, players, replace, to);
			} else {
				replace.put("arg", "world");
				replace.put("cmdName", "/tpall");
				LocaleHelper.MISSING_ARG.sendLocale(sender, replace);
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

	private void doTeleportAndMessage(final Player[] players,
			final HashMap<String, String> replace, final Location loc) {
		if (loc == null) {
			return;
		}
		if (players == null) {
			return;
		}
		for (final Player p : players) {
			if (p == null) {
				continue;
			}
			p.teleport(loc);
			LocaleHelper.TP_ALL.sendLocale(p, replace);
		}
	}

	private void doTeleportAndMessage(final Player sender,
			final Player[] players, final HashMap<String, String> replace,
			final Location loc) {
		if (loc == null) {
			return;
		}
		if (players == null) {
			return;
		}
		for (final Player p : players) {
			if (p == null) {
				continue;
			}
			if (p == sender) {
				continue;
			}
			p.teleport(loc);
			LocaleHelper.TP_ALL.sendLocale(p, replace);
		}
	}

	private void parseNumber(final CommandSender sender, final String toParse,
			Double parsed) {
		try {
			parsed = (double) Integer.parseInt(toParse);
		} catch (final NumberFormatException e) {
			Utils.sI18n(sender, "NaN", "number", toParse);
			return;
		}
	}
}