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

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Egg.CreaturesInEgg;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.ParameterMissingException;
import be.Balor.Tools.Egg.ProcessingArgsException;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class MobEgg extends EggType<CreaturesInEgg> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Egg.EggType#execute(org.bukkit.event.player.
	 * PlayerEggThrowEvent)
	 */
	@Override
	public void onEvent(PlayerEggThrowEvent event) {
		event.setHatching(true);
		event.setHatchType(value.getType());
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
	protected void processArguments(Player sender, CommandArgs args) throws ProcessingArgsException {
		if (!args.hasFlag('m'))
			throw new ParameterMissingException("m");
		int nbre = 1;
		String valFlag = args.getValueFlag('n');
		String mob = args.getValueFlag('m');
		if (args.hasFlag('n'))
			try {
				nbre = Integer.parseInt(valFlag);
			} catch (NumberFormatException e) {
				Utils.sI18n(sender, "NaN", "number", valFlag);
				return;
			}
		CreatureType ct = null;

		ct = CreatureType.fromName(mob);
		if (ct == null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("mob", mob);
			Utils.sI18n(sender, "errorMob", replace);
			return;
		}
		value = new CreaturesInEgg(ct, Integer.valueOf(nbre).byteValue());

	}
}
