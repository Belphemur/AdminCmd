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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import be.Balor.Listeners.Events.ACSignChangeEvent;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.SignEditor;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACSignEditListener implements Listener {

	private final Map<Location, SignEditor> signEditor = new HashMap<Location, SignEditor>();

	@EventHandler(ignoreCancelled = true)
	public void onSignChange(final SignChangeEvent event) {
		if (event instanceof ACSignChangeEvent) {
			return;
		}
		final Location signLoc = event.getBlock().getLocation();
		final SignEditor saved = signEditor.get(signLoc);
		if (saved == null) {
			return;
		}
		final Player player = event.getPlayer();
		final ACSignChangeEvent signChange = new ACSignChangeEvent(
				saved.getUpdatedSignBlk(), player, event.getLines());
		Bukkit.getPluginManager().callEvent(signChange);
		if (signChange.isCancelled()) {
			signEditor.remove(signLoc);
			return;
		}
		saved.updateSign(signChange.getLines());
		saved.removeEditorSign(player);
		signEditor.remove(signLoc);
		event.setCancelled(true);

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void signPlace(final BlockPlaceEvent event) {
		final Block block = event.getBlockPlaced();
		if (block.getType() != Material.WALL_SIGN
				&& block.getType() != Material.SIGN_POST) {
			return;
		}
		final Block blockAgainst = event.getBlockAgainst();
		final BlockState blockAgainstState = blockAgainst.getState();
		if (!(blockAgainstState instanceof Sign)) {
			return;
		}
		if (PermissionManager.hasPerm(event.getPlayer(),
				"admincmd.spec.signedit")) {
			signEditor.put(block.getLocation(), new SignEditor(block,
					blockAgainst));
		}

	}
}
