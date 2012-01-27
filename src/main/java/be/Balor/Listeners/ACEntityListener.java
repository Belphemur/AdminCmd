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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACEntityListener implements Listener {

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		final Player player = (Player) event.getEntity();
		ACPlayer.getPlayer(player).setLastLocation(player.getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getTarget() instanceof Player))
			return;
		final Player p = (Player) event.getTarget();
		if (InvisibleWorker.getInstance().hasInvisiblePowers(p.getName())
				&& PermissionManager.hasPerm(p, "admincmd.invisible.notatarget", false))
			event.setCancelled(true);
	}
}
