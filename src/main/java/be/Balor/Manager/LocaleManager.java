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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class LocaleManager {
	private static LocaleManager instance = null;
	private ConfigurationManager localeFile;
	private HashMap<String, String> locales = new HashMap<String, String>();
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
		localeFile = new ConfigurationManager(fileName, "locales");
	}

	/**
	 * Load the locale from the file
	 */
	public void load() {
		for (String key : localeFile.getKeys())
			locales.put(key, localeFile.getString(key));
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
		if(localeFile.addProperty(key, value))
			locales.put(key, value);
	}

	public String get(String key) {
		return get(key, null);
	}

	public String get(String key, Map<String, String> values) {
		if (noMsg)
			return null;
		String locale = locales.get(key);
		if (locale != null && values != null)
			for (String toReplace : values.keySet())
				locale = locale.replaceAll("%" + toReplace, values.get(toReplace));
		return locale;
	}
}
