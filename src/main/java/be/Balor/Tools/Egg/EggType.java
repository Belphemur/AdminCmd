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

import java.io.Serializable;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.permissions.Permission;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.Mob.EggSpawner;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Egg.Exceptions.DontHaveThePermissionException;
import be.Balor.Tools.Egg.Exceptions.ExceptionType;
import be.Balor.Tools.Egg.Exceptions.ParameterMissingException;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class EggType<T> implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 2793422400211176328L;

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
	 * @throws DontHaveThePermissionException
	 *             when the player don't have the permission, with the message
	 *             to display to the user
	 */
	protected boolean checkPermission(final Player player)
			throws DontHaveThePermissionException {
		final Permission perm = EggPermissionManager.INSTANCE
				.getPermission(this);
		if (!PermissionManager.hasPerm(player, perm, false)) {
			throw new DontHaveThePermissionException(Utils.I18n("errorNotPerm",
					"p", perm.getName()));
		}
		return true;
	}

	public T getValue() {
		return value;
	}

	public void setValue(final T value) {
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
	public static EggType<?> createEggType(final Player player,
			final CommandArgs args) throws ProcessingArgsException,
			DontHaveThePermissionException, NullPointerException {
		if (!args.hasFlag('E')) {
			throw new ParameterMissingException('E',
					LocaleHelper.EGG_PARAM.getLocale());
		}
		EggType<?> eggType;
		final String className = args.getValueFlag('E');
		try {
			eggType = matchEggClass(className);
		} catch (final ClassNotFoundException e) {
			throw new ProcessingArgsException(ExceptionType.NO_CLASS,
					className, e);
		} catch (final InstantiationException e) {
			throw new ProcessingArgsException(ExceptionType.INSTANCE,
					className, e);
		} catch (final IllegalAccessException e) {
			throw new ProcessingArgsException(ExceptionType.ILLEGAL_ACCESS,
					className, e);
		} catch (final NullPointerException e) {
			throw new NullPointerException();
		}
		eggType.checkPermission(player);
		eggType.processArguments(player, args);
		return eggType;
	}

	@SuppressWarnings("unchecked")
	private static EggType<?> matchEggClass(final String name)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NullPointerException {
		final Class<? extends EggType<?>> c = (Class<? extends EggType<?>>) eggTypeLoader
				.loadClass(name);
		return c.newInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " = " + getValue();
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
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EggType)) {
			return false;
		}
		final EggType other = (EggType) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

}
