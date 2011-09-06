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
/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public enum Type {
	FLY(Category.SUPER_POWER),
	VULCAN(Category.SUPER_POWER),
	GOD(Category.SUPER_POWER),
	THOR(Category.SUPER_POWER),
	BANNED(	Category.SANCTION),
	FIREBALL(Category.SUPER_POWER),
	SPYMSG(Category.OTHER),
	FROZEN(	Category.SANCTION),
	MUTED(Category.SANCTION),
	MOB_LIMIT(Category.WORLD),
	NO_PICKUP(Category.SUPER_POWER),
	WEATHER_FROZEN(Category.WORLD),
	REPEAT_CMD(Category.OTHER),
	TIME_FREEZED(Category.WORLD),
	TP_REQUEST(Category.OTHER),
	TP_AT_SEE(Category.SUPER_POWER),
	SUPER_BREAKER(Category.SUPER_POWER),
	INVISIBLE(Category.SUPER_POWER);

	private static final Map<String, Type> lookupName = new HashMap<String, Type>();

	@Override
	public String toString() {
		String s = super.toString();
		return s.toLowerCase();
	}
	
	public String display() {
		String s = super.toString();
		return s.substring(0, 1) + s.substring(1).toLowerCase().replaceAll("_", " ");
	}

	private final Category category;

	private Type(Category category) {
		this.category = category;
	}

	/**
	 * Attempts to match the Type with the given name. This is a match lookup;
	 * names will be converted to uppercase, then stripped of special characters
	 * in an attempt to format it like the enum
	 * 
	 * @param name
	 *            Name of the type to get
	 * @return Type if found, or null
	 */
	public static Type matchType(final String name) {
		Type result = null;

		String filtered = name.toUpperCase();
		filtered = filtered.replaceAll("\\s+", "_").replaceAll("\\W", "");
		result = lookupName.get(filtered);

		return result;
	}

	static {
		for (Type type : values()) {
			lookupName.put(type.name(), type);
		}
	}

	/**
	 * Gets the Category assigned to this type
	 * 
	 * @return Category of this Type
	 */
	public Category getCategory() {
		return category;
	}

	public enum Category {
		SUPER_POWER, WORLD, OTHER, SANCTION;
	}

	public enum Weather {
		STORM, RAIN, CLEAR, FREEZE;
	}

	public enum Tp {
		HERE, TO, PLAYERS;
		@Override
		public String toString() {
			String s = super.toString();
			return s.toLowerCase().replaceAll("_", " ");
		}
	}
}
