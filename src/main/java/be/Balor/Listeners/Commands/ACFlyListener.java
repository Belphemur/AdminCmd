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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACFlyListener implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(final PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		if (!ACPlayer.getPlayer(p).hasPower(Type.FLY))
			return;
		p.setAllowFlight(true);
		p.setFlying(true);

	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		final Player player = (Player) event.getEntity();
		if (ACPlayer.getPlayer(player).hasPower(Type.FLY)
				&& event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			event.setCancelled(true);
			event.setDamage(0);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerKick(final PlayerKickEvent event) {
		final Player p = event.getPlayer();
		if ((event.getReason().toLowerCase().contains("flying") || event.getReason().toLowerCase()
				.contains("floating"))
				&& PermissionManager.hasPerm(p, "admincmd.player.fly.allowed"))
			event.setCancelled(true);
	}
}
