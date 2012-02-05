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
package be.Balor.Tools.Egg;

import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class EggType<T> {
	protected T value;

	private final static ClassLoader eggTypeLoader = new EggTypeClassLoader();
	static {
		EggTypeClassLoader.addPackage("be.Balor.Tools.Egg.Types");
	}

	/**
	 * This function will be executed by the listener when the player have the
	 * power Egg with this EggType
	 * 
	 * @param event
	 *            triggered when the player throw an egg.
	 */
	public abstract void onEvent(PlayerEggThrowEvent event);

	/**
	 * Will be called by the command {@link EggSpawner} to set the value
	 * 
	 * @param args
	 *            argument that will be used to set the EggType.
	 * @throws ProcessingArgsException
	 *             when there is a problem in the arguments
	 */
	public abstract void processArguments(CommandArgs args) throws ProcessingArgsException;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public static EggType<?> createEggType(CommandArgs args) throws ProcessingArgsException {
		if (!args.hasFlag('e'))
			throw new ParameterMissingException("e");
		EggType<?> eggType;
		String className = args.getValueFlag('e');
		try {
			eggType = matchEggClass(className);
		} catch (ClassNotFoundException e) {
			throw new ProcessingArgsException(className, e);
		} catch (InstantiationException e) {
			throw new ProcessingArgsException(className, e);
		} catch (IllegalAccessException e) {
			throw new ProcessingArgsException(className, e);
		}
		eggType.processArguments(args);
		return eggType;
	}

	@SuppressWarnings("unchecked")
	private static EggType<?> matchEggClass(String name) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		Class<? extends EggType<?>> c = (Class<? extends EggType<?>>) eggTypeLoader.loadClass(name);
		return c.newInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EggType " + "[Type=" + getClass().getSimpleName() + ", Value=" + getValue() + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EggType))
			return false;
		EggType other = (EggType) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
