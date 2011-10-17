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
package be.Balor.Tools.Configuration;

import java.util.HashSet;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ExMemorySection extends MemorySection {
	protected static final HashSet<Class<? extends Object>> exNaturalClass = new HashSet<Class<? extends Object>>();
	/**
	 * 
	 */
	public ExMemorySection() {
		super();
	}

	/**
	 * @param exMemorySection
	 * @param key
	 */
	protected ExMemorySection(ConfigurationSection exMemorySection, String key) {
		super(exMemorySection, key);
	}

	/**
	 * Create a {@link ConfigurationSection} if it not existing else return the
	 * existing one.
	 * 
	 * @param path
	 *            Path to create/get the section at.
	 * @return the ConfigurationSection
	 */
	public ConfigurationSection addSection(String path) {
		ConfigurationSection result = getConfigurationSection(path);
		if (result == null) {
			result = createSection(path);
		}
		return result;
	}

	/**
	 * Create an entry in the {@link ConfigurationSection} if it not existing
	 * else return the existing one.
	 * 
	 * @param path
	 *            Path to create/get the entry at.
	 */
	public void add(String path, Object value) {
		if (isSet(path))
			return;
		set(path, value);
	}

	/**
	 * Shortcut to remove an item by setting it null
	 * 
	 * @param path
	 *            Path to remove the entry at.
	 */
	public void remove(String path) {
		set(path, null);
	}
	/* (non-Javadoc)
	 * @see org.bukkit.configuration.MemorySection#isNaturallyStorable(java.lang.Object)
	 */
	@Override
	protected boolean isNaturallyStorable(Object input) {
		return super.isNaturallyStorable(input) || exNaturalClass.contains(input.getClass());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.configuration.MemorySection#createSection(java.lang.String)
	 */
	@Override
	public ConfigurationSection createSection(String path) {
		if (path == null) {
			throw new IllegalArgumentException("Path cannot be null");
		} else if (path.length() == 0) {
			throw new IllegalArgumentException("Cannot create section at empty path");
		}

		String[] split = path.split(Pattern.quote(Character.toString(getRoot().options()
				.pathSeparator())));
		ConfigurationSection section = this;

		for (int i = 0; i < split.length - 1; i++) {
			ConfigurationSection last = section;

			section = getConfigurationSection(split[i]);

			if (section == null) {
				section = last.createSection(split[i]);
			}
		}

		String key = split[split.length - 1];

		if (section == this) {
			ConfigurationSection result = new ExMemorySection(this, key);
			map.put(key, result);
			return result;
		} else {
			return section.createSection(key);
		}
	}
	@Override
    public String getString(String path, String def) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        Object val = get(path, def);
        if(val == null)
        	return def;
        return (val instanceof String) ? (String)val : val.toString();
    }
}
