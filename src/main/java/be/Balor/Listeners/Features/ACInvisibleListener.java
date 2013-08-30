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
package be.Balor.Listeners.Features;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import be.Balor.Manager.LocaleManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Debug.DebugLog;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Antoine
 * 
 */
public class ACInvisibleListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		DebugLog.beginInfo("Player Invisibility check");
		final Player p = event.getPlayer();
		DebugLog.addInfo("Player : " + p);
		final ACPlayer player = ACPlayer.getPlayer(p);
		InvisibleWorker.getInstance().makeInvisibleToPlayer(p);
		if (player.hasPower(Type.INVISIBLE)) {
			DebugLog.addInfo("This player is invisible");
			event.setJoinMessage(null);
			LocaleManager.sI18n(p, "stillInv");
			InvisibleWorker.getInstance().vanish(p, true);
		}
		DebugLog.endInfo();
	}
}
