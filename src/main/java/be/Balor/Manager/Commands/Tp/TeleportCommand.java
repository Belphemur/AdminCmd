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
package be.Balor.Manager.Commands.Tp;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import be.Balor.Listeners.Events.ACTeleportEvent;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.TpRequest;
import be.Balor.Tools.Type;
import be.Balor.Tools.Type.Tp;
import be.Balor.Tools.CommandUtils.Immunity;
import be.Balor.Tools.Compatibility.ACMinecraftReflection;
import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.Tools.Compatibility.Reflect.MethodHandler;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Threads.TeleportTask;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class TeleportCommand extends CoreCommand {
	/**
 *
 */
	public TeleportCommand() {
		super();
		this.permParent = plugin.getPermissionLinker().getPermParent(
				"admincmd.tp.*");
	}

	/**
	 * @param string
	 * @param string2
	 */
	public TeleportCommand(final String cmd, final String permNode) {
		super(cmd, permNode);
		this.permParent = plugin.getPermissionLinker().getPermParent(
				"admincmd.tp.*");
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
			LocaleManager.sI18n(sender, "playerNotFound", replace);
			found = false;
		}
		if (pTo == null) {
			replace.put("player", nTo);
			LocaleManager.sI18n(sender, "playerNotFound", replace);
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
			LocaleManager.sI18n(sender, "diffWorld", replace);
			return;
		}
		if (type.equals(Type.Tp.TO) && !Immunity.checkImmunity(pFrom, pTo)) {
			LocaleManager.sI18n(sender, "insufficientLvl");
			return;
		}
		if (type.equals(Type.Tp.HERE) && !Immunity.checkImmunity(pTo, pFrom)) {
			LocaleManager.sI18n(sender, "insufficientLvl");
			return;
		}
		if (type.equals(Type.Tp.PLAYERS)
				&& !Immunity.checkImmunity(sender, pFrom)
				&& !Immunity.checkImmunity(sender, pTo)) {
			LocaleManager.sI18n(sender, "insufficientLvl");
			return;
		}
		if ((type.equals(Type.Tp.TO) || type.equals(Type.Tp.PLAYERS))
				&& InvisibleWorker.getInstance().hasInvisiblePowers(pTo)
				&& !PermissionManager.hasPerm(pFrom,
						"admincmd.invisible.cansee", false)) {
			replace.put("player", nTo);
			LocaleManager.sI18n(sender, "playerNotFound", replace);
			return;
		}
		if ((type.equals(Type.Tp.HERE) || type.equals(Type.Tp.PLAYERS))
				&& (InvisibleWorker.getInstance().hasInvisiblePowers(pFrom) && !PermissionManager
						.hasPerm(pTo, "admincmd.invisible.cansee", false))) {
			replace.put("player", nFrom);
			LocaleManager.sI18n(sender, "playerNotFound", replace);
			return;
		}
		if (PermissionManager.hasPerm(sender, "admincmd.spec.notprequest",
				false)) {
			ACPluginManager.scheduleSyncTask(new TeleportTask(pFrom, pTo
					.getLocation()));
			replace.put("fromPlayer", pFrom.getName());
			replace.put("toPlayer", pTo.getName());
			LocaleManager.sI18n(sender, "tp", replace);
		} else if ((type.equals(Type.Tp.TO) || type.equals(Type.Tp.PLAYERS))
				&& ACPlayer.getPlayer(pTo.getName()).hasPower(Type.TP_REQUEST)) {
			ACPlayer.getPlayer(pTo).setTpRequest(new TpRequest(pFrom, pTo));
			LocaleManager.sI18n(pTo, "tpRequestTo", "player", pFrom.getName());
			final HashMap<String, String> replace2 = new HashMap<String, String>();
			replace2.put("player", pTo.getName());
			if (type.toString().equalsIgnoreCase("to")) {
				replace2.put("tp_type", LocaleManager.I18n("tpTO"));
			} else if (type.toString().equalsIgnoreCase("players")) {
				replace2.put("tp_type", LocaleManager.I18n("tpPLAYERSTO"));
				replace2.put("target", pTo.getName());
			} else {
				replace2.put("tp_type", type.toString());
			}
			LocaleManager.sI18n(pFrom, "tpRequestSend", replace2);
	
		} else if ((type.equals(Type.Tp.HERE) || type.equals(Type.Tp.PLAYERS))
				&& ACPlayer.getPlayer(pFrom.getName())
						.hasPower(Type.TP_REQUEST)) {
			ACPlayer.getPlayer(pFrom).setTpRequest(new TpRequest(pFrom, pTo));
			LocaleManager.sI18n(pFrom, "tpRequestFrom", "player", pTo.getName());
			final HashMap<String, String> replace2 = new HashMap<String, String>();
			replace2.put("player", pFrom.getName());
			if (type.toString().equalsIgnoreCase("here")) {
				replace2.put("tp_type", LocaleManager.I18n("tpHERE"));
			} else if (type.toString().equalsIgnoreCase("players")) {
				replace2.put("tp_type", LocaleManager.I18n("tpPLAYERSFROM"));
				replace2.put("target", pFrom.getName());
			} else {
				replace2.put("tp_type", type.toString());
			}
			LocaleManager.sI18n(pTo, "tpRequestSend", replace2);
	
		} else {
			ACPluginManager.scheduleSyncTask(new TeleportTask(pFrom, pTo
					.getLocation()));
			replace.put("fromPlayer", pFrom.getName());
			replace.put("toPlayer", pTo.getName());
			LocaleManager.sI18n(sender, "tp", replace);
	
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
		DebugLog.beginInfo("Teleport player (" + player.getName() + ")");
		DebugLog.addInfo("[TO] " + loc);
		try {
			final PlayerTeleportEvent event = new ACTeleportEvent(player,
					player.getLocation(), loc, TeleportCause.PLUGIN);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return;
			}
			final Location toLocation = event.getTo();
			final int x = toLocation.getBlockX() >> 4;
			final int z = toLocation.getBlockZ() >> 4;
			if (!toLocation.getWorld().isChunkLoaded(x, z)) {
				toLocation.getWorld().loadChunk(x, z);
			}
			final Location playerLoc = player.getLocation();
			ACPluginManager.runTaskLaterAsynchronously(new Runnable() {
	
				@Override
				public void run() {
	
					ACPlayer.getPlayer(player).setLastLocation(playerLoc);
	
				}
			});
	
			TeleportCommand.teleport(player, toLocation);
		} finally {
			DebugLog.endInfo();
		}
	
	}

	public static void teleport(final Player player, final Location toLocation) {
		try {
			final Object server = ACMinecraftReflection.getHandle(player
					.getServer());
			final Object entityPlayer = ACMinecraftReflection.getHandle(player);
			final Object toWorld = ACMinecraftReflection.getHandle(toLocation
					.getWorld());
	
			// Check if the fromWorld and toWorld are the same.
			if (player.getWorld().equals(toLocation.getWorld())) {
				ACMinecraftReflection.teleportPlayer(player, toLocation);
			} else {
	
				final Object activeContainer = FieldUtils.getField(
						entityPlayer, "activeContainer");
				final Object defaultContainer = FieldUtils.getField(
						entityPlayer, "defaultContainer");
	
				// Close any foreign inventory
				if (activeContainer != defaultContainer) {
					final MethodHandler closeInventory = new MethodHandler(
							entityPlayer.getClass(), "closeInventory");
					closeInventory.invoke(entityPlayer);
				}
	
				final int dimension = FieldUtils.getField(toWorld, "dimension");
				try {
					final MethodHandler moveToWorld = new MethodHandler(
							server.getClass(), "moveToWorld",
							entityPlayer.getClass(), int.class, boolean.class,
							toLocation.getClass());
					moveToWorld.invoke(server, entityPlayer, dimension, true,
							toLocation);
				} catch (final RuntimeException e2) {
					final MethodHandler moveToWorld = new MethodHandler(
							server.getClass(), "moveToWorld",
							entityPlayer.getClass(), int.class, boolean.class,
							toLocation.getClass(), boolean.class);
					moveToWorld.invoke(server, entityPlayer, dimension, true,
							toLocation, true);
				}
			}
		} catch (final Throwable e) {
			DebugLog.INSTANCE.log(
					Level.SEVERE,
					"Problem when trying to teleport" + player + " ("
							+ player.getClass() + ")", e);
			player.teleport(toLocation);
		}
	
	}
}