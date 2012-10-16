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
package be.Balor.Tools.Egg.Types;

import net.minecraft.server.EntityTypes;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.EntityInEgg;
import be.Balor.Tools.Egg.Exceptions.ExceptionType;
import be.Balor.Tools.Egg.Exceptions.ParameterMissingException;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class EntityEgg extends EggType<EntityInEgg> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 805828153137593363L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Egg.EggType#onEvent(org.bukkit.event.player.
	 * PlayerEggThrowEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(final PlayerEggThrowEvent event) {
		event.getEgg().remove();
		event.setHatching(false);
		final World w = event.getEgg().getWorld();
		final Location loc = event.getEgg().getLocation();

		for (int i = 0; i < value.getNb(); i++) {
			w.spawn(loc, value.getEntityClass());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Egg.EggType#processArguments(org.bukkit.entity.Player,
	 * be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	protected void processArguments(final Player sender, final CommandArgs args)
			throws ProcessingArgsException {
		final String entityParam = args.getValueFlag('t');
		if (entityParam == null) {
			throw new ParameterMissingException('t',
					LocaleHelper.EGG_PARAM_ENTITY.getLocale());
		}
		final String valFlag = args.getValueFlag('n');
		int nbre = 1;
		int entityNb = 93;
		try {
			entityNb = Integer.parseInt(entityParam);
		} catch (final NumberFormatException e) {
			Utils.sI18n(sender, "NaN", "number", valFlag);
			return;
		}
		if (args.hasFlag('n')) {
			try {
				nbre = Integer.parseInt(valFlag);
				entityNb = Integer.parseInt(entityParam);
			} catch (final NumberFormatException e) {
				Utils.sI18n(sender, "NaN", "number", valFlag);
				return;
			}
		}
		try {
			final Entity entity = EntityTypes.a(entityNb,
					((CraftWorld) sender.getWorld()).getHandle())
					.getBukkitEntity();
			value = new EntityInEgg(entity.getClass().getName(), nbre, entity
					.getClass().getSimpleName());
		} catch (final NullPointerException e) {
			throw new ProcessingArgsException(ExceptionType.DONT_EXISTS,
					String.valueOf(entityNb));
		}

	}

}
