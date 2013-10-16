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
package be.Balor.Kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
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
	private String color;
	protected final Map<Type.ArmorPart, MaterialContainer> armor = new EnumMap<Type.ArmorPart, MaterialContainer>(Type.ArmorPart.class);

	/**
	 * @param name
	 * @param delay
	 * @param items
	 */
	public ArmoredKitInstance(final String name, final int delay, final List<MaterialContainer> items, final Map<Type.ArmorPart, MaterialContainer> armor) {
		super(name, delay, items);
		for (final Entry<Type.ArmorPart, MaterialContainer> e : armor.entrySet()) {
			if (!e.getKey().isValid(e.getValue().getMaterial())) {
				continue;
			}
			this.armor.put(e.getKey(), e.getValue());
		}

	}

	/**
	 * @param name
	 * @param delay
	 * @param player
	 */
	public ArmoredKitInstance(final String name, final int delay, final Player player) {
		super(name, delay, player);
		final PlayerInventory inventory = player.getInventory();
		final ItemStack boots = inventory.getBoots();
		if (boots != null) {
			this.armor.put(ArmorPart.BOOTS, new MaterialContainer(boots));
		}
		final ItemStack chestplate = inventory.getChestplate();
		if (chestplate != null) {
			this.armor.put(ArmorPart.CHEST, new MaterialContainer(chestplate));
		}
		final ItemStack helmet = inventory.getHelmet();
		if (helmet != null) {
			this.armor.put(ArmorPart.HEAD, new MaterialContainer(helmet));
		}
		final ItemStack leggings = inventory.getLeggings();
		if (leggings != null) {
			this.armor.put(ArmorPart.LEGS, new MaterialContainer(leggings));
		}
	}

	public ArmoredKitInstance(final KitInstance kit) {
		super(kit.name, kit.delay, kit.items);
	}

	private ArmoredKitInstance(final KitInstance kit, final Map<Type.ArmorPart, MaterialContainer> armor) {
		this(kit.name, kit.delay, kit.items, armor);
	}

	/**
	 * Get the wanted part of the armor
	 * 
	 * @param part
	 * @return itemstack containing the part of the armor
	 */
	public ItemStack getArmorPart(final Type.ArmorPart part) {
		final MaterialContainer mat = armor.get(part);
		if (mat == null) {
			return null;
		}
		try {
			mat.setColor(color);
		} catch (final Exception e) {
		}
		return mat.getItemStack();
	}

	/**
	 * Set the player armor using the armor set in the kit
	 * 
	 * @param p
	 *            player to change the armor's inventory.
	 * @return contain the Part of the armor that couldn't be setted.
	 */
	public ArmorPart[] setPlayerArmorParts(final Player p) {
		final List<ArmorPart> armorParts = new ArrayList<Type.ArmorPart>(ArmorPart.values().length);
		final ItemStack[] armors = new ItemStack[ArmorPart.values().length];
		final PlayerInventory inventory = p.getInventory();
		if (ConfigEnum.ARMOR_KIT_OVERRIDE.getBoolean()) {
			for (final ArmorPart part : ArmorPart.values()) {
				ItemStack toadd = getArmorPart(part);
				if (toadd == null) {
					toadd = inventory.getItem(inventory.getSize() + part.getPlaceInInventory());
					armorParts.add(part);
				}
				armors[part.getPlaceInInventory()] = toadd;
			}
		} else {
			for (final ArmorPart part : ArmorPart.values()) {
				ItemStack toadd = getArmorPart(part);
				if (inventory.getItem(inventory.getSize() + part.getPlaceInInventory()) != null) {
					continue;
				}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Files.KitInstance#addParent(be.Balor.Tools.Files.KitInstance
	 * )
	 */
	@Override
	public void addParent(final KitInstance parent) {
		super.addParent(parent);
		if (!(parent instanceof ArmoredKitInstance)) {
			return;
		}
		final ArmoredKitInstance armoredParent = (ArmoredKitInstance) parent;
		for (final ArmorPart part : ArmorPart.values()) {
			if (armor.get(part) == null) {
				armor.put(part, armoredParent.armor.get(part));
			}
		}

	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(final String color) {
		this.color = color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Kit.KitInstance#serialize()
	 */
	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> serialized = super.serialize();
		serialized.put("color", color);
		final Map<String, MaterialContainer> armor = new LinkedHashMap<String, MaterialContainer>();
		for (final Entry<ArmorPart, MaterialContainer> armPart : this.armor.entrySet()) {
			armor.put(armPart.getKey().name().toLowerCase(), armPart.getValue());
		}
		serialized.put("armor", armor);
		return serialized;
	}

	/**
	 * Required method for configuration serialization
	 * 
	 * @param args
	 *            map to deserialize
	 * @return deserialized item stack
	 * @see ConfigurationSerializable
	 */
	public static ArmoredKitInstance deserialize(final Map<String, Object> args) {
		final KitInstance kit = KitInstance.deserialize(args);
		final Map<Type.ArmorPart, MaterialContainer> armor = new LinkedHashMap<Type.ArmorPart, MaterialContainer>();
		final Object armorObj = args.get("armor");
		if (armorObj instanceof Map<?, ?>) {
			for (final Entry<?, ?> entry : ((Map<?, ?>) armorObj).entrySet()) {
				armor.put(ArmorPart.getByName(entry.getKey().toString()), (MaterialContainer) entry.getValue());
			}
		}
		final ArmoredKitInstance armKit = new ArmoredKitInstance(kit, armor);
		final Object color = args.get("color");
		if (color != null) {
			armKit.setColor(color.toString());
		}
		return armKit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((armor == null) ? 0 : armor.hashCode());
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ArmoredKitInstance other = (ArmoredKitInstance) obj;
		if (armor == null) {
			if (other.armor != null) {
				return false;
			}
		} else if (!armor.equals(other.armor)) {
			return false;
		}
		if (color == null) {
			if (other.color != null) {
				return false;
			}
		} else if (!color.equals(other.color)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("ArmoredKitInstance [color=%s, armor=%s, toString()=%s]", color, Arrays.toString(armor.entrySet().toArray()), super.toString());
	}

}
