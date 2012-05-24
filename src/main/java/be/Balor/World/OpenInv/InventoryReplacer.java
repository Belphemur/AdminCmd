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
package be.Balor.World.OpenInv;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PlayerInventory;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;

import be.Balor.Tools.ClassUtils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class InventoryReplacer {
	public static final InventoryReplacer INSTANCE = new InventoryReplacer();
	private final Set<Player> replacedInv = new HashSet<Player>();

	/**
 * 
 */
	private InventoryReplacer() {
	}

	public void onQuit(final Player p) {
		replacedInv.remove(p);
	}

	public boolean isReplaced(final Player p) {
		return replacedInv.contains(p);
	}

	public void openInv(final Player sender, final Player target) {
		if (!isReplaced(target)) {
			replaceInv(target);
		}
		final EntityPlayer eh = ((CraftPlayer) sender).getHandle();
		eh.openContainer(((CraftPlayer) target).getHandle().inventory);
	}

	private void replaceInv(final Player player) {
		final CraftPlayer craftPlayer = (CraftPlayer) player;
		final EntityPlayer mcPlayer = craftPlayer.getHandle();
		final PlayerInventory inv = new ACPlayerInventory(mcPlayer);
		inv.a(mcPlayer.inventory);
		mcPlayer.inventory = inv;
		final CraftInventoryPlayer cInv = (CraftInventoryPlayer) craftPlayer.getInventory();
		try {
			ClassUtils.setPrivateField(cInv, "inventory", inv);
		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final NoSuchFieldException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		}
		replacedInv.add(player);
	}

}
