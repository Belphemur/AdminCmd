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

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

import com.google.common.base.Joiner;

/**
 * @author Antoine
 * 
 */
public class ACRemoveNoAccessPowers implements Listener {

	/**
	 * 
	 */
	public ACRemoveNoAccessPowers() {

	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		ACPluginManager.runTaskLaterAsynchronously(new Runnable() {
			@Override
			public void run() {
				final Set<Type> powersRemoved = ACPlayer.getPlayer(player).removePermissionPowers();
				LocaleHelper.REMOVED_SP.sendLocale(player, "powers", Joiner.on(", ").join(powersRemoved));
			}
		});
	}
}
