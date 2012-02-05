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

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Permissions.PermissionManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class EggType<T> {
	protected T value;

	private final static ClassLoader eggTypeLoader = new EggTypeClassLoader();

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
	 * @param sender
	 *            Player that send the command.
	 * @param args
	 *            argument that will be used to set the EggType.
	 * 
	 * @throws ProcessingArgsException
	 *             when there is a problem in the arguments
	 */
	protected abstract void processArguments(Player sender, CommandArgs args)
			throws ProcessingArgsException;

	/**
	 * Check if the user have the permission to use this Egg
	 * 
	 * @param player
	 * @return
	 */
	protected boolean checkPermission(Player player) {
		String perm;
		if (this.getClass().isAnnotationPresent(EggPermission.class))
			perm = this.getClass().getAnnotation(EggPermission.class).permission();
		else {
			String simpleName = this.getClass().getSimpleName();
			perm = simpleName.substring(0, simpleName.length() - 4).toLowerCase();
		}
		if (perm == null || (perm != null && perm.isEmpty()))
			return true;
		return PermissionManager.hasPerm(player, perm);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * Will create the Egg that can be assigned later to the player. This method
	 * check if the player has the permission to use that egg and if the
	 * arguments of the command are right.
	 * 
	 * @param player
	 *            That execute the EggSpawn command.
	 * @param args
	 *            Provided by the EggSpawn command.
	 * @return an EggType of the chosen type if every test is passed.
	 * @throws ProcessingArgsException
	 *             if there is a problem while processing the arguments, like a
	 *             Parameter missing, or some other error.
	 * @throws DontHaveThePermissionException
	 *             if the player don't have the permission to use that egg
	 */
	public static EggType<?> createEggType(Player player, CommandArgs args)
			throws ProcessingArgsException, DontHaveThePermissionException {
		if (!args.hasFlag('e'))
			throw new ParameterMissingException("e");
		EggType<?> eggType;
		String className = args.getValueFlag('e');
		try {
			eggType = matchEggClass(className);
		} catch (ClassNotFoundException e) {
			throw new ProcessingArgsException("classNotFound", className, e);
		} catch (InstantiationException e) {
			throw new ProcessingArgsException("instance", className, e);
		} catch (IllegalAccessException e) {
			throw new ProcessingArgsException("IllegalAccess", className, e);
		}
		if (!eggType.checkPermission(player))
			throw new DontHaveThePermissionException();
		eggType.processArguments(player, args);
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
