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
package be.Balor.Tools.CommandUtils;

import info.somethingodd.OddItem.OddItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Exceptions.InvalidInputException;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Antoine
 * 
 */
public final class Materials {

	public static final Character CHATCOLOR_DELIMITER = '&';
	public static final Pattern REGEX_COLOR_PERSER = Pattern
			.compile(CHATCOLOR_DELIMITER + "[A-Fa-f]|" + CHATCOLOR_DELIMITER
					+ "1[0-5]|" + CHATCOLOR_DELIMITER + "[0-9]|"
					+ CHATCOLOR_DELIMITER + "[L-Ol-o]");

	/**
	 * 
	 */
	private Materials() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 * @throws InvalidInputException
	 *             if the input is invalid
	 */
	public static MaterialContainer checkMaterial(final String mat)
			throws InvalidInputException {
		MaterialContainer mc = new MaterialContainer();
		try {
			if (Utils.oddItem != null) {
				final ItemStack is = OddItem.getItemStack(mat);
				if (is != null) {
					return new MaterialContainer(is);
				}
			}
		} catch (final Exception e) {
		}
		String[] info = new String[2];
		if (mat.contains(":")) {
			info = mat.split(":");
			if (info.length < 2) {
				throw new InvalidInputException(mat);
			}
			mc = new MaterialContainer(info[0], info[1]);
		} else {
			info[0] = mat;
			info[1] = "0";
			if ((mc = ACHelper.getInstance().getAlias(info[0])) == null) {
				mc = new MaterialContainer(info[0], info[1]);
			}
		}
		return mc;

	}

	/**
	 * Parse a string and replace the color in it
	 * 
	 * @author Speedy64
	 * @param toParse
	 * @return
	 */
	public static String colorParser(final String toParse) {
		try {
			return ChatColor.translateAlternateColorCodes(
					Materials.CHATCOLOR_DELIMITER, toParse);
		} catch (final NoSuchMethodError e) {
			return oldColorParser(toParse);
		}
	}

	private static String oldColorParser(final String toParse) {
		String ResultString = null;
		try {
			Matcher regexMatcher = Materials.REGEX_COLOR_PERSER
					.matcher(toParse);
			String result = toParse;
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group();
				int colorint = Integer.parseInt(ResultString.substring(1, 2),
						16);
				if (ResultString.length() > 1) {
					if (colorint == 1
							&& ResultString.substring(2).matches("[012345]")) {
						colorint = colorint * 10
								+ Integer.parseInt(ResultString.substring(2));
					}
				}
				result = regexMatcher.replaceFirst(ChatColor.getByChar(
						Integer.toHexString(colorint)).toString());
				regexMatcher = Materials.REGEX_COLOR_PERSER.matcher(result);
			}
			return result;
		} catch (final Exception ex) {
			return toParse;
		}
	}

}
