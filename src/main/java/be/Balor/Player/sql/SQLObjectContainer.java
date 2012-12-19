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
		return super.getEggType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Files.ObjectContainer#castBoolean(java.lang.Object)
	 */
	@Override
	protected Boolean castBoolean(final Object o) {
		if (o instanceof String) {
			synchronized (yaml) {
				return super.castBoolean(yaml.load((String) o));
			}
		} else {
			return super.castBoolean(o);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Files.ObjectContainer#castDouble(java.lang.Object)
	 */
	@Override
	protected Double castDouble(final Object o) {
		if (o instanceof String) {
			synchronized (yaml) {
				return super.castDouble(yaml.load((String) o));
			}
		} else {
			return super.castDouble(o);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Files.ObjectContainer#castFloat(java.lang.Object)
	 */
	@Override
	protected Float castFloat(final Object o) {
		if (o instanceof String) {
			synchronized (yaml) {
				return super.castFloat(yaml.load((String) o));
			}
		} else {
			return super.castFloat(o);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Files.ObjectContainer#castInt(java.lang.Object)
	 */
	@Override
	protected Integer castInt(final Object o) {
		if (o instanceof String) {
			synchronized (yaml) {
				return super.castInt(yaml.load((String) o));
			}
		} else {
			return super.castInt(o);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Files.ObjectContainer#castLong(java.lang.Object)
	 */
	@Override
	protected Long castLong(final Object o) {
		if (o instanceof String) {
			synchronized (yaml) {
				return super.castLong(yaml.load((String) o));
			}
		} else {
			return super.castLong(o);
		}
	}

}
