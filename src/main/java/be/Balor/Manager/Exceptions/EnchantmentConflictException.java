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

import org.bukkit.enchantments.Enchantment;

/**
 * @author Antoine
 * 
 */
public class EnchantmentConflictException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2613263260612622670L;
	private final Enchantment triedEnchant, conflictEnchant;

	/**
	 * @param triedEnchant
	 * @param conflictEnchant
	 */
	public EnchantmentConflictException(final Enchantment triedEnchant,
			final Enchantment conflictEnchant) {
		super("Conflict between " + triedEnchant + " and " + conflictEnchant);
		this.triedEnchant = triedEnchant;
		this.conflictEnchant = conflictEnchant;
	}

	/**
	 * @return the triedEnchant
	 */
	public Enchantment getTriedEnchant() {
		return triedEnchant;
	}

	/**
	 * @return the conflictEnchant
	 */
	public Enchantment getConflictEnchant() {
		return conflictEnchant;
	}

}
