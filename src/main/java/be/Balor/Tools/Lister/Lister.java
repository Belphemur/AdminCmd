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
package be.Balor.Tools.Lister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import org.bukkit.ChatColor;

import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class Lister {
	private final static Map<List, Lister> lastListed = new EnumMap<Lister.List, Lister>(
			Lister.List.class);

	public enum List {
		BAN, MUTE;
	}

	/**
	 * 
	 */
	Lister() {
	}

	/**
	 * Get a Lister for the wanted type
	 * 
	 * @param type
	 *            type of the lister
	 * @return lister
	 */
	public static Lister getLister(final Lister.List type) {
		return getLister(type, true);
	}

	/**
	 * Get a Lister for the wanted type
	 * 
	 * @param type
	 *            type of the lister
	 * @param instanciate
	 *            if we want to create it if doesn't exists.
	 * @return lister
	 */
	public static Lister getLister(final Lister.List type,
			final boolean instanciate) {
		Lister lister = lastListed.get(type);
		if (!instanciate) {
			return lister;
		} else if (lister == null) {
			switch (type) {
				case BAN :
					lister = new BanLister();
					break;
				case MUTE :
					lister = new MuteLister();
					break;

				default :
					break;
			}
			lastListed.put(type, lister);
		}

		return lister;
	}

	/**
	 * Get the wanted page
	 * 
	 * @param page
	 *            nb of the page
	 * @return the items of the page to be displayed
	 * @throws EmptyListException
	 *             the list is empty
	 */
	public synchronized Collection<String> getPage(int page)
			throws EmptyListException {
		final int entryPerPage = ConfigEnum.LISTER_ITEMS.getInt();
		final Collection<String> list = getList();

		if (list.isEmpty()) {
			throw new EmptyListException();
		}

		final int maxPages = (int) Math.ceil(list.size()
				/ (double) entryPerPage);
		page = page > maxPages ? maxPages : page;
		final int start = (page - 1) * entryPerPage;
		final int end = start + entryPerPage > list.size()
				? list.size()
				: start + entryPerPage;
		final java.util.List<String> result = new ArrayList<String>();
		result.add(ChatColor.AQUA
				+ ACMinecraftFontWidthCalculator.strPadCenterChat(
						ChatColor.DARK_GREEN + " " + getType() + " (" + page
								+ "/" + maxPages + ") " + ChatColor.AQUA, '='));
		final String[] array = list.toArray(new String[]{});
		for (int i = start; i < end; i++) {
			result.add(array[i]);
		}

		return result;
	}

	/**
	 * Ask the lister to update its inner list
	 */
	public abstract void update();

	abstract Collection<String> getList();

	abstract List getType();
}
