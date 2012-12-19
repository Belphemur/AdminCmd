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
package be.Balor.Listeners.Commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACFlyListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(final PlayerJoinEvent event) {
		setFly(event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onOldFly(final PlayerMoveEvent event) {
		final Player p = event.getPlayer();
		final ACPlayer player = ACPlayer.getPlayer(p);
		if (!player.hasPower(Type.FLY_OLD)) {
			return;
		}
		final Float power = player.getPower(Type.FLY_OLD).getFloat(0);
		if (power != 0) {
			if (p.isSneaking()) {
				p.setVelocity(p.getLocation().getDirection().multiply(power));
			} else if (ConfigEnum.GLIDE.getBoolean()) {
				final Vector vel = p.getVelocity();
				vel.add(p.getLocation().getDirection()
						.multiply(ConfigEnum.G_MULT.getFloat()).setY(0));
				if (vel.getY() < ConfigEnum.G_VELCHECK.getFloat()) {
					vel.setY(ConfigEnum.G_NEWYVEL.getFloat());
					p.setVelocity(vel);
				}
			}
		}
	}

	@EventHandler
	public void onRespawn(final PlayerRespawnEvent event) {
		final Player p = event.getPlayer();
		if (!ACPlayer.getPlayer(p).hasPower(Type.FLY)) {
			return;
		}
		// Have to set a task to reset the power, since after the event bukkit
		// is resetting the "abilities",etc ... of the player
		ACPluginManager.scheduleSyncTask(new Runnable() {
			@Override
			public void run() {
				p.setAllowFlight(true);
				p.setFlying(true);
			}
		});

	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		final Player player = (Player) event.getEntity();
		if ((ACPlayer.getPlayer(player).hasPower(Type.FLY) || ACPlayer
				.getPlayer(player).hasPower(Type.FLY_OLD))
				&& event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			event.setCancelled(true);
			event.setDamage(0);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerKick(final PlayerKickEvent event) {
		final Player p = event.getPlayer();
		if ((event.getReason().toLowerCase().contains("flying") || event
				.getReason().toLowerCase().contains("floating"))
				&& PermissionManager.hasPerm(p, "admincmd.player.fly.allowed")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
		setFly(event);
	}

	private void setFly(final PlayerEvent event) {
		final Player bPlayer = event.getPlayer();
		final ACPlayer player = ACPlayer.getPlayer(bPlayer);
		if (!player.hasPower(Type.FLY)) {
			return;
		}
		bPlayer.setAllowFlight(true);
		bPlayer.setFlying(true);

	}
}
