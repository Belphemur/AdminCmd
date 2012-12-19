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
package be.Balor.Tools.Configuration.File;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import be.Balor.Tools.TpRequest;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class YamlConstructor extends Constructor {
	private class ConstructCustomObject extends ConstructYamlMap {
		@SuppressWarnings("unchecked")
		@Override
		public Object construct(final Node node) {
			if (node.isTwoStepsConstruction()) {
				throw new YAMLException(
						"Unexpected referential mapping structure. Node: "
								+ node);
			}

			final Map<Object, Object> raw = (Map<Object, Object>) super
					.construct(node);

			if (raw.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
				final Map<String, Object> typed = new LinkedHashMap<String, Object>(
						raw.size());
				for (final Map.Entry<Object, Object> entry : raw.entrySet()) {
					typed.put(entry.getKey().toString(), entry.getValue());
				}

				try {
					return ConfigurationSerialization.deserializeObject(typed);
				} catch (final IllegalArgumentException ex) {
					throw new YAMLException("Could not deserialize object", ex);
				}
			}

			return raw;
		}

		@Override
		public void construct2ndStep(final Node node, final Object object) {
			throw new YAMLException(
					"Unexpected referential mapping structure. Node: " + node);
		}
	}

	private class ConstructTpRequest extends AbstractConstruct {
		@Override
		public Object construct(final Node node) {
			final String val = (String) constructScalar((ScalarNode) node);
			final String[] split = val.split(";");
			return new TpRequest(ACPluginManager.getServer()
					.getPlayer(split[0]), ACPluginManager.getServer()
					.getPlayer(split[1]));
		}
	}

	private final ClassLoader pluginClassLoader;

	/**
	 * 
	 */
	public YamlConstructor(final ClassLoader classLoader) {
		super();
		this.yamlConstructors.put(new Tag("!tpRequest"),
				new ConstructTpRequest());
		this.yamlConstructors.put(Tag.MAP, new ConstructCustomObject());
		this.pluginClassLoader = classLoader;
	}

	/*
	 * This is a modified version of the Constructor. Rather than using a class
	 * loader to get external classes, they are already predefined above. This
	 * approach works similar to the typeTags structure in the original
	 * constructor, except that class information is pre-populated during
	 * initialization rather than runtime.
	 * 
	 * @see org.yaml.snakeyaml.constructor.Constructor#getClassForName(org.yaml
	 * .snakeyaml.nodes.Node)
	 */
	@Override
	protected Class<?> getClassForName(final String name)
			throws ClassNotFoundException {
		return Class.forName(name, true, pluginClassLoader);
	}
}
