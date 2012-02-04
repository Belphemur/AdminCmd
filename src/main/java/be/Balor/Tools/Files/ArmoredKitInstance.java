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
package be.Balor.Tools.Files;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Type.ArmorPart;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ArmoredKitInstance extends KitInstance {
	private static final int firstArmorItemId = 298;
	private static final int lastArmorItemId = 317;
	private final Map<Type.ArmorPart, MaterialContainer> armor = new EnumMap<Type.ArmorPart, MaterialContainer>(
			Type.ArmorPart.class);

	/**
	 * @param name
	 * @param delay
	 * @param items
	 */
	public ArmoredKitInstance(String name, int delay, List<MaterialContainer> items,
			Map<Type.ArmorPart, MaterialContainer> armor) {
		super(name, delay, items);
		for (Entry<Type.ArmorPart, MaterialContainer> e : armor.entrySet())
			if (lastArmorItemId <= e.getValue().getMaterial().getId()
					&& e.getValue().getMaterial().getId() >= firstArmorItemId)
				this.armor.put(e.getKey(), e.getValue());

	}

	/**
	 * Get the wanted part of the armor
	 * 
	 * @param part
	 * @return itemstack containing the part of the armor
	 */
	public ItemStack getArmorPart(Type.ArmorPart part) {
		MaterialContainer mat = armor.get(part);
		if (mat == null)
			return null;
		return mat.getItemStack();
	}

	/**
	 * Set the player armor using the armor set in the kit
	 * 
	 * @param p
	 *            player to change the armor's inventory.
	 * @return contain the Part of the armor that couldn't be setted.
	 */
	public ArmorPart[] setPlayerArmorParts(Player p) {
		List<ArmorPart> armorParts = new ArrayList<Type.ArmorPart>(ArmorPart.values().length);
		final ItemStack[] armors = new ItemStack[ArmorPart.values().length];
		final PlayerInventory inventory = p.getInventory();
		if (ConfigEnum.ARMOR_KIT_OVERRIDE.getBoolean()) {
			for (ArmorPart part : ArmorPart.values()) {
				ItemStack toadd = getArmorPart(part);
				if (toadd == null) {
					toadd = inventory.getItem(inventory.getSize() + part.getPlaceInInventory());
					armorParts.add(part);
				}
				armors[part.getPlaceInInventory()] = toadd;
			}
		} else {
			for (ArmorPart part : ArmorPart.values()) {
				ItemStack toadd = getArmorPart(part);
				if (inventory.getItem(inventory.getSize() + part.getPlaceInInventory()) != null)
					continue;
				if (toadd == null) {
					toadd = inventory.getItem(inventory.getSize() + part.getPlaceInInventory());
					armorParts.add(part);
				}
				armors[part.getPlaceInInventory()] = toadd;
			}
		}
		ACPluginManager.scheduleSyncTask(new Runnable() {

			@Override
			public void run() {
				inventory.setArmorContents(armors);
			}
		});
		return armorParts.toArray(new ArmorPart[0]);
	}
}
