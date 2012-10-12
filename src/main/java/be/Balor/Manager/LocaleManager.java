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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
	private final Map<String, ExtendedConfiguration> localesFiles = new HashMap<String, ExtendedConfiguration>();
	public static final String PRIMARY_LOCALE = "primary";
	public static final String DEFAULT_LOCALE = "default_locale";
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
	public void setNoMsg(final boolean noMsg) {
		this.noMsg = noMsg;
	}

	/**
	 * add a locale file.
	 * 
	 * @param name
	 *            of the file
	 * @param fileName
	 */
	public void addLocaleFile(final String name,
			final ExtendedConfiguration file) {
		localesFiles.put(name, file);
	}

	/**
	 * Save all the change made to the locale.
	 */
	public void save() {
		try {
			for (final ExtendedConfiguration conf : localesFiles.values()) {
				conf.save();
			}
		} catch (final Exception e) {
			ACLogger.severe("Problem while saving locale file", e);
		}

	}

	/**
	 * Add a locale to the primary file.
	 * 
	 * @param key
	 *            key of the locale
	 * @param value
	 *            text of the locale
	 */
	public void addLocale(final String key, final String value) {
		addLocale(key, value, false);
	}

	public void addLocale(final String key, final String value,
			final boolean override) {
		if (override) {
			localesFiles.get(PRIMARY_LOCALE).set(key, value);
		} else {
			localesFiles.get(DEFAULT_LOCALE).add(key, value);
		}
	}

	/**
	 * Add a locale to the wanted file.
	 * 
	 * @param file
	 *            name of the file.
	 * @param key
	 *            key of the locale
	 * @param value
	 *            text of the locale
	 * @param override
	 *            if true, override the existing locale
	 * @return true if everything went fine, else false.
	 */
	public boolean addLocale(final String file, final String key,
			final String value, final boolean override) {
		final ExtendedConfiguration conf = localesFiles.get(file);
		if (conf == null) {
			return false;
		}
		if (override) {
			conf.set(key, value);
			return true;
		} else {
			return conf.add(key, value);
		}
	}

	/**
	 * Add a locale to the wanted file.
	 * 
	 * @param file
	 *            name of the file.
	 * @param key
	 *            key of the locale
	 * @param value
	 *            text of the locale
	 * @return true if everything went fine, else false.
	 */
	public boolean addLocale(final String file, final String key,
			final String value) {
		return addLocale(file, key, value, false);
	}

	/**
	 * get the locale txt
	 * 
	 * @param key
	 * @return
	 */
	public String get(final String key) {
		return get(key, null);
	}

	/**
	 * Get the locale, replace the keyword by the given strings
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public String get(final String key, final Map<String, String> values) {
		if (noMsg) {
			return null;
		}
		String locale = getLocale(key);
		if (locale != null && values != null) {
			locale = recursiveReplaceLocale(locale, values);
		}
		return locale;
	}

	/**
	 * Get the locale in the particular file, replace the keyword by the given
	 * strings
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public String get(final String file, final String key,
			final Map<String, String> values) {
		if (noMsg) {
			return null;
		}
		String locale = getLocale(file, key);
		if (locale != null && values != null) {
			locale = recursiveReplaceLocale(locale, values);
		}
		return locale;
	}

	/**
	 * Get the locale in the wanted file.
	 * 
	 * @param file
	 * @param key
	 * @return
	 */
	private String getLocale(final String file, final String key) {
		final ExtendedConfiguration ex = localesFiles.get(file);
		if (ex == null) {
			return null;
		}
		return ex.getString(key);
	}

	/**
	 * Looking in every locale file for the locale asked, begin by the
	 * principalLocale;
	 * 
	 * @param key
	 * @return
	 */
	private String getLocale(final String key) {
		String result = localesFiles.get(PRIMARY_LOCALE).getString(key);
		if (result == null || (result != null && result.isEmpty())) {
			for (final Entry<String, ExtendedConfiguration> e : localesFiles
					.entrySet()) {
				if (e.getKey().equals(PRIMARY_LOCALE)) {
					continue;
				}
				result = e.getValue().getString(key);
				if (result != null && !result.isEmpty()) {
					break;
				}

			}
		}
		return result;

	}

	private String recursiveReplaceLocale(final String locale,
			final Map<String, String> values) {
		String ResultString = null;
		String result = locale;
		try {
			Matcher regexMatcher = recursiveLocale.matcher(locale);
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
				final String recLocale = getLocale(ResultString);
				if (recLocale != null) {
					result = regexMatcher.replaceFirst(recLocale);
				} else {
					result = regexMatcher.replaceFirst("");
				}
				regexMatcher = recursiveLocale.matcher(result);
			}
			regexMatcher = replaceLocale.matcher(result);
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
				final String replaceValue = values.get(ResultString);
				if (replaceValue != null) {
					try {
						result = regexMatcher.replaceFirst(Matcher.quoteReplacement(replaceValue));
					} catch (final StringIndexOutOfBoundsException e) {
						result = regexMatcher.replaceFirst(replaceValue
								.replaceAll("\\W", ""));
					}

				} else {
					result = regexMatcher.replaceFirst("");
				}
				regexMatcher = replaceLocale.matcher(result);
			}
		} catch (final PatternSyntaxException ex) {
			// Syntax error in the regular expression
		}
		// To correct interrogation point (?) problem in the locale file.
		final Matcher regexMatcher = buggedLocale.matcher(result);
		result = regexMatcher.replaceAll("§$1");
		return result;
	}

	public String get(final String key, final String alias,
			final String replaceBy) {
		if (noMsg) {
			return null;
		}
		String locale = getLocale(key);
		if (locale != null && alias != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put(alias, replaceBy);
			locale = recursiveReplaceLocale(locale, replace);
		}

		return locale;
	}

	public String get(final String file, final String key, final String alias,
			final String replaceBy) {
		if (noMsg) {
			return null;
		}
		String locale = getLocale(file, key);
		if (locale != null && alias != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
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
		final Set<String> locales = new HashSet<String>();
		for (final ExtendedConfiguration exConf : localesFiles.values()) {
			locales.addAll(exConf.getKeys(false));
		}
		return locales;
	}

	/**
	 * Reload the locale file
	 */
	public void reload() {
		try {
			for (final ExtendedConfiguration exConf : localesFiles.values()) {
				exConf.reload();
			}
		} catch (final FileNotFoundException e) {
			ACLogger.severe("Locale Reload Problem :", e);
		} catch (final IOException e) {
			ACLogger.severe("Locale Reload Problem :", e);
		} catch (final InvalidConfigurationException e) {
			ACLogger.severe("Locale Reload Problem :", e);
		}
	}
}
