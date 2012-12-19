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
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import be.Balor.Player.IBan;
import be.Balor.Player.ITempBan;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACBanListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		if (!event.getResult().equals(Result.ALLOWED)) {
			return;
		}
		final Player player = event.getPlayer();
		IBan ban = ACHelper.getInstance().getBan(player.getName());
		if (ban == null) {
			try {
				ban = ACHelper.getInstance().getBan(
						event.getAddress().toString().substring(1));
			} catch (final NoSuchMethodError e) {}

		}
		if (ban != null) {
			if (ban instanceof ITempBan) {
				final ITempBan banTemp = (ITempBan) ban;
				event.disallow(Result.KICK_BANNED, banTemp.getReason()
						+ " Time left: " + banTemp.timeLeft());
			} else {
				event.disallow(Result.KICK_BANNED, ban.getReason());
			}
		}
	}
}
