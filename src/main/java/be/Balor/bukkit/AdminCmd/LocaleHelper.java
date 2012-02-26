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
			+ "%item" + ChatColor.WHITE + ":" + ChatColor.RED + "%data");

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

	public void sendLocale(CommandSender sender, Map<String, String> replace) {
		Utils.sI18n(sender, this, replace);
	}

	public String getLocale(Map<String, String> replace) {
		return Utils.I18n(this, replace);
	}

}
