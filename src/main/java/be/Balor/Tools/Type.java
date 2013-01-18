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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public enum Type {
	FLY(Category.SUPER_POWER),
	FLY_OLD(Category.SUPER_POWER),
	VULCAN(Category.SUPER_POWER),
	GOD(Category.SUPER_POWER),
	THOR(Category.SUPER_POWER),
	BANNED(Category.SANCTION),
	KICKED(Category.SANCTION),
	FIREBALL(Category.SUPER_POWER),
	SPYMSG(Category.OTHER),
	FROZEN(Category.SANCTION),
	MUTED(Category.SANCTION),
	MUTED_COMMAND(Category.SANCTION),
	MOB_LIMIT(Category.WORLD),
	NO_PICKUP(Category.SUPER_POWER),
	WEATHER_FROZEN(Category.WORLD),
	REPEAT_CMD(Category.OTHER),
	TIME_FROZEN(Category.WORLD),
	TP_REQUEST(Category.OTHER),
	TP_AT_SEE(Category.SUPER_POWER),
	SUPER_BREAKER(Category.SUPER_POWER),
	INVISIBLE(Category.SUPER_POWER),
	FAKEQUIT(Category.SUPER_POWER),
	ETERNAL(Category.SUPER_POWER),
	NO_DROP(Category.SUPER_POWER),
	EGG(Category.SUPER_POWER),
	CUSTOM(Category.MISC);

	private static final Map<String, Type> lookupName = new HashMap<String, Type>();

	@Override
	public String toString() {
		final String s = super.toString();
		return s.toLowerCase();
	}

	public String display() {
		final String s = super.toString();
		return s.substring(0, 1)
				+ s.substring(1).toLowerCase().replaceAll("_", " ");
	}

	private final Category category;

	private Type(final Category category) {
		this.category = category;
	}

	public int id() {
		return ordinal() ^ getClass().getName().hashCode();
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

	public static void addCustomPower(final String name) {
		lookupName.put(name, CUSTOM);
	}

	static {
		for (final Type type : values()) {
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
		SUPER_POWER, WORLD, OTHER, SANCTION, MISC;
	}

	public enum Weather {
		STORM, RAIN, CLEAR, FREEZE;
	}

	public enum Tp {
		HERE, TO, PLAYERS;
		@Override
		public String toString() {
			final String s = super.toString();
			return s.toLowerCase().replaceAll("_", " ");
		}
	}

	public enum Whois {
		LOGOUT("lastDisconnect"), LOGIN("lastConnection");
		private final String val;

		private Whois(final String val) {
			this.val = val;
		}

		/**
		 * @return the val
		 */
		public String getVal() {
			return val;
		}
	}

	public enum ArmorPart {
		BOOTS(0, 301), LEGS(1, 300), CHEST(2, 299), HEAD(3, 298);
		private final int placeInInventory;
		private final List<Integer> possibleId = new ArrayList<Integer>();
		private static final int nbEquipment = 5;

		/**
		 * @param placeInInventory
		 */
		private ArmorPart(final int placeInInventory, final int firstPossible) {
			this.placeInInventory = placeInInventory;
			for (int i = 0; i < nbEquipment; i++) {
				possibleId.add((i * 4) + firstPossible);
			}
		}

		/**
		 * @return the placeInInventory
		 */
		public int getPlaceInInventory() {
			return placeInInventory;
		}

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

		/**
		 * Check if the id is valid for that ArmorPart
		 * 
		 * @param toCheck
		 * @return
		 */
		public boolean isValid(final int toCheck) {
			return possibleId.contains(toCheck);
		}
	}

	public enum Health {
		KILL, HEAL, FEED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

	}

	public enum Limit {
		IMMUNITY("immunityLvl"), MAX_HOME("maxHomeByUser"), MAX_ITEMS(
				"maxItemAmount");
		/**
		 * 
		 */
		private final String text;

		/**
		 * @param text
		 * @param defaultLimit
		 */
		private Limit(final String text) {
			this.text = text;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return text;
		}

	}

}
