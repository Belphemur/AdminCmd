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
package be.Balor.bukkit.AdminCmd;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.LocaleManager;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public enum LocaleHelper {
	ITEMID("itemId", ChatColor.GOLD + "%player" + ChatColor.GRAY + " is holding " + ChatColor.GREEN
			+ "%item" + ChatColor.WHITE + ":" + ChatColor.RED + "%data"),
	EGG_PARAM("eggCmdParam", ChatColor.RED + "The parameter " + ChatColor.GOLD + "-E"
			+ ChatColor.RED + " is needed to choose the egg."),
	EGG_PARAM_BLOCK("eggBlockParam", ChatColor.RED + "The parameter " + ChatColor.GOLD + "-b"
			+ ChatColor.RED + " is needed to choose the block to use for replacement."),
	EGG_PARAM_ENTITY("eggEntityParam", ChatColor.RED + "The parameter " + ChatColor.GOLD + "-e"
			+ ChatColor.RED + " is needed to choose the entity id to spawn."),
	EGG_PARAM_MOB("eggMobParam", ChatColor.RED + "The parameter " + ChatColor.GOLD + "-m"
			+ ChatColor.RED + " is needed to choose the mob to spawn."),
	ECHANT_DONT_EXISTS("echantDontExists", ChatColor.RED + "The enchantment " + ChatColor.GOLD
			+ "%enchant " + ChatColor.RED + "don't exists !"),
	CANT_ENCHANT("cantEnchantItem", ChatColor.RED + "This item " + ChatColor.GOLD + "%item"
			+ ChatColor.RED + " can't be enchanted with " + ChatColor.GREEN + "%enchant"),
	SUCCESS_ENCHANT("enchantSuccess", ChatColor.GREEN + "Your item " + ChatColor.GOLD + "%item"
			+ ChatColor.GREEN + " is now with " + ChatColor.BLUE + "%enchant" + ChatColor.GREEN
			+ " lvl " + ChatColor.LIGHT_PURPLE + "%lvl");

	private final String key;
	private final String locale;

	private LocaleHelper(String key, String locale) {
		this.key = key;
		this.locale = locale;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	public static void addAllLocales() {
		for (LocaleHelper lh : values())
			LocaleManager.getInstance().addLocale(lh.key, lh.locale);
	}

	/**
	 * Send the locale the the CommandSender
	 * 
	 * @param sender
	 *            CommandSender that will get the message
	 * @param replace
	 *            Replacement variables
	 */
	public void sendLocale(CommandSender sender, Map<String, String> replace) {
		Utils.sI18n(sender, this, replace);
	}

	/**
	 * Get the locale with replacement variables
	 * 
	 * @param replace
	 *            Replacement variables
	 * @return the locale with the variables replaced in.
	 */
	public String getLocale(Map<String, String> replace) {
		return Utils.I18n(this, replace);
	}

	/**
	 * Get the locale without replacement variables
	 * 
	 * @return the locale
	 */
	public String getLocale() {
		return Utils.I18n(this);
	}

}
