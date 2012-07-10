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
 * **************************************************************************/

package be.Balor.Listeners.Features;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import be.Balor.Player.ACPlayer;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class ACPowerOffListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		ACPlayer p = ACPlayer.getPlayer(event.getPlayer());
		if (p == null)
			return;
		p.removeAllSuperPower();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerKick(final PlayerKickEvent event) {
		ACPlayer p = ACPlayer.getPlayer(event.getPlayer());
		if (p == null)
			return;
		p.removeAllSuperPower();
	}
}