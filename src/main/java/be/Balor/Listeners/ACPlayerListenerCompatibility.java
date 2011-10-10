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

import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPlayerListenerCompatibility extends ACPlayerListener {
	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		super.onPlayerTeleport(event);
		Location from = event.getFrom();
		Location to = event.getTo();
		if (to.getWorld().equals(from.getWorld()))
			return;
		ACPlayer player = ACPlayer.getPlayer(event.getPlayer());
		if (ACHelper.getInstance().getConfBoolean("resetPowerWhenTpAnotherWorld")
				&& !PermissionManager
						.hasPerm(player.getHandler(), "admincmd.player.noreset", false)) {
			player.removeAllSuperPower();
			if (InvisibleWorker.getInstance().hasInvisiblePowers(player.getName())) {
				InvisibleWorker.getInstance().reappear(event.getPlayer());
			}
			Utils.sI18n(event.getPlayer(), "changedWorld");
		}
	}
}
