/*This file is part of AdminCmd.

    AdminCmd is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AdminCmd is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.*/
package be.Balor.Player.sql;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import be.Balor.Tools.Configuration.File.ExtendedRepresenter;
import be.Balor.Tools.Configuration.File.YamlConstructor;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Files.ObjectContainer;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLObjectContainer extends ObjectContainer {
	public final static Yaml yaml = new Yaml(new YamlConstructor(
			ObjectContainer.class.getClassLoader()), new ExtendedRepresenter(),
			new DumperOptions());
	/**
	 * @param obj
	 */
	public SQLObjectContainer(final Object obj) {
		super(obj);
	}
	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Tools.Files.ObjectContainer#getEggType()
	 */
	@Override
	public EggType<?> getEggType() {
		if (getObj() instanceof String) {
			synchronized (yaml) {
				return (EggType<?>) yaml.load((String) getObj());
			}
		}
		return null;
	}
}
