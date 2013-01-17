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
package be.Balor.Manager.Exceptions;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

/**
 * @author Antoine
 * 
 */
public class CantEnchantItemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5986958191431798997L;
	private final Enchantment enchant;
	private final Material mat;

	/**
	 * @param enchant
	 * @param mat
	 */
	public CantEnchantItemException(final Enchantment enchant,
			final Material mat) {
		super("Can't enchant " + mat + " with " + enchant);
		this.enchant = enchant;
		this.mat = mat;
	}

	/**
	 * @return the enchant
	 */
	public Enchantment getEnchant() {
		return enchant;
	}

	/**
	 * @return the mat
	 */
	public Material getMaterial() {
		return mat;
	}

}
