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
package be.Balor.OpenInv;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.EntityPlayer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class InventoryManager {
	public static final InventoryManager INSTANCE = new InventoryManager();
	private final Map<Player, ACPlayerInventory> replacedInv = new HashMap<Player, ACPlayerInventory>();

	/**
 * 
 */
	private InventoryManager() {
	}

	public void onQuit(final Player p) {
		replacedInv.remove(p);
	}

	public void openInv(final Player sender, final Player target) {

		final ACPlayerInventory inventory = getInventory(target);
		final EntityPlayer eh = ((CraftPlayer) sender).getHandle();
		eh.openContainer(inventory);
	}

	private ACPlayerInventory getInventory(final Player player) {
		ACPlayerInventory inventory = replacedInv.get(player);
		if (inventory == null) {
			inventory = new ACPlayerInventory(((CraftPlayer) player).getHandle());
		}
		return inventory;

	}

}
