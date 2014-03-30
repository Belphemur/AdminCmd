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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import be.Balor.Manager.Exceptions.CantEnchantItemException;
import be.Balor.Manager.Exceptions.EnchantmentConflictException;
import be.Balor.Tools.Help.String.Str;

import com.google.common.base.Joiner;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class MaterialContainer implements Comparable<MaterialContainer>,
		ConfigurationSerializable {
	private Material material = null;
	private short dmg = 0;
	private int amount = 1;
	private final Map<Enchantment, Integer> enchantments;
	private ItemMeta meta;
	private static final Map<String, Color> COLORS = new HashMap<String, Color>();
	private final static Map<String, Enchantment> ENCHANT_LIST = new HashMap<String, Enchantment>();
	static {
		COLORS.put("AQUA".toLowerCase(), Color.AQUA);
		COLORS.put("BLACK".toLowerCase(), Color.BLACK);
		COLORS.put("BLUE".toLowerCase(), Color.BLUE);
		COLORS.put("FUCHSIA".toLowerCase(), Color.FUCHSIA);
		COLORS.put("GRAY".toLowerCase(), Color.GRAY);
		COLORS.put("GREEN".toLowerCase(), Color.GREEN);
		COLORS.put("LIME".toLowerCase(), Color.LIME);
		COLORS.put("MAROON".toLowerCase(), Color.MAROON);
		COLORS.put("NAVY".toLowerCase(), Color.NAVY);
		COLORS.put("OLIVE".toLowerCase(), Color.OLIVE);
		COLORS.put("PURPLE".toLowerCase(), Color.PURPLE);
		COLORS.put("RED".toLowerCase(), Color.RED);
		COLORS.put("SILVER".toLowerCase(), Color.SILVER);
		COLORS.put("TEAL".toLowerCase(), Color.TEAL);
		COLORS.put("WHITE".toLowerCase(), Color.WHITE);
		COLORS.put("YELLOW".toLowerCase(), Color.YELLOW);
		for (final Enchantment enchant : Enchantment.values()) {
			ENCHANT_LIST.put(enchant.getName().toLowerCase(), enchant);
		}
	}

	/**
	 * Possible color for LEATHER ARMOR
	 * 
	 * @return
	 */
	public static String possibleColors() {
		return Joiner.on(", ").join(COLORS.keySet());
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

	/**
	 * Existing Enchantment
	 * 
	 * @return
	 */
	public static String possibleEnchantment() {
		return Joiner.on(", ").join(ENCHANT_LIST.keySet());
	}

	public MaterialContainer(final ItemStack is) {
		material = is.getType();
		dmg = is.getDurability();
		this.enchantments = new HashMap<Enchantment, Integer>(
				is.getEnchantments());
		amount = is.getAmount();
		meta = is.getItemMeta();
	}

	public MaterialContainer(final MaterialContainer from) {
		material = from.material;
		dmg = from.dmg;
		enchantments = new HashMap<Enchantment, Integer>(from.enchantments);
		amount = from.amount;
		meta = from.meta;
	}

	/**
	 * @return the meta
	 */
	public ItemMeta getMeta() {
		return (meta == null ? meta = Bukkit.getItemFactory().getItemMeta(
				material) : meta);
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
	 * Add the wanted enchantment to the item with the wanted lvl
	 * 
	 * @param enchant
	 *            enchantment
	 * @param lvl
	 *            level
	 * @return false if the enchantment is already set
	 * @throws EnchantmentConflictException
	 *             if there is a conflict between an already existed enchantment
	 *             on the item.
	 * @throws CantEnchantItemException
	 *             if the item can't be enchanted by this enchantment.
	 */
	public boolean addEnchantment(final Enchantment enchant, final int lvl)
			throws EnchantmentConflictException, CantEnchantItemException {
		try {
			if (getMeta().hasEnchant(enchant)) {
				return false;
			}
		} catch (final Exception ex) {
			if (enchantments.containsKey(enchant)) {
				return false;
			}
		}
		if (!enchant.canEnchantItem(getItemStack())) {
			throw new CantEnchantItemException(enchant, this.material);
		}
		Set<Enchantment> enchants = new HashSet<Enchantment>();
		try {
			enchants = getMeta().getEnchants().keySet();
		} catch (final Exception e) {
			enchants = enchantments.keySet();
		}
		for (final Enchantment e : enchants) {
			if (e.conflictsWith(enchant)) {
				throw new EnchantmentConflictException(enchant, e);
			}
		}
		try {
			getMeta().addEnchant(enchant, lvl, true);
		} catch (final Exception ex) {
			enchantments.put(enchant, lvl);
		}

		return true;
	}

	/**
	 * Try to add an Enchantment to the item by parsing it
	 * 
	 * @param enchant
	 *            string containing the enchantment like
	 *            <code>enchant:lvl</code>
	 * @return true if we can add the enchantment on the item, false if the
	 *         enchantment don't exist or already present.
	 * @throws EnchantmentConflictException
	 *             if there is a conflict between an already existed enchantment
	 *             on the item.
	 * @throws CantEnchantItemException
	 *             if the item can't be enchanted by this enchantment.
	 */
	public boolean addEnchantment(final String enchant)
			throws EnchantmentConflictException, CantEnchantItemException {
		final String split[] = enchant.split(":");
		if (split.length < 2) {
			return false;
		}
		int lvl;
		try {
			lvl = Integer.parseInt(split[1]);
		} catch (final NumberFormatException e) {
			lvl = 1;
		}
		final Enchantment e = ENCHANT_LIST.get(split[0]);
		if (e != null) {
			return addEnchantment(e, lvl);
		}
		final String found = Str.matchString(ENCHANT_LIST.keySet(), split[0]);
		if (found == null) {
			return false;
		}
		return addEnchantment(ENCHANT_LIST.get(found), lvl);

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
		if (color == null) {
			return false;
		}
		Color foundColor = COLORS.get(color);
		if (foundColor != null) {
			setColorMeta(foundColor);
			return true;
		}
		final String found = Str.matchString(COLORS.keySet(), color);
		if (found == null) {
			return false;
		}
		foundColor = COLORS.get(found);
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
			((LeatherArmorMeta) getMeta()).setColor(color);
			break;
		default:
			throw new IllegalArgumentException("This material :" + material
					+ " can't be modified");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.configuration.serialization.ConfigurationSerializable#serialize
	 * ()
	 */
	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> result = new LinkedHashMap<String, Object>();

		result.put("type", material.name());

		if (this.dmg != 0) {
			result.put("damage", this.dmg);
		}

		if (getAmount() != 1) {
			result.put("amount", getAmount());
		}

		final ItemMeta meta = this.meta;
		if (!Bukkit.getItemFactory().equals(meta, null)) {
			result.put("meta", meta);
		} else if (!enchantments.isEmpty()) {
			final Map<String, Integer> convert = new LinkedHashMap<String, Integer>();
			for (final Entry<Enchantment, Integer> entry : enchantments
					.entrySet()) {
				convert.put(entry.getKey().getName(), entry.getValue());
			}
			result.put("enchantements", convert);
		}
		return result;
	}

	/**
	 * Required method for configuration serialization
	 * 
	 * @param args
	 *            map to deserialize
	 * @return deserialized item stack
	 * @see ConfigurationSerializable
	 */
	public static MaterialContainer deserialize(final Map<String, Object> args) {
		final Material type = Material.getMaterial((String) args.get("type"));
		short damage = 0;
		int amount = 1;

		if (args.containsKey("damage")) {
			damage = ((Number) args.get("damage")).shortValue();
		}

		if (args.containsKey("amount")) {
			amount = (Integer) args.get("amount");
		}

		final ItemStack result = new ItemStack(type, amount, damage);

		if (args.containsKey("enchantments")) { // Backward compatiblity,
												// @deprecated
			final Object raw = args.get("enchantments");

			if (raw instanceof Map) {
				final Map<?, ?> map = (Map<?, ?>) raw;

				for (final Map.Entry<?, ?> entry : map.entrySet()) {
					final Enchantment enchantment = Enchantment.getByName(entry
							.getKey().toString());

					if ((enchantment != null)
							&& (entry.getValue() instanceof Integer)) {
						result.addUnsafeEnchantment(enchantment,
								(Integer) entry.getValue());
					}
				}
			}
		} else if (args.containsKey("meta")) { // We cannot and will not have
												// meta when enchantments
												// (pre-ItemMeta) exist
			final Object raw = args.get("meta");
			if (raw instanceof ItemMeta) {
				result.setItemMeta((ItemMeta) raw);
			}
		}

		return new MaterialContainer(result);
	}

}
