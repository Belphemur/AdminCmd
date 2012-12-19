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

import java.util.HashMap;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Egg.CreaturesInEgg;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.Exceptions.ParameterMissingException;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class MobEgg extends EggType<CreaturesInEgg> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9183441312817879597L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Egg.EggType#execute(org.bukkit.event.player.
	 * PlayerEggThrowEvent)
	 */
	@Override
	public void onEvent(final PlayerEggThrowEvent event) {
		event.setHatching(true);
		event.setHatchingType(value.getType());
		event.setNumHatches(value.getNb());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Egg.EggType#processArguments(be.Balor.Manager.Commands
	 * .CommandArgs)
	 */
	@Override
	protected void processArguments(final Player sender, final CommandArgs args)
			throws ProcessingArgsException {
		if (!args.hasFlag('m')) {
			throw new ParameterMissingException('m',
					LocaleHelper.EGG_PARAM_MOB.getLocale());
		}
		int nbre = 1;
		final String valFlag = args.getValueFlag('n');
		final String mob = args.getValueFlag('m');
		if (args.hasFlag('n')) {
			try {
				nbre = Integer.parseInt(valFlag);
			} catch (final NumberFormatException e) {
				Utils.sI18n(sender, "NaN", "number", valFlag);
				return;
			}
		}
		EntityType ct = null;

		ct = EntityType.fromName(mob);
		if (ct == null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("mob", mob);
			Utils.sI18n(sender, "errorMob", replace);
			return;
		}
		value = new CreaturesInEgg(ct, Integer.valueOf(nbre).byteValue());
	}
}
