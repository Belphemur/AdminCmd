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

import org.bukkit.Material;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public enum Type {
	FLY(Category.SUPER_POWER, "admincmd.player.fly"),
	FLY_OLD(Category.SUPER_POWER, "admincmd.player.fly"),
	VULCAN(Category.SUPER_POWER, "admincmd.player.vulcan"),
	GOD(Category.SUPER_POWER, "admincmd.player.god"),
	THOR(Category.SUPER_POWER, "admincmd.player.thor"),
	BANNED(Category.SANCTION),
	KICKED(Category.SANCTION),
	FIREBALL(Category.SUPER_POWER, "admincmd.player.fireball"),
	SPYMSG(Category.OTHER),
	FROZEN(Category.SANCTION),
	MUTED(Category.SANCTION),
	MUTED_COMMAND(Category.SANCTION),
	MOB_LIMIT(Category.WORLD),
	NO_PICKUP(Category.SUPER_POWER, "admincmd.player.nopickup"),
	WEATHER_FROZEN(Category.WORLD),
	REPEAT_CMD(Category.OTHER),
	TIME_FROZEN(Category.WORLD),
	TP_REQUEST(Category.OTHER),
	TP_AT_SEE(Category.SUPER_POWER, "admincmd.tp.see"),
	SUPER_BREAKER(Category.SUPER_POWER, "admincmd.player.superbreaker"),
	INVISIBLE(Category.SUPER_POWER, "admincmd.player.invisible"),
	FAKEQUIT(Category.SUPER_POWER, "admincmd.player.fakequit"),
	ETERNAL(Category.SUPER_POWER, "admincmd.player.eternal"),
	NO_DROP(Category.SUPER_POWER, "admincmd.player.nodrop"),
	EGG(Category.SUPER_POWER),
	BLOCK_IN_TIME(Category.OTHER),
	CUSTOM(Category.MISC);

	private static final Map<String, Type> lookupName = new HashMap<String, Type>();

	@Override
	public String toString() {
		final String s = super.toString();
		return s.toLowerCase();
	}

	public String display() {
		final String s = super.toString();
		return s.substring(0, 1) + s.substring(1).toLowerCase().replaceAll("_", " ");
	}

	private final Category category;
	private final String permission;

	/**
	 * @param category
	 * @param permission
	 */
	private Type(final Category category, final String permission) {
		this.category = category;
		this.permission = permission;
	}

	/**
	 * @param category
	 */
	private Type(final Category category) {
		this.category = category;
		this.permission = null;
	}

	/**
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
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
		BOOTS(0) {
			@Override
			public boolean isValid(final Material toCheck) {
				return toCheck.toString().endsWith("BOOTS");
			}
		},
		LEGS(1) {
			@Override
			public boolean isValid(final Material toCheck) {
				return toCheck.toString().endsWith("LEGGINGS");
			}
		},
		CHEST(2) {
			@Override
			public boolean isValid(final Material toCheck) {
				return toCheck.toString().endsWith("CHESTPLATE");
			}
		},
		HEAD(3) {
			@Override
			public boolean isValid(final Material toCheck) {
				return toCheck.toString().endsWith("HELMET");
			}
		};
		private final int placeInInventory;
		private static Map<String, ArmorPart> perName = new HashMap<String, Type.ArmorPart>();
		static {
			for (final ArmorPart part : ArmorPart.values()) {
				perName.put(part.name().toLowerCase(), part);
			}
		}

		public static ArmorPart getByName(final String name) {
			return perName.get(name.toLowerCase());
		}

		/**
		 * @param placeInInventory
		 */
		private ArmorPart(final int placeInInventory) {
			this.placeInInventory = placeInInventory;
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
		public abstract boolean isValid(final Material toCheck);
	}

	public enum Health {
		KILL, HEAL, FEED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

	}

	public enum Limit {
		IMMUNITY("immunityLvl"), MAX_HOME("maxHomeByUser"), MAX_ITEMS("maxItemAmount");
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

	public enum Spawn {
		GLOBALSPAWN, HOME, BED, GROUP;
	}

}
