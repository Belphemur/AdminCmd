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

import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.ProcessingArgsException;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class NormalEgg extends EggType<Object> {

	/* (non-Javadoc)
	 * @see be.Balor.Tools.Egg.EggType#onEvent(org.bukkit.event.player.PlayerEggThrowEvent)
	 */
	@Override
	public void onEvent(PlayerEggThrowEvent event) {
		
	}

	/* (non-Javadoc)
	 * @see be.Balor.Tools.Egg.EggType#processArguments(be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void processArguments(CommandArgs args) throws ProcessingArgsException {
		
	}

}
