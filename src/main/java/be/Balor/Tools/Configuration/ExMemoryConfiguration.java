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

import java.util.Map;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class ExMemoryConfiguration extends ExMemorySection implements Configuration {
    protected Configuration defaults;
    protected ExMemoryConfigurationOption options;

    /**
     * Creates an empty {@link MemoryConfiguration} with no default values.
     */
    public ExMemoryConfiguration() {}

    /**
     * Creates an empty {@link MemoryConfiguration} using the specified {@link Configuration}
     * as a source for all default values.
     *
     * @param defaults Default value provider
     * @throws IllegalArgumentException Thrown if defaults is null
     */
    public ExMemoryConfiguration(Configuration defaults) {
        this.defaults = defaults;
    }

    @Override
    public void addDefault(String path, Object value) {
        if (path == null) {
            throw new IllegalArgumentException("Path may not be null");
        }

        if (defaults == null) {
            defaults = new MemoryConfiguration();
        }

        defaults.set(path, value);
    }

    public void addDefaults(Map<String, Object> defaults) {
        if (defaults == null) {
            throw new IllegalArgumentException("Defaults may not be null");
        }

        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            addDefault(entry.getKey(), entry.getValue());
        }
    }

    public void addDefaults(Configuration defaults) {
        if (defaults == null) {
            throw new IllegalArgumentException("Defaults may not be null");
        }

        addDefaults(defaults.getValues(true));
    }

    public void setDefaults(Configuration defaults) {
        if (defaults == null) {
            throw new IllegalArgumentException("Defaults may not be null");
        }

        this.defaults = defaults;
    }

    public Configuration getDefaults() {
        return defaults;
    }

    @Override
    public ConfigurationSection getParent() {
        return null;
    }

    public ExMemoryConfigurationOption options() {
        if (options == null) {
            options = new ExMemoryConfigurationOption(this);
        }

        return options;
    }
}
