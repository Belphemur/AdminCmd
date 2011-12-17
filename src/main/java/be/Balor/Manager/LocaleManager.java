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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.bukkit.configuration.InvalidConfigurationException;

import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class LocaleManager {
	private static LocaleManager instance = new LocaleManager();
	private ExtendedConfiguration localeFile;
	private boolean noMsg = false;
	private final Pattern recursiveLocale = Pattern.compile("#([\\w]+)#");
	private final Pattern replaceLocale = Pattern.compile("%([\\w]+)");
	private final Pattern buggedLocale = Pattern.compile("\\?(\\w)");

	/**
	 * @return the instance
	 */
	public static LocaleManager getInstance() {
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
	public void setLocaleFile(File file) {
		localeFile = ExtendedConfiguration.loadConfiguration(file);
	}

	/**
	 * Save all the change made to the locale.
	 */
	public void save() {
		try {
			localeFile.save();
		} catch (Exception e) {
			ACLogger.severe("Problem while saving locale file", e);
		}

	}

	/**
	 * Add a locale.
	 * 
	 * @param key
	 * @param value
	 */
	public void addLocale(String key, String value) {
		addLocale(key, value, false);
	}

	public void addLocale(String key, String value, boolean override) {
		if (override)
			localeFile.set(key, value);
		else
			localeFile.add(key, value);
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
			locale = recursiveReplaceLocale(locale, values);
		return locale;
	}

	private String recursiveReplaceLocale(String locale, Map<String, String> values) {
		String ResultString = null;
		String result = locale;
		try {
			Matcher regexMatcher = recursiveLocale.matcher(locale);
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
				String recLocale = localeFile.getString(ResultString);
				if (recLocale != null)
					result = regexMatcher.replaceFirst(recLocale);
				else
					result = regexMatcher.replaceFirst("");
				regexMatcher = recursiveLocale.matcher(result);
			}
			regexMatcher = replaceLocale.matcher(result);
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
				String replaceValue = values.get(ResultString);
				if (replaceValue != null) {
					try {
						result = regexMatcher.replaceFirst(replaceValue);
					} catch (StringIndexOutOfBoundsException e) {
						result = regexMatcher.replaceFirst(replaceValue.replaceAll("\\W", ""));
					}

				} else
					result = regexMatcher.replaceFirst("");
				regexMatcher = replaceLocale.matcher(result);
			}
		} catch (PatternSyntaxException ex) {
			// Syntax error in the regular expression
		}
		// To correct interrogation point (?) problem in the locale file.
		Matcher regexMatcher = buggedLocale.matcher(result);
		result = regexMatcher.replaceAll("§$1");

		return result;
	}

	public String get(String key, String alias, String replaceBy) {
		if (noMsg)
			return null;
		String locale = localeFile.getString(key);
		if (locale != null && alias != null) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put(alias, replaceBy);
			locale = recursiveReplaceLocale(locale, replace);
		}

		return locale;
	}

	/**
	 * Get all the possible locale key.
	 * 
	 * @return
	 */
	public Set<String> getKeys() {
		return localeFile.getKeys(false);
	}

	/**
	 * Reload the locale file
	 */
	public void reload() {
		try {
			localeFile.reload();
		} catch (FileNotFoundException e) {
			ACLogger.severe("Locale Reload Problem :", e);
		} catch (IOException e) {
			ACLogger.severe("Locale Reload Problem :", e);
		} catch (InvalidConfigurationException e) {
			ACLogger.severe("Locale Reload Problem :", e);
		}
	}
}
