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
package be.Balor.Manager;

import java.util.List;
import java.util.Map;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class LocaleManager {
	private static LocaleManager instance = null;
	private ExtendedConfiguration localeFile;
	private boolean noMsg = false;

	/**
	 * @return the instance
	 */
	public static LocaleManager getInstance() {
		if (instance == null)
			instance = new LocaleManager();
		return instance;
	}

	/**
	 * @param noMsg
	 *            the noMsg to set
	 */
	public void setNoMsg(boolean noMsg) {
		this.noMsg = noMsg;
	}

	/**
	 * Set the locale file.
	 * 
	 * @param fileName
	 */
	public void setLocaleFile(String fileName) {
		localeFile = new ExtendedConfiguration(fileName, "locales");
	}

	/**
	 * Save all the change made to the locale.
	 */
	public void save() {
		localeFile.save();
	}

	/**
	 * Add a locale.
	 * 
	 * @param key
	 * @param value
	 */
	public void addLocale(String key, String value) {
		localeFile.addProperty(key, value);
	}

	public void addLocale(String key, String value, boolean override) {
		localeFile.addProperty(key, value, override);
	}

	/**
	 * get the locale txt
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return get(key, null);
	}

	/**
	 * Get the locale, replace the keyword by the given strings
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public String get(String key, Map<String, String> values) {
		if (noMsg)
			return null;
		String locale = localeFile.getString(key);
		if (locale != null && values != null)
			for (String toReplace : values.keySet())
				locale = locale.replaceAll("%" + toReplace, values.get(toReplace));
		return locale;
	}

	public String get(String key, String alias, String replaceBy) {
		if (noMsg)
			return null;
		String locale = localeFile.getString(key);
		if (locale != null && alias != null)
			locale = locale.replaceAll("%" + alias, replaceBy);
		return locale;
	}

	/**
	 * Get all the possible locale key.
	 * 
	 * @return
	 */
	public List<String> getKeys() {
		return localeFile.getKeys();
	}

	/**
	 * Reload the locale file
	 */
	public void reload() {
		localeFile.load();
	}
}
