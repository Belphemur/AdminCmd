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
package be.Balor.Tools;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import be.Balor.Tools.Help.String.Str;

import com.google.common.base.Joiner;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class MaterialContainer implements Comparable<MaterialContainer> {
	private Material material = null;
	private short dmg = 0;
	private int amount = 1;
	private final Map<Enchantment, Integer> enchantments;
	private ItemMeta meta;
	private static final Map<String, Color> colors = new HashMap<String, Color>();
	static {
		colors.put("AQUA".toLowerCase(), Color.AQUA);
		colors.put("BLACK".toLowerCase(), Color.BLACK);
		colors.put("BLUE".toLowerCase(), Color.BLUE);
		colors.put("FUCHSIA".toLowerCase(), Color.FUCHSIA);
		colors.put("GRAY".toLowerCase(), Color.GRAY);
		colors.put("GREEN".toLowerCase(), Color.GREEN);
		colors.put("LIME".toLowerCase(), Color.LIME);
		colors.put("MAROON".toLowerCase(), Color.MAROON);
		colors.put("NAVY".toLowerCase(), Color.NAVY);
		colors.put("OLIVE".toLowerCase(), Color.OLIVE);
		colors.put("PURPLE".toLowerCase(), Color.PURPLE);
		colors.put("RED".toLowerCase(), Color.RED);
		colors.put("SILVER".toLowerCase(), Color.SILVER);
		colors.put("TEAL".toLowerCase(), Color.TEAL);
		colors.put("WHITE".toLowerCase(), Color.WHITE);
		colors.put("YELLOW".toLowerCase(), Color.YELLOW);
	}

	/**
	 * Possible color for LEATHER ARMOR
	 * 
	 * @return
	 */
	public static String possibleColors() {
		return Joiner.on(", ").join(colors.keySet());
	}

	/**
	 * Colorable items.
	 * 
	 * @return
	 */
	public static String possibleColoredItems() {
		return Joiner.on(", ").join(
				new Material[] { Material.LEATHER_HELMET,
						Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE,
						Material.LEATHER_BOOTS });
	}

	public MaterialContainer(final ItemStack is) {
		material = is.getType();
		dmg = is.getDurability();
		this.enchantments = is.getEnchantments();
	}

	public MaterialContainer(final String mat) {
		String[] info = new String[2];
		if (mat.contains(":")) {
			info = mat.split(":");
		} else {
			info[0] = mat;
			info[1] = "0";
		}
		parseMat(info[0]);
		parseDmg(info[1]);
		this.enchantments = new HashMap<Enchantment, Integer>();
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(final int amount) {
		this.amount = amount;
	}

	public MaterialContainer(final String mat, final String damage) {
		parseMat(mat);
		parseDmg(damage);
		this.enchantments = new HashMap<Enchantment, Integer>();
	}

	public MaterialContainer() {
		this.enchantments = new HashMap<Enchantment, Integer>();
	}

	private void parseMat(final String material) {
		Material m = null;
		try {
			final int id = Integer.parseInt(material);
			m = Material.getMaterial(id);
		} catch (final NumberFormatException e) {
			m = Material.matchMaterial(material);
		}
		this.material = m;
	}

	private void parseDmg(final String damage) {
		short d = 0;
		try {
			d = Short.parseShort(damage);
		} catch (final NumberFormatException e) {
		}
		this.dmg = d;
	}

	public boolean isNull() {
		return material == null;
	}

	/**
	 * Transform the MaterialContainer to an ItemStack
	 * 
	 * @param amount
	 * @return
	 */

	public ItemStack getItemStack() {
		final ItemStack toReturn = new ItemStack(material, amount, dmg);
		if (meta != null) {
			toReturn.setItemMeta(meta);
		}
		toReturn.addUnsafeEnchantments(enchantments);
		return toReturn;
	}

	@Override
	public String toString() {
		if (material != null) {
			return material.getId() + ":" + dmg;
		} else {
			return "";
		}
	}

	public String display() {
		if (material != null) {
			return material + ":" + dmg;
		} else {
			return "";
		}
	}

	/**
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @return the dmg
	 */
	public short getDmg() {
		return dmg;
	}

	/**
	 * @return the enchantments
	 */
	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	/**
	 * @param material
	 *            the material to set
	 */
	public void setMaterial(final Material material) {
		this.material = material;
	}

	/**
	 * @param dmg
	 *            the dmg to set
	 */
	public void setDmg(final short dmg) {
		this.dmg = dmg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final MaterialContainer o) {
		if (material.equals(o.getMaterial())) {
			return dmg - o.getDmg();
		}
		return material.compareTo(o.getMaterial());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dmg;
		result = prime * result
				+ ((material == null) ? 0 : material.hashCode());
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
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MaterialContainer)) {
			return false;
		}
		final MaterialContainer other = (MaterialContainer) obj;
		if (dmg != other.dmg) {
			return false;
		}
		if (material != other.material) {
			return false;
		}
		return true;
	}

	/**
	 * Try to set the color of the item
	 * 
	 * @param color
	 * 
	 * @return false if we can't find the wanted color
	 * @throws IllegalArgumentException
	 *             if the material can't be colored
	 */
	public boolean setColor(final String color) throws IllegalArgumentException {
		Color foundColor = colors.get(color);
		if (foundColor != null) {
			setColorMeta(foundColor);
			return true;
		}
		final String found = Str.matchString(colors.keySet(), color);
		if (found == null) {
			return false;
		}
		foundColor = colors.get(found);
		setColorMeta(foundColor);
		return true;
	}

	private void setColorMeta(final Color color)
			throws IllegalArgumentException {
		switch (material) {
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
			this.meta = Bukkit.getItemFactory().getItemMeta(material);
			((LeatherArmorMeta) this.meta).setColor(color);
			break;
		default:
			throw new IllegalArgumentException("This material :" + material
					+ " can't be modified");
		}
	}

}
