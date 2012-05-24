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
package be.Balor.Listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import be.Balor.Manager.CommandManager;
import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Manager.Permissions.Plugins.SuperPermissions;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.TextLocale;
import belgium.Balor.Workers.AFKWorker;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPlayerListener implements Listener {

	@EventHandler
	public void onPlayerChat(final PlayerChatEvent event) {
		final Player p = event.getPlayer();
		final ACPlayer player = ACPlayer.getPlayer(p);
		if (ConfigEnum.AUTO_AFK.getBoolean()) {
			AFKWorker.getInstance().updateTimeStamp(p);
			if (AFKWorker.getInstance().isAfk(p)) {
				AFKWorker.getInstance().setOnline(p);
			}
		}
		if (player.hasPower(Type.MUTED)) {
			event.setCancelled(true);
			Utils.sI18n(p, "muteEnabled");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
		final Player p = event.getPlayer();
		final ACPlayer player = ACPlayer.getPlayer(p);
		final String message = event.getMessage();
		if (player.hasPower(Type.MUTED_COMMAND)) {
			final String[] split = message.split("\\s+");
			if (split.length != 0) {
				if (split[0].contains("/")) {
					event.setCancelled(true);
					Utils.sI18n(p, "commandMuteEnabled");
				}
			}
		}
		if (CommandManager.getInstance().processCommandString(event.getPlayer(), message)) {
			event.setCancelled(true);
			event.setMessage("/AdminCmd : " + message);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		if (ConfigEnum.AUTO_AFK.getBoolean()) {
			AFKWorker.getInstance().updateTimeStamp(p);
			if (AFKWorker.getInstance().isAfk(p)) {
				AFKWorker.getInstance().setOnline(p);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		final ACPlayer player = PlayerManager.getInstance().setOnline(p);
		InvisibleWorker.getInstance().makeInvisibleToPlayer(p);
		player.setInformation("last-ip", p.getAddress().getAddress().toString());
		if (ConfigEnum.JQMSG.getBoolean() && !SuperPermissions.isApiSet()) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("name", Utils.getPlayerName(p, null, true));
			event.setJoinMessage(Utils.I18n("joinMessage", replace));
		}
		if (player.hasPower(Type.INVISIBLE)) {
			event.setJoinMessage(null);
			Utils.sI18n(p, "stillInv");
			InvisibleWorker.getInstance().vanish(p, true);

		}
		ACPluginManager.getScheduler().scheduleAsyncDelayedTask(ACPluginManager.getCorePlugin(),
				new Runnable() {

					@Override
					public void run() {
						if (ConfigEnum.AUTO_AFK.getBoolean()) {
							AFKWorker.getInstance().updateTimeStamp(p);
						}
						final int imLvl = ACHelper.getInstance().getLimit(p, "immunityLvl",
								"defaultImmunityLvl");
						player.setInformation("immunityLvl",
								imLvl == Integer.MAX_VALUE ? ConfigEnum.DIMMUNITY.getInt() : imLvl);
						if (player.hasPower(Type.SPYMSG)) {
							ACHelper.getInstance().addSpy(p);
						}
						final long lastConn = player.getInformation("lastConnection").getLong(0);
						player.setInformation("lastConnection", System.currentTimeMillis());
						final long modifTime = TextLocale.NEWS.getModifTime();
						if (ConfigEnum.NEWS.getBoolean()
								&& (modifTime == 0 || lastConn <= modifTime)) {
							Utils.sParsedLocale(p, "NEWS");
						}
						if (ConfigEnum.RULES.getBoolean() && !ConfigEnum.FJ_RULES.getBoolean()) {
							Utils.sParsedLocale(p, "Rules");
						}
						if (ConfigEnum.TPREQUEST.getBoolean() && !player.hasPower(Type.TP_REQUEST)
								&& PermissionManager.hasPerm(p, "admincmd.tp.toggle.allow", false)) {
							player.setPower(Type.TP_REQUEST);
						}

					}
				});

		if (player.hasPower(Type.FAKEQUIT)) {
			event.setJoinMessage(null);
			ACHelper.getInstance().addFakeQuit(p);
		}

		if (player.getInformation("firstTime").getBoolean(true)) {
			player.setInformation("firstTime", false);
			if (ConfigEnum.JQMSG.getBoolean() && !SuperPermissions.isApiSet()) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("name", Utils.getPlayerName(p, null, true));
				event.setJoinMessage(Utils.I18n("joinMessageFirstTime", replace));
			}
			if (ConfigEnum.FCSPAWN.getBoolean()) {
				ACHelper.getInstance().spawn(p);
			}
			if (!ConfigEnum.FCSPAWN.getBoolean()
					&& ConfigEnum.GSPAWN.getString().equalsIgnoreCase("group")) {
				ACHelper.getInstance().groupSpawn(p);
			}
			if (ConfigEnum.FJ_RULES.getBoolean()) {
				Utils.sParsedLocale(p, "Rules");
			}
			if (ConfigEnum.MOTD.getBoolean()) {
				Utils.sParsedLocale(p, "MOTDNewUser");
			}
		} else if (ConfigEnum.MOTD.getBoolean()) {
			Utils.sParsedLocale(p, "MOTD");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		if (event.getResult().equals(Result.ALLOWED)) {
			return;
		}
		if (PermissionManager.hasPerm(event.getPlayer(), "admincmd.player.bypass", false)
				&& event.getResult() == Result.KICK_FULL) {
			event.allow();
		}
	}

	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Player p = event.getPlayer();
		if (ConfigEnum.AUTO_AFK.getBoolean()) {
			AFKWorker.getInstance().updateTimeStamp(p);
			if (AFKWorker.getInstance().isAfk(p)) {
				AFKWorker.getInstance().setOnline(p);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		final ACPlayer player = ACPlayer.getPlayer(event.getPlayer());
		if (player.hasPower(Type.NO_PICKUP)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerKick(final PlayerKickEvent event) {
		final Player p = event.getPlayer();
		final ACPlayer player = ACPlayer.getPlayer(p);
		if (player != null && player.hasPower(Type.KICKED)) {
			event.setLeaveMessage(null);
			player.removePower(Type.KICKED);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final Player p = event.getPlayer();
		final ACPlayer player = ACPlayer.getPlayer(p);
		player.setInformation("lastDisconnect", System.currentTimeMillis());
		ACPluginManager.getScheduler().scheduleAsyncDelayedTask(ACPluginManager.getCorePlugin(),
				new Runnable() {

					@Override
					public void run() {
						final int imLvl = ACHelper.getInstance().getLimit(p, "immunityLvl",
								"defaultImmunityLvl");
						player.setInformation("immunityLvl",
								imLvl == Integer.MAX_VALUE ? ConfigEnum.DIMMUNITY.getInt() : imLvl);

					}
				});
		if (ConfigEnum.JQMSG.getBoolean() && !SuperPermissions.isApiSet()) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("name", Utils.getPlayerName(p, null, true));
			event.setQuitMessage(Utils.I18n("quitMessage", replace));
		}
		if (player.hasPower(Type.FAKEQUIT)) {
			event.setQuitMessage(null);
		} else if (InvisibleWorker.getInstance().hasInvisiblePowers(p)) {
			event.setQuitMessage(null);
		}
		PlayerManager.getInstance().setOffline(player);
		ACHelper.getInstance().removeDisconnectedPlayer(p);
	}

	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		final String spawn = ConfigEnum.GSPAWN.getString();
		Location loc = null;
		final String worldName = player.getWorld().getName();
		if (spawn.isEmpty() || spawn.equalsIgnoreCase("globalspawn")) {
			loc = ACWorld.getWorld(worldName).getSpawn();
			event.setRespawnLocation(loc);
		} else if (spawn.equalsIgnoreCase("home")) {
			loc = ACPlayer.getPlayer(player).getHome(worldName);
			if (loc == null) {
				loc = ACWorld.getWorld(worldName).getSpawn();
			}
			event.setRespawnLocation(loc);
		} else if (spawn.equalsIgnoreCase("bed")) {
			try {
				loc = player.getBedSpawnLocation();
				if (loc == null) {
					loc = ACWorld.getWorld(worldName).getSpawn();
				}
			} catch (final NullPointerException e) {
				loc = ACWorld.getWorld(worldName).getSpawn();
			}

			event.setRespawnLocation(loc);
		} else if (spawn.equalsIgnoreCase("group")) {
			final List<String> groups = ACHelper.getInstance().getGroupList();
			if (!groups.isEmpty()) {
				for (final String groupName : groups) {
					try {
						if (PermissionManager.isInGroup(groupName, player)) {
							loc = ACWorld.getWorld(worldName).getWarp(
									"spawn" + groupName.toLowerCase()).loc;
						}
						break;
					} catch (final NoPermissionsPlugin e) {
						loc = ACWorld.getWorld(worldName).getSpawn();
						break;
					}
				}
			}
			if (loc == null) {
				loc = ACWorld.getWorld(worldName).getSpawn();
			}
			event.setRespawnLocation(loc);
		} /*
		 * else { loc = ACWorld.getWorld(worldName).getSpawn();
		 * event.setRespawnLocation(loc); }
		 */

	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		final ACPlayer player = ACPlayer.getPlayer(event.getPlayer());
		if (player.hasPower(Type.FROZEN)) {
			event.setCancelled(true);
			return;
		}
	}

}
