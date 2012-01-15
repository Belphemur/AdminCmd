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

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ExMemorySection extends MemorySection implements ExConfigurationSection {
	protected final Lock lock = new ReentrantLock(true);

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

	@Override
	public ExConfigurationSection addSection(String path) {
		ExConfigurationSection result = getConfigurationSection(path);
		if (result == null) {
			result = createSection(path);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.configuration.MemorySection#set(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void set(String path, Object value) {
		lock.lock();
		try {
			super.set(path, value);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.configuration.MemorySection#get(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public Object get(String path, Object def) {
		lock.lock();
		Object info;
		try {
			info = super.get(path, def);
		} finally {
			lock.unlock();
		}
		return info;
	}

	@Override
	public void add(String path, Object value) {
		if (isSet(path))
			return;
		set(path, value);
	}

	@Override
	public long getLong(String path, long def) {
		if (path == null) {
			throw new IllegalArgumentException("Path cannot be null");
		}

		Object val = get(path, def);
		long returnVal;
		try {
			returnVal = castToLong(val);
		} catch (NumberFormatException e) {
			returnVal = def;
		}
		return returnVal;
	}

	private long castToLong(Object value) throws NumberFormatException {
		if (value instanceof Long)
			return (Long) value;
		else if (value instanceof String)
			return Long.parseLong((String) value);
		else if (value instanceof Integer)
			return Long.valueOf(((Integer) value));
		throw new NumberFormatException();
	}

	/**
	 * Shortcut to remove an item by setting it null
	 * 
	 * @param path
	 *            Path to remove the entry at.
	 */
	@Override
	public void remove(String path) {
		set(path, null);
	}


	@Override
	public ExConfigurationSection getConfigurationSection(String path) {
		if (path == null) {
			throw new IllegalArgumentException("Path cannot be null");
		}

		Object val = get(path, getDefault(path));
		return (val instanceof ExConfigurationSection) ? (ExConfigurationSection) val : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.configuration.MemorySection#createSection(java.lang.String)
	 */
	@Override
	public ExConfigurationSection createSection(String path) {
		if (path == null) {
			throw new IllegalArgumentException("Path cannot be null");
		} else if (path.length() == 0) {
			throw new IllegalArgumentException("Cannot create section at empty path");
		}

		String[] split = path.split(Pattern.quote(Character.toString(getRoot().options()
				.pathSeparator())));
		ExConfigurationSection section = this;

		for (int i = 0; i < split.length - 1; i++) {
			ExConfigurationSection last = section;

			section = getConfigurationSection(split[i]);

			if (section == null) {
				section = last.createSection(split[i]);
			}
		}

		String key = split[split.length - 1];

		if (section == this) {
			ExConfigurationSection result = new ExMemorySection(this, key);
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
		if (val == null)
			return def;
		return (val instanceof String) ? (String) val : val.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Configuration.ExConfigurationSection#getDoubleList(java
	 * .lang.String, java.util.List)
	 */
	@Override
	public List<Double> getDoubleList(String path, List<Double> def) {
		List<Double> result = super.getDoubleList(path);
		if (result == null || (result != null && result.isEmpty()))
			return def;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Configuration.ExConfigurationSection#getBooleanList(java
	 * .lang.String, java.util.List)
	 */
	@Override
	public List<Boolean> getBooleanList(String path, List<Boolean> def) {
		List<Boolean> result = super.getBooleanList(path);
		if (result == null || (result != null && result.isEmpty()))
			return def;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Configuration.ExConfigurationSection#getStringList(java
	 * .lang.String, java.util.List)
	 */
	@Override
	public List<String> getStringList(String path, List<String> def) {
		List<String> result = super.getStringList(path);
		if (result == null || (result != null && result.isEmpty()))
			return def;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Configuration.ExConfigurationSection#getIntList(java.lang
	 * .String, java.util.List)
	 */
	@Override
	public List<Integer> getIntList(String path, List<Integer> def) {
		List<Integer> result = getIntegerList(path);
		if (result == null || (result != null && result.isEmpty()))
			return def;
		return result;
	}

}
